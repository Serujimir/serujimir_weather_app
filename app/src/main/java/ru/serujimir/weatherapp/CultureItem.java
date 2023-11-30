package ru.serujimir.weatherapp;

public class CultureItem {
    public CultureItem(String text){
        this.text=text;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;
}
