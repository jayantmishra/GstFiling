package com.me.urbanmart.GSTFiling.Service;

import com.me.urbanmart.GSTFiling.POJO.GSTR1;
import com.me.urbanmart.GSTFiling.Utilities.Utils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ReportBuilder {

    @Autowired
    Utils utils;

    public void buildGSTR1Report() throws Exception{

        // Consume Sheets to java Objects
       HashMap<String, Workbook> workbookHashMap =  utils.consumeSheets();

       //Remove Cancel shipment Coloumn
        utils.sanititzeSheets(workbookHashMap);

        // Mapipfy SanitizedSheets Statewise
        HashMap<String, HashMap<String, List<GSTR1>>> mapifiedSheets = utils.mapifySheets(workbookHashMap);

        // Prepare StateWise Tax Sum
        HashMap<String, List<GSTR1>> stateTaxwiseSummationMap = utils.generateTaxSlabStateWise(mapifiedSheets);

        // Print GSTR1 Files
        utils.printGSTRToExcel(stateTaxwiseSummationMap);
    }



    public void buildGSTR3BReport() throws Exception{

    }

}
