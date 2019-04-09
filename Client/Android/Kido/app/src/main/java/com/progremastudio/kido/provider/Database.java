/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.BaseColumns;

import com.progremastudio.kido.provider.Contract.ActivityColumns;
import com.progremastudio.kido.provider.Contract.BabyColumns;
import com.progremastudio.kido.provider.Contract.Diaper;
import com.progremastudio.kido.provider.Contract.DiaperColumns;
import com.progremastudio.kido.provider.Contract.Disease;
import com.progremastudio.kido.provider.Contract.DiseaseColumns;
import com.progremastudio.kido.provider.Contract.Measurement;
import com.progremastudio.kido.provider.Contract.MeasurementColumns;
import com.progremastudio.kido.provider.Contract.Photo;
import com.progremastudio.kido.provider.Contract.Sleep;
import com.progremastudio.kido.provider.Contract.SleepColumns;
import com.progremastudio.kido.provider.Contract.UserBabyMap;
import com.progremastudio.kido.provider.Contract.UserBabyMapColumns;
import com.progremastudio.kido.provider.Contract.UserColumns;
import com.progremastudio.kido.provider.Contract.Vaccine;
import com.progremastudio.kido.provider.Contract.VaccineColumns;

public class Database extends SQLiteOpenHelper {

    private static final int VER_2014_01 = 100;
    private static final String DATABASE_NAME = "babylog.db";
    private static final int DATABASE_VERSION = VER_2014_01;

    interface Tables {
        String USER = "user";
        String USER_BABY_MAP = "user_baby_map";
        String BABY = "baby";
        String ACTIVITY = "activity";
        String FEEDING = "feeding";
        String SLEEP = "sleep";
        String DIAPER = "diaper";
        String MEASUREMENT = "measurement";
        String PHOTO = "photo";
        String DISEASE = "disease";
        String VACCINE = "vaccine";

    }

    private interface TriggersName {
        // Deletes from all activities table, when corresponding baby deleted
        String BABY_FEEDING_DELETE = "baby_feeding_delete";
        String BABY_SLEEP_DELETE = "baby_sleep_delete";
        String BABY_DIAPER_DELETE = "baby_diaper_delete";
        String BABY_USER_DELETE = "baby_user_delete";
        String BABY_MEASUREMENT_DELETE = "baby_measurement_delete";
        String BABY_PHOTO_DELETE = "baby_photo_delete";
        String BABY_DISEASE_DELETE = "baby_disease_delete";
        String BABY_VACCINE_DELETE = "baby_vaccine_delete";
    }

    private interface Qualified {
        String BABY_DIAPER = Tables.DIAPER + "." + Diaper.BABY_ID;
        String BABY_SLEEP = Tables.SLEEP + "." + Sleep.BABY_ID;
        String BABY_FEEDING = Tables.FEEDING + "." + Contract.Feeding.BABY_ID;
        String BABY_USER_MAP = Tables.USER_BABY_MAP + "." + UserBabyMap.BABY_ID;
        String BABY_MEASUREMENT = Tables.MEASUREMENT + "." + Measurement.BABY_ID;
        String BABY_PHOTO = Tables.PHOTO + "." + Photo.BABY_ID;
        String BABY_DISEASE = Tables.DISEASE + "." + Disease.BABY_ID;
        String BABY_VACCINE = Tables.VACCINE + "." + Vaccine.BABY_ID;
    }

