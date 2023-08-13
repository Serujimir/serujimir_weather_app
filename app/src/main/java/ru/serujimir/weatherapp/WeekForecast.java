package ru.serujimir.weatherapp;

public class WeekForecast {

    public WeekForecast(String dayOfWeek, String minDayOfWeekTemp, String maxDayOfWeekTemp, String icon) {
        this.dayOfWeek=dayOfWeek;
        this.minDayOfWeekTemp=minDayOfWeekTemp;
        this.maxDayOfWeekTemp=maxDayOfWeekTemp;
        this.icon=icon;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getMinDayOfWeekTemp() {
        return minDayOfWeekTemp;
    }

    public void setMinDayOfWeekTemp(String minDayOfWeekTemp) {
        this.minDayOfWeekTemp = minDayOfWeekTemp;
    }

    public String getMaxDayOfWeekTemp() {
        return maxDayOfWeekTemp;
    }

    public void setMaxDayOfWeekTemp(String maxDayOfWeekTemp) {
        this.maxDayOfWeekTemp = maxDayOfWeekTemp;
    }

    private String dayOfWeek;
    private String minDayOfWeekTemp;
    private String maxDayOfWeekTemp;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    private String icon;
}
