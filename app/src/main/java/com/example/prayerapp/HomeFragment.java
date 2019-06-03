package com.example.prayerapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;


import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    TextView cityNameTv, todayDate, hijriDate, prayerTimeTv, nextPrayerTimeTv;
    ImageView updateLocation;
    String countryName = "Uzbekistan", cityName = "Tashkent";//Default values to keep prayer calculations happy.
    TextView currentPrayerName;
    private RequestQueue mQueue;
    String fajrTime, sunRiseTime, dhuhrTime, asrTime, maghribTime, ishaTime;
    String currentDate, currentTimeString, islamicDate;
    Date currentTime;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        cityNameTv = (TextView) v.findViewById(R.id.location_name);
        updateLocation = (ImageView) v.findViewById(R.id.update_location);
        todayDate = (TextView) v.findViewById(R.id.home_greg_time);
        prayerTimeTv = (TextView) v.findViewById(R.id.home_prayer_time);
        nextPrayerTimeTv = (TextView) v.findViewById(R.id.home_next_prayer_time);
        currentPrayerName = (TextView) v.findViewById(R.id.home_current_prayer_name);
        hijriDate = (TextView) v.findViewById(R.id.home_hijri_time);



        //defalut values before location update
        mQueue = Volley.newRequestQueue(getActivity());
        jsonParse();
        /*Set Gregorian date*/
        currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        todayDate.setText(currentDate);

        /*Set Islamic date*/
        Locale en = Locale.ENGLISH;
        UmmalquraCalendar islamicCal = new UmmalquraCalendar(en);
        islamicDate = islamicCal.get(Calendar.DAY_OF_MONTH) + " " + islamicCal.getDisplayName(Calendar.MONTH, Calendar.LONG, en) + " " + islamicCal.get(Calendar.YEAR);
        hijriDate.setText(islamicDate);

        /*Get current local time*/
        currentTimeString = new SimpleDateFormat("HH:mm ", Locale.getDefault()).format(Calendar.getInstance().getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        try {
            currentTime = formatter.parse(currentTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        /*Update location*/
        updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });


        return v;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1000: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


                    try {
                        cityName = hereLocation(location.getLatitude(), location.getLongitude());
                        cityNameTv.setText(cityName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private String hereLocation(double lat, double lon) {

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 10);
            if (addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getLocality() != null && adr.getLocality().length() > 0) {
                        cityName = adr.getLocality();
                        countryName = adr.getCountryName();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    /*Gets json values and updates prayer timings*/
    private void jsonParse() {


        String url = "http://api.aladhan.com/v1/timingsByCity?city=" + cityName + "&country=" + countryName + "&method=1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonArray = response.getJSONObject("data");
                            JSONObject timings = jsonArray.getJSONObject("timings");
                            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

                            fajrTime = timings.getString("Fajr");
                            sunRiseTime = timings.getString("Sunrise");
                            dhuhrTime = timings.getString("Dhuhr");
                            asrTime = timings.getString("Asr");
                            maghribTime = timings.getString("Maghrib");
                            ishaTime = timings.getString("Isha");

                            Date fajr, sunRise, dhuhr, asr, maghrib, isha;

                            fajr = formatter.parse(fajrTime);
                            sunRise = formatter.parse(dhuhrTime);
                            dhuhr = formatter.parse(sunRiseTime);
                            asr = formatter.parse(asrTime);
                            maghrib = formatter.parse(maghribTime);
                            isha = formatter.parse(ishaTime);

                            if (currentTime.equals(fajr) || currentTime.after(fajr) && currentTime.before(sunRise)) {
                                prayerTimeTv.setText(fajrTime);
                                currentPrayerName.setText("Fajr");
                                prayerTimeTv.append("\nSunrise " + sunRiseTime);
                                nextPrayerTimeTv.setText("Dhuhr ");
                                nextPrayerTimeTv.append(dhuhrTime);
                            } else if (currentTime.equals(dhuhr) || currentTime.after(dhuhr) && currentTime.before(asr)) {
                                prayerTimeTv.setText(dhuhrTime);
                                currentPrayerName.setText("Dhuhr");
                                nextPrayerTimeTv.setText("Asr ");
                                nextPrayerTimeTv.append(asrTime);
                            } else if (currentTime.equals(asr) || currentTime.after(asr) && currentTime.before(maghrib)) {
                                prayerTimeTv.setText(asrTime);
                                currentPrayerName.setText("Asr");
                                nextPrayerTimeTv.setText("Maghrib ");
                                nextPrayerTimeTv.append(maghribTime);
                            } else if (currentTime.equals(maghrib) || currentTime.after(maghrib) && currentTime.before(isha)) {
                                prayerTimeTv.setText(maghribTime);
                                currentPrayerName.setText("Maghrib");
                                nextPrayerTimeTv.setText("Isha ");
                                nextPrayerTimeTv.append(ishaTime);
                            } else {
                                prayerTimeTv.setText(ishaTime);
                                currentPrayerName.setText("Isha");
                                nextPrayerTimeTv.setText("Fajr ");
                                nextPrayerTimeTv.append(fajrTime);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }



    /*Get location */
    public void getLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            try {
                //update city and current values
                cityName = hereLocation(location.getLatitude(), location.getLongitude());
                cityNameTv.setText(cityName + ", " + countryName);

                /*Update prayer timings using api*/
                mQueue = Volley.newRequestQueue(getActivity());
                jsonParse();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Please check location settings and enable network location access" +
                        "", Toast.LENGTH_SHORT).show();
            }
        }


    }
}

