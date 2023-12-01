package ru.serujimir.weatherapp;

public class CultureItem {
    public CultureItem(String title, String text){
        this.title=title;
        this.text=text;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
}
