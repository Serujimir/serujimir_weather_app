package ru.serujimir.weatherapp;

public class City {
    City(String city, String id) {
        this.city=city;
        this.id=id;
    }

    public String getCity() {
        return city;
    }

    private String city;

    public String getId() {
        return id;
    }

    private  String id;
}
