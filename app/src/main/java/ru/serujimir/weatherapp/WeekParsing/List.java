package ru.serujimir.weatherapp.WeekParsing;

import com.google.gson.annotations.SerializedName;

public class List {
    public String getDt_txt() {
        return dt_txt;
    }

    public Main getMain() {
        return main;
    }


    private Main main;

    public java.util.List<Weather> getWeather() {
        return weather;
    }

    private java.util.List<Weather> weather;

    private String dt_txt;

}
