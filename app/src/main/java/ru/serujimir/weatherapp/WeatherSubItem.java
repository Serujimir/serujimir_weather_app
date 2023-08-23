package ru.serujimir.weatherapp;

public class WeatherSubItem {

    WeatherSubItem (String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    String icon;
}
