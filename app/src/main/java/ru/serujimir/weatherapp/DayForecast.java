package ru.serujimir.weatherapp;

public class DayForecast {

    public DayForecast(String day_time, String day_temp, String day_icon) {
        this.day_time=day_time;
        this.day_temp=day_temp;
        this.day_icon=day_icon;
    }

    public String getDay_time() {
        return day_time;
    }

    public String getDay_temp() {
        return day_temp;
    }

    public String getDay_icon() {
        return day_icon;
    }

    public void setDay_time(String day_time) {
        this.day_time = day_time;
    }

    public void setDay_temp(String day_temp) {
        this.day_temp = day_temp;
    }

    public void setDay_icon(String day_icon) {
        this.day_icon = day_icon;
    }

    private String day_time;
    private String day_temp;
    private String day_icon;
}
