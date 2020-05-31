package com.me.urbanmart.GSTFiling.Utilities;

import com.me.urbanmart.GSTFiling.Config.AmazonConstants;
import com.me.urbanmart.GSTFiling.POJO.GSTR1;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.DoubleToIntFunction;

@Service
public class Utils {


    public HashMap<String, Workbook> consumeSheets() throws Exception {

        ClassLoader classLoader = this.getClass().getClassLoader();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource resource[] = null;
        HashMap<String, Workbook> stringWorkbookMap = new HashMap<>();

        try {
            resource = resourcePatternResolver.getResources("classpath:MTR_Reports_Amazon/*");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (resource != null) {

            for (Resource res : resource) {

                InputStream inputStream = res.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream);
                stringWorkbookMap.put(res.getFilename().replaceFirst("[.][^.]+$", ""), workbook);

            }
        }

        return stringWorkbookMap;

    }

    public void sanititzeSheets(HashMap<String, Workbook> workbookHashMap) {

        for (Map.Entry<String, Workbook> element : workbookHashMap.entrySet()) {

            String key = (String) element.getKey();

            Workbook workbook = workbookHashMap.get(key);

            System.out.println("Sanitizing " + key);

            //Sanitizing this workbook and again putting it in Map
            sanitizeWorkbook(workbook);
        }
    }

