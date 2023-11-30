package ru.serujimir.weatherapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import ru.serujimir.weatherapp.DayParsing.DayCast;
import ru.serujimir.weatherapp.WeekParsing.WeekCast;

public interface JsonHolderApi {
    @GET("weather")
    Call<DayCast> getDayCast(@Query("q") String city, @Query("appid") String appid, @Query("units") String units, @Query("lang") String lang);

    @GET("forecast")
    Call<WeekCast> getWeekCast(@Query("q") String city, @Query("appid") String appid, @Query("units") String units, @Query("lang") String lang);
}
