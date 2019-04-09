/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.sleep;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;

import com.progremastudio.kido.models.BaseActivity;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.TextFormation;

import java.util.concurrent.TimeUnit;

public class SleepModel extends BaseActivity {

    private long duration;
    private SleepType type;

    public SleepModel() {
    }

    public SleepModel(Parcel parcel) {
        readFromParcel(parcel);
    }

    public SleepType getType() {
        return type;
    }

    public void setType(SleepType type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(duration);
    }

    public void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        duration = parcel.readLong();
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public void insert(Context context) {
        String day = TextFormation.date(context, getStringTimeStamp());
        Long yesterdayMilisLong = Long.valueOf(getStringTimeStamp()) - TimeUnit.DAYS.toMillis(1);
        String yesterdayMilisString = String.valueOf(yesterdayMilisLong);
        if(!day.equals(ActiveContext.getDayFilter(context))) {
            ContentValues values = new ContentValues();
            values.put(Contract.Sleep.BABY_ID, getBabyID());
            values.put(Contract.Sleep.FAMILY_ID, getFamilyId());
            values.put(Contract.Sleep.TIMESTAMP, yesterdayMilisString);
            values.put(Contract.Sleep.DURATION, 0);
            values.put(Contract.Sleep.TYPE, SleepType.SUB_HEADER.getTitle());
            context.getContentResolver().insert(Contract.Sleep.CONTENT_URI, values);
        }
        ContentValues values = new ContentValues();
        values.put(Contract.Sleep.BABY_ID, getBabyID());
        values.put(Contract.Sleep.FAMILY_ID, getFamilyId());
        values.put(Contract.Sleep.TIMESTAMP, getStringTimeStamp());
        values.put(Contract.Sleep.DURATION, getDuration());
        values.put(Contract.Sleep.TYPE, getType().getTitle());
        context.getContentResolver().insert(Contract.Sleep.CONTENT_URI, values);
    }

    @Override
    public void delete(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        context.getContentResolver().delete(
                Contract.Sleep.CONTENT_URI,
                "baby_id = ? AND activity_id = ?",
                selectionArgs);
    }

    @Override
    public void edit(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        ContentValues values = new ContentValues();
        values.put(Contract.Sleep.DURATION, getDuration());
        values.put(Contract.Sleep.TYPE, getType().getTitle());
        context.getContentResolver().update(Contract.Sleep.CONTENT_URI, values,
                "baby_id = ? AND activity_id = ?", selectionArgs);
    }

    @Override
    public void httpPost(Context context) {

    }

    public enum SleepType {
        DAY("DAY"),
        NIGHT("NIGHT"),
        SUB_HEADER("SUB_HEADER");
        private String title;

        SleepType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }
}
