package uk.ac.ncl.csc8109.team1.controller;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Huan on 2017/2/28.
 */

@ControllerAdvice
public class ExceptionController {
    private static Logger log = Logger.getLogger(ExceptionController.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public String handleException(Exception e){
        // log.debug(e.getStackTrace());
        log.info(e.getMessage());
        e.printStackTrace();
        return e.getMessage();
    }
}
