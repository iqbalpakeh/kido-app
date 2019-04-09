/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.models;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;

import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.TextFormation;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class Baby extends BaseActor {

    public static final Creator CREATOR = new Creator<Baby>() {
        @Override
        public Baby createFromParcel(Parcel parcel) {
            return new Baby(parcel);
        }

        @Override
        public Baby[] newArray(int size) {
            return new Baby[size];
        }
    };

    private Calendar birthday;
    private Uri picture;

    public Baby() {
    }

    public Baby(Parcel parcel) {
        readFromParcel(parcel);
    }

    public void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        birthday = Calendar.getInstance();
        birthday.setTimeInMillis(Long.valueOf(parcel.readString()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(String.valueOf(birthday.getTimeInMillis()));
    }

    public String getBirthdayInString() {
        return String.valueOf(birthday.getTimeInMillis());
    }

    public String getAgeInReadableFormat(Context context) {
        return TextFormation.age(birthday.getTimeInMillis());
    }

    public String getBirthdayInReadableFormat(Context context) {
        return TextFormation.date(context, getBirthdayInString());
    }

    public Calendar getBirthdayInCalendar() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = Calendar.getInstance();
        this.birthday.setTimeInMillis(Long.valueOf(birthday));
    }

    public void setBirthday(Date dateOfBirth) {
        birthday.setTime(dateOfBirth);
    }

    public Uri getPicture() {
        return picture;
    }

    public void setPicture(Uri picture) {
        this.picture = picture;
    }

    @Override
    public void insert(Context context) {
        User user = ActiveContext.getActiveUser(context);
        ContentValues values = new ContentValues();
        values.put(Contract.Baby.NAME, getName());
        values.put(Contract.Baby.FAMILY_ID, getFamilyId());
        values.put(Contract.Baby.BIRTHDAY, getBirthdayInString());
        values.put(Contract.Baby.SEX, getSex().getTitle());
        values.put(Contract.Baby.PICTURE, getPicture().toString());
        values.put(Contract.UserBabyMap.USER_ID, user.getActivityId());
        context.getContentResolver().insert(Contract.Baby.CONTENT_URI, values);
    }

    @Override
    public void delete(Context context) {
        deleteThumbnail();
        deleteDb(context);
    }

    private void deleteThumbnail() {
        String thumbnailName = getPicture().toString();
        File tempFile = new File(thumbnailName.substring(6)); // to remove "file://" from string
        if (tempFile.exists()) tempFile.delete();
    }

    private void deleteDb(Context context) {
        String[] selectionArgs = {String.valueOf(getName())};
        context.getContentResolver().delete(
                Contract.Baby.CONTENT_URI,
                "name = ?",
                selectionArgs);
    }

    @Override
    public void edit(Context context) {
        String[] selectionArgs = {String.valueOf(ActiveContext.getActiveBaby(context).getName())};
        ContentValues values = new ContentValues();
        values.put(Contract.Baby.NAME, getName());
        values.put(Contract.Baby.FAMILY_ID, getFamilyId());
        values.put(Contract.Baby.BIRTHDAY, getBirthdayInString());
        values.put(Contract.Baby.SEX, getSex().getTitle());
        values.put(Contract.Baby.PICTURE, getPicture().toString());
        context.getContentResolver().update(Contract.Baby.CONTENT_URI, values, "name = ?", selectionArgs);
    }

    @Override
    public void httpPost(Context context) {

    }
}
