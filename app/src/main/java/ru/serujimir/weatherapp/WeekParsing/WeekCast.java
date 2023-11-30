package ru.serujimir.weatherapp.WeekParsing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class WeekCast {
    public String getCod() {
        return cod;
    }
    private String cod;

    public List<ru.serujimir.weatherapp.WeekParsing.List> getList() {
        return list;
    }

    List<ru.serujimir.weatherapp.WeekParsing.List> list;

    public Main getMain() {
        return main;
    }

    Main main;
}
