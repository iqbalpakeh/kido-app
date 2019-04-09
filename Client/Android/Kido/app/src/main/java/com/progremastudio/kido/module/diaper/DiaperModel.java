/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.diaper;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;

import com.progremastudio.kido.models.BaseActivity;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.TextFormation;

import java.util.concurrent.TimeUnit;

public class DiaperModel extends BaseActivity {

    private DiaperType type;

    public DiaperType getType() {
        return type;
    }

    public void setType(DiaperType newType) {
        this.type = newType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(type.getTitle());
    }

    @Override
    public void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        type = DiaperType.valueOf(parcel.readString());
    }

    @Override
    public void insert(Context context) {
        String day = TextFormation.date(context, getStringTimeStamp());
        Long yesterdayMilisLong = Long.valueOf(getStringTimeStamp()) - TimeUnit.DAYS.toMillis(1);
        String yesterdayMilisString = String.valueOf(yesterdayMilisLong);
        if (!day.equals(ActiveContext.getDayFilter(context))) {
            ActiveContext.setDayFilter(context, day);
            ContentValues values = new ContentValues();
            values.put(Contract.Diaper.BABY_ID, getBabyID());
            values.put(Contract.Diaper.FAMILY_ID, getFamilyId());
            values.put(Contract.Diaper.TIMESTAMP, yesterdayMilisString);
            values.put(Contract.Diaper.TYPE, DiaperType.SUB_HEADER.getTitle());
            context.getContentResolver().insert(Contract.Diaper.CONTENT_URI, values);
        }
        ContentValues values = new ContentValues();
        values.put(Contract.Diaper.BABY_ID, getBabyID());
        values.put(Contract.Diaper.FAMILY_ID, getFamilyId());
        values.put(Contract.Diaper.TIMESTAMP, getStringTimeStamp());
        values.put(Contract.Diaper.TYPE, getType().getTitle());
        context.getContentResolver().insert(Contract.Diaper.CONTENT_URI, values);
    }

    @Override
    public void delete(Context context) {
        // todo: to check whether sub header need to be delete or not
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        context.getContentResolver().delete(Contract.Diaper.CONTENT_URI,
                "baby_id = ? AND activity_id = ?", selectionArgs);
    }

    @Override
    public void edit(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        ContentValues values = new ContentValues();
        values.put(Contract.Diaper.TYPE, getType().getTitle());
        context.getContentResolver().update(Contract.Diaper.CONTENT_URI, values,
                "baby_id = ? AND activity_id = ?", selectionArgs);
    }

    @Override
    public void httpPost(Context context) {
        // todo push to firebase database
    }

    public enum DiaperType {
        DRY("DRY"),
        WET("WET"),
        MIXED("MIXED"),
        SUB_HEADER("SUB_HEADER");
        private String title;

        DiaperType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }
}
