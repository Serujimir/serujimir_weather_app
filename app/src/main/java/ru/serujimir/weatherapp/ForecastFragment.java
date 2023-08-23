package ru.serujimir.weatherapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
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
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment {
    View view;
    SharedPreferences sharedPreferences;
    RecyclerView weekRecyclerView;
    ArrayList<WeekForecast> weekForecastArrayList = new ArrayList<WeekForecast>();
    WeekForecastAdapter weekForecastAdapter;
    OkHttpClient okHttpClient;
    String current_city, first_temp, second_temp;
    ConstraintLayout constraintLayout;
    TextView tvWeekForecast;
    ProgressDialog progressDialog;
    Date dayWeek;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForecastFragment newInstance(String param1, String param2) {
        ForecastFragment fragment = new ForecastFragment();
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
        view = inflater.inflate(R.layout.fragment_forecast, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Current_city",MODE_PRIVATE);
        current_city = sharedPreferences.getString("Current_city","Yakutsk");

        init();

        return view;
    }
    public void init() {
        tvWeekForecast = view.findViewById(R.id.tvWeekForecast);
        sharedPreferences = getActivity().getSharedPreferences("Current_city",MODE_PRIVATE);
        current_city = sharedPreferences.getString("Current_city","Yakutsk");
        tvWeekForecast.setText("Week Forecast for: " + current_city);


        weekForecastArrayList.clear();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("City verifying...");
        progressDialog.setCanceledOnTouchOutside(false);

        weekRecyclerView = view.findViewById(R.id.weekRecyclerView);
        weekForecastAdapter = new WeekForecastAdapter(getContext(), weekForecastArrayList);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        weekRecyclerView.setAdapter(weekForecastAdapter);

        weekForecastArrayList.add(new WeekForecast("Loadind...","...","...","01d"));

        constraintLayout = view.findViewById(R.id.consLayout);

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setInitionalData1();
            }
        });
    }
    public void setInitionalData1() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                okHttpClient = new OkHttpClient();
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

                                    weekForecastArrayList.clear();

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
                                        requireActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Animation animation1 = AnimationUtils.loadAnimation(getContext(),R.anim.adapter_update);
                                                constraintLayout.startAnimation(animation1);
                                                Timer timer = new Timer();
                                                timer.schedule(new TimerTask()  {
                                                    @Override
                                                    public void run() {
                                                        requireActivity().runOnUiThread(new Runnable() {
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
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (RuntimeException e) {
                                getActivity().runOnUiThread(new Runnable() {
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
                            tvWeekForecast.setText("No Internet");
                        }
                    });
                }
            }
        });
        thread.start();

        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void Restart() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setInitionalData1();
            }
        });
    }
}