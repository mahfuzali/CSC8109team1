package uk.ac.ncl.csc8109.team1.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ncl.csc8109.team1.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.service.RegisterService;

/**
 * Created by Huan on 2017/2/28.
 */

@RestController
public class RestfulController {
    private Logger log = Logger.getLogger(RestfulController.class);

    @Autowired
    private RegisterService registerService;

    @RequestMapping(value = "/register/{id}")
    @ResponseBody
    public RegisterEntity register(@PathVariable String id){
        log.info("id");
        return registerService.registerUser(id);
    }
    @RequestMapping(value = "/")
    @ResponseBody
    public String hello(){
        return "hello";
    }

}
