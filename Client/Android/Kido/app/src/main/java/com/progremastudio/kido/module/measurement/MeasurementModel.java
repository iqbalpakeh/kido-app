/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.measurement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.progremastudio.kido.models.BaseActivity;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;

import java.io.File;

public class MeasurementModel extends BaseActivity {

    private float height;
    private float weight;
    private float head;
    private String picture;

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHead() {
        return head;
    }

    public void setHead(float head) {
        this.head = head;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public void insert(Context context) {
        ContentValues values = new ContentValues();
        values.put(Contract.Measurement.BABY_ID, getBabyID());
        values.put(Contract.Measurement.FAMILY_ID, getFamilyId());
        values.put(Contract.Measurement.TIMESTAMP, getStringTimeStamp());
        values.put(Contract.Measurement.HEIGHT, getHeight());
        values.put(Contract.Measurement.WEIGHT, getWeight());
        values.put(Contract.Measurement.HEAD, getHead());
        values.put(Contract.Measurement.PICTURE, getPicture());
        context.getContentResolver().insert(Contract.Measurement.CONTENT_URI, values);
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
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        context.getContentResolver().delete(
                Contract.Measurement.CONTENT_URI,
                "baby_id = ? AND activity_id = ?",
                selectionArgs);
    }

    @Override
    public void edit(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())};
        ContentValues values = new ContentValues();
        values.put(Contract.Measurement.HEIGHT, getHeight());
        values.put(Contract.Measurement.WEIGHT, getWeight());
        values.put(Contract.Measurement.HEAD, getHead());
        values.put(Contract.Measurement.PICTURE, getPicture());
        context.getContentResolver().update(Contract.Measurement.CONTENT_URI, values,
                "baby_id = ? AND activity_id = ?", selectionArgs);
    }

    @Override
    public void httpPost(Context context) {

    }

    public static MeasurementModel getMeasurementModel(Context context, String id) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                id
        };
        Cursor cursor = context.getContentResolver().query(
                Contract.Measurement.CONTENT_URI,
                Contract.Measurement.Query.PROJECTION,
                "baby_id = ? AND activity_id = ?",
                selectionArgs,
                Contract.Measurement.Query.SORT_BY_TIMESTAMP_DESC
        );
        cursor.moveToFirst();
        MeasurementModel measurementModel = new MeasurementModel();
        measurementModel.setActivityId(Long.valueOf(id));
        measurementModel.setFamilyId(cursor.getString(Contract.Measurement.Query.OFFSET_FAMILY_ID));
        measurementModel.setTimeStamp(cursor.getString(Contract.Measurement.Query.OFFSET_TIMESTAMP));
        measurementModel.setHeight(cursor.getFloat(Contract.Measurement.Query.OFFSET_HEIGHT));
        measurementModel.setWeight(cursor.getFloat(Contract.Measurement.Query.OFFSET_WEIGHT));
        measurementModel.setHead(cursor.getFloat(Contract.Measurement.Query.OFFSET_HEAD));
        measurementModel.setPicture(cursor.getString(Contract.Measurement.Query.OFFSET_PICTURE));
        return measurementModel;
    }
}
