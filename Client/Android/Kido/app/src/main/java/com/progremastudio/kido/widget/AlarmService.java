/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.progremastudio.kido.core.ActivityChild;
import com.progremastudio.kido.module.vaccine.VaccineModel;

public class AlarmService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            VaccineModel entry = intent.getParcelableExtra("ENTRY");
            Intent alarmIntent = new Intent(this, ActivityChild.class);
            alarmIntent.putExtra("FRAGMENT", "ALARM");
            alarmIntent.putExtra("ENTRY", entry);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(alarmIntent);
            AlarmManagerHelper.setAlarms(this);
        } catch (NullPointerException error) {
            //do nothing
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
