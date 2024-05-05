package com.codecool.solarwatch.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class SunriseSunset {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;

    private String sunrise;

    private String sunset;

    @ManyToOne
    private City city;


    public long getId() {
        return id;
    }

    public City getCity() {
        return city;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "SunriseSunset{" +
                "id=" + id +
                ", date=" + date +
                ", sunrise='" + sunrise + '\'' +
                ", sunset='" + sunset + '\'' +
                ", city=" + city +
                '}';
    }
}
