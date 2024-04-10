package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.SunriseSunset;
import com.codecool.solarwatch.service.SunriseSunsetService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.DateTimeException;
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
    public ResponseEntity<?> getSunriseAndSunset(@RequestParam String city, @RequestParam(required = false, defaultValue = "today") String date) {
        if (date.equals("today")) {
            date = LocalDate.now().toString();
        }
        System.out.println(date);
        try {
            SunriseSunset sunriseSunset = sunriseSunsetService.getSunriseSunset(city, LocalDate.parse(date));
            return ResponseEntity.ok(sunriseSunset);
        } catch (InvalidCityNameException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (DateTimeException exception) {
            logger.info("Invalid date format");
            return ResponseEntity.badRequest().body("Bad Date Time Format");
        }
    }
}
