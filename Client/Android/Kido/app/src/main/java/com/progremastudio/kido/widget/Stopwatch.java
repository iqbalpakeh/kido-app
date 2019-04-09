/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

import java.util.concurrent.TimeUnit;

public class Stopwatch extends Chronometer {

    private long miliSeconds;
    private long timeWhenStop;

    public Stopwatch(Context context) {
        super(context);
        miliSeconds = 0;
        timeWhenStop = 0;
    }

    public Stopwatch(Context context, AttributeSet attrs) {
        super(context, attrs);
        miliSeconds = 0;
        timeWhenStop = 0;
    }

    public Stopwatch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        miliSeconds = 0;
        timeWhenStop = 0;
    }

    public long getDuration() {
        String value;
        String[] parts;
        long seconds, minutes, hours;
        seconds = 0;
        minutes = 0;
        hours = 0;
        value = super.getText().toString();
        parts = value.split(":");
        if (parts.length < 2 || parts.length > 3) return 0; // wrong format checking
        if (parts.length == 2) {
            seconds = Integer.parseInt(parts[1]);
            minutes = Integer.parseInt(parts[0]);
        } else if (parts.length == 3) {
            seconds = Integer.parseInt(parts[2]);
            minutes = Integer.parseInt(parts[1]);
            hours = Integer.parseInt(parts[0]);
        }
        return TimeUnit.SECONDS.toMillis(seconds + (minutes * 60) + (hours * 3600));
    }

    @Override
    public void start() {
        super.setBase(SystemClock.elapsedRealtime() + timeWhenStop);
        super.start();
    }

    @Override
    public void stop() {
        timeWhenStop = super.getBase() - SystemClock.elapsedRealtime();
        super.stop();
    }

    public void reset() {
        super.setBase(SystemClock.elapsedRealtime());
        timeWhenStop = 0;
        super.start();
    }
}
