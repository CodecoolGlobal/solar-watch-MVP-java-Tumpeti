package com.codecool.solarwatch.service;

import com.codecool.solarwatch.controller.InvalidCityNameException;
import com.codecool.solarwatch.model.CityDTO;
import com.codecool.solarwatch.model.SunriseSunsetDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SunriseSunsetdServiceTest {

    private SunriseSunsetService service;

//    @BeforeEach
//    void setUp() {
//        RestTemplate restTemplate = new RestTemplate();
//        service = new SunriseSunsetService(restTemplate);
//    }

    @Disabled
    public void test_getCityGeocodeCityShouldBeEqualToSavedRecord() {
        //arrange

        //act
//        CityDTO expected = new CityDTO("London", 51.5073219, -0.1276474);
        CityDTO actual = service.getCityCoordinatesByName("London");
        //assert
//        assertEquals(expected,actual);
    }

    @Test
    void getSunriseSunsetLondonShouldnotBeNull() {
        //Arrange

        //Act

        //Assert
        SunriseSunsetDTO london = service.getSunriseSunset("London", LocalDate.now());
        System.out.println(london);
    }

    @Test
    void getSunriseSunsetWrongCityNameShouldThrowInvalidCityNameException() {
        //Arrange

        //Act

        //Assert
        assertThrows(InvalidCityNameException.class, () -> service.getCityCoordinatesByName("Londddon"));
    }

}