/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.vaccine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.progremastudio.kido.models.BaseActivity;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.widget.AlarmManagerHelper;

public class VaccineModel extends BaseActivity {

    private String name;
    private String location;
    private String reminder;
    private String notes;

    public VaccineModel() {

    }

    public VaccineModel(Parcel parcel) {
        readFromParcel(parcel);
    }

    public VaccineModel(Cursor cursor) {
        readFromCursor(cursor);
    }

    public static final Parcelable.Creator<VaccineModel> CREATOR = new Parcelable.Creator<VaccineModel>() {
        @Override
        public VaccineModel createFromParcel(Parcel source) {
            return new VaccineModel(source);
        }

        @Override
        public VaccineModel[] newArray(int size) {
            return new VaccineModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(name);
        parcel.writeString(location);
        parcel.writeString(reminder);
        parcel.writeString(notes);
    }

    @Override
    public void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        name = parcel.readString();
        location = parcel.readString();
        reminder = parcel.readString();
        notes = parcel.readString();
    }

    public void readFromCursor(Cursor cursor) {
        activityId = Long.valueOf(cursor.getString(Contract.Vaccine.Query.OFFSET_ACTIVITY_ID));
        babyID = Long.valueOf(cursor.getString(Contract.Vaccine.Query.OFFSET_BABY_ID));
        familyId = cursor.getString(Contract.Vaccine.Query.OFFSET_FAMILY_ID);
        timeStamp =  cursor.getString(Contract.Vaccine.Query.OFFSET_TIMESTAMP);
        name = cursor.getString(Contract.Vaccine.Query.OFFSET_NAME);
        location = cursor.getString(Contract.Vaccine.Query.OFFSET_LOCATION);
        reminder = cursor.getString(Contract.Vaccine.Query.OFFSET_REMINDER);
        notes = cursor.getString(Contract.Vaccine.Query.OFFSET_NOTES);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public void insert(Context context) {
        ContentValues values = new ContentValues();
        values.put(Contract.Vaccine.BABY_ID, getBabyID());
        values.put(Contract.Vaccine.FAMILY_ID, getFamilyId());
        values.put(Contract.Vaccine.TIMESTAMP, getStringTimeStamp());
        values.put(Contract.Vaccine.NAME, getName());
        values.put(Contract.Vaccine.LOCATION, getLocation());
        values.put(Contract.Vaccine.REMINDER, getReminder());
        values.put(Contract.Vaccine.NOTES, getNotes());
        context.getContentResolver().insert(Contract.Vaccine.CONTENT_URI, values);
    }

    @Override
    public void delete(Context context) {
        deleteAlarm(context);
        deleteDb(context);
    }

    private void deleteAlarm(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())
        };
        Cursor cursor = context.getContentResolver().query(
                Contract.Vaccine.CONTENT_URI,
                Contract.Vaccine.Query.PROJECTION,
                "baby_id = ? AND activity_id = ?",
                selectionArgs,
                Contract.Vaccine.Query.SORT_BY_TIMESTAMP_DESC
        );
        cursor.moveToFirst();
        AlarmManagerHelper.cancelAlarms(context, cursor);
    }

    private void deleteDb(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())
        };
        context.getContentResolver().delete(Contract.Vaccine.CONTENT_URI,
                "baby_id = ? AND activity_id = ?", selectionArgs);
    }

    @Override
    public void edit(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        ContentValues values = new ContentValues();
        values.put(Contract.Vaccine.TIMESTAMP, getStringTimeStamp());
        values.put(Contract.Vaccine.NAME, getName());
        values.put(Contract.Vaccine.LOCATION, getLocation());
        values.put(Contract.Vaccine.REMINDER, getReminder());
        values.put(Contract.Vaccine.NOTES, getNotes());
        context.getContentResolver().update(Contract.Vaccine.CONTENT_URI, values,
                "baby_id = ? AND activity_id = ?", selectionArgs);
    }

    @Override
    public void httpPost(Context context) {

    }

    public static VaccineModel getVaccineModel(Context context, String id) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                id
        };
        Cursor cursor = context.getContentResolver().query(
                Contract.Vaccine.CONTENT_URI,
                Contract.Vaccine.Query.PROJECTION,
                "baby_id = ? AND activity_id = ?",
                selectionArgs,
                Contract.Vaccine.Query.SORT_BY_TIMESTAMP_DESC
        );
        cursor.moveToFirst();
        VaccineModel vaccine = new VaccineModel();
        vaccine.setActivityId(Long.valueOf(id));
        vaccine.setStringTimeStamp(cursor.getString(Contract.Vaccine.Query.OFFSET_TIMESTAMP));
        vaccine.setName(cursor.getString(Contract.Vaccine.Query.OFFSET_NAME));
        vaccine.setLocation(cursor.getString(Contract.Vaccine.Query.OFFSET_LOCATION));
        vaccine.setReminder(cursor.getString(Contract.Vaccine.Query.OFFSET_REMINDER));
        vaccine.setNotes(cursor.getString(Contract.Vaccine.Query.OFFSET_NOTES));
        return vaccine;
    }
}
