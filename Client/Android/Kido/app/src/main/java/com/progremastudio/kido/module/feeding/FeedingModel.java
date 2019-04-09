/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.feeding;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.progremastudio.kido.models.BaseActivity;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.TextFormation;

import java.util.concurrent.TimeUnit;

public class FeedingModel extends BaseActivity {

    private long duration = 0;
    private FeedingType type;
    private float volume = 0;
    private float pump = 0;
    private String name = ""; // String type has to be initialize, otherwise the SQL will be failed!!

    public FeedingModel() {

    }

    public FeedingModel(Parcel parcel) {
        readFromParcel(parcel);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public FeedingType getType() {
        return type;
    }

    public void setType(FeedingType type) {
        this.type = type;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPump() {
        return pump;
    }

    public void setPump(float pump) {
        this.pump = pump;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final Parcelable.Creator<FeedingModel> CREATOR = new Parcelable.Creator<FeedingModel>() {
        @Override
        public FeedingModel createFromParcel(Parcel source) {
            return new FeedingModel(source);
        }

        @Override
        public FeedingModel[] newArray(int size) {
            return new FeedingModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(duration);
        parcel.writeString(type.getTitle());
        parcel.writeFloat(volume);
        parcel.writeFloat(pump);
        parcel.writeString(name);
    }

    @Override
    public void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        duration = parcel.readLong();
        String sType = parcel.readString();
        if (FeedingType.LEFT.getTitle().equals(sType)) type = FeedingType.LEFT;
        if (FeedingType.RIGHT.getTitle().equals(sType)) type = FeedingType.RIGHT;
        if (FeedingType.PUMP.getTitle().equals(sType)) type = FeedingType.PUMP;
        if (FeedingType.FORMULA.getTitle().equals(sType)) type = FeedingType.FORMULA;
        if (FeedingType.SOLID.getTitle().equals(sType)) type = FeedingType.SOLID;
        volume = parcel.readFloat();
        pump = parcel.readFloat();
        name = parcel.readString();
    }

    @Override
    public void insert(Context context) {
        String day = TextFormation.date(context, getStringTimeStamp());
        Long yesterdayMilisLong = Long.valueOf(getStringTimeStamp()) - TimeUnit.DAYS.toMillis(1);
        String yesterdayMilisString = String.valueOf(yesterdayMilisLong);
        if (!day.equals(ActiveContext.getDayFilter(context))) {
            ActiveContext.setDayFilter(context, day);
            ContentValues values = new ContentValues();
            values.put(Contract.Feeding.BABY_ID, getBabyID());
            values.put(Contract.Feeding.FAMILY_ID, getFamilyId());
            values.put(Contract.Feeding.TIMESTAMP, yesterdayMilisString);
            values.put(Contract.Feeding.SIDES, FeedingType.SUB_HEADER.getTitle());
            values.put(Contract.Feeding.DURATION, 0L);
            values.put(Contract.Feeding.VOLUME, 0.0f);
            values.put(Contract.Feeding.NAME, "");
            values.put(Contract.Feeding.PUMP, 0.0f);
            context.getContentResolver().insert(Contract.Feeding.CONTENT_URI, values);
        }
        ContentValues values = new ContentValues();
        values.put(Contract.Feeding.BABY_ID, getBabyID());
        values.put(Contract.Feeding.FAMILY_ID, getFamilyId());
        values.put(Contract.Feeding.TIMESTAMP, getStringTimeStamp());
        values.put(Contract.Feeding.SIDES, getType().getTitle());
        values.put(Contract.Feeding.DURATION, getDuration());
        values.put(Contract.Feeding.VOLUME, getVolume());
        values.put(Contract.Feeding.NAME, getName());
        values.put(Contract.Feeding.PUMP, getPump());
        context.getContentResolver().insert(Contract.Feeding.CONTENT_URI, values);
    }

    @Override
    public void delete(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        context.getContentResolver().delete(
                Contract.Feeding.CONTENT_URI,
                "baby_id = ? AND activity_id = ?",
                selectionArgs);
    }

    @Override
    public void edit(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        ContentValues values = new ContentValues();
        values.put(Contract.Feeding.DURATION, getDuration());
        values.put(Contract.Feeding.SIDES, getType().getTitle());
        if (getType().getTitle().equals(FeedingType.FORMULA.getTitle())) {
            values.put(Contract.Feeding.VOLUME, getVolume());
        }
        if (getType().getTitle().equals(FeedingType.PUMP.getTitle())) {
            values.put(Contract.Feeding.PUMP, getPump());
        }
        if (getType().getTitle().equals(FeedingType.SOLID.getTitle())) {
            values.put(Contract.Feeding.VOLUME, getVolume());
            values.put(Contract.Feeding.NAME, getName());
        }
        context.getContentResolver().update(Contract.Feeding.CONTENT_URI, values,
                "baby_id = ? AND activity_id = ?", selectionArgs);
    }

    @Override
    public void httpPost(Context context) {

    }

    public enum FeedingType {
        LEFT("LEFT"),
        RIGHT("RIGHT"),
        PUMP("PUMP"),
        FORMULA("FORMULA"),
        SOLID("SOLID"),
        SUB_HEADER("SUB_HEADER");

        private String title;

        FeedingType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }
}