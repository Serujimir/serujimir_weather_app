package ru.serujimir.weatherapp;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

public class CultureItem {
    public CultureItem(String title, String text, Drawable imImage){
        this.title=title;
        this.text=text;
        this.imImage=imImage;
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

    public Drawable getImImage() {
        return imImage;
    }

    public void setImImage(Drawable imImage) {
        this.imImage = imImage;
    }

    private Drawable imImage;
}
