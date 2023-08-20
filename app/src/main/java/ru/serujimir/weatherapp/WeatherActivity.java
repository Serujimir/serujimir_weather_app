package ru.serujimir.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


import okhttp3.OkHttpClient;
import okhttp3.Request;


public class WeatherActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ArrayList<DayForecast> dayForecastArrayList = new ArrayList<DayForecast>();
    ArrayList<WeekForecast> weekForecastArrayList = new ArrayList<WeekForecast>();
    RecyclerView dayRecyclerView;
    RecyclerView weekRecyclerView;
    DayForecastAdapter dayForecastAdapter;
    WeekForecastAdapter weekForecastAdapter;
    OkHttpClient okHttpClient;
    Date dayWeek;

    TextView tvCurrTemp, tvCurrWeatherDesk, tvCurrTime, tvWindDegrees, tvWindSpeed, tvSunrise, tvSunset, tvForecast;
    ImageView tvCurrWeather;

    String first_temp, second_temp, current_city;
    LinearLayout linearLayout;

    ImageView imTurbine;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_weather);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Forecasting...");
        progressDialog.create();
        progressDialog.setCanceledOnTouchOutside(false);
        tvForecast = findViewById(R.id.tvForecast);
        sharedPreferences = getSharedPreferences("Current_city",MODE_PRIVATE);
        current_city = sharedPreferences.getString("Current_city","Yakutsk");
        tvForecast.setText("Forecast for: " + current_city.substring(0,1).toUpperCase() + current_city.substring(1).toLowerCase());

        imTurbine = findViewById(R.id.imTurbine);
        AnimationDrawable animationDrawable = (AnimationDrawable) imTurbine.getDrawable();
        animationDrawable.start();

        Log.d("Check", "setContentView() Check!");
        init();
    }

    public void init() {
        linearLayout = findViewById(R.id.linearLayout);
        tvCurrTemp = findViewById(R.id.tvCurrTemp);
        tvCurrTemp.setText("Pls wait...");
        tvCurrWeather = findViewById(R.id.tvCurrWeather);
        tvCurrWeatherDesk = findViewById(R.id.tvCurrWeatherDesk);
        tvCurrTime = findViewById(R.id.tvCurrTime);

        tvWindDegrees = findViewById(R.id.tvWindDegrees);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);

        dayRecyclerView = findViewById(R.id.dayRecyclerView);
        dayForecastAdapter = new DayForecastAdapter(getApplicationContext(), dayForecastArrayList);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        dayRecyclerView.setAdapter(dayForecastAdapter);

        weekRecyclerView = findViewById(R.id.weekRecyclerView);
        weekForecastAdapter = new WeekForecastAdapter(getApplicationContext(), weekForecastArrayList);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        weekRecyclerView.setAdapter(weekForecastAdapter);

        dayForecastArrayList.add(new DayForecast("Loading","...","01d"));
        weekForecastArrayList.add(new WeekForecast("Loadind...","...","...","01d"));

        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        okHttpClient = new OkHttpClient();


        Thread set0 = new Thread(new Runnable() {
            @Override
            public void run() {
                setInitionalData0();
            }
        });

        Thread set1 = new Thread(new Runnable() {
            @Override
            public void run() {
                setInitionalData1();
            }
        });



        set0.start();
        set1.start();

        Log.d("Check", "setAdapter check!");
    }
