package ru.serujimir.weatherapp.DayParsing;

import java.util.List;

public class DayCast {
    Coord coord;

    public Coord getCoord() {
        return coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public String getVisibility() {
        return visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public Sys getSys() {
        return sys;
    }

    public String getName() {
        return name;
    }

    public String getCod() {
        return cod;
    }

    List<Weather> weather;
    Main main;
    String visibility;
    Wind wind;
    Sys sys;
    String name;
    String cod;
}
