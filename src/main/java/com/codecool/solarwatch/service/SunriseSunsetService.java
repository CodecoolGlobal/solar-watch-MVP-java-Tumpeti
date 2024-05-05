package com.codecool.solarwatch.service;

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
import java.util.Optional;

@Service
public class SunriseSunsetService {

    private final CityRepository cityRepository;
    
    private final SunriseSunsetRepository sunriseSunsetRepository;

    private final WebClient webClient;

    private final static String SUNRISE_SUNSET_API_URL = "https://api.sunrisesunset.io/json?lat=%f&lng=%f&date=%s";

    //https://api.sunrisesunset.io/json?lat=46.3484884&lng=18.701663&date=2024-04-23
    private final static String GEOCODE_API_KEY = "09c0c6a17660a915d870fa825e1fa012";
    private final static String GEOCODE_API_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s";

    //http://api.openweathermap.org/geo/1.0/direct?q=Washington&limit=1&appid=09c0c6a17660a915d870fa825e1fa012

    private final Logger logger = LoggerFactory.getLogger(SunriseSunsetService.class);

    @Autowired
    public SunriseSunsetService(CityRepository cityRepository, SunriseSunsetRepository sunriseSunsetRepository, WebClient webClient) {
        this.cityRepository = cityRepository;
        this.sunriseSunsetRepository = sunriseSunsetRepository;
        this.webClient = webClient;
    }

    private static String getSunriseSunsetApiUrl(double lat, double lng, LocalDate date) {
        return String.format(SUNRISE_SUNSET_API_URL,
                lat, lng, date.toString());
    }

    private static String getGeocodeApiUrl(String city) {
        return String.format(GEOCODE_API_URL,
                city, GEOCODE_API_KEY);
    }

    public CityDTO getCityCoordinatesByName(String city) {

        Optional<City> requestedCityOpt = cityRepository.findByNameIgnoreCase(city);

        if (requestedCityOpt.isPresent()) {
            logger.info("Requested City found in database: {}", city);
            City requestedCity = requestedCityOpt.get();

            return convertCityToDTO(requestedCity);
        }

        logger.info("Requested City NOT found in database: {}", city);
        logger.info("Requesting City coordinates: {}",city);
        CityDTO cityDTO = fetchCity(city);
        City savedCity = saveFetchedCity(cityDTO);
        logger.info("Saved City: {}",savedCity);
        return cityDTO;
    }

    private CityDTO fetchCity(String city) {
        Mono<CityDTO[]> response = webClient
                .get()
                .uri(getGeocodeApiUrl(city))
                .retrieve()
                .bodyToMono(CityDTO[].class)
                .log();
//        if (Objects.requireNonNull(responseEntity.isEmpty()) {
//            logger.info("Invalid City name provided");
//            throw new InvalidCityNameException();
//        }
        CityDTO[] cities = response.block();
        logger.info("Coordinates requested successfully: {}", cities);
        return cities[0];
    }

    private static CityDTO convertCityToDTO(City requestedCity) {
        return new CityDTO(requestedCity.getName(), requestedCity.getLatitude(), requestedCity.getLongitude(), requestedCity.getCountry(), requestedCity.getState());
    }

    private City saveFetchedCity(CityDTO cityDTO) {
        City newlyFetchedCity = new City();
        newlyFetchedCity.setName(cityDTO.name());
        newlyFetchedCity.setCountry(cityDTO.country());
        newlyFetchedCity.setLatitude(cityDTO.lat());
        newlyFetchedCity.setLongitude(cityDTO.lon());
        logger.info("Newly fetched country saved: {} id: {}", newlyFetchedCity, newlyFetchedCity.getId());
        return cityRepository.save(newlyFetchedCity);
    }

    public SunriseSunsetDTO getSunriseSunset(String city, LocalDate date) {
        City requestedCity;

        logger.info("Fetching Sunrise and Sunset for City: {} on day: {}", city, date);
        Optional<City> requestedCityOpt = cityRepository.findByNameIgnoreCase(city);

        if (requestedCityOpt.isPresent()) {
            logger.info("Requested City found in database: {}", city);
            requestedCity = requestedCityOpt.get();

        } else {
            CityDTO cityDTO = fetchCity(city);
            requestedCity = saveFetchedCity(cityDTO);
        }
        Optional<SunriseSunset> requestedSunriseSunsetOpt = sunriseSunsetRepository.findByDateAndCity(date, requestedCity);

        if (requestedSunriseSunsetOpt.isPresent()) {
            SunriseSunset requestedSunriseSunset = requestedSunriseSunsetOpt.get();
            logger.info("Requested City sunrise and sunset information found in database: {}", requestedSunriseSunset);
            return new SunriseSunsetDTO(requestedCity.getName(), requestedSunriseSunset.getDate(), requestedSunriseSunset.getSunrise(), requestedSunriseSunset.getSunset());
        }

        String sunriseSunsetApiUrl = getSunriseSunsetApiUrl(requestedCity.getLatitude(), requestedCity.getLongitude(), date);

        logger.info("Requested City sunrise and sunset information NOT found in database, fetching...");


        SunriseSunsetResult sunriseSunsetResult = webClient
                .get()
                .uri(sunriseSunsetApiUrl)
                .retrieve()
                .bodyToMono(SunriseSunsetResult.class)
                .block();

        SunriseSunsetDTO sunriseSunsetDTO = sunriseSunsetResult.results();
        System.out.println(requestedCity.getName() + "!!!!");
//        sunriseSunsetDTO.setName(requestedCity.getName());
        System.out.println(sunriseSunsetDTO);

        logger.info("Sunset and Sunrise for City fetched: {}, {}", city, sunriseSunsetDTO);

        saveFetchedSunriseSunset(sunriseSunsetDTO, requestedCity);
        System.out.println(sunriseSunsetDTO);

        return sunriseSunsetDTO;
    }

    private SunriseSunset saveFetchedSunriseSunset(SunriseSunsetDTO sunriseSunsetDTO, City requestedCity) {
        SunriseSunset newlyFetchedSunriseSunset = new SunriseSunset();
        newlyFetchedSunriseSunset.setSunrise(sunriseSunsetDTO.sunrise());
        newlyFetchedSunriseSunset.setSunset(sunriseSunsetDTO.sunset());
        newlyFetchedSunriseSunset.setCity(requestedCity);
        newlyFetchedSunriseSunset.setDate(sunriseSunsetDTO.date());
        logger.info("Newly fetched Sunrise Sunset saved: {}", newlyFetchedSunriseSunset);
        return sunriseSunsetRepository.save(newlyFetchedSunriseSunset);
    }

}
