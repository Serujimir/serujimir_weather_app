package ru.serujimir.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.Locale;
import java.util.TimerTask;

public class LaunchScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("Current_city", MODE_PRIVATE);
        Locale locale = new Locale(sharedPreferences.getString("lang", "en"));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(configuration, null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}