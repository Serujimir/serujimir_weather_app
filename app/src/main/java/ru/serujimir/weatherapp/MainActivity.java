package ru.serujimir.weatherapp;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    // used attribute
    // <a href="https://www.flaticon.com/free-icons/culture" title="culture icons">Culture icons created by Freepik - Flaticon</a>
    // <a href="https://www.flaticon.com/free-icons/collaboration" title="collaboration icons">Collaboration icons created by Smashicons - Flaticon</a>
    // <a href="https://www.flaticon.com/free-icons/weather" title="weather icons">Weather icons created by Freepik - Flaticon</a>

    ViewPager2 viewPager2;
    BottomNavigationView bottomNavigationView;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("Current_city", MODE_PRIVATE);
        Locale locale = new Locale(sharedPreferences.getString("lang", "ru"));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(configuration, null);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.viewPager2);
//        viewPager2.setUserInputEnabled(true);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentArrayList.add(new SettingsFragment());
        fragmentArrayList.add(new WeatherFragment0());
        fragmentArrayList.add(new ForecastFragment0());
        fragmentArrayList.add(new CultureFragment());

        ViewPagerAdapter2 viewPagerAdapter2 = new ViewPagerAdapter2(this, fragmentArrayList);

        viewPager2.setAdapter(viewPagerAdapter2);
        viewPager2.setCurrentItem(1, false
        );

        bottomNavigationView.setSelectedItemId(R.id.itWeather);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.itSetting);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.itWeather);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.itForecast);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.itCulture);
                }
                super.onPageSelected(position);
            }
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itSetting:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.itWeather:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.itForecast:
                        viewPager2.setCurrentItem(2);
                        break;
                    case R.id.itCulture:
                        viewPager2.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });

        if (sharedPreferences.getBoolean("is_first_launch", true)){
            editor = sharedPreferences.edit();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.is_first_launch_title));
            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.putBoolean("is_first_launch", false);
                    editor.apply();
                    editor.commit();
                    return;
                }
            });
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.putBoolean("is_first_launch", false);
                    editor.apply();
                    editor.commit();
                    viewPager2.setCurrentItem(0, true);
                    return;
                }
            });
            builder.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    editor.putBoolean("is_first_launch", false);
                    editor.apply();
                    editor.commit();
                }
            });
            builder.show();
        }


    }
}