/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.progremastudio.kido.module.vaccine.VaccineModel;
import com.progremastudio.kido.provider.Contract;

import java.util.Calendar;

public class AlarmManagerHelper extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context);
    }

    public static void setAlarms(Context context) {
        Cursor cursor = context.getContentResolver().query(
                Contract.Vaccine.CONTENT_URI,
                Contract.Vaccine.Query.PROJECTION,
                "",
                null,
                null
        );

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            String alarm = cursor.getString(Contract.Vaccine.Query.OFFSET_REMINDER);

            if (!alarm.equals("ALARM_NOT_SET")) {
                cancelAlarms(context, cursor);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(alarm));
                Calendar now = Calendar.getInstance();

                if (now.getTimeInMillis() <= calendar.getTimeInMillis()) {
                    PendingIntent pendingIntent = createPendingIntent(context, cursor);
                    setAlarm(context, calendar, pendingIntent);
                }
            }
        }
    }

    private static void setAlarm(Context context, Calendar calendar, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void cancelAlarms(Context context, Cursor cursor) {
        PendingIntent pendingIntent = createPendingIntent(context, cursor);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private static PendingIntent createPendingIntent(Context context, Cursor cursor) {
        int id = Integer.valueOf(cursor.getString(Contract.Vaccine.Query.OFFSET_ACTIVITY_ID));
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra("ENTRY", new VaccineModel(cursor));
        return PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
