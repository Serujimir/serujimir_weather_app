package ru.serujimir.weatherapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment implements CityAdapter.OnCityClickListener {
    View view;
    SharedPreferences sharedPreferences;

    ImageButton btnReload;
    TextView tvCurrWeatherCity, tvCurrWeatherDesk, tvCurrWeatherTemp;
    ImageView ivCurrWeather;

    ArrayList<DayForecast> dayForecastArrayList = new ArrayList<DayForecast>();
    ArrayList<WeatherSubItem> weatherSubItemArrayList = new ArrayList<WeatherSubItem>();
    RecyclerView dayRecyclerView, rvWeatherSubItem;
    DayForecastAdapter dayForecastAdapter;
    WeatherSubItemAdapter weatherSubItemAdapter;

    OkHttpClient okHttpClient;
    Date dayWeek;

    String current_city;

//    ImageView imTurbine;
    ProgressDialog progressDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeatherFragment() {
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
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
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
        if(!current_city.equals(sharedPreferences.getString("Current_city","Yakutsk"))) {
            init();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_weather, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Current_city",MODE_PRIVATE);
        current_city = sharedPreferences.getString("Current_city","Yakutsk");


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Forecasting...");
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
        tvCurrWeatherCity.setText("Loading...");
        tvCurrWeatherDesk = view.findViewById(R.id.tvWeatherDesk);
        tvCurrWeatherDesk.setText("Loading...");
        tvCurrWeatherTemp = view.findViewById(R.id.tvCurrWeatherTemp);
        tvCurrWeatherTemp.setText("Loading...");

        ivCurrWeather = view.findViewById(R.id.ivCurrWeatherIcon);

        dayRecyclerView = view.findViewById(R.id.rvDayForecast);
        rvWeatherSubItem = view.findViewById(R.id.rvWeatherSubItem);

        dayForecastArrayList.clear();
        weatherSubItemArrayList.clear();
        sharedPreferences = getActivity().getSharedPreferences("Current_city",MODE_PRIVATE);
        current_city = sharedPreferences.getString("Current_city","Yakutsk");

        weatherSubItemAdapter = new WeatherSubItemAdapter(getContext(), weatherSubItemArrayList);
        rvWeatherSubItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvWeatherSubItem.setAdapter(weatherSubItemAdapter);

        dayForecastAdapter = new DayForecastAdapter(getContext(), dayForecastArrayList);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dayRecyclerView.setAdapter(dayForecastAdapter);

//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
//        weatherSubItemArrayList.add(new WeatherSubItem("Loading...", "visibility", "Loading..."));
        weatherSubItemArrayList.add(new WeatherSubItem("Coordinates:\nSpeed: 25 meter/sec\nDegreed: 248", "wind"));
        weatherSubItemArrayList.add(new WeatherSubItem("Coordinates:\nSunrise: 1180909\nSunset: 594949499", "sun"));
        weatherSubItemArrayList.add(new WeatherSubItem("Coordinates:\n10000km", "visibility"));
        weatherSubItemArrayList.add(new WeatherSubItem("Coordinates:\n65%", "humidity"));
        weatherSubItemArrayList.add(new WeatherSubItem("Coordinates:\n1014mm", "pressure"));
        weatherSubItemArrayList.add(new WeatherSubItem("Coordinates:\nLongitude: 64.4949 \nLatitude: 67.5955", "coordinates"));
        dayForecastArrayList.add(new DayForecast("Loading","...","01d"));

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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, okhttp3.Response response) throws IOException {
                            String responseData = response.body().string();
                            Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);

                            try {
                                JSONObject jsonObjectObs = new JSONObject(responseData);

                                JSONObject coord = jsonObjectObs.getJSONObject("coord");
                                String lon = coord.getString("lon");
                                String lat = coord.getString("lat");

                                JSONArray weather_array = jsonObjectObs.getJSONArray("weather");
                                JSONObject weather = weather_array.getJSONObject(0);
                                String description = weather.getString("description");
                                String icon = weather.getString("icon");

                                JSONObject main = jsonObjectObs.getJSONObject("main");
                                String temp = main.getString("temp");
                                String pressure = main.getString("pressure");
                                String humidity = main.getString("humidity");

                                String visibility = jsonObjectObs.getString("visibility");

                                JSONObject wind = jsonObjectObs.getJSONObject("wind");
                                String speed = wind.getString("speed");
                                String deg = wind.getString("deg");

                                String dt = jsonObjectObs.getString("dt");

                                JSONObject sys = jsonObjectObs.getJSONObject("sys");
                                String country = sys.getString("country");
                                int sunrise_int = Integer.parseInt(sys.getString("sunrise"));
                                int sunset_int = Integer.parseInt(sys.getString("sunset"));
                                @SuppressLint("SimpleDateFormat") String sunrise = new SimpleDateFormat("HH:mm").format(new Date(sunrise_int * 1000L));
                                @SuppressLint("SimpleDateFormat") String sunset = new SimpleDateFormat("HH:mm").format(new Date(sunset_int * 1000L));

                                String name = jsonObjectObs.getString("name");

                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setDayIcon(ivCurrWeather, icon);
                                        tvCurrWeatherTemp.setText(temp + "°C");
                                        tvCurrWeatherDesk.setText(description.substring(0,1).toUpperCase() + description.substring(1));
                                        tvCurrWeatherCity.setText(name + ", " + country);

                                        weatherSubItemArrayList.clear();

                                        weatherSubItemArrayList.add(new WeatherSubItem("Wind:\nSpeed: " + speed + " meter/sec" + "\nDegrees: " + deg + "°", "wind"));

                                        weatherSubItemArrayList.add(new WeatherSubItem("Sun:\nSunrise: " + sunrise + "\nSunset: " + sunset,"sun"));

                                        weatherSubItemArrayList.add(new WeatherSubItem("Visibility:\n" + visibility + " meters","visibility"));

                                        weatherSubItemArrayList.add(new WeatherSubItem("Humidity:\n" + humidity +"%","humidity"));

                                        weatherSubItemArrayList.add(new WeatherSubItem("Pressure:\n" + pressure + " mm.","pressure"));

                                        weatherSubItemArrayList.add(new WeatherSubItem("Coordinates:\nLongitude: " + lon + "\nLatitude: " + lat,"coordinates"));

                                        weatherSubItemAdapter.Update();

                                        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.adapter_update_scale);
                                        tvCurrWeatherDesk.startAnimation(animation);

                                    }
                                });
                            } catch (JSONException e) {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                } catch (IOException e) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCurrWeatherTemp.setText("No internet");
                        }
                    });
                }
            }
        });
        thread0.start();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
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
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (RuntimeException e) {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
                } catch (IOException e) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCurrWeatherTemp.setText("No Internet");
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