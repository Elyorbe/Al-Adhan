package com.example.prayerapp;



import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_Navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //To keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }



    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.homeItem:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.qiblaItem:
                            selectedFragment = new QiblaFragment();
                            break;
                        case R.id.mosqueItem:
                            selectedFragment = new MosqueFragment();
                            break;
                        case R.id.calendarItem:
                            selectedFragment = new CalendarFragment();
                            break;
                        case R.id.settingsItem:
                            selectedFragment = new SettingsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

}

