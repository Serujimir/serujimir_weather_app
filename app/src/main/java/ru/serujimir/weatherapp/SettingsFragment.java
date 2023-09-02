package ru.serujimir.weatherapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements CityAdapter.OnCityClickListener {
    CityAdapter.OnCityClickListener onCityClickListener;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    RecyclerView rvCities;

    SQLiteDatabase citiesDatabase;
    DatabaseHelper databaseHelper;
    Cursor cursor;
    ArrayList<City> cityArrayList;

    ProgressDialog progressDialog;
    TextView tvCurrCity;
    String activity_city;
    String responce_city = null;
    String databaseCities;
    int response_code;
    Button btnAddCity;
    View view;
    int database_size;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        tvCurrCity = view.findViewById(R.id.tvCurrCity);
        sharedPreferences = getContext().getSharedPreferences("Current_city", MODE_PRIVATE);
        activity_city = sharedPreferences.getString("Current_city", "Yakutsk");


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
        return view;
    }

    public void init() {


        tvCurrCity.setText("Current city: " + sharedPreferences.getString("Current_city","Yakutsk").substring(0,1).toUpperCase() +
                sharedPreferences.getString("Current_city","Yakutsk").substring(1).toLowerCase());

        btnAddCity = view.findViewById(R.id.btnAddCity);
        btnAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Test", "Button check!");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.add_city, null);
                builder.setView(constraintLayout);
                EditText edCity = constraintLayout.findViewById(R.id.edCity);
                Button btnAdd = constraintLayout.findViewById(R.id.btnAdd);
                Button btnClose = constraintLayout.findViewById(R.id.btnClose);
                AlertDialog alertDialog = builder.create();

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Test", "btnAdd pressed!");
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.show();
                                    }
                                });
                                if (!edCity.getText().toString().isEmpty()) {
                                    database_size = 0;


                                    String city = edCity.getText().toString().trim();

                                    Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
                                    OkHttpClient okHttpClient = new OkHttpClient();
                                    Request request = new Request.Builder()
                                            .url("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=23df53519973b7a0f5b39b79e5b9aec4")
                                            .get()
                                            .build();
                                    Log.d("Test", "Request builder!");
                                    StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                    StrictMode.setThreadPolicy(threadPolicy);
                                    try {
                                        Response response = okHttpClient.newCall(request).execute();
                                        Log.d("Test", "Responce execute!");
                                        okHttpClient.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                                        Toast.makeText(getContext(), "Unvailable city!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                String responseData = response.body().string();
                                                try {
                                                    JSONObject jsonObjectObs = new JSONObject(responseData);
                                                    try {
                                                        responce_city = jsonObjectObs.getString("name");
                                                        response_code = (int) jsonObjectObs.get("cod");
                                                    }catch (Exception e)
                                                    {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getContext(), "Error, City not found", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                        return;
                                                    }
                                                    if(response_code != 200)  {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getContext(), "City not found", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                        return;
                                                    }
                                                    else if (response_code == 200) {

                                                        int dSize = databaseSize();

                                                        citiesDatabase = databaseHelper.open();
                                                        Cursor cursor = citiesDatabase.rawQuery("select * from " + DatabaseHelper.TABLE, null);

                                                        int cityId = cursor.getColumnIndex("city");
                                                        cursor.moveToFirst();
                                                        databaseCities = cursor.getString(cityId);

                                                        if(Objects.equals(databaseCities, responce_city)) {
                                                            requireActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(getContext(), "Duplicate city!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            progressDialog.dismiss();
                                                            database_size = 0;
                                                            return;
                                                        }
                                                        else {
                                                            for (int i = 0; i <= dSize; i++) {
                                                                if (cursor.moveToNext()) {
                                                                    databaseCities = cursor.getString(cityId);
                                                                    if (Objects.equals(databaseCities, responce_city)) {
                                                                        requireActivity().runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                Toast.makeText(getContext(), "Duplicate city!", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                        progressDialog.dismiss();
                                                                        database_size = 0;
                                                                        return;
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        cursor.close();
                                                        citiesDatabase.close();




                                                        Log.d("Test", "onResponce!");
                                                        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
                                                        DatabaseHelper databaseHelper1 = new DatabaseHelper(getContext());
                                                        databaseHelper1.create_db();
                                                        SQLiteDatabase sqLiteDatabase = databaseHelper1.open();
                                                        ContentValues contentValues = new ContentValues();
                                                        if(responce_city != null) {
                                                            contentValues.put("city", responce_city);
                                                        }
                                                        else {
                                                            Toast.makeText(getContext(), "Incorrect city!", Toast.LENGTH_SHORT).show();
                                                        }
                                                        sqLiteDatabase.insert("citiesDatabases", null, contentValues);
                                                        CityAdapter cityAdapter = new CityAdapter(getContext(), cityArrayList, onCityClickListener);
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                getActivity().runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        alertDialog.dismiss();
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(getContext(), "City added!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                cityAdapter.Update();
                                                            }
                                                        });
                                                        sqLiteDatabase.close();
                                                    }
                                                } catch (JSONException e) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(getContext(), "JSON parse failed!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }


                                            }
                                        });
                                    } catch (IOException e) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                                Toast.makeText(getContext(), "Timeout", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.dismiss();
                                                }
                                            });
                                            Toast.makeText(getContext(), "Fill the field!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        thread.start();
                    }
                });
                alertDialog.show();
            }
        });

        cityArrayList = new ArrayList<>();
        rvCities = view.findViewById(R.id.rvCities);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        CityAdapter.OnCityClickListener onCityClickListener = new CityAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(City city) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvCurrCity.setText("Current city: " + sharedPreferences.getString("Current_city","Yakutsk").substring(0,1).toUpperCase() +
                                sharedPreferences.getString("Current_city","Yakutsk").substring(1).toLowerCase());
                    }
                });
            }
        };
        CityAdapter cityAdapter = new CityAdapter(getContext(),cityArrayList,onCityClickListener);
        rvCities.setAdapter(cityAdapter);

        databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.create_db();
        citiesDatabase = databaseHelper.open();
        cursor = citiesDatabase.rawQuery("select * from " + DatabaseHelper.TABLE, null);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("City verifying...");
        progressDialog.setCanceledOnTouchOutside(false);

        if (cursor.moveToFirst()) {
            int id = cursor.getColumnIndex("_id");
            int city = cursor.getColumnIndex("city");

            do {
                String text_city = cursor.getString(city);
                String text_id = cursor.getString(id);
                cityArrayList.add(new City(text_city, text_id));
            } while (cursor.moveToNext());
        } else {
        }
        cityAdapter.Update();
        cursor.close();
        citiesDatabase.close();
    }

    public void Update() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    @Override
    public void onCityClick(City city) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvCurrCity.setText("Current city: " + sharedPreferences.getString("Current_city","Yakutsk").substring(0,1).toUpperCase() +
                        sharedPreferences.getString("Current_city","Yakutsk").substring(1).toLowerCase());
            }
        });
    }
    public int databaseSize() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        citiesDatabase = databaseHelper.open();
        cursor = citiesDatabase.rawQuery("select * from " + DatabaseHelper.TABLE, null);
        if(cursor.moveToFirst()) {
            do {
                database_size++;
            }while (cursor.moveToNext());
        }
        Log.d("Test", String.valueOf(database_size));
        cursor.close();
        citiesDatabase.close();
        return database_size;
    }
}