package com.me.urbanmart.GSTFiling.Service;

import com.me.urbanmart.GSTFiling.Utilities.Utils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ReportBuilder {

    @Autowired
    Utils utils;

    public void buildGSTR1Report() throws Exception{

        // Consume Sheets to java Objects
       HashMap<String, Workbook> workbookHashMap =  utils.consumeSheets();

       System.out.println(workbookHashMap);

    }



    public void buildGSTR3BReport() throws Exception{

    }

}
