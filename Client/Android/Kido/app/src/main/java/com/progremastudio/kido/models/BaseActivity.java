/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.models;

import android.os.Parcel;

import java.util.Calendar;

abstract public class BaseActivity extends BaseModel {

    protected long babyID;
    protected String familyId;
    protected String timeStamp;

    public BaseActivity() {
    }

    public BaseActivity(Parcel parcel) {
        readFromParcel(parcel);
    }

    public BaseActivity(long babyID, String newTime) {
        this.babyID = babyID;
        this.timeStamp = newTime;
    }

    public BaseActivity(long babyID, Calendar newTime) {
        this.babyID = babyID;
        this.timeStamp = String.valueOf(newTime.getTimeInMillis());
    }

    public long getBabyID() {
        return babyID;
    }

    public void setBabyID(long babyID) {
        this.babyID = babyID;
    }

    public void setTimeStamp(String newTime) {
        this.timeStamp = newTime;
    }

    public String getStringTimeStamp() {
        return timeStamp;
    }

    public void setStringTimeStamp(String time) {
        timeStamp = time;
    }

    public Calendar getCalendarTimeStamp() {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(Long.parseLong(timeStamp));
        return time;
    }

    public void setCalendarTimeStamp(Calendar newTime) {
        timeStamp = String.valueOf(newTime.getTimeInMillis());
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(babyID);
        parcel.writeString(timeStamp);
    }

    public void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        babyID = parcel.readLong();
        timeStamp = parcel.readString();
    }
}
