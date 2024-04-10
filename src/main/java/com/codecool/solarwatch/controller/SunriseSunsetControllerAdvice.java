package com.codecool.solarwatch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SunriseSunsetControllerAdvice {

    @ResponseBody
    @ExceptionHandler(InvalidCityNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidCityNameExceptionHandler(InvalidCityNameException exception) {
        return exception.getMessage();
    }

}

