/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.disease;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.progremastudio.kido.models.BaseActivity;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;

public class DiseaseModel extends BaseActivity {

    private String name;
    private String symptom;
    private String treatment;
    private String notes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
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
        values.put(Contract.Disease.BABY_ID, getBabyID());
        values.put(Contract.Disease.FAMILY_ID, getFamilyId());
        values.put(Contract.Disease.TIMESTAMP, getStringTimeStamp());
        values.put(Contract.Disease.NAME, getName());
        values.put(Contract.Disease.SYMPTOM, getSymptom());
        values.put(Contract.Disease.TREATMENT, getTreatment());
        values.put(Contract.Disease.NOTES, getNotes());
        context.getContentResolver().insert(Contract.Disease.CONTENT_URI, values);
    }

    @Override
    public void delete(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())
        };
        context.getContentResolver().delete(Contract.Disease.CONTENT_URI,
                "baby_id = ? AND activity_id = ?", selectionArgs);
    }

    @Override
    public void edit(Context context) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                String.valueOf(getActivityId())
        };
        ContentValues values = new ContentValues();
        values.put(Contract.Disease.TIMESTAMP, getStringTimeStamp());
        values.put(Contract.Disease.NAME, getName());
        values.put(Contract.Disease.SYMPTOM, getSymptom());
        values.put(Contract.Disease.TREATMENT, getTreatment());
        values.put(Contract.Disease.NOTES, getNotes());
        context.getContentResolver().update(Contract.Disease.CONTENT_URI, values,
                "baby_id = ? AND activity_id = ?", selectionArgs);
    }

    @Override
    public void httpPost(Context context) {

    }

    public static DiseaseModel getDiseaseModel(Context context, String id) {
        String[] selectionArgs = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                id
        };
        Cursor cursor = context.getContentResolver().query(
                Contract.Disease.CONTENT_URI,
                Contract.Disease.Query.PROJECTION,
                "baby_id = ? AND activity_id = ?",
                selectionArgs,
                Contract.Disease.Query.SORT_BY_TIMESTAMP_DESC
        );
        cursor.moveToFirst();
        DiseaseModel disease = new DiseaseModel();
        disease.setActivityId(Long.valueOf(id));
        disease.setStringTimeStamp(cursor.getString(Contract.Disease.Query.OFFSET_TIMESTAMP));
        disease.setName(cursor.getString(Contract.Disease.Query.OFFSET_NAME));
        disease.setSymptom(cursor.getString(Contract.Disease.Query.OFFSET_SYMPTOM));
        disease.setTreatment(cursor.getString(Contract.Disease.Query.OFFSET_TREATMENT));
        disease.setNotes(cursor.getString(Contract.Disease.Query.OFFSET_NOTES));
        return disease;
    }
}
