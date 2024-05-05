package com.codecool.solarwatch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public record SunriseSunsetResultDTO(SunriseSunsetDTO results) {
}
