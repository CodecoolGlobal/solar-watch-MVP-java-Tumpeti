package com.codecool.solarwatch.controller;

public class InvalidCityNameException extends RuntimeException {

    public InvalidCityNameException() {
        super("City name must be valid");
    }
}
