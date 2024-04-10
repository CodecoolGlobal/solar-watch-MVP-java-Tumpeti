package com.codecool.solarwatch.service;

import com.codecool.solarwatch.controller.InvalidCityNameException;
import com.codecool.solarwatch.model.CityGeocode;
import com.codecool.solarwatch.model.SunriseSunset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SunriseSunsetdServiceTest {

    private SunriseSunsetService service;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        service = new SunriseSunsetService(restTemplate);
    }

    @Test
    public void test_getCityGeocodeCityShouldBeEqualToSavedRecord() {
        //arrange

        //act
        CityGeocode expected = new CityGeocode("London", 51.5073219, -0.1276474);
        CityGeocode actual = service.getCityCoordinatesByName("London");
        //assert
        assertEquals(expected,actual);
    }

    @Test
    void getSunriseSunsetLondonShouldnotBeNull() {
        //Arrange

        //Act

        //Assert
        SunriseSunset london = service.getSunriseSunset("London", LocalDate.now());
        System.out.println(london);
        assertTrue(london.sunrise() != null && london.sunset() != null && london.dayLength() != null);
    }

    @Test
    void getSunriseSunsetWrongCityNameShouldThrowInvalidCityNameException() {
        //Arrange

        //Act

        //Assert
        assertThrows(InvalidCityNameException.class, () -> service.getCityCoordinatesByName("Londddon"));
    }

}