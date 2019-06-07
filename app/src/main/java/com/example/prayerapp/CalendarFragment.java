package com.example.prayerapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Date;
import java.util.HashSet;

public class CalendarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        HashSet<Date> events = new HashSet<>();
        events.add(new Date());

        CalendarView cv1 = ((CalendarView) v.findViewById(R.id.calendar_view));
        cv1.updateCalendar();


        HijriCalendarView cv2 = ((HijriCalendarView) v.findViewById(R.id.calendar_view_hijri));
        cv2.updateCalendar();


        return v;
    }

}
