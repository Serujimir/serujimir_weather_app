package ru.serujimir.weatherapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.serujimir.weatherapp.DayParsing.Coord;
import ru.serujimir.weatherapp.DayParsing.DayCast;
import ru.serujimir.weatherapp.DayParsing.Main;
import ru.serujimir.weatherapp.DayParsing.Sys;
import ru.serujimir.weatherapp.DayParsing.Weather;
import ru.serujimir.weatherapp.DayParsing.Wind;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment0#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment0 extends Fragment implements CityAdapter.OnCityClickListener {
    View view;
    SharedPreferences sharedPreferences;

    ImageButton btnReload;
    TextView tvCurrWeatherCity, tvCurrWeatherDesk, tvCurrWeatherTemp;
    ImageView ivCurrWeather;

    ArrayList<DayForecast> dayForecastArrayList = new ArrayList<DayForecast>();
    ArrayList<WeatherSubItem> weatherSubItemArrayList = new ArrayList<WeatherSubItem>();
    RecyclerView rvDay, rvWeatherSubItem;
    DayForecastAdapter dayForecastAdapter;
    WeatherSubItemAdapter weatherSubItemAdapter;

    OkHttpClient okHttpClient;
    Date dayWeek;

    String current_city;

//    ImageView imTurbine;
    ProgressDialog progressDialog;
    private static final String DEF_CITY = "Москва";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeatherFragment0() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment0 newInstance(String param1, String param2) {
        WeatherFragment0 fragment = new WeatherFragment0();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Test", current_city);
        sharedPreferences = getActivity().getSharedPreferences("Current_city",MODE_PRIVATE);
        if(!current_city.equals(sharedPreferences.getString("Current_city",DEF_CITY))) {
            init();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_weather, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Current_city",MODE_PRIVATE);
        current_city = sharedPreferences.getString("Current_city",DEF_CITY);


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.forecasting));
        progressDialog.create();
        progressDialog.setCanceledOnTouchOutside(false);

//        AnimationDrawable animationDrawable = (AnimationDrawable) imTurbine.getDrawable();
//        animationDrawable.start();

        Log.d("Check", "setContentView() Check!");
        init();

        return view;
    }
    public void init() {
        btnReload = view.findViewById(R.id.btnReload);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reload(v);
            }
        });

        tvCurrWeatherCity = view.findViewById(R.id.tvCurrWeatherCity);
        tvCurrWeatherCity.setText(getString(R.string.loading_with_points));
        tvCurrWeatherDesk = view.findViewById(R.id.tvWeatherDesk);
        tvCurrWeatherDesk.setText(getString(R.string.loading_with_points));
        tvCurrWeatherTemp = view.findViewById(R.id.tvCurrWeatherTemp);
        tvCurrWeatherTemp.setText(getString(R.string.loading_with_points));

        ivCurrWeather = view.findViewById(R.id.ivCurrWeatherIcon);

        rvDay = view.findViewById(R.id.rvDayForecast);
        rvWeatherSubItem = view.findViewById(R.id.rvWeatherSubItem);

        dayForecastArrayList.clear();
        weatherSubItemArrayList.clear();
        sharedPreferences = getActivity().getSharedPreferences("Current_city",MODE_PRIVATE);
        current_city = sharedPreferences.getString("Current_city",DEF_CITY);

        weatherSubItemAdapter = new WeatherSubItemAdapter(getContext(), weatherSubItemArrayList);
        rvWeatherSubItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvWeatherSubItem.setAdapter(weatherSubItemAdapter);

        dayForecastAdapter = new DayForecastAdapter(getContext(), dayForecastArrayList);
        rvDay.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDay.setAdapter(dayForecastAdapter);

