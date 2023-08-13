package ru.serujimir.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonHolderApi {
    @GET("weather")
    Call<DayForecast> getDayForecast(@Query("q") String city, @Query("appid") String appid, @Query("units") String units, @Query("lang") String lang);

    @GET("forecast")
    Call<WeekForecast> getWeekForecast(@Query("q") String city, @Query("appid") String appid, @Query("units") String units, @Query("lang") String lang);
}
