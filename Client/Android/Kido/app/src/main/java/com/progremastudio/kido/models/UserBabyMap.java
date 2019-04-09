/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.models;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;

import com.progremastudio.kido.provider.Contract;

public class UserBabyMap extends BaseModel {

    private long userId;
    private long babyId;

    public UserBabyMap() {
    }

    public UserBabyMap(Parcel parcel) {
        readFromParcel(parcel);
    }

    public void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        userId = parcel.readLong();
        babyId = parcel.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(userId);
        parcel.writeLong(babyId);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getBabyId() {
        return babyId;
    }

    public void setBabyId(long babyId) {
        this.babyId = babyId;
    }

    @Override
    public void insert(Context context) {
        ContentValues values = new ContentValues();
        values.put(Contract.UserBabyMap.BABY_ID, getBabyId());
        values.put(Contract.UserBabyMap.USER_ID, getUserId());
        context.getContentResolver().insert(Contract.UserBabyMap.CONTENT_URI, values);
    }

    @Override
    public void delete(Context context) {
    }

    @Override
    public void edit(Context context) {

    }

    @Override
    public void httpPost(Context context) {

    }
}
