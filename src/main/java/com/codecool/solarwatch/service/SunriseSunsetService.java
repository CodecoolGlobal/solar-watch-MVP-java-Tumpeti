package com.codecool.solarwatch.service;

import com.codecool.solarwatch.controller.InvalidCityNameException;
import com.codecool.solarwatch.model.CityGeocode;
import com.codecool.solarwatch.model.SunriseSunset;
import com.codecool.solarwatch.model.SunriseSunsetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class SunriseSunsetService {

    private final RestTemplate restTemplate;

    private final static String SUNRISE_SUNSET_API_URL = "https://api.sunrise-sunset.org/json?lat=%f&lng=%f&date=%s";
    private final static String GEOCODE_API_KEY = "09c0c6a17660a915d870fa825e1fa012";
    private final static String GEOCODE_API_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s";

    private final Logger logger = LoggerFactory.getLogger(SunriseSunsetService.class);

    public SunriseSunsetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static String getSunriseSunsetApiUrl(double lat, double lng, LocalDate date) {
        return String.format(SUNRISE_SUNSET_API_URL,
                lat, lng, date.toString());
    }

    private static String getGeocodeApiUrl(String city) {
        return String.format(GEOCODE_API_URL,
                city, GEOCODE_API_KEY);
    }

    public CityGeocode getCityCoordinatesByName(String city) {
        logger.info("Requesting City coordinates: {}",city);
        ResponseEntity<List<CityGeocode>> responseEntity = restTemplate.exchange(
                getGeocodeApiUrl(city),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        if (Objects.requireNonNull(responseEntity.getBody()).isEmpty()) {
            logger.info("Invalid City name provided");
            throw new InvalidCityNameException();
        }
        CityGeocode cityGeocode = responseEntity.getBody().getFirst();
        logger.info("Coordinates requested successfully: {}", cityGeocode);
        return cityGeocode;
    }

    public SunriseSunset getSunriseSunset(String city, LocalDate date) {
        logger.info("Fetching Sunrise and Sunset for City: {} on day: {}", city, date);
        CityGeocode cityGeocode = getCityCoordinatesByName(city);
        SunriseSunsetResult sunriseSunsetResult = restTemplate.getForObject(
                getSunriseSunsetApiUrl(cityGeocode.lat(), cityGeocode.lon(), date), SunriseSunsetResult.class);
        SunriseSunset sunriseSunset = null;
        if (sunriseSunsetResult != null) {
             sunriseSunset = sunriseSunsetResult.results();
        }
        logger.info("Sunset and Sunrise for City: {}, {}", city, sunriseSunset);
        return sunriseSunset;
    }

}