    /**
     * Remove all Transaction Type which are cancel
     *
     * @param workbook
     * @return
     */
    private void sanitizeWorkbook(Workbook workbook) {

        int k = 0;
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            boolean isRowCancel = false;
            // Removing null rows
            if (sheet.getRow(i) == null) {
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
                i--;
                continue;
            }
            final Row actualRow = sheet.getRow(i);

            //Check Cancel rows
            String type = actualRow.getCell(AmazonConstants.CANCELLED_ORDER_INDEX).getStringCellValue();
            if (type.equalsIgnoreCase(AmazonConstants.CANCELLED_ORDER)) {
                System.out.println(++k);
                //check if last row, just delete it
                if (i == sheet.getLastRowNum()) {
                    sheet.removeRow(actualRow);
                } else {
                    sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);

                }
                i--;
            }

        }

    }

    public HashMap<String, HashMap<String, List<GSTR1>>> mapifySheets(HashMap<String, Workbook> workbookHashMap) {

        HashMap<String, HashMap<String, List<GSTR1>>> monthStateJavaMap = new HashMap<>();

        for (Map.Entry<String, Workbook> element : workbookHashMap.entrySet()) {


            String key = (String) element.getKey(); //filename

            HashMap<String, List<GSTR1>> stateWiseJavaMap = new HashMap<>();

            Workbook workbook = workbookHashMap.get(key);
            // assuming one workbook has one sheet
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row currentRow = sheet.getRow(i);
                System.out.println("---------------");
                //populating object
                GSTR1 gstr1 = new GSTR1();
                String state = currentRow.getCell(AmazonConstants.STATE_COLUMN_INDEX).getStringCellValue();
                gstr1.setState(state);

                gstr1.setInvoiceAmount(currentRow.getCell(AmazonConstants.INVOICE_AMOUNT_INDEX).getNumericCellValue());
                gstr1.setTaxableAmount(currentRow.getCell(AmazonConstants.TAXABLE_AMOUNT_INDEX).getNumericCellValue());
                gstr1.setGstAmount(currentRow.getCell(AmazonConstants.TOTAL_TAX_INDEX).getNumericCellValue());
                gstr1.setTaxCGST(currentRow.getCell(AmazonConstants.GST_CGST_INDEX).getNumericCellValue() * 100);
                gstr1.setTaxSGST(currentRow.getCell(AmazonConstants.GST_SGST_INDEX).getNumericCellValue() * 100);
                gstr1.setTaxUTGST(currentRow.getCell(AmazonConstants.GST_UTGST_INDEX).getNumericCellValue() * 100);
                gstr1.setTaxIGST(currentRow.getCell(AmazonConstants.GST_IGST_INDEX).getNumericCellValue() * 100);

                if (stateWiseJavaMap.get(state) == null)
                    stateWiseJavaMap.put(state, new ArrayList<>(Arrays.asList(gstr1)));
                else
                    stateWiseJavaMap.get(state).add(gstr1);
            }

            monthStateJavaMap.put(key, stateWiseJavaMap);
        }
        return monthStateJavaMap;
    }

    public HashMap<String, List<GSTR1>> generateTaxSlabStateWise(HashMap<String, HashMap<String, List<GSTR1>>> mapifiedSheets) {

        // Do not want to modify original
        HashMap<String, HashMap<String, List<GSTR1>>> tmp = mapifiedSheets;

        HashMap<String, List<GSTR1>> FileNameStateWiseTaxSlab = new HashMap<>();
        List<GSTR1> StatewiseTaxSlab = new ArrayList<>();

        for(Map.Entry<String, HashMap<String, List<GSTR1>>> element : tmp.entrySet()){

            String fileName = (String) element.getKey();

            HashMap<String, List<GSTR1>> stateWiseJavaMap = tmp.get(fileName);

            for(Map.Entry<String, List<GSTR1>> el  : stateWiseJavaMap.entrySet()){

                String State = (String) el.getKey();
                List<GSTR1> gstr1entries = stateWiseJavaMap.get(State);

                System.out.println("Generating TaxSlab for "+ State);

                double GST_SG_CG = 0;
                double invoice_sum_sg_cg = 0;
                double invoice_sum_igst = 0;
                double taxable_sum_sg_cg = 0;
                double taxable_sum_igst = 0;
                double GST_IGST = 0;

                for(GSTR1 gstr1 : gstr1entries){

                    if(gstr1.getTaxCGST() == 9.0 && gstr1.getTaxSGST() == 9.0) {
                        GST_SG_CG = GST_SG_CG + gstr1.getGstAmount();
                        invoice_sum_sg_cg = invoice_sum_sg_cg + gstr1.getInvoiceAmount();
                        taxable_sum_sg_cg = taxable_sum_sg_cg + gstr1.getTaxableAmount();
                    }
                    if (gstr1.getTaxIGST() == 18.0) {
                        GST_IGST = GST_IGST + gstr1.getGstAmount();
                        invoice_sum_igst = invoice_sum_igst + gstr1.getInvoiceAmount();
                        taxable_sum_igst = taxable_sum_igst + gstr1.getTaxableAmount();
                    }
                    else
                        System.out.println("********EXCEPTIONAL COMBINATION SLAB***********");
                }

                GSTR1 gstr1_igst_slab = new GSTR1();
                gstr1_igst_slab.setState(State);
                gstr1_igst_slab.setGstAmount(GST_IGST);
                gstr1_igst_slab.setInvoiceAmount(invoice_sum_igst);
                gstr1_igst_slab.setTaxableAmount(taxable_sum_igst);
                gstr1_igst_slab.setTaxCGST(0);
                gstr1_igst_slab.setTaxSGST(0);
                gstr1_igst_slab.setTaxUTGST(0);
                gstr1_igst_slab.setTaxIGST(18);

                GSTR1 gstr1_sg_cg_slab = new GSTR1();
                gstr1_sg_cg_slab.setTaxIGST(0);
                gstr1_sg_cg_slab.setTaxUTGST(0);
                gstr1_sg_cg_slab.setState(State);
                gstr1_sg_cg_slab.setTaxSGST(9);
                gstr1_sg_cg_slab.setTaxCGST(9);
                gstr1_sg_cg_slab.setGstAmount(GST_SG_CG);
                gstr1_sg_cg_slab.setTaxableAmount(taxable_sum_sg_cg);
                gstr1_sg_cg_slab.setInvoiceAmount(invoice_sum_sg_cg);

                //putting these two entries in map
                StatewiseTaxSlab.add(gstr1_igst_slab);
                StatewiseTaxSlab.add(gstr1_sg_cg_slab);

            }
            FileNameStateWiseTaxSlab.put(fileName, StatewiseTaxSlab);
        }

        return FileNameStateWiseTaxSlab;
    }

    public void printGSTRToExcel(HashMap<String, List<GSTR1>> stateTaxwiseSummationMap) throws IOException {

        //Create directory in resource folder
        String directory = "createdReports";
        Path source = Paths.get(this.getClass().getResource("/").getPath());

        Path storeFolder = Paths.get(source.toAbsolutePath() + "/"+ directory + "/");
        System.out.println("output directory is --> "+ storeFolder.toAbsolutePath());

        try {
            Files.createDirectories(storeFolder);
        } catch (IOException e) {
            System.out.println("Cannot Create directory to store reports");
            e.printStackTrace();
        }

        for(Map.Entry<String, List<GSTR1>> element : stateTaxwiseSummationMap.entrySet()){

            String filename = (String) element.getKey();
            List<GSTR1> gstr1sEntries =stateTaxwiseSummationMap.get(filename);
            Workbook resultWorkbook = new XSSFWorkbook();

            Sheet resultSheet = resultWorkbook.createSheet("result_gstr1");
            Row firstRow = resultSheet.createRow(0);
            firstRow.createCell(0).setCellValue("State");
            firstRow.createCell(1).setCellValue("INVOICE amount");
            firstRow.createCell(2).setCellValue("taxable amount");
            firstRow.createCell(3).setCellValue("cgst");
            firstRow.createCell(4).setCellValue("sgst");
            firstRow.createCell(5).setCellValue("utgst");
            firstRow.createCell(6).setCellValue("igst");
            firstRow.createCell(7).setCellValue("gst");


            int rowIndex = 1;
            for (GSTR1 gstr1 : gstr1sEntries){

                Row row = resultSheet.createRow(rowIndex++);
                int cellIndex = 0;

                row.createCell(cellIndex++).setCellValue(gstr1.getState());
                row.createCell(cellIndex++).setCellValue(gstr1.getInvoiceAmount());
                row.createCell(cellIndex++).setCellValue(gstr1.getTaxableAmount());
                row.createCell(cellIndex++).setCellValue(gstr1.getTaxCGST());
                row.createCell(cellIndex++).setCellValue(gstr1.getTaxSGST());
                row.createCell(cellIndex++).setCellValue(gstr1.getTaxUTGST());
                row.createCell(cellIndex++).setCellValue(gstr1.getTaxIGST());
                row.createCell(cellIndex++).setCellValue(gstr1.getGstAmount());
            }

            //Writing out this file
            String path = storeFolder.toAbsolutePath()+ "/"+filename+"_report.xlsx";

         //   Files.createFile(Paths.get(path));
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(path);
                resultWorkbook.write(fileOutputStream);
                fileOutputStream.close();
            }
            catch (FileNotFoundException f){
                f.printStackTrace();
            }
        }
    }
}
