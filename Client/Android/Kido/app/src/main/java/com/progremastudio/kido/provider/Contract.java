/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import com.progremastudio.kido.BuildConfig;

public class Contract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_USER = "user";
    private static final String PATH_USER_BABY_MAP = "user_baby_map";
    private static final String PATH_BABY = "baby";
    private static final String PATH_ACTIVITY = "activity";
    private static final String PATH_FEEDING = "feeding";
    private static final String PATH_SLEEP = "sleep";
    private static final String PATH_DIAPER = "diaper";
    private static final String PATH_MEASUREMENT = "measurement";
    private static final String PATH_PHOTO = "photo";
    private static final String PATH_DISEASE = "disease";
    private static final String PATH_VACCINE = "vaccine";

    interface UserColumns {
        String ACTIVITY_ID = "activity_id";
        String USER_NAME = "user_name";
        String FAMILY_ID = "family_id";
        String ACCESS_TOKEN = "access_token";
        String LOGIN_TYPE = "login_type"; // Facebook or Google+
    }

    interface UserBabyMapColumns {
        String USER_ID = "user_id";
        String BABY_ID = "baby_id";
    }

    interface BabyColumns {
        String ACTIVITY_ID = "activity_id";
        String NAME = "name";
        String FAMILY_ID = "family_id";
        String BIRTHDAY = "birthday";
        String SEX = "sex";
        String PICTURE = "picture";
    }

    interface ActivityColumns {
        String BABY_ID = "baby_id";
        String FAMILY_ID = "family_id";
        String ACTIVITY_TYPE = "activity_type";
        String TIMESTAMP = "timestamp";
    }

    interface FeedingColumns {
        String ACTIVITY_ID = "activity_id";
        String BABY_ID = "baby_id";
        String FAMILY_ID = "family_id";
        String TIMESTAMP = "timestamp";
        String DURATION = "duration";
        String SIDES = "sides"; // Left or Right or Formula or Food
        String VOLUME = "volume"; // mL or gr (for "liquid" and "solid" type)
        String NAME = "name"; // (for "solid" type)
        String PUMP = "pump"; // mL (for "pump" type)
    }

    interface SleepColumns {
        String ACTIVITY_ID = "activity_id";
        String BABY_ID = "baby_id";
        String FAMILY_ID = "family_id";
        String TIMESTAMP = "timestamp";
        String DURATION = "duration";
        String TYPE = "type"; // Night or Day
    }

    interface DiaperColumns {
        String ACTIVITY_ID = "activity_id";
        String BABY_ID = "baby_id";
        String FAMILY_ID = "family_id";
        String TIMESTAMP = "timestamp";
        String TYPE = "type"; // Wet or Dry or Mix
    }

    interface MeasurementColumns {
        String ACTIVITY_ID = "activity_id"; //same column name as activity
        String BABY_ID = "baby_id";
        String FAMILY_ID = "family_id";
        String TIMESTAMP = "timestamp";
        String HEIGHT = "height";
        String WEIGHT = "weight";
        String HEAD = "head";
        String PICTURE = "picture";
    }

    interface PhotoColumns {
        String ACTIVITY_ID = "activity_id"; //same column name as activity
        String BABY_ID = "baby_id";
        String TIMESTAMP = "timestamp";
        String PHOTO_LOCATION = "photo_location";
    }

    interface DiseaseColumns {
        String ACTIVITY_ID = "activity_id"; //same column name as activity
        String BABY_ID = "baby_id";
        String FAMILY_ID = "family_id";
        String TIMESTAMP = "timestamp";
        String NAME = "name";
        String SYMPTOM = "symptom";
        String TREATMENT = "treatment";
        String NOTES = "notes";
    }

    interface VaccineColumns {
        String ACTIVITY_ID = "activity_id"; //same column name as activity
        String BABY_ID = "baby_id";
        String FAMILY_ID = "family_id";
        String TIMESTAMP = "timestamp";
        String NAME = "name";
        String LOCATION = "location";
        String REMINDER = "reminder";
        String NOTES = "notes";
    }

    public static class User implements UserColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static Uri buildUri(String activityId) {
            return CONTENT_URI.buildUpon().appendPath(activityId).build();
        }

        public interface Query {
            String[] PROJECTION =
                    {
                            BaseColumns._ID,
                            Contract.User.USER_NAME,
                            Contract.User.FAMILY_ID,
                            Contract.User.ACCESS_TOKEN,
                            Contract.User.LOGIN_TYPE
                    };
            int OFFSET_ID = 0;
            int OFFSET_NAME = OFFSET_ID + 1;
            int OFFSET_FAMILY_ID = OFFSET_NAME + 1;
            int OFFSET_ACCESS_TOKEN = OFFSET_FAMILY_ID + 1;
            int OFFSET_LOGIN_TYPE = OFFSET_ACCESS_TOKEN + 1;
        }
    }

    public static class UserBabyMap implements UserBabyMapColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_BABY_MAP).build();

        public static Uri buildUri(String activityId) {
            return CONTENT_URI.buildUpon().appendPath(activityId).build();
        }
    }

    public static class Baby implements BabyColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BABY).build();

        public static Uri buildUri(String activityId) {
            return CONTENT_URI.buildUpon().appendPath(activityId).build();
        }

        public interface Query {
            String[] PROJECTION =
                    {
                            BaseColumns._ID,
                            Baby.NAME,
                            Baby.FAMILY_ID,
                            Baby.BIRTHDAY,
                            Baby.SEX,
                            Baby.PICTURE
                    };
            int OFFSET_ID = 0;
            int OFFSET_NAME = OFFSET_ID + 1;
            int OFFSET_FAMILY_ID = OFFSET_NAME + 1;
            int OFFSET_BIRTHDAY = OFFSET_FAMILY_ID + 1;
            int OFFSET_SEX = OFFSET_BIRTHDAY + 1;
            int OFFSET_PICTURE = OFFSET_SEX + 1;
        }
    }

    public static class Activity implements ActivityColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVITY).build();
        public static final String TYPE_SLEEP = "SLEEP";
        public static final String TYPE_DIAPER = "DIAPER";
        public static final String TYPE_FEEDING = "FEEDING";
        public static final String TYPE_MEASUREMENT = "MEASUREMENT";
        public static final String TYPE_DISEASE = "DISEASE";
        public static final String TYPE_VACCINE = "VACCINE";

        public interface Query {
            String[] PROJECTION =
                    {
                            BaseColumns._ID,
                            Activity.BABY_ID,
                            Activity.FAMILY_ID,
                            Activity.ACTIVITY_TYPE,
                            Activity.TIMESTAMP,
                            Diaper.TYPE,
                            Sleep.DURATION,
                            Sleep.TYPE,
                            Feeding.SIDES,
                            Feeding.DURATION,
                            Feeding.VOLUME,
                            Feeding.NAME,
                            Feeding.PUMP,
                            Measurement.HEIGHT,
                            Measurement.WEIGHT,
                            Measurement.HEAD,
                            Disease.NAME,
                            Disease.SYMPTOM,
                            Disease.TREATMENT,
                            Disease.NOTES,
                            Vaccine.NAME,
                            Vaccine.LOCATION,
                            Vaccine.REMINDER,
                            Vaccine.NOTES
                    };
            int OFFSET_ID = 0;
            int OFFSET_BABY_ID = OFFSET_ID + 1;
            int OFFSET_FAMILY_ID = OFFSET_BABY_ID + 1;
            int OFFSET_ACTIVITY_TYPE = OFFSET_FAMILY_ID + 1;
            int OFFSET_TIMESTAMP = OFFSET_ACTIVITY_TYPE + 1;
            int OFFSET_DIAPER_TYPE = OFFSET_TIMESTAMP + 1;
            int OFFSET_SLEEP_DURATION = OFFSET_DIAPER_TYPE + 1;
            int OFFSET_SLEEP_TYPE = OFFSET_SLEEP_DURATION + 1;
            int OFFSET_FEEDING_SIDES = OFFSET_SLEEP_TYPE + 1;
            int OFFSET_FEEDING_DURATION = OFFSET_FEEDING_SIDES + 1;
            int OFFSET_FEEDING_VOLUME = OFFSET_FEEDING_DURATION + 1;
            int OFFSET_FEEDING_NAME = OFFSET_FEEDING_VOLUME + 1;
            int OFFSET_FEEDING_PUMP = OFFSET_FEEDING_NAME + 1;
            int OFFSET_MEASUREMENT_HEIGHT = OFFSET_FEEDING_PUMP + 1;
            int OFFSET_MEASUREMENT_WEIGHT = OFFSET_MEASUREMENT_HEIGHT + 1;
            int OFFSET_MEASUREMENT_HEAD = OFFSET_MEASUREMENT_WEIGHT + 1;
            int OFFSET_DISEASE_NAME = OFFSET_MEASUREMENT_HEAD + 1;
            int OFFSET_DISEASE_SYMPTOM = OFFSET_DISEASE_NAME + 1;
            int OFFSET_DISEASE_TREATMENT = OFFSET_DISEASE_SYMPTOM + 1;
            int OFFSET_DISEASE_NOTES = OFFSET_DISEASE_TREATMENT + 1;
            int OFFSET_VACCINE_NAME = OFFSET_DISEASE_NOTES + 1;
            int OFFSET_VACCINE_LOCATION = OFFSET_VACCINE_NAME + 1;
            int OFFSET_VACCINE_REMINDER = OFFSET_VACCINE_LOCATION + 1;
            int OFFSET_VACCINE_NOTES = OFFSET_VACCINE_REMINDER + 1;
            String SORT_BY_TIMESTAMP_ASC = ActivityColumns.TIMESTAMP + " ASC ";
            String SORT_BY_TIMESTAMP_DESC = ActivityColumns.TIMESTAMP + " DESC ";
        }
    }

    public static class Feeding implements FeedingColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FEEDING).build();
        public static final Uri MAX_TIMESTAMP =
                BASE_CONTENT_URI.buildUpon().appendPath("feeding_max_timestamp").build();
        public static final Uri LAST_TYPE =
                BASE_CONTENT_URI.buildUpon().appendPath("feeding_last_side").build();

        public static Uri buildUri(String activityId) {
            return CONTENT_URI.buildUpon().appendPath(activityId).build();
        }

        public interface Query {
            String[] PROJECTION =
                    {
                            BaseColumns._ID,
                            Feeding.ACTIVITY_ID,
                            Feeding.BABY_ID,
                            Feeding.FAMILY_ID,
                            Feeding.TIMESTAMP,
                            Feeding.SIDES,
                            Feeding.DURATION,
                            Feeding.VOLUME,
                            Feeding.NAME,
                            Feeding.PUMP
                    };
            int OFFSET_ID = 0;
            int OFFSET_ACTIVITY_ID = OFFSET_ID + 1;
            int OFFSET_BABY_ID = OFFSET_ACTIVITY_ID + 1;
            int OFFSET_FAMILY_ID = OFFSET_BABY_ID + 1;
            int OFFSET_TIMESTAMP = OFFSET_FAMILY_ID + 1;
            int OFFSET_TYPE = OFFSET_TIMESTAMP + 1;
            int OFFSET_DURATION = OFFSET_TYPE + 1;
            int OFFSET_VOLUME = OFFSET_DURATION + 1;
            int OFFSET_NAME = OFFSET_VOLUME + 1;
            int OFFSET_PUMP = OFFSET_NAME + 1;
            String SORT_BY_TIMESTAMP_ASC = FeedingColumns.TIMESTAMP + " ASC ";
            String SORT_BY_TIMESTAMP_DESC = FeedingColumns.TIMESTAMP + " DESC ";
            String SORT_BY_ACTIVITY_ID_DESC = FeedingColumns.ACTIVITY_ID + " DESC ";
        }

        public static final String table = PATH_FEEDING;
    }

    public static class Sleep implements SleepColumns, BaseColumns {
        public static final Uri MAX_TIMESTAMP =
                BASE_CONTENT_URI.buildUpon().appendPath("sleep_max_timestamp").build();
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SLEEP).build();

        public static Uri buildUri(String activityId) {
            return CONTENT_URI.buildUpon().appendPath(activityId).build();
        }

        public interface Query {
            String[] PROJECTION =
                    {
                            BaseColumns._ID,
                            Sleep.ACTIVITY_ID,
                            Sleep.BABY_ID,
                            Sleep.FAMILY_ID,
                            Sleep.TIMESTAMP,
                            Sleep.DURATION,
                            Sleep.TYPE
                    };
            int OFFSET_ID = 0;
            int OFFSET_ACTIVITY_ID = OFFSET_ID + 1;
            int OFFSET_BABY_ID = OFFSET_ACTIVITY_ID + 1;
            int OFFSET_FAMILY_ID = OFFSET_BABY_ID + 1;
            int OFFSET_TIMESTAMP = OFFSET_FAMILY_ID + 1;
            int OFFSET_DURATION = OFFSET_TIMESTAMP + 1;
            int OFFSET_TYPE = OFFSET_DURATION + 1;
            String SORT_BY_TIMESTAMP_ASC = SleepColumns.TIMESTAMP + " ASC ";
            String SORT_BY_TIMESTAMP_DESC = SleepColumns.TIMESTAMP + " DESC ";
            String SORT_BY_ACTIVITY_ID_DESC = SleepColumns.ACTIVITY_ID + " DESC ";
        }

        public static final String table = PATH_SLEEP;

    }

    public static class Diaper implements DiaperColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIAPER).build();

        public static Uri buildUri(String activityId) {
            return CONTENT_URI.buildUpon().appendPath(activityId).build();
        }

        public interface Query {
            String[] PROJECTION =
                    {
                            BaseColumns._ID,
                            Diaper.ACTIVITY_ID,
                            Diaper.BABY_ID,
                            Diaper.FAMILY_ID,
                            Diaper.TIMESTAMP,
                            Diaper.TYPE
                    };
            int OFFSET_ID = 0;
            int OFFSET_ACTIVITY_ID = OFFSET_ID + 1;
            int OFFSET_BABY_ID = OFFSET_ACTIVITY_ID + 1;
            int OFFSET_FAMILY_ID = OFFSET_BABY_ID + 1;
            int OFFSET_TIMESTAMP = OFFSET_FAMILY_ID + 1;
            int OFFSET_TYPE = OFFSET_TIMESTAMP + 1;
            String SORT_BY_TIMESTAMP_ASC = DiaperColumns.TIMESTAMP + " ASC ";
            String SORT_BY_TIMESTAMP_DESC = DiaperColumns.TIMESTAMP + " DESC ";
            String SORT_BY_ACTIVITY_ID_DESC = DiaperColumns.ACTIVITY_ID + " DESC ";
        }

        public static final String table = PATH_DIAPER;
    }

    public static class Measurement implements MeasurementColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEASUREMENT).build();

        public static Uri buildUri(String activityId) {
            return CONTENT_URI.buildUpon().appendPath(activityId).build();
        }

        public interface Query {
            String[] PROJECTION =
                    {
                            BaseColumns._ID,
                            Measurement.ACTIVITY_ID,
                            Measurement.BABY_ID,
                            Measurement.FAMILY_ID,
                            Measurement.TIMESTAMP,
                            Measurement.HEIGHT,
                            Measurement.WEIGHT,
                            Measurement.HEAD,
                            Measurement.PICTURE
                    };
            int OFFSET_ID = 0;
            int OFFSET_ACTIVITY_ID = OFFSET_ID + 1;
            int OFFSET_BABY_ID = OFFSET_ACTIVITY_ID + 1;
            int OFFSET_FAMILY_ID = OFFSET_BABY_ID + 1;
            int OFFSET_TIMESTAMP = OFFSET_FAMILY_ID + 1;
            int OFFSET_HEIGHT = OFFSET_TIMESTAMP + 1;
            int OFFSET_WEIGHT = OFFSET_HEIGHT + 1;
            int OFFSET_HEAD = OFFSET_WEIGHT + 1;
            int OFFSET_PICTURE = OFFSET_HEAD + 1;
            String SORT_BY_TIMESTAMP_ASC = MeasurementColumns.TIMESTAMP + " ASC ";
            String SORT_BY_TIMESTAMP_DESC = MeasurementColumns.TIMESTAMP + " DESC ";
            String SORT_BY_ACTIVITY_ID_DESC = FeedingColumns.ACTIVITY_ID + " DESC ";
        }

        public static final String table = PATH_MEASUREMENT;
    }

    public static class Photo implements PhotoColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PHOTO).build();
    }

    public static class Disease implements DiseaseColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DISEASE).build();

        public static Uri buildUri(String activityId) {
            return CONTENT_URI.buildUpon().appendPath(activityId).build();
        }

        public interface Query {
            String[] PROJECTION =
                    {
                            BaseColumns._ID,
                            Disease.ACTIVITY_ID,
                            Disease.BABY_ID,
                            Disease.FAMILY_ID,
                            Disease.TIMESTAMP,
                            Disease.NAME,
                            Disease.SYMPTOM,
                            Disease.TREATMENT,
                            Disease.NOTES
                    };
            int OFFSET_ID = 0;
            int OFFSET_ACTIVITY_ID = OFFSET_ID + 1;
            int OFFSET_BABY_ID = OFFSET_ACTIVITY_ID + 1;
            int OFFSET_FAMILY_ID = OFFSET_BABY_ID + 1;
            int OFFSET_TIMESTAMP = OFFSET_FAMILY_ID + 1;
            int OFFSET_NAME = OFFSET_TIMESTAMP + 1;
            int OFFSET_SYMPTOM = OFFSET_NAME + 1;
            int OFFSET_TREATMENT = OFFSET_SYMPTOM + 1;
            int OFFSET_NOTES = OFFSET_TREATMENT + 1;
            String SORT_BY_TIMESTAMP_ASC = DiseaseColumns.TIMESTAMP + " ASC ";
            String SORT_BY_TIMESTAMP_DESC = DiseaseColumns.TIMESTAMP + " DESC ";
            String SORT_BY_ACTIVITY_ID_DESC = DiaperColumns.ACTIVITY_ID + " DESC ";
        }
    }

    public static class Vaccine implements VaccineColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VACCINE).build();

        public static Uri buildUri(String activityId) {
            return CONTENT_URI.buildUpon().appendPath(activityId).build();
        }

        public interface Query {
            String[] PROJECTION = {
                    BaseColumns._ID,
                    Vaccine.ACTIVITY_ID,
                    Vaccine.BABY_ID,
                    Vaccine.FAMILY_ID,
                    Vaccine.TIMESTAMP,
                    Vaccine.NAME,
                    Vaccine.LOCATION,
                    Vaccine.REMINDER,
                    Vaccine.NOTES
            };
            int OFFSET_ID = 0;
            int OFFSET_ACTIVITY_ID = OFFSET_ID + 1;
            int OFFSET_BABY_ID = OFFSET_ACTIVITY_ID + 1;
            int OFFSET_FAMILY_ID = OFFSET_BABY_ID + 1;
            int OFFSET_TIMESTAMP = OFFSET_FAMILY_ID + 1;
            int OFFSET_NAME = OFFSET_TIMESTAMP + 1;
            int OFFSET_LOCATION = OFFSET_NAME + 1;
            int OFFSET_REMINDER = OFFSET_LOCATION + 1;
            int OFFSET_NOTES = OFFSET_REMINDER + 1;
            String SORT_BY_TIMESTAMP_ASC = VaccineColumns.TIMESTAMP + " ASC ";
            String SORT_BY_TIMESTAMP_DESC = VaccineColumns.TIMESTAMP + " DESC ";
            String SORT_BY_ACTIVITY_ID_DESC = DiaperColumns.ACTIVITY_ID + " DESC ";
        }
    }
}
