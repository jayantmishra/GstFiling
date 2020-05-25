package com.me.urbanmart.GSTFiling.Controller;


import com.me.urbanmart.GSTFiling.Service.ReportBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApi {

    @Autowired
    ReportBuilder reportBuilder;

    @GetMapping("/gsr1")
    public void createGSTR1Report(){

        try {
            reportBuilder.buildGSTR1Report();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
