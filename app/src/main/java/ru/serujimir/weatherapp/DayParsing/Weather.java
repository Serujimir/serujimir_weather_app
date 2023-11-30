package ru.serujimir.weatherapp.DayParsing;

public class Weather {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    private String main;
    private String description;
    private String icon;
}
