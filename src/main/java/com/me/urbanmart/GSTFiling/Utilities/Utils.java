package com.me.urbanmart.GSTFiling.Utilities;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class Utils {


    public HashMap<String, Workbook> consumeSheets() throws Exception {

        ClassLoader classLoader = this.getClass().getClassLoader();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource resource[] = null;
        HashMap<String, Workbook> stringWorkbookMap = new HashMap<>();

        try {
             resource  = resourcePatternResolver.getResources("classpath:MTR_Reports_Amazon/**");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (resource != null){

            for (Resource res : resource){

                InputStream inputStream = res.getInputStream();
                Workbook workbook =  new XSSFWorkbook(inputStream);
                stringWorkbookMap.put(res.getFilename(), workbook);

            }
        }

        return stringWorkbookMap;

    }
}