    public final static String JOIN_ALL = ("SELECT "
            + Tables.ACTIVITY + "." + BaseColumns._ID + " , "
            + Tables.ACTIVITY + "." + ActivityColumns.BABY_ID + " , "
            + Tables.ACTIVITY + "." + ActivityColumns.FAMILY_ID + " , "
            + Tables.ACTIVITY + "." + ActivityColumns.ACTIVITY_TYPE + " , "
            + Tables.ACTIVITY + "." + ActivityColumns.TIMESTAMP + " , "
            + Tables.DIAPER + "." + Diaper.TYPE + " , "
            + Tables.SLEEP + "." + Sleep.DURATION + " , "
            + Tables.SLEEP + "." + Sleep.TYPE + " , "
            + Tables.FEEDING + "." + Contract.Feeding.SIDES + " , "
            + Tables.FEEDING + "." + Contract.Feeding.DURATION + " , "
            + Tables.FEEDING + "." + Contract.Feeding.VOLUME + " , "
            + Tables.FEEDING + "." + Contract.Feeding.NAME + " , "
            + Tables.FEEDING + "." + Contract.Feeding.PUMP + " , "
            + Tables.MEASUREMENT + "." + Measurement.HEIGHT + " , "
            + Tables.MEASUREMENT + "." + Measurement.WEIGHT + " , "
            + Tables.MEASUREMENT + "." + Measurement.HEAD + " , "
            + Tables.DISEASE + "." + Disease.NAME + " , "
            + Tables.DISEASE + "." + Disease.SYMPTOM + " , "
            + Tables.DISEASE + "." + Disease.TREATMENT + " , "
            + Tables.DISEASE + "." + Disease.NOTES + " , "
            + Tables.VACCINE + "." + Vaccine.NAME + " , "
            + Tables.VACCINE + "." + Vaccine.LOCATION + " , "
            + Tables.VACCINE + "." + Vaccine.REMINDER + " , "
            + Tables.VACCINE + "." + Vaccine.NOTES
            + " FROM " + Tables.ACTIVITY
            + " LEFT JOIN " + Tables.DIAPER + " ON " + Tables.ACTIVITY + "." + BaseColumns._ID + " = " + Tables.DIAPER + "." + DiaperColumns.ACTIVITY_ID
            + " LEFT JOIN " + Tables.SLEEP + " ON " + Tables.ACTIVITY + "." + BaseColumns._ID + " = " + Tables.SLEEP + "." + SleepColumns.ACTIVITY_ID
            + " LEFT JOIN " + Tables.FEEDING + " ON " + Tables.ACTIVITY + "." + BaseColumns._ID + " = " + Tables.FEEDING + "." + SleepColumns.ACTIVITY_ID
            + " LEFT JOIN " + Tables.MEASUREMENT + " ON " + Tables.ACTIVITY + "." + BaseColumns._ID + " = " + Tables.MEASUREMENT + "." + MeasurementColumns.ACTIVITY_ID
            + " LEFT JOIN " + Tables.DISEASE + " ON " + Tables.ACTIVITY + "." + BaseColumns._ID + " = " + Tables.DISEASE + "." + DiseaseColumns.ACTIVITY_ID
            + " LEFT JOIN " + Tables.VACCINE + " ON " + Tables.ACTIVITY + "." + BaseColumns._ID + " = " + Tables.VACCINE + "." + VaccineColumns.ACTIVITY_ID
            + " WHERE " + Tables.ACTIVITY + "." + ActivityColumns.BABY_ID + " = ? AND " + Tables.ACTIVITY + "." + ActivityColumns.TIMESTAMP + " >= ? AND " + Tables.ACTIVITY + "." + ActivityColumns.TIMESTAMP + " <= ?"
            + " ORDER BY " + Tables.ACTIVITY + "." + Contract.Activity.Query.SORT_BY_TIMESTAMP_DESC + " ;");

