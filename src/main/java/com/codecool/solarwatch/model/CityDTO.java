package com.codecool.solarwatch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CityDTO(String name, double lat, double lon, String country, String state) {
}
