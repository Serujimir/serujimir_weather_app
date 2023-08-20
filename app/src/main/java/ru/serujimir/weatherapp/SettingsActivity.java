package ru.serujimir.weatherapp;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {
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
    int response_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        tvCurrCity = findViewById(R.id.tvCurrCity);
        sharedPreferences = getSharedPreferences("Current_city",MODE_PRIVATE);
        activity_city = sharedPreferences.getString("Current_city", "Yakutsk");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });

    }

    public void init() {

        tvCurrCity.setText("Current city: " + sharedPreferences.getString("Current_city","Yakutsk").substring(0,1).toUpperCase() +
                sharedPreferences.getString("Current_city","Yakutsk").substring(1).toLowerCase());

        cityArrayList = new ArrayList<>();
        rvCities = findViewById(R.id.rvCities);
        rvCities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        CityAdapter.OnCityClickListener onCityClickListener = new CityAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(City city) {
                tvCurrCity.setText("Current city: " + sharedPreferences.getString("Current_city","Yakutsk").substring(0,1).toUpperCase() +
                        sharedPreferences.getString("Current_city","Yakutsk").substring(1).toLowerCase());
            }
        };
        CityAdapter cityAdapter = new CityAdapter(this,cityArrayList,onCityClickListener);
        rvCities.setAdapter(cityAdapter);

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.create_db();
        citiesDatabase = databaseHelper.open();
        cursor = citiesDatabase.rawQuery("select * from " + DatabaseHelper.TABLE, null);
        progressDialog = new ProgressDialog(this);
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

    public void addCity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.show();
                            }
                        });
                        if (!edCity.getText().toString().isEmpty()) {
                            String city = edCity.getText().toString();

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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                                Toast.makeText(getApplicationContext(), "Unvailable city!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String responseData = response.body().string();
                                        try {
                                            JSONObject jsonObjectObs = new JSONObject(responseData);
                                            try {
                                                response_code = (int) jsonObjectObs.get("cod");
                                            }catch (Exception e)
                                            {
                                                Toast.makeText(getApplicationContext(), "Error...", Toast.LENGTH_SHORT).show();
                                            }
                                            if(response_code == 404 || response_code == 0)  {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "City not found", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else {
                                                Log.d("Test", "onResponce!");
                                                Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
                                                DatabaseHelper databaseHelper1 = new DatabaseHelper(getApplicationContext());
                                                databaseHelper1.create_db();
                                                SQLiteDatabase sqLiteDatabase = databaseHelper1.open();
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("city", city);
                                                sqLiteDatabase.insert("citiesDatabases", null, contentValues);
                                                CityAdapter cityAdapter = new CityAdapter(getApplicationContext(), cityArrayList, onCityClickListener);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                alertDialog.dismiss();
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getApplicationContext(), "City added!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                        cityAdapter.Update();
                                                    }
                                                });
                                                sqLiteDatabase.close();
                                            }
                                        } catch (JSONException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "JSON parse failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }


                                    }
                                });
                            } catch (IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        });
                                        Toast.makeText(getApplicationContext(), "Timeout", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                        }
                                    });
                                    Toast.makeText(getApplicationContext(), "Fill the field!", Toast.LENGTH_SHORT).show();
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

    public void goToMain(View v) {
        if(activity_city.equals(sharedPreferences.getString("Current_city","Yakutsk"))) {
            finish();
        }
        else {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void Update() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }
}


