//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
        weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.loading), "wind"));
        weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.loading), "sun"));
        weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.loading), "visibility"));
        weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.loading), "humidity"));
        weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.loading), "pressure"));
        weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.loading), "coordinates"));
        dayForecastArrayList.add(new DayForecast(getString(R.string.loading),"...","01d"));

        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        okHttpClient = new OkHttpClient();


        Thread set0 = new Thread(new Runnable() {
            @Override
            public void run() {
                setInitionalData0();
            }
        });




        set0.start();

        Log.d("Check", "setAdapter check!");
    }
    public void setInitionalData0() {
        Thread thread0 = new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.openweathermap.org/data/2.5/")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            JsonHolderApi jsonHolderApi = retrofit.create(JsonHolderApi.class);
            sharedPreferences = getActivity().getSharedPreferences("Current_city", MODE_PRIVATE);
            String lang = sharedPreferences.getString("lang", "ru");
            if(lang.equals("sah")){
                lang = "ru";
            }
            retrofit2.Call<DayCast> dayCastCall = jsonHolderApi.getDayCast(current_city,"23df53519973b7a0f5b39b79e5b9aec4","metric",lang);

            dayCastCall.enqueue(new retrofit2.Callback<DayCast>() {
                @Override
                public void onResponse(retrofit2.Call<DayCast> call, retrofit2.Response<DayCast> response) {
                    if(!response.isSuccessful()) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getContext(), "Error " + response.code(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }
                    else if(response.isSuccessful()) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getContext(), "Success " + response.code(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
                        DayCast dayCast = response.body();

                        Coord coord = dayCast.getCoord();
                        String lon = coord.getLon();
                        String lat = coord.getLat();

                        Weather weather = dayCast.getWeather().get(0);
                        String description = weather.getDescription();
                        String icon = weather.getIcon();

                        Main main = dayCast.getMain();
                        String temp = main.getTemp();
                        String pressure = main.getPressure();
                        String humidity = main.getHumidity();

                        String visibility = dayCast.getVisibility();

                        Wind wind = dayCast.getWind();
                        String speed = wind.getSpeed();
                        String deg = wind.getDeg();

                        Sys sys = dayCast.getSys();
                        String country = sys.getCountry();
                        int sunrise_int = Integer.parseInt(sys.getSunrise());
                        int sunset_int = Integer.parseInt(sys.getSunset());
                        @SuppressLint("SimpleDateFormat") String sunrise = new SimpleDateFormat("HH:mm").format(new Date(sunrise_int * 1000L));
                        @SuppressLint("SimpleDateFormat") String sunset = new SimpleDateFormat("HH:mm").format(new Date(sunset_int * 1000L));

                        String name = dayCast.getName();

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDayIcon(ivCurrWeather, icon);
                                tvCurrWeatherTemp.setText(temp + "°C");
                                tvCurrWeatherDesk.setText(description.substring(0,1).toUpperCase() + description.substring(1));
                                tvCurrWeatherCity.setText(name + ", " + country);

                                weatherSubItemArrayList.clear();

                                weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.wind) + "\n" + getString(R.string.speed) + " " + speed + " " + getString(R.string.meter_sec) + "\n" + getString(R.string.degrees) + " " + deg + "°", "wind"));

                                weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.sun) + "\n" + getString(R.string.sunrise) + " " + sunrise + "\n" + getString(R.string.sunset) + " " + sunset,"sun"));

                                weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.visibility) + "\n" + visibility + " " + getString(R.string.meters),"visibility"));

                                weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.humidity) + "\n" + humidity +"%","humidity"));

                                weatherSubItemArrayList.add(new WeatherSubItem( getString(R.string.pressure)+ "\n" + pressure + getString(R.string.mm),"pressure"));

                                weatherSubItemArrayList.add(new WeatherSubItem(getString(R.string.coordinates) + "\n" + getString(R.string.longitude) + " " + lon + "\n" + getString(R.string.latitude) + " " + lat,"coordinates"));

                                weatherSubItemAdapter.Update();

                                Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.adapter_update_scale);
                                tvCurrWeatherDesk.startAnimation(animation);

                            }
                        });
                    }


                }

                @Override
                public void onFailure(retrofit2.Call<DayCast> call, Throwable t) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            }
        });
        thread0.start();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                sharedPreferences = getActivity().getSharedPreferences("Current_city", MODE_PRIVATE);
                String lang = sharedPreferences.getString("lang", "ru");
                if(lang.equals("sah")){
                    lang = "ru";
                }
                Request request = new Request.Builder()
                        .url("https://api.openweathermap.org/data/2.5/forecast?q=" + current_city + "&appid=23df53519973b7a0f5b39b79e5b9aec4&units=metric&lang=" + lang)
                        .get()
                        .build();
                StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(threadPolicy);

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), R.string.error + " " + e, Toast.LENGTH_SHORT).show();
                                }
                            });
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
                                        requireActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dayForecastAdapter.Update();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), R.string.error + " " + e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (RuntimeException e) {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), R.string.error + " " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
                } catch (IOException e) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCurrWeatherTemp.setText(getString(R.string.no_internet));
                        }
                    });
                }
            }
        });
        thread1.start();
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    public void Reload(View v) {
        Thread refresh = new Thread(new Runnable() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                        dayForecastArrayList.clear();
                        dayForecastAdapter.Update();
                        init();
                    }
                });
            }
        });
        refresh.start();
    }
    public void setDayIcon(final ImageView tvCurrWeather, final String icon) {
        requireActivity().runOnUiThread(new Runnable() {
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

    @Override
    public void onCityClick(City city) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                tvForecast.setText("Forecast for: " + sharedPreferences.getString("Current_city","Yakutsk").substring(0,1).toUpperCase() +
//                        sharedPreferences.getString("Current_city","Yakutsk").substring(1).toLowerCase());
//            }
//        });
    }
}