package com.codecool.solarwatch.dto;
import java.time.LocalDate;

public record SunriseSunsetReportDTO(String name, LocalDate date, String sunrise, String sunset) { }