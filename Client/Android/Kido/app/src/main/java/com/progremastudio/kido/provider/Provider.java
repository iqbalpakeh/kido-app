/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.progremastudio.kido.util.SelectionBuilder;

public class Provider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private static final int USER = 100;
    private static final int USER_BABY_MAP = 200;
    private static final int BABY = 300;
    private static final int FEEDING = 400;
    private static final int FEEDING_MAX_TIMESTAMP = 401;
    private static final int FEEDING_LAST_TYPE = 402;
    private static final int SLEEP = 500;
    private static final int SLEEP_MAX_TIMESTAMP = 501;
    private static final int DIAPER = 600;
    private static final int DIAPER_MAX_TIMESTAMP = 601;
    private static final int MEASUREMENT = 700;
    private static final int MEASUREMENT_MAX_TIMESTAMP = 701;
    private static final int PHOTO = 800;
    private static final int ACTIVITY = 900;
    private static final int DISEASE = 1000;
    private static final int VACCINE = 1100;
    private Database databaseHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = Contract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "user", USER);
        matcher.addURI(authority, "user_baby_map", USER_BABY_MAP);
        matcher.addURI(authority, "baby", BABY);
        matcher.addURI(authority, "feeding", FEEDING);
        matcher.addURI(authority, "feeding_max_timestamp", FEEDING_MAX_TIMESTAMP);
        matcher.addURI(authority, "feeding_last_side", FEEDING_LAST_TYPE);
        matcher.addURI(authority, "sleep", SLEEP);
        matcher.addURI(authority, "sleep_max_timestamp", SLEEP_MAX_TIMESTAMP);
        matcher.addURI(authority, "diaper", DIAPER);
        matcher.addURI(authority, "diaper_max_timestamp", DIAPER_MAX_TIMESTAMP);
        matcher.addURI(authority, "measurement", MEASUREMENT);
        matcher.addURI(authority, "measurement_max_timestamp", MEASUREMENT_MAX_TIMESTAMP);
        matcher.addURI(authority, "photo", PHOTO);
        matcher.addURI(authority, "activity", ACTIVITY);
        matcher.addURI(authority, "disease", DISEASE);
        matcher.addURI(authority, "vaccine", VACCINE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new Database(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case ACTIVITY:
                cursor = database.rawQuery(Database.JOIN_ALL, selectionArgs);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case FEEDING_MAX_TIMESTAMP:
                cursor = database.rawQuery("SELECT MAX(timestamp) FROM feeding WHERE baby_id = ?", selectionArgs);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case FEEDING_LAST_TYPE:
                cursor = database.rawQuery("SELECT sides FROM feeding WHERE baby_id = ? AND timestamp = (SELECT MAX(timestamp) FROM feeding)", selectionArgs);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case SLEEP_MAX_TIMESTAMP:
                cursor = database.rawQuery("SELECT MAX(timestamp) FROM sleep WHERE baby_id = ?", selectionArgs);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case DIAPER_MAX_TIMESTAMP:
                cursor = database.rawQuery("SELECT MAX(timestamp) FROM diaper WHERE baby_id = ?", selectionArgs);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case MEASUREMENT_MAX_TIMESTAMP:
                cursor = database.rawQuery("SELECT MAX(timestamp) FROM measurement WHERE baby_id = ?", selectionArgs);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            default:
                SelectionBuilder defaultBuilder = buildSelection(match);
                cursor = defaultBuilder.where(selection, selectionArgs).query(database, projection, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {

            case USER:
                // add new user to user table TODO: REMOVE USER TABLE FROM ANDROID CLIENT??
                database.insertOrThrow(Database.Tables.USER, null, contentValues);
                return Contract.User.buildUri(contentValues.getAsString(BaseColumns._ID));

            case BABY:
                // add new baby to baby table
                ContentValues baby = new ContentValues();
                baby.put(Contract.Baby.NAME, contentValues.getAsString(Contract.Baby.NAME));
                baby.put(Contract.Baby.FAMILY_ID, contentValues.getAsString(Contract.Baby.FAMILY_ID));
                baby.put(Contract.Baby.BIRTHDAY, contentValues.getAsString(Contract.Baby.BIRTHDAY));
                baby.put(Contract.Baby.SEX, contentValues.getAsString(Contract.Baby.SEX));
                baby.put(Contract.Baby.PICTURE, contentValues.getAsString(Contract.Baby.PICTURE));
                long babyID = database.insertOrThrow(Database.Tables.BABY, null, baby);
                // add new user-baby map to user-baby map table
                ContentValues userBabyMap = new ContentValues();
                userBabyMap.put(Contract.UserBabyMap.USER_ID, contentValues.getAsLong(Contract.UserBabyMap.USER_ID));
                userBabyMap.put(Contract.UserBabyMap.BABY_ID, babyID);
                database.insertOrThrow(Database.Tables.USER_BABY_MAP, null, userBabyMap);
                return Contract.Baby.buildUri(contentValues.getAsString(BaseColumns._ID));

            case SLEEP:
                // add new sleep activity to activity table
                ContentValues sleep = new ContentValues();
                sleep.put(Contract.ActivityColumns.BABY_ID, contentValues.getAsString(Contract.Activity.BABY_ID));
                sleep.put(Contract.ActivityColumns.FAMILY_ID, contentValues.getAsString(Contract.Activity.FAMILY_ID));
                sleep.put(Contract.ActivityColumns.ACTIVITY_TYPE, Contract.Activity.TYPE_SLEEP);
                sleep.put(Contract.ActivityColumns.TIMESTAMP, contentValues.getAsString(Contract.Activity.TIMESTAMP));
                long sleepActivityId = database.insertOrThrow(Database.Tables.ACTIVITY, null, sleep);
                // add sleep details to sleep table
                contentValues.put(Contract.SleepColumns.ACTIVITY_ID, sleepActivityId);
                database.insertOrThrow(Database.Tables.SLEEP, null, contentValues);
                // notify all observer that subscribe to sleep table and activity table
                notifyChange(uri);
                notifyChange(Contract.Activity.CONTENT_URI);
                return Contract.Sleep.buildUri(contentValues.getAsString(BaseColumns._ID));

            case DIAPER:
                // add new diaper activity to activity table
                ContentValues diaper = new ContentValues();
                diaper.put(Contract.ActivityColumns.BABY_ID, contentValues.getAsString(Contract.Activity.BABY_ID));
                diaper.put(Contract.ActivityColumns.FAMILY_ID, contentValues.getAsString(Contract.Activity.FAMILY_ID));
                diaper.put(Contract.ActivityColumns.ACTIVITY_TYPE, Contract.Activity.TYPE_DIAPER);
                diaper.put(Contract.ActivityColumns.TIMESTAMP, contentValues.getAsString(Contract.Activity.TIMESTAMP));
                long diaperActivityId = database.insertOrThrow(Database.Tables.ACTIVITY, null, diaper);
                // add sleep details to sleep table
                contentValues.put(Contract.DiaperColumns.ACTIVITY_ID, diaperActivityId);
                database.insertOrThrow(Database.Tables.DIAPER, null, contentValues);
                // notify all observer that subscribe to diaper table and activity table
                notifyChange(uri);
                notifyChange(Contract.Activity.CONTENT_URI);
                return Contract.Diaper.buildUri(contentValues.getAsString(BaseColumns._ID));

            case FEEDING:
                // add new feeding activity to activity table
                ContentValues feeding = new ContentValues();
                feeding.put(Contract.ActivityColumns.BABY_ID, contentValues.getAsString(Contract.Activity.BABY_ID));
                feeding.put(Contract.ActivityColumns.FAMILY_ID, contentValues.getAsString(Contract.Activity.FAMILY_ID));
                feeding.put(Contract.ActivityColumns.ACTIVITY_TYPE, Contract.Activity.TYPE_FEEDING);
                feeding.put(Contract.ActivityColumns.TIMESTAMP, contentValues.getAsString(Contract.Activity.TIMESTAMP));
                long feedingActivityId = database.insertOrThrow(Database.Tables.ACTIVITY, null, feeding);
                // add feeding details to feeding table
                contentValues.put(Contract.FeedingColumns.ACTIVITY_ID, feedingActivityId);
                database.insertOrThrow(Database.Tables.FEEDING, null, contentValues);
                // notify all observer that subscribe to diaper table and activity table
                notifyChange(uri);
                notifyChange(Contract.Activity.CONTENT_URI);
                notifyChange(Contract.Feeding.LAST_TYPE);
                //notifyChange(Contract.Feeding.FEEDING_PUMP_VOLUME);
                return Contract.Feeding.buildUri(contentValues.getAsString(BaseColumns._ID));

            case MEASUREMENT:
                // add new measurement activity to activity table
                ContentValues measurement = new ContentValues();
                measurement.put(Contract.ActivityColumns.BABY_ID, contentValues.getAsString(Contract.Activity.BABY_ID));
                measurement.put(Contract.ActivityColumns.FAMILY_ID, contentValues.getAsString(Contract.Activity.FAMILY_ID));
                measurement.put(Contract.ActivityColumns.ACTIVITY_TYPE, Contract.Activity.TYPE_MEASUREMENT);
                measurement.put(Contract.ActivityColumns.TIMESTAMP, contentValues.getAsString(Contract.Activity.TIMESTAMP));
                long measurementActivityId = database.insertOrThrow(Database.Tables.ACTIVITY, null, measurement);
                // add measurement details to measurement table
                contentValues.put(Contract.MeasurementColumns.ACTIVITY_ID, measurementActivityId);
                database.insertOrThrow(Database.Tables.MEASUREMENT, null, contentValues);
                // notify all observer that subscribe to measurement table and activity table
                notifyChange(uri);
                notifyChange(Contract.Activity.CONTENT_URI);
                return Contract.Measurement.buildUri(contentValues.getAsString(BaseColumns._ID));

            case DISEASE:
                // add new disease activity to activity table
                ContentValues disease = new ContentValues();
                disease.put(Contract.ActivityColumns.BABY_ID, contentValues.getAsString(Contract.Activity.BABY_ID));
                disease.put(Contract.ActivityColumns.FAMILY_ID, contentValues.getAsString(Contract.Activity.FAMILY_ID));
                disease.put(Contract.ActivityColumns.ACTIVITY_TYPE, Contract.Activity.TYPE_DISEASE);
                disease.put(Contract.ActivityColumns.TIMESTAMP, contentValues.getAsString(Contract.Activity.TIMESTAMP));
                long diseaseActivityId = database.insertOrThrow(Database.Tables.ACTIVITY, null, disease);
                // add disease details to disease table
                contentValues.put(Contract.DiseaseColumns.ACTIVITY_ID, diseaseActivityId);
                database.insertOrThrow(Database.Tables.DISEASE, null, contentValues);
                // notify all observer that subscribe to disease table and activity table
                notifyChange(uri);
                notifyChange(Contract.Activity.CONTENT_URI);
                return Contract.Disease.buildUri(contentValues.getAsString(BaseColumns._ID));

            case VACCINE:
                // add new vaccine activity to activity table
                ContentValues vaccine = new ContentValues();
                vaccine.put(Contract.ActivityColumns.BABY_ID, contentValues.getAsString(Contract.Activity.BABY_ID));
                vaccine.put(Contract.ActivityColumns.FAMILY_ID, contentValues.getAsString(Contract.Activity.BABY_ID));
                vaccine.put(Contract.ActivityColumns.ACTIVITY_TYPE, Contract.Activity.TYPE_VACCINE);
                vaccine.put(Contract.ActivityColumns.TIMESTAMP, contentValues.getAsString(Contract.Activity.TIMESTAMP));
                long vaccineActivityId = database.insertOrThrow(Database.Tables.ACTIVITY, null, vaccine);
                // add vaccine details to vaccine table
                contentValues.put(Contract.VaccineColumns.ACTIVITY_ID, vaccineActivityId);
                database.insertOrThrow(Database.Tables.VACCINE, null, contentValues);
                // notify all observer that subscribe to vaccine table and activity table
                notifyChange(uri);
                notifyChange(Contract.Activity.CONTENT_URI);
                return Contract.Disease.buildUri(contentValues.getAsString(BaseColumns._ID));

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int returnValue;
        int match = uriMatcher.match(uri);
        SQLiteDatabase table;
        SelectionBuilder builder;
        // Delete whole table
        if (uri == Contract.BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDataBase();
            notifyChange(uri);
            return 1;
        }
        // Delete specified data from user
        table = databaseHelper.getWritableDatabase();
        builder = buildSelection(uriMatcher.match(uri));
        builder.where(selection, selectionArgs).delete(table);
        // Delete corresponding data on activity table
        selection = "baby_id = ? AND _id = ?";
        table = databaseHelper.getWritableDatabase();
        builder = buildSelection(uriMatcher.match(Contract.Activity.CONTENT_URI));
        returnValue = builder.where(selection, selectionArgs).delete(table);
        // Notify all necessary table
        switch (match) {
            case FEEDING:
                notifyChange(Contract.Feeding.LAST_TYPE);
                notifyChange(uri);
                notifyChange(Contract.Activity.CONTENT_URI);
                break;
            default:
                notifyChange(uri);
                notifyChange(Contract.Activity.CONTENT_URI);
                break;
        }
        return returnValue;
    }

    private void deleteDataBase() {
        databaseHelper.close();
        Database.deleteDataBase(getContext());
        databaseHelper = new Database(getContext());
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        SelectionBuilder builder = buildSelection(uriMatcher.match(uri));
        int retVal = builder.where(selection, selectionArgs).update(db, contentValues);
        int match = uriMatcher.match(uri);
        switch (match) {
            case FEEDING:
                notifyChange(Contract.Feeding.LAST_TYPE);
                notifyChange(uri);
                notifyChange(Contract.Activity.CONTENT_URI);
                break;
            default:
                notifyChange(uri);
                notifyChange(Contract.Activity.CONTENT_URI);
                break;
        }
        return retVal;
    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        context.getContentResolver().notifyChange(uri, null);
    }

    private SelectionBuilder buildSelection(int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case USER:
                return builder.table(Database.Tables.USER);
            case BABY:
                return builder.table(Database.Tables.BABY);
            case USER_BABY_MAP:
                return builder.table(Database.Tables.USER_BABY_MAP);
            case SLEEP:
                return builder.table(Database.Tables.SLEEP);
            case DIAPER:
                return builder.table(Database.Tables.DIAPER);
            case FEEDING:
                return builder.table(Database.Tables.FEEDING);
            case MEASUREMENT:
                return builder.table(Database.Tables.MEASUREMENT);
            case ACTIVITY:
                return builder.table(Database.Tables.ACTIVITY);
            case DISEASE:
                return builder.table(Database.Tables.DISEASE);
            case VACCINE:
                return builder.table(Database.Tables.VACCINE);
            default:
                return null;
        }
    }
}
