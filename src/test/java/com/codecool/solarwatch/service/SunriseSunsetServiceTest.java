package com.codecool.solarwatch.service;

import com.codecool.solarwatch.dto.SunriseSunsetReportDTO;
import com.codecool.solarwatch.exceptionhandler.InvalidCityNameException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SunriseSunset;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunriseSunsetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SunriseSunsetServiceTest {

    private SunriseSunsetService underTest;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private SunriseSunsetRepository sunriseSunsetRepository;

    @Mock
    private WebClient webClient;

    @BeforeEach
    public void setup() {
        cityRepository = mock(CityRepository.class);
        sunriseSunsetRepository = mock(SunriseSunsetRepository.class);
        underTest = new SunriseSunsetService(cityRepository, sunriseSunsetRepository, webClient);
    }

    @Test
    void givenEmptyCityNameShouldThrowException() {
        //Arrange
        //Act
        //Assert
        assertThrows(InvalidCityNameException.class, () -> underTest.getSunriseSunset("", LocalDate.of(2024, 5, 5)));
    }

    @Test
    void givenCityAndReportStoredInDatabaseShouldReturnValidReportDTO() {
        //Arrange
        String cityName = "Washington";
        double latitude = 38.8950368;
        double longitude = -77.0365427;
        String country = "US";
        String state = "District of Columbia";
        LocalDate date = LocalDate.of(2024, 5, 5);
        City washington = new City(cityName, latitude, longitude, country, state);
        when(cityRepository.findByNameIgnoreCase(cityName)).thenReturn(Optional.of(washington));

        long id = 1;
        String sunrise = "6:07:33 AM";
        String sunset = "8:04:42 PM";
        SunriseSunset sunriseSunset = new SunriseSunset(date, sunrise, sunset, washington);
        when(sunriseSunsetRepository.findByDateAndCity(date, washington)).thenReturn(Optional.of(sunriseSunset));
        //Act
        SunriseSunsetReportDTO expected = new SunriseSunsetReportDTO(cityName, date, sunrise, sunset);
        SunriseSunsetReportDTO actual = underTest.getSunriseSunset(cityName, date);
        //Assert
        assertEquals(expected, actual);
    }
}