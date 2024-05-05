package com.codecool.solarwatch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SunriseSunsetDTO {
    private String name;
    private LocalDate date;
    private String sunrise;
    private String sunset;

    public SunriseSunsetDTO(String name, LocalDate date, String sunrise, String sunset) {
        this.name = name;
        this.date = date;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public String name() {
        return name;
    }

    public LocalDate date() {
        return date;
    }

    public String sunrise() {
        return sunrise;
    }

    public String sunset() {
        return sunset;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SunriseSunsetDTO) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.date, that.date) &&
                Objects.equals(this.sunrise, that.sunrise) &&
                Objects.equals(this.sunset, that.sunset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, sunrise, sunset);
    }

    @Override
    public String toString() {
        return "SunriseSunsetDTO[" +
                "name=" + name + ", " +
                "date=" + date + ", " +
                "sunrise=" + sunrise + ", " +
                "sunset=" + sunset + ']';
    }
}