    private final Context context;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static void deleteDataBase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.USER + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UserColumns.USER_NAME + " TEXT NOT NULL,"
                + UserColumns.FAMILY_ID + " TEXT NOT NULL,"
                + UserColumns.ACCESS_TOKEN + " TEXT NOT NULL,"
                + UserColumns.LOGIN_TYPE + " TEXT NOT NULL,"
                + " UNIQUE (" + UserColumns.USER_NAME + ") ON CONFLICT FAIL)");

        db.execSQL("CREATE TABLE " + Tables.USER_BABY_MAP + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UserBabyMapColumns.USER_ID + " INTEGER,"
                + UserBabyMapColumns.BABY_ID + " INTEGER,"
                + " FOREIGN KEY (" + UserBabyMapColumns.USER_ID + ") REFERENCES " + Tables.USER + " (" + BaseColumns._ID + "),"
                + " FOREIGN KEY (" + UserBabyMapColumns.BABY_ID + ") REFERENCES " + Tables.BABY + " (" + BaseColumns._ID + ")" + ")");

        db.execSQL("CREATE TABLE " + Tables.BABY + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BabyColumns.NAME + " TEXT NOT NULL,"
                + BabyColumns.FAMILY_ID + " TEXT NOT NULL,"
                + BabyColumns.BIRTHDAY + " TEXT NOT NULL,"
                + BabyColumns.SEX + " TEXT NOT NULL,"
                + BabyColumns.PICTURE + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + Tables.ACTIVITY + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ActivityColumns.BABY_ID + " TEXT NOT NULL,"
                + ActivityColumns.FAMILY_ID + " TEXT NOT NULL,"
                + ActivityColumns.ACTIVITY_TYPE + " TEXT NOT NULL,"
                + ActivityColumns.TIMESTAMP + " TEXT NOT NULL,"
                + " FOREIGN KEY (" + ActivityColumns.BABY_ID + ") REFERENCES " + Tables.BABY + " (" + BaseColumns._ID + ")" + ")");

        db.execSQL("CREATE TABLE " + Tables.FEEDING + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.FeedingColumns.ACTIVITY_ID + " TEXT NOT NULL,"
                + Contract.FeedingColumns.BABY_ID + " TEXT NOT NULL,"
                + Contract.FeedingColumns.FAMILY_ID + " TEXT NOT NULL,"
                + Contract.FeedingColumns.TIMESTAMP + " TEXT NOT NULL,"
                + Contract.FeedingColumns.DURATION + " TEXT NOT NULL,"
                + Contract.FeedingColumns.SIDES + " TEXT NOT NULL,"
                + Contract.FeedingColumns.VOLUME + " TEXT NOT NULL,"
                + Contract.FeedingColumns.NAME + " TEXT NOT NULL,"
                + Contract.FeedingColumns.PUMP + " TEXT NOT NULL,"
                + " FOREIGN KEY (" + Contract.FeedingColumns.ACTIVITY_ID + ") REFERENCES " + Tables.ACTIVITY + " (" + BaseColumns._ID + "),"
                + " FOREIGN KEY (" + Contract.FeedingColumns.BABY_ID + ") REFERENCES " + Tables.BABY + " (" + BaseColumns._ID + ")" + ")");

        db.execSQL("CREATE TABLE " + Tables.SLEEP + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SleepColumns.ACTIVITY_ID + " TEXT NOT NULL,"
                + SleepColumns.BABY_ID + " TEXT NOT NULL,"
                + SleepColumns.FAMILY_ID + " TEXT NOT NULL,"
                + SleepColumns.TIMESTAMP + " TEXT NOT NULL,"
                + SleepColumns.DURATION + " TEXT NOT NULL,"
                + SleepColumns.TYPE + " TEXT NOT NULL,"
                + " FOREIGN KEY (" + SleepColumns.ACTIVITY_ID + ") REFERENCES " + Tables.ACTIVITY + " (" + BaseColumns._ID + "),"
                + " FOREIGN KEY (" + SleepColumns.BABY_ID + ") REFERENCES " + Tables.BABY + " (" + BaseColumns._ID + ")" + ")");

        db.execSQL("CREATE TABLE " + Tables.DIAPER + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DiaperColumns.ACTIVITY_ID + " TEXT NOT NULL,"
                + DiaperColumns.BABY_ID + " TEXT NOT NULL,"
                + DiaperColumns.FAMILY_ID + " TEXT NOT NULL,"
                + DiaperColumns.TIMESTAMP + " TEXT NOT NULL,"
                + DiaperColumns.TYPE + " TEXT NOT NULL,"
                + " FOREIGN KEY (" + DiaperColumns.ACTIVITY_ID + ") REFERENCES " + Tables.ACTIVITY + " (" + BaseColumns._ID + "),"
                + " FOREIGN KEY (" + DiaperColumns.BABY_ID + ") REFERENCES " + Tables.BABY + " (" + BaseColumns._ID + ")" + ")");

        db.execSQL("CREATE TABLE " + Tables.MEASUREMENT + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Measurement.ACTIVITY_ID + " TEXT NOT NULL,"
                + Measurement.BABY_ID + " TEXT NOT NULL,"
                + Measurement.FAMILY_ID + " TEXT NOT NULL,"
                + Measurement.TIMESTAMP + " TEXT NOT NULL,"
                + Measurement.HEIGHT + " TEXT NOT NULL,"
                + Measurement.WEIGHT + " TEXT NOT NULL,"
                + Measurement.HEAD + " TEXT NOT NULL,"
                + Measurement.PICTURE + " TEXT NOT NULL,"
                + " FOREIGN KEY (" + Measurement.ACTIVITY_ID + ") REFERENCES " + Tables.ACTIVITY + " (" + BaseColumns._ID + "),"
                + " FOREIGN KEY (" + Measurement.BABY_ID + ") REFERENCES " + Tables.BABY + " (" + BaseColumns._ID + ")" + ")");

        db.execSQL("CREATE TABLE " + Tables.PHOTO + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Photo.ACTIVITY_ID + " TEXT NOT NULL,"
                + Photo.BABY_ID + " TEXT NOT NULL,"
                + Photo.TIMESTAMP + " TEXT NOT NULL,"
                + Photo.PHOTO_LOCATION + " TEXT NOT NULL,"
                + " FOREIGN KEY (" + Photo.ACTIVITY_ID + ") REFERENCES " + Tables.ACTIVITY + " (" + BaseColumns._ID + "),"
                + " FOREIGN KEY (" + Photo.BABY_ID + ") REFERENCES " + Tables.BABY + " (" + BaseColumns._ID + ")" + ")");

        db.execSQL("CREATE TABLE " + Tables.DISEASE + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Disease.ACTIVITY_ID + " TEXT NOT NULL,"
                + Disease.BABY_ID + " TEXT NOT NULL,"
                + Disease.FAMILY_ID + " TEXT NOT NULL,"
                + Disease.TIMESTAMP + " TEXT NOT NULL,"
                + Disease.NAME + " TEXT NOT NULL,"
                + Disease.SYMPTOM + " TEXT NOT NULL,"
                + Disease.TREATMENT + " TEXT NOT NULL,"
                + Disease.NOTES + " TEXT NOT NULL,"
                + " FOREIGN KEY (" + Photo.ACTIVITY_ID + ") REFERENCES " + Tables.ACTIVITY + " (" + BaseColumns._ID + "),"
                + " FOREIGN KEY (" + Photo.BABY_ID + ") REFERENCES " + Tables.BABY + " (" + BaseColumns._ID + ")" + ")");

        db.execSQL("CREATE TABLE " + Tables.VACCINE + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Vaccine.ACTIVITY_ID + " TEXT NOT NULL,"
                + Vaccine.BABY_ID + " TEXT NOT NULL,"
                + Vaccine.FAMILY_ID + " TEXT NOT NULL,"
                + Vaccine.TIMESTAMP + " TEXT NOT NULL,"
                + Vaccine.NAME + " TEXT NOT NULL,"
                + Vaccine.LOCATION + " TEXT NOT NULL,"
                + Vaccine.REMINDER + " TEXT NOT NULL,"
                + Vaccine.NOTES + " TEXT NOT NULL,"
                + " FOREIGN KEY (" + Photo.ACTIVITY_ID + ") REFERENCES " + Tables.ACTIVITY + " (" + BaseColumns._ID + "),"
                + " FOREIGN KEY (" + Photo.BABY_ID + ") REFERENCES " + Tables.BABY + " (" + BaseColumns._ID + ")" + ")");

        //trigger for delete on baby table
        db.execSQL("CREATE TRIGGER " + TriggersName.BABY_DIAPER_DELETE
                + " AFTER DELETE ON " + Tables.BABY
                + " FOR EACH ROW BEGIN "
                + " DELETE FROM " + Tables.DIAPER
                + " WHERE " + Qualified.BABY_DIAPER + "=old." + BaseColumns._ID
                + ";" + " END;");

        db.execSQL("CREATE TRIGGER " + TriggersName.BABY_FEEDING_DELETE
                + " AFTER DELETE ON " + Tables.BABY
                + " FOR EACH ROW BEGIN "
                + " DELETE FROM " + Tables.FEEDING
                + " WHERE " + Qualified.BABY_FEEDING + "=old." + BaseColumns._ID
                + ";" + " END;");

        db.execSQL("CREATE TRIGGER " + TriggersName.BABY_SLEEP_DELETE
                + " AFTER DELETE ON " + Tables.BABY
                + " FOR EACH ROW BEGIN "
                + " DELETE FROM " + Tables.SLEEP
                + " WHERE " + Qualified.BABY_SLEEP + "=old." + BaseColumns._ID
                + ";" + " END;");

        db.execSQL("CREATE TRIGGER " + TriggersName.BABY_USER_DELETE
                + " AFTER DELETE ON " + Tables.BABY
                + " FOR EACH ROW BEGIN "
                + " DELETE FROM " + Tables.USER_BABY_MAP
                + " WHERE " + Qualified.BABY_USER_MAP + "=old." + BaseColumns._ID
                + ";" + " END;");

        db.execSQL("CREATE TRIGGER " + TriggersName.BABY_MEASUREMENT_DELETE
                + " AFTER DELETE ON " + Tables.BABY
                + " FOR EACH ROW BEGIN "
                + " DELETE FROM " + Tables.MEASUREMENT
                + " WHERE " + Qualified.BABY_MEASUREMENT + "=old." + BaseColumns._ID
                + ";" + " END;");

        db.execSQL("CREATE TRIGGER " + TriggersName.BABY_PHOTO_DELETE
                + " AFTER DELETE ON " + Tables.BABY
                + " FOR EACH ROW BEGIN "
                + " DELETE FROM " + Tables.PHOTO
                + " WHERE " + Qualified.BABY_PHOTO + "=old." + BaseColumns._ID
                + ";" + " END;");

        db.execSQL("CREATE TRIGGER " + TriggersName.BABY_DISEASE_DELETE
                + " AFTER DELETE ON " + Tables.BABY
                + " FOR EACH ROW BEGIN "
                + " DELETE FROM " + Tables.DISEASE
                + " WHERE " + Qualified.BABY_DISEASE + "=old." + BaseColumns._ID
                + ";" + " END;");

        db.execSQL("CREATE TRIGGER " + TriggersName.BABY_VACCINE_DELETE
                + " AFTER DELETE ON " + Tables.BABY
                + " FOR EACH ROW BEGIN "
                + " DELETE FROM " + Tables.VACCINE
                + " WHERE " + Qualified.BABY_VACCINE + "=old." + BaseColumns._ID
                + ";" + " END;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = super.getReadableDatabase();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //TODO: reactivate foreign key after debugging
            db.setForeignKeyConstraintsEnabled(false);
        }
        return db;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //TODO: reactivate foreign key after debugging
            db.setForeignKeyConstraintsEnabled(false);
        }
        return db;
    }

}
