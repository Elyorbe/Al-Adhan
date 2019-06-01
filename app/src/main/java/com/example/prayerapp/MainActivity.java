package com.example.prayerapp;


import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    final Fragment homeFragment = new HomeFragment();
    final Fragment qiblaFragment = new QiblaFragment();
    final Fragment calendarFragment = new CalendarFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        fm.beginTransaction().add(R.id.fragment_container, calendarFragment).hide(calendarFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, qiblaFragment).hide(qiblaFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, homeFragment).commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_Navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.homeItem:
                            fm.beginTransaction().hide(active).show(homeFragment).commit();
                            active = homeFragment;
                            return true;

                        case R.id.qiblaItem:
                            fm.beginTransaction().hide(active).show(qiblaFragment).commit();
                            active = qiblaFragment;
                            return true;

                        case R.id.calendarItem:
                            fm.beginTransaction().hide(active).show(calendarFragment).commit();
                            active = calendarFragment;
                            return true;
                    }
                    return false;



                }
            };

}

