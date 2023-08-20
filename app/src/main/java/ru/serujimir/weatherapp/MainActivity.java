package ru.serujimir.weatherapp;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    BottomNavigationView bottomNavigationView;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.viewPager2);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentArrayList.add(new SettingsFragment());
        fragmentArrayList.add(new WeatherFragment());
        fragmentArrayList.add(new ForecastFragment());

        ViewPagerAdapter2 viewPagerAdapter2 = new ViewPagerAdapter2(this,fragmentArrayList);
        viewPager2.setAdapter(viewPagerAdapter2);
        viewPager2.setCurrentItem(1);
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
                }
                return true;
            }
        });
    }
}