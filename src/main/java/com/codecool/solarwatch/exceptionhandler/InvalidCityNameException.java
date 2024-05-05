package com.codecool.solarwatch.exceptionhandler;

public class InvalidCityNameException extends RuntimeException {

    public InvalidCityNameException(String message) {
        super(message);
    }
}
