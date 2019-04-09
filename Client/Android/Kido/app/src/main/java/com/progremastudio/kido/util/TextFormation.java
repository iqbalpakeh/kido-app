/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.util;

import android.content.Context;
import android.text.format.DateUtils;

import com.progremastudio.kido.R;
import com.squareup.phrase.Phrase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TextFormation {

    private static final String[] DAY_OF_WEEK_SHORT = {
            "Sun",
            "Mon",
            "Tue",
            "Wed",
            "Thur",
            "Fri",
            "Sat"
    };

    private static final String[] MONTH_OF_YEAR_SHORT = {
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
    };

    private static final String[] DAY_OF_WEEK_COMPLETE = {
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday"
    };

    private static final String[] MONTH_OF_YEAR_COMPLETE = {
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };

    public static String timeBoundary(Context context, String startTime, String duration) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(startTime));
        String start = new SimpleDateFormat("HH:mm").format(cal.getTime());
        cal.setTimeInMillis(Long.parseLong(startTime) + Long.parseLong(duration));
        String end = new SimpleDateFormat("HH:mm").format(cal.getTime());
        CharSequence formatted = Phrase.from(context.getResources().getString(R.string.fmt_time_span))
                .put("start", start)
                .put("end", end)
                .format();
        return String.valueOf(formatted);
    }

    public static String duration(Context context, String duration) {
        SimpleTimeFormat time = new SimpleTimeFormat(Long.parseLong(duration));
        CharSequence formatted = Phrase.from(context.getResources().getString(R.string.fmt_duration))
                .put("hour", time.getHour())
                .put("minute", time.getMinute())
                .put("second", time.getSecond())
                .format();
        return String.valueOf(formatted);
    }

    public static String sleepDuration(Context context, String duration) {
        SimpleTimeFormat time = new SimpleTimeFormat(Long.parseLong(duration));
        CharSequence formatted = Phrase.from(context.getResources()
                .getString(R.string.fmt_sleep_duration))
                .put("hour", time.getHour())
                .put("minute", time.getMinute())
                .put("second", time.getSecond())
                .format();
        return String.valueOf(formatted);
    }

    public static String volume(Context context, String volume) {
        CharSequence formatted = Phrase.from(context.getResources().getString(R.string.fmt_volume))
                .put("volume", volume)
                .format();
        return String.valueOf(formatted);
    }

    public static String total(Context context, String values) {
        CharSequence formatted = Phrase.from(context.getResources()
                .getString(R.string.fmt_total_activity_number))
                .put("value", values)
                .format();
        return String.valueOf(formatted);
    }

    public static String totalDiaperUsed(Context context, String values) {
        CharSequence formatted = Phrase.from(context.getResources()
                .getString(R.string.fmt_diaper_used))
                .put("value", values)
                .format();
        return String.valueOf(formatted);
    }

    public static String last(Context context, String numberToday) {
        String value = DateUtils.getRelativeTimeSpanString(Long.parseLong(numberToday)).toString();
        CharSequence formatted = Phrase.from(context.getResources()
                .getString(R.string.fmt_activity_last))
                .put("value", value)
                .format();
        return String.valueOf(formatted);
    }

    public static String age(long birthdayInMilis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birthdayInMilis);
        AgeCalculator age = new AgeCalculator(calendar);
        return age.getFormattedBirthday();
    }

    public static String date(Context context, String timeStamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String day = DAY_OF_WEEK_SHORT[cal.get(Calendar.DAY_OF_WEEK) - 1];
        String date = String.valueOf(cal.get(Calendar.DATE));
        String month = MONTH_OF_YEAR_SHORT[cal.get(Calendar.MONTH)];
        String year = String.valueOf(cal.get(Calendar.YEAR));
        CharSequence formatted = Phrase.from(context.getResources()
                .getString(R.string.fmt_date_complete))
                .put("day", day)
                .put("date", date)
                .put("month", month)
                .put("year", year)
                .format();
        return String.valueOf(formatted);
    }

    public static String dateComplete(Context context, String timeStamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String day = DAY_OF_WEEK_COMPLETE[cal.get(Calendar.DAY_OF_WEEK) - 1];
        String date = String.valueOf(cal.get(Calendar.DATE));
        String month = MONTH_OF_YEAR_COMPLETE[cal.get(Calendar.MONTH)];
        String year = String.valueOf(cal.get(Calendar.YEAR));
        CharSequence formatted = Phrase.from(context.getResources()
                .getString(R.string.fmt_date_complete))
                .put("day", day)
                .put("date", date)
                .put("month", month)
                .put("year", year)
                .format();
        return String.valueOf(formatted);
    }

    public static String time(String timeStamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        return "@ " + new SimpleDateFormat("hh:mm a").format(cal.getTime());
    }

    public static boolean checkValidNumber(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static class SimpleTimeFormat {

        private long second;
        private long minute;
        private long hour;

        public SimpleTimeFormat(long millisecond) {
            second = (millisecond / 1000) % 60;
            minute = (millisecond / (1000 * 60)) % 60;
            hour = (millisecond / (1000 * 60 * 60));
        }

        public String getSecond() {
            return ((second<10) ? ("0" + String.valueOf(second)) : (String.valueOf(second)));
        }

        public String getMinute() {
            return ((minute<10) ? ("0" + String.valueOf(minute)) : (String.valueOf(minute)));
        }

        public String getHour() {
            return ((hour<10) ? ("0" + String.valueOf(hour)) : (String.valueOf(hour)));
        }
    }
}
