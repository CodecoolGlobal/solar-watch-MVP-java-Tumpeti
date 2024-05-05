package com.codecool.solarwatch.service;

import com.codecool.solarwatch.dto.CityDTO;
import com.codecool.solarwatch.dto.SunriseSunsetDTO;
import com.codecool.solarwatch.dto.SunriseSunsetReportDTO;
import com.codecool.solarwatch.dto.SunriseSunsetResultDTO;
import com.codecool.solarwatch.exceptionhandler.InvalidCityNameException;
import com.codecool.solarwatch.model.*;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunriseSunsetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SunriseSunsetService {

    private final CityRepository cityRepository;
    private final SunriseSunsetRepository sunriseSunsetRepository;
    private final WebClient webClient;
    private final static String SUNRISE_SUNSET_API_URL = "https://api.sunrisesunset.io/json?lat=%f&lng=%f&date=%s";
    private final static String GEOCODE_API_KEY = System.getenv("GEOCODE_API_KEY");
    private final static String GEOCODE_API_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s";
    private final Logger logger = LoggerFactory.getLogger(SunriseSunsetService.class);

    @Autowired
    public SunriseSunsetService(CityRepository cityRepository, SunriseSunsetRepository sunriseSunsetRepository, WebClient webClient) {
        this.cityRepository = cityRepository;
        this.sunriseSunsetRepository = sunriseSunsetRepository;
        this.webClient = webClient;
    }

    public SunriseSunsetReportDTO getSunriseSunset(String cityName, LocalDate date) {
        if (cityName.isEmpty()) {
            throw new InvalidCityNameException("The City name cannot be empty.");
        }

        logger.info("Getting Sunrise and Sunset for City: {} on day: {}", cityName, date);
        City city = getRequestedCity(cityName);

        Optional<SunriseSunset> requestedSunriseSunsetOpt = sunriseSunsetRepository.findByDateAndCity(date, city);
        if (requestedSunriseSunsetOpt.isPresent()) {
            SunriseSunset requestedSunriseSunset = requestedSunriseSunsetOpt.get();
            logger.info("Requested City sunrise and sunset information found in database: {}", requestedSunriseSunset);
            return new SunriseSunsetReportDTO(city.getName(), requestedSunriseSunset.getDate(), requestedSunriseSunset.getSunrise(), requestedSunriseSunset.getSunset());
        }

        SunriseSunsetResultDTO sunriseSunsetResult = fetchSunriseSunset(date, city);
        if (sunriseSunsetResult == null) {
            throw new NoSuchElementException(String.format("Sunrise and Sunset information for city %s not found.",cityName));
        }
        SunriseSunsetDTO sunriseSunsetDTO = sunriseSunsetResult.results();
        SunriseSunsetReportDTO reportDTO = new SunriseSunsetReportDTO(city.getName(), sunriseSunsetDTO.date(), sunriseSunsetDTO.sunrise(), sunriseSunsetDTO.sunset());
        saveFetchedSunriseSunset(reportDTO, city);
        return reportDTO;
    }

    private City getRequestedCity(String cityName) {
        City requestedCity;
        Optional<City> requestedCityOpt = cityRepository.findByNameIgnoreCase(cityName);
        if (requestedCityOpt.isPresent()) {
            logger.info("Requested City found in database: {}", cityName);
            requestedCity = requestedCityOpt.get();
        } else {
            logger.info("Requested City NOT found in database: {}", cityName);
            CityDTO cityDTO = fetchCity(cityName);
            requestedCity = saveFetchedCity(cityDTO);
        }
        return requestedCity;
    }

    private CityDTO fetchCity(String city) {
        logger.info("Fetching city: {}", city);
        int mostAccurateResultIndex = 0;
        Mono<CityDTO[]> response = webClient
                .get()
                .uri(getGeocodeApiUrl(city))
                .retrieve()
                .bodyToMono(CityDTO[].class)
                .log();
        CityDTO[] cities = response.block();
        if (cities == null | cities.length == 0) {
            throw new NoSuchElementException("Requested city: " + city + " not found");
        }
        logger.info("Coordinates requested successfully: {}", cities[mostAccurateResultIndex]);
        return cities[mostAccurateResultIndex];
    }

    private SunriseSunsetResultDTO fetchSunriseSunset(LocalDate date, City city) {
        logger.info("Requested City sunrise and sunset information NOT found in database, fetching...");
        String sunriseSunsetApiUrl = getSunriseSunsetApiUrl(city.getLatitude(), city.getLongitude(), date);
        return webClient
                .get()
                .uri(sunriseSunsetApiUrl)
                .retrieve()
                .bodyToMono(SunriseSunsetResultDTO.class)
                .block();
    }

    private static String getSunriseSunsetApiUrl(double lat, double lng, LocalDate date) {
        return String.format(SUNRISE_SUNSET_API_URL,
                lat, lng, date.toString());
    }

    private static String getGeocodeApiUrl(String city) {
        return String.format(GEOCODE_API_URL,
                city, GEOCODE_API_KEY);
    }

    private static City convertDTOToCity(CityDTO cityDTO) {
        return new City(cityDTO.name(),cityDTO.lat(), cityDTO.lon(), cityDTO.country(), cityDTO.state());
    }

    private City saveFetchedCity(CityDTO cityDTO) {
        City newCity = convertDTOToCity(cityDTO);
        City saved = cityRepository.save(newCity);
        logger.info("Newly fetched country saved: {} id: {}", saved, saved.getId());
        return saved;
    }

    private void saveFetchedSunriseSunset(SunriseSunsetReportDTO sunriseSunsetReportDTO, City requestedCity) {
        SunriseSunset newlyFetchedSunriseSunset = new SunriseSunset(sunriseSunsetReportDTO.date(), sunriseSunsetReportDTO.sunrise(), sunriseSunsetReportDTO.sunset(), requestedCity);
        logger.info("Newly fetched Sunrise Sunset saved: {}", newlyFetchedSunriseSunset);
        sunriseSunsetRepository.save(newlyFetchedSunriseSunset);
    }

}
