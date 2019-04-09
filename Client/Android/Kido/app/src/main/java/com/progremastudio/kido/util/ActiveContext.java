/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.progremastudio.kido.R;
import com.progremastudio.kido.models.Baby;
import com.progremastudio.kido.models.BaseActor;
import com.progremastudio.kido.models.User;
import com.progremastudio.kido.provider.Contract;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ActiveContext {

    public static final int FOR_TODAY = 0;
    public static final int FOR_THIS_WEEK = 1;
    public static final int FOR_THIS_MONTH = 2;
    public static final int FOR_ALL = 3;

    private static final String PREF_CONTEXT = "prefContext";
    private static final String PREF_BABY_ID = "prefBabyId";
    private static final String PREF_BABY_NAME = "prefBabyName";
    private static final String PREF_BABY_FAMILY_ID = "prefFamilyId";
    private static final String PREF_BABY_BIRTHDAY = "prefBabyBirthday";
    private static final String PREF_BABY_SEX = "prefBabySex";
    private static final String PREF_BABY_PICTURE = "prefBabyPicture";
    private static final String PREF_USER_ID = "prefUserId";
    private static final String PREF_USER_NAME = "prefUserName";
    private static final String PREF_USER_FAMILY_ID = "prefUserFamilyId";
    private static final String PREF_USER_ACCESS_TOKEN = "prefUserAccessToken";
    private static final String PREF_USER_LOGIN_TYPE = "prefUserLoginType";
    private static final String PREF_CURRENT_FRAGMENT = "prefCurrentFragment";
    private static final String PREF_DAY_FILTER = "prefDayFilter";

    public static void setDayFilter(Context context, String day) {
        SharedPreferences setting = context.getSharedPreferences(PREF_CONTEXT, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(PREF_DAY_FILTER, day);
        editor.commit();
    }

    public static String getDayFilter(Context context) {
        SharedPreferences setting = context.getSharedPreferences(PREF_CONTEXT, 0);
        return setting.getString(PREF_DAY_FILTER, "");
    }

    public static void setCurrentFragment(Context context, String currentFragment) {
        SharedPreferences setting = context.getSharedPreferences(PREF_CONTEXT, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(PREF_CURRENT_FRAGMENT, currentFragment);
        editor.commit();
    }

    public static String getCurrentFragment(Context context) {
        SharedPreferences setting = context.getSharedPreferences(PREF_CONTEXT, 0);
        return setting.getString(PREF_CURRENT_FRAGMENT, "");
    }

    public static void clearCurrentFragment(Context context) {
        SharedPreferences setting = context.getSharedPreferences(PREF_CONTEXT, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(PREF_CURRENT_FRAGMENT, "");
        editor.commit();
    }

    public static void setActiveBaby(Context context, String babyName) {
        Cursor cursor = babyQueryByName(context, babyName);
        SharedPreferences setting = context.getSharedPreferences(PREF_CONTEXT, 0);
        SharedPreferences.Editor editor = setting.edit();
        cursor.moveToFirst();
        editor.putLong(PREF_BABY_ID, cursor.getLong(Contract.Baby.Query.OFFSET_ID));
        editor.putString(PREF_BABY_NAME, cursor.getString(Contract.Baby.Query.OFFSET_NAME));
        editor.putString(PREF_BABY_FAMILY_ID, cursor.getString(Contract.Baby.Query.OFFSET_FAMILY_ID));
        editor.putString(PREF_BABY_BIRTHDAY, cursor.getString(Contract.Baby.Query.OFFSET_BIRTHDAY));
        editor.putString(PREF_BABY_SEX, cursor.getString(Contract.Baby.Query.OFFSET_SEX));
        editor.putString(PREF_BABY_PICTURE, cursor.getString(Contract.Baby.Query.OFFSET_PICTURE));
        editor.commit();
    }

    public static Baby getActiveBaby(Context context) {
        SharedPreferences setting = context.getSharedPreferences(PREF_CONTEXT, 0);
        Baby baby = new Baby();
        baby.setActivityId(setting.getLong(PREF_BABY_ID, 0));
        baby.setName(setting.getString(PREF_BABY_NAME, ""));
        baby.setFamilyId(setting.getString(PREF_BABY_FAMILY_ID, ""));
        baby.setBirthday(setting.getString(PREF_BABY_BIRTHDAY, "0"));
        baby.setPicture(Uri.parse(setting.getString(PREF_BABY_PICTURE, "")));
        if (setting.getString(PREF_BABY_SEX, "").equals(BaseActor.Sex.MALE.getTitle())) {
            baby.setSex(BaseActor.Sex.MALE);
        } else if (setting.getString(PREF_BABY_SEX, "").equals(BaseActor.Sex.FEMALE.getTitle())) {
            baby.setSex(BaseActor.Sex.FEMALE);
        }
        return baby;
    }

    public static boolean isBabyCreated(Context context) {
        Cursor cursor = babyQueryAll(context);
        if(cursor.getCount() > 0) return true;
        else return false;
    }

    public static void setActiveUser(Context context, User user) {
        SharedPreferences setting = context.getSharedPreferences(PREF_CONTEXT, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putLong(PREF_USER_ID, user.getActivityId());
        editor.putString(PREF_USER_NAME, user.getName());
        editor.putString(PREF_USER_FAMILY_ID, user.getFamilyId());
        editor.putString(PREF_USER_ACCESS_TOKEN, user.getAccessToken());
        editor.putString(PREF_USER_LOGIN_TYPE, user.getLoginType());
        editor.commit();
    }

    public static User getActiveUser(Context context) {
        SharedPreferences setting = context.getSharedPreferences(PREF_CONTEXT, 0);
        User user = new User();
        user.setActivityId(setting.getLong(PREF_USER_ID, 0));
        user.setName(setting.getString(PREF_USER_NAME, ""));
        user.setFamilyId(setting.getString(PREF_USER_FAMILY_ID, ""));
        user.setAccessToken(setting.getString(PREF_USER_ACCESS_TOKEN, ""));
        user.setLoginType(setting.getString(PREF_USER_LOGIN_TYPE, ""));
        return user;
    }

    private static Cursor babyQueryByName(Context context, String babyName) {
        String[] selectionArgument = {babyName};
        return context.getContentResolver().query(
                Contract.Baby.CONTENT_URI,
                Contract.Baby.Query.PROJECTION,
                Contract.Baby.NAME + "=?",
                selectionArgument,
                Contract.Baby.NAME);
    }

    private static Cursor babyQueryAll(Context context) {
        String[] selectionArgument = {};
        return context.getContentResolver().query(
                Contract.Baby.CONTENT_URI,
                Contract.Baby.Query.PROJECTION,
                "",
                selectionArgument,
                Contract.Baby.NAME
                ) ;
    }

    public static int getPreferenceTimeFilter(Context context, String preferenceName) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sharedPref.getString(preferenceName, "");
        if (context.getString(R.string.str_for_today).equals(value)) {
            return FOR_TODAY;
        } else if (context.getString(R.string.str_for_this_week).equals(value)) {
            return FOR_THIS_WEEK;
        } else if (context.getString(R.string.str_for_this_month).equals(value)) {
            return FOR_THIS_MONTH;
        } else {
            return  FOR_ALL;
        }
    }

    public static String[] createTimeFilter(Context context, int position) {
        /**
         * as stated here: http://developer.android.com/reference/java/util/Calendar.html
         * 24:00:00 "belongs" to the following day.
         * That is, 23:59 on Dec 31, 1969 < 24:00 on Jan 1, 1970 < 24:01:00 on Jan 1, 1970
         * form a sequence of three consecutive minutes in time.
         */
        long oneWeekDuration = TimeUnit.DAYS.toMillis(7);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        String startTime;
        switch (position) {
            case FOR_TODAY:
                break;
            case FOR_THIS_MONTH:
                startCalendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case FOR_ALL:
                startCalendar.set(Calendar.YEAR, 1);
                break;
            case FOR_THIS_WEEK:
            default:
                startCalendar.setTimeInMillis(startCalendar.getTimeInMillis() - oneWeekDuration);
                break;
        }
        startTime = String.valueOf(startCalendar.getTimeInMillis());
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 0);
        String endTime = String.valueOf(endCalendar.getTimeInMillis());
        String[] timeFilter = {
                String.valueOf(getActiveBaby(context).getActivityId()),
                startTime,
                endTime
        };
        return timeFilter;
    }
}
