package com.example.prayerapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class HijriCalendarView extends LinearLayout {

    //for Logging
    private static final String LOGTAG = "Calendar View";

    //how many days to show, default to 6 weeks, 42 days
    private static final int DAYS_COUNT = 35;

    //default date format
    private static final String DATE_FORMAT = "MMMM y";

    //date format
    private String dateFormat;

    //current displayed month
    private UmmalquraCalendar mUmmalquraCalendar = new UmmalquraCalendar();
    private UmmalquraCalendar currentHijriCalendar = new UmmalquraCalendar(mUmmalquraCalendar.get(Calendar.YEAR), mUmmalquraCalendar.get(Calendar.MONTH),
            mUmmalquraCalendar.get(Calendar.DAY_OF_MONTH));

    //for testing
    private int date = mUmmalquraCalendar.get(Calendar.DAY_OF_MONTH);
    private int month = mUmmalquraCalendar.get(Calendar.MONTH);
    private int year = mUmmalquraCalendar.get(Calendar.YEAR);

    SimpleDateFormat dateFormat1 = new SimpleDateFormat("", Locale.ENGLISH);


    //event handling


    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    public HijriCalendarView(Context context) {
        super(context);
    }

    public HijriCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar_hijri, this);
        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();
        Log.d("umday:", String.valueOf(mUmmalquraCalendar));
        Log.d("ummonth:", String.valueOf(month));
        Log.d("umyear:", String.valueOf(year));

        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.f);

        try {
            //try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.f_test);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;

        } finally {
            ta.recycle();
        }
    }

    private void assignUiElements() {
        //layout is inflated, assign local variables to components
        header = (LinearLayout) findViewById(R.id.calender_header_hijri);
        btnPrev = (ImageView) findViewById(R.id.calendar_prev_button_hijri);
        btnNext = (ImageView) findViewById(R.id.calendar_next_button_hijri);
        txtDate = (TextView) findViewById(R.id.calendar_date_display_hijri);
        grid = (GridView) findViewById(R.id.calendar_grid_hijri);
    }

    private void assignClickHandlers() {

        //add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                currentHijriCalendar.add(Calendar.MONTH, 1);

                updateCalendar();
            }
        });

        //subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentHijriCalendar.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar() {

        updateCalendar(null);
    }

    /**
     * Display dates correctly in grid
     */

    public void updateCalendar(HashSet<Date> events) {

        Calendar date = Calendar.getInstance();

        ArrayList<String> cells = new ArrayList<>();

        UmmalquraCalendar calendar = (UmmalquraCalendar) currentHijriCalendar.clone();
        //determine the cell for your current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        //move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);
        Log.d("umCal:", String.valueOf(mUmmalquraCalendar));
        Log.d("umGetTime:", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        //fill cells

        while (cells.size() < DAYS_COUNT) {
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("", Locale.ENGLISH);
            dateFormat1.setCalendar(calendar);
            dateFormat1.applyPattern("d");
            cells.add(dateFormat1.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        //update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells));
        calendar.add(Calendar.MONTH, 3);
        String now = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + calendar.get(Calendar.YEAR);
        txtDate.setText(now);

    }


    private class CalendarAdapter extends ArrayAdapter<String> {


        //for view inflation
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<String> days) {
            super(context, R.layout.control_calendr_day, days);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            //day in question
            String date = getItem(position);


            //today
            UmmalquraCalendar today = new UmmalquraCalendar();
            String todayDate = String.valueOf(today.get(Calendar.DAY_OF_MONTH));
            if (view == null)
                view = inflater.inflate(R.layout.control_calendr_day, parent, false);


            //clear styling
            ((TextView) view).setTypeface(null, Typeface.NORMAL);
            ((TextView) view).setTextColor(Color.BLACK);



            if (date != null && date.equals(todayDate)) {
                //if it is today, set it to green/bold
                ((TextView) view).setTypeface(null, Typeface.BOLD);
                ((TextView) view).setTextColor(getResources().getColor(R.color.green));
            }

            //set text
            ((TextView) view).setText(String.valueOf(date));
            return view;
        }


    }
}