//    public void setInitionalData0() {
//
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.openweathermap.org/data/2.5/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//        JsonHolderApi jsonHolderApi = retrofit.create(JsonHolderApi.class);
//
//        Call<DayForecast> dayForecastCall = jsonHolderApi.getDayForecast("Yakutsk", "23df53519973b7a0f5b39b79e5b9aec4", "metric", "ru");
//
//        dayForecastCall.enqueue(new Callback<DayForecast>() {
//
//            @Override
//            public void onResponse(Call<DayForecast> call, Response<DayForecast> response) {
//
//                String responseData = response.body().toString();
//                try {
//                    JSONObject jsonObjectObs = new JSONObject(responseData);
//                    JSONObject main = jsonObjectObs.getJSONObject("main");
//                    JSONArray weather = jsonObjectObs.getJSONArray("weather");
//
//                    String temp = main.getString("temp");
//                    tvCurrTemp.setText(temp + "°");
//
//                    String icon = weather.getString(4);
//                    setDayIcon(tvCurrWeather, icon);
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DayForecast> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "Error connection: " + t, Toast.LENGTH_SHORT).show();
//                Log.d("Responce", String.valueOf(t));
//            }
//        });
//    }

    public void setInitionalData0() {
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q=" + current_city + "&appid=23df53519973b7a0f5b39b79e5b9aec4&units=metric&lang=en")
                .get()
                .build();
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        try {
          Response response = okHttpClient.newCall(request).execute();
          okHttpClient.newCall(request).enqueue(new Callback() {
              @Override
              public void onFailure(Call call, IOException e) {

              }

              @Override
              public void onResponse(Call call, okhttp3.Response response) throws IOException {
                  String responseData = response.body().string();
                  Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);

                  try {
                      JSONObject jsonObjectObs = new JSONObject(responseData);

                      JSONObject main = jsonObjectObs.getJSONObject("main");
                      String temp = String.valueOf(Math.round(main.getDouble("temp"))) + "°";

                      JSONArray weather = jsonObjectObs.getJSONArray("weather");
                      JSONObject weather_data = weather.getJSONObject(0);
                      String description = weather_data.getString("description");
                      String icon = weather_data.getString("icon");

                      JSONObject wind = jsonObjectObs.getJSONObject("wind");
                      String wind_deg = wind.getString("deg") + "°";
                      String wind_speed = String.valueOf(Math.round(wind.getDouble("speed")) + " meter/sec");

                      JSONObject sys = jsonObjectObs.getJSONObject("sys");
                      int sunrise = sys.getInt("sunrise");
                      int sunset = sys.getInt("sunset");
                      String time_sunrise = new SimpleDateFormat("HH:mm").format(new Date(sunrise * 1000L));
                      String time_sunset = new SimpleDateFormat("HH:mm").format(new Date(sunset * 1000L));

                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {


                              int dateTime = 0;
                              try {dateTime = (int) jsonObjectObs.getInt("dt");} catch (JSONException e) {throw new RuntimeException(e);}
                              @SuppressLint("SimpleDateFormat")
                              String result = new java.text.SimpleDateFormat("HH:mm").format(new Date(dateTime * 1000L));

                              tvCurrTime.setText("Actual on " + result);
                              setDayIcon(tvCurrWeather, icon);
                              tvCurrWeatherDesk.setText(description.substring(0,1).toUpperCase() + description.substring(1));
                              tvCurrTemp.setText(temp);

                              tvWindDegrees.setText(wind_deg);
                              tvWindSpeed.setText(wind_speed);

                              tvSunrise.setText(time_sunrise);
                              tvSunset.setText(time_sunset);


                              Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.adapter_update_scale);
                              tvCurrTime.startAnimation(animation);
                              tvCurrWeather.startAnimation(animation);
                              tvCurrWeatherDesk.startAnimation(animation);
                              tvCurrTemp.startAnimation(animation);

                          }
                      });
                  } catch (JSONException e) {
                      throw new RuntimeException(e);
                  }
              }
          });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInitionalData1() {


        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/forecast?q=" + current_city + "&appid=23df53519973b7a0f5b39b79e5b9aec4&units=metric&lang=en")
                .get()
                .build();
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        try {
            Response response = okHttpClient.newCall(request).execute();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responceData = response.body().string();
                    Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
                    try {
                        try {
                            JSONObject jsonObjectObs = new JSONObject(responceData);
                            JSONArray list = jsonObjectObs.getJSONArray("list");
                            JSONObject listObject = list.getJSONObject(0);
                            String curr_day = listObject.getString("dt_txt").substring(0,10);

                            dayForecastArrayList.clear();
                            weekForecastArrayList.clear();

                            for(int i = 0; i < list.length(); i++){
                                Log.d("Responce", "for0");
                                listObject = list.getJSONObject(i);

                                JSONObject main = listObject.getJSONObject("main");

                                JSONArray weather = listObject.getJSONArray("weather");
                                JSONObject weather_data = weather.getJSONObject(0);
                                String icon = weather_data.getString("icon");

                                String day = listObject.getString("dt_txt").substring(0,10);
                                if(day.equals(curr_day)) {
                                    String day_time = listObject.getString("dt_txt").substring(11,16);
                                    String day_temp = String.valueOf(Math.round(main.getDouble("temp")) + "°");
                                    String day_icon = icon;
                                    dayForecastArrayList.add(new DayForecast(day_time,day_temp,day_icon));
                                    Log.d("Responce",day_time + "   " + day_temp + "   " + day_icon);
                                }
                                else {
                                  break;
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dayForecastAdapter.Update();
                                    }
                                });
                            }

                            for(int u = 0; u < list.length(); u++) {
                                Log.d("Responce", "for1: " + u);

                                listObject = list.getJSONObject(u);

                                JSONObject main = listObject.getJSONObject("main");

                                JSONArray weather = listObject.getJSONArray("weather");
                                JSONObject weather_data = weather.getJSONObject(0);

                                String now_day = listObject.getString("dt_txt").substring(0,10);
                                String day_time = listObject.getString("dt_txt").substring(11);

                                Log.d("Responce", day_time.toString());


                                if(now_day.equals(curr_day) || day_time.equals("03:00:00") || day_time.equals("09:00:00") || day_time.equals("15:00:00") || day_time.equals("18:00:00") || day_time.equals("21:00:00") || day_time.equals("00:00:00")) {
                                    Log.d("Check", "if used! if used! if used!");
                                }
                                else if(day_time.equals("06:00:00")) {
                                    first_temp = String.valueOf(Math.round(main.getDouble("temp_min"))) + "°";
                                }
                                else if(day_time.equals("12:00:00")) {
                                    second_temp = String.valueOf(Math.round(main.getDouble("temp_max"))) + "°";
                                    String week_icon = weather_data.getString("icon");

                                    String year_month_day = listObject.getString("dt_txt");
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    dayWeek = null;
                                    try {
                                        dayWeek = simpleDateFormat.parse(year_month_day);
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(dayWeek);


                                    weekForecastArrayList.add(new WeekForecast(dayOfWeek,first_temp,second_temp,week_icon));
                                    Log.d("Responce", first_temp + "   " + second_temp + "   " + dayOfWeek);
                                }
                                else {
                                   break;
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.adapter_update);
                                        linearLayout.startAnimation(animation1);
                                        Timer timer = new Timer();
                                        timer.schedule(new TimerTask()  {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        weekForecastAdapter.Update();
                                                    }
                                                });
                                            }
                                        },1000);
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (RuntimeException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        } catch (IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvCurrTemp.setText("No Internet");
                }
            });
        }
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    public void goToSettings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void Reload(View v) {
        Thread refresh = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                        dayForecastArrayList.clear();
                        weekForecastArrayList.clear();
                        dayForecastAdapter.Update();
                        weekForecastAdapter.Update();
                        init();
                    }
                });
            }
        });
        refresh.start();
    }
    public void setDayIcon(final ImageView tvCurrWeather, final String icon) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (icon) {
                    case "01d":
                        tvCurrWeather.setImageResource(R.drawable.d01);
                        break;
                    case "02d":
                        tvCurrWeather.setImageResource(R.drawable.d02);
                        break;
                    case "03d":
                        tvCurrWeather.setImageResource(R.drawable.d03);
                        break;
                    case "04d":
                        tvCurrWeather.setImageResource(R.drawable.d04);
                        break;
                    case "09d":
                        tvCurrWeather.setImageResource(R.drawable.d09);
                        break;
                    case "10d":
                        tvCurrWeather.setImageResource(R.drawable.d10);
                        break;
                    case "11d":
                        tvCurrWeather.setImageResource(R.drawable.d11);
                        break;
                    case "13d":
                        tvCurrWeather.setImageResource(R.drawable.d13);
                        break;
                    case "50d":
                        tvCurrWeather.setImageResource(R.drawable.d50);
                        break;

                    case "01n":
                        tvCurrWeather.setImageResource(R.drawable.n01);
                        break;
                    case "02n":
                        tvCurrWeather.setImageResource(R.drawable.n02);
                        break;
                    case "03n":
                        tvCurrWeather.setImageResource(R.drawable.n03);
                        break;
                    case "04n":
                        tvCurrWeather.setImageResource(R.drawable.n04);
                        break;
                    case "09n":
                        tvCurrWeather.setImageResource(R.drawable.n09);
                        break;
                    case "10n":
                        tvCurrWeather.setImageResource(R.drawable.n10);
                        break;
                    case "11n":
                        tvCurrWeather.setImageResource(R.drawable.n11);
                        break;
                    case "13n":
                        tvCurrWeather.setImageResource(R.drawable.n13);
                        break;
                    case "50n":
                        tvCurrWeather.setImageResource(R.drawable.n50);
                        break;

                }
            }
        });
    }
}