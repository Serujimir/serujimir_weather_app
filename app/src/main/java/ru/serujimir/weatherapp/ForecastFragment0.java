package ru.serujimir.weatherapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.serujimir.weatherapp.WeekParsing.List;
import ru.serujimir.weatherapp.WeekParsing.Main;
import ru.serujimir.weatherapp.WeekParsing.Weather;
import ru.serujimir.weatherapp.WeekParsing.WeekCast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment0#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment0 extends Fragment {
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
    ImageButton btnReload2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String DEF_CITY = "Москва";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForecastFragment0() {
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
    public static ForecastFragment0 newInstance(String param1, String param2) {
        ForecastFragment0 fragment = new ForecastFragment0();
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
        view = inflater.inflate(R.layout.fragment_forecast, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Current_city",MODE_PRIVATE);
        current_city = sharedPreferences.getString("Current_city",DEF_CITY);

        init();

        return view;
    }
    public void init() {
        btnReload2 = view.findViewById(R.id.btnReload2);
        btnReload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });

        tvWeekForecast = view.findViewById(R.id.tvWeekForecast);
        sharedPreferences = getActivity().getSharedPreferences("Current_city",MODE_PRIVATE);
        current_city = sharedPreferences.getString("Current_city",DEF_CITY);
        tvWeekForecast.setText(getString(R.string.week_forecast_title) + " " + current_city);


        weekForecastArrayList.clear();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.city_verifying);
        progressDialog.setCanceledOnTouchOutside(false);

        weekRecyclerView = view.findViewById(R.id.weekRecyclerView);
        weekForecastAdapter = new WeekForecastAdapter(getContext(), weekForecastArrayList);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        weekRecyclerView.setAdapter(weekForecastAdapter);

        weekForecastArrayList.add(new WeekForecast(getString(R.string.loading),"...","...","01d"));

        constraintLayout = view.findViewById(R.id.consLayout);
        Log.d("Test", "Before runOnUiThread");

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
                Log.d("Test", "After runOnUiThread");

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Log.d("Test", "After Gson builder");
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
                Call<WeekCast> weekCastCall = jsonHolderApi.getWeekCast(current_city,"23df53519973b7a0f5b39b79e5b9aec4","metric",lang);

                weekCastCall.enqueue(new Callback<WeekCast>() {
                    @Override

                    public void onResponse(Call<WeekCast> call, Response<WeekCast> response) {
                        if(!response.isSuccessful()) {
                            Log.d("Test", "After call !success");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("Test", String.valueOf(response.code()));
                                }
                            });
                        }
                        else if(response.isSuccessful()) {
                            Log.d("Test", "After call success");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("Test", String.valueOf(response.code()));
                                }
                            });
                            weekForecastArrayList.clear();

                            WeekCast weekCast = response.body();

                            Main main = weekCast.getMain();
                            List list = weekCast.getList().get(0);
                            Weather weather = list.getWeather().get(0);
                            String curr_day = list.getDt_txt().substring(0,10);





                            for(int u = 0; u < weekCast.getList().size(); u++) {
                                Log.d("Responce", "for1: " + u);

                                list = weekCast.getList().get(u);

                                main = list.getMain();

                                Weather weather_data = list.getWeather().get(0);

                                String now_day = list.getDt_txt().substring(0,10);
                                String day_time = list.getDt_txt().substring(11);

                                Log.d("Responce", day_time.toString());


                                if(now_day.equals(curr_day) || day_time.equals("03:00:00") || day_time.equals("09:00:00") || day_time.equals("15:00:00") || day_time.equals("18:00:00") || day_time.equals("21:00:00") || day_time.equals("00:00:00")) {
                                    Log.d("Check", "if used! if used! if used!");
                                }
                                else if(day_time.equals("06:00:00")) {
                                    first_temp = String.valueOf(Math.round(main.getTemp_min()) + "°");
                                }
                                else if(day_time.equals("12:00:00")) {
                                    second_temp = String.valueOf(Math.round(main.getTemp_max()) + "°");
                                    String week_icon = weather_data.getIcon();

                                    String year_month_day = list.getDt_txt();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    dayWeek = null;
                                    try {
                                        dayWeek = simpleDateFormat.parse(year_month_day);
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    String dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(dayWeek);


                                    weekForecastArrayList.add(new WeekForecast((dayOfWeek.toUpperCase().charAt(0) + dayOfWeek.substring(1)),first_temp,second_temp,week_icon));
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
                        }
                    }

                    @Override
                    public void onFailure(Call<WeekCast> call, Throwable t) {
                        Log.d("Test", t.toString() + "232");
                    }
                });
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