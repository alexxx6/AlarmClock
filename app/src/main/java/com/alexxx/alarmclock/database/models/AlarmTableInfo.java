package com.alexxx.alarmclock.database.models;

import android.provider.BaseColumns;

public class AlarmTableInfo implements BaseColumns {
    public static final String TABLE_NAME = "alarms";
    public static final String HOUR_COLUMN = "hour";
    public static final String MINUTES_COLUMN = "minutes";
    public static final String DAYS_COLUMN = "days";
    public static final String CONDITION_CODES_COLUMN = "condition_codes";
    public static final String RINGTONE_NAME_COLUMN = "ringtone_name";
    public static final String RINGTONE_URL_COLUMN = "ringtone_url";
    public static final String IS_ON_COLUMN = "is_on";
}
