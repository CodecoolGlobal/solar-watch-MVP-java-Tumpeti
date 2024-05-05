package com.codecool.solarwatch.controller;

public class InvalidCityNameException extends RuntimeException {

    public InvalidCityNameException(String message) {
        super(message);
    }
}
