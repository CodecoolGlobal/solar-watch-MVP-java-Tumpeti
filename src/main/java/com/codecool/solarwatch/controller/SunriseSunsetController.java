package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.dto.SunriseSunsetReportDTO;
import com.codecool.solarwatch.service.SunriseSunsetService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@RestController
public class SunriseSunsetController {

    private final SunriseSunsetService sunriseSunsetService;
    private final Logger logger = LoggerFactory.getLogger(SunriseSunsetController.class);

    @Autowired
    public SunriseSunsetController(SunriseSunsetService sunriseSunsetService) {
        this.sunriseSunsetService = sunriseSunsetService;
    }

    @GetMapping("/sunrisesunset")
    public SunriseSunsetReportDTO getSunriseAndSunset(@RequestParam String city, @RequestParam(required = false, defaultValue = "today") String date) {
        if (date.equals("today")) {
            date = LocalDate.now().toString();
        }
        SunriseSunsetReportDTO sunriseSunsetReport = sunriseSunsetService.getSunriseSunset(city, LocalDate.parse(date));
        logger.info("Sunset Sunrise for requested City: {}", sunriseSunsetReport);
        return sunriseSunsetReport;
    }
}