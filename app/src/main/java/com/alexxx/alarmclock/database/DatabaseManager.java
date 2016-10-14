package com.alexxx.alarmclock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alexxx.alarmclock.database.models.AlarmModel;
import com.alexxx.alarmclock.database.models.AlarmTableInfo;
import com.alexxx.alarmclock.database.models.NotificationViewModelExtended;
import com.alexxx.alarmclock.database.models.RecyclerViewItemModel;
import com.alexxx.alarmclock.helpers.SetUtil;
import com.alexxx.alarmclock.helpers.StringUtil;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class DatabaseManager extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "alarmClock.db";
    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    public DatabaseManager(Context context) {
        super(context, DatabaseManager.DATABASE_NAME, null, DatabaseManager.DATABASE_VERSION);

        this.mContext = context;
    }

    public AlarmModel getAlarmById(long alarmId) {
        List<AlarmModel> alarms = this.getAlarmsWhere(AlarmTableInfo._ID + " = ? ",
                new String[]{String.valueOf(alarmId)});

        if (!alarms.isEmpty()) {
            return alarms.get(0);
        }

        return null;
    }

    private List<AlarmModel> getAlarmsWhere(String sqlSelection, String[] selectionArgs) {
        SQLiteDatabase db = super.getReadableDatabase();

        String[] projection = {AlarmTableInfo._ID, AlarmTableInfo.HOUR_COLUMN,
                AlarmTableInfo.MINUTES_COLUMN, AlarmTableInfo.DAYS_COLUMN,
                AlarmTableInfo.CONDITION_CODES_COLUMN, AlarmTableInfo.RINGTONE_NAME_COLUMN,
                AlarmTableInfo.RINGTONE_URL_COLUMN, AlarmTableInfo.IS_ON_COLUMN};

        Cursor cursor = db.query(AlarmTableInfo.TABLE_NAME, projection, sqlSelection, selectionArgs, null, null, null);

        List<AlarmModel> alarms = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() != 0) {
                do {
                    String alarmDaysStr = cursor.getString(cursor.getColumnIndex(AlarmTableInfo.DAYS_COLUMN));
                    TreeSet<String> alarmDays = new TreeSet<>();
                    if (alarmDaysStr != null) {
                        alarmDays = new TreeSet<>(Arrays.asList(alarmDaysStr.split(", ")));
                    }

                    String conditionCodesStr = cursor.getString(cursor.getColumnIndex(AlarmTableInfo.CONDITION_CODES_COLUMN));
                    TreeSet<String> conditionCodes = new TreeSet<>();
                    if (conditionCodesStr != null) {
                        conditionCodes = new TreeSet<>(Arrays.asList(conditionCodesStr.split(", ")));
                    }

                    AlarmModel alarm = new AlarmModel(cursor.getInt(cursor.getColumnIndex(AlarmTableInfo._ID)),
                            cursor.getInt(cursor.getColumnIndex(AlarmTableInfo.HOUR_COLUMN)),
                            cursor.getInt(cursor.getColumnIndex(AlarmTableInfo.MINUTES_COLUMN)),
                            alarmDays,
                            conditionCodes,
                            cursor.getString(cursor.getColumnIndex(AlarmTableInfo.RINGTONE_NAME_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(AlarmTableInfo.RINGTONE_URL_COLUMN)),
                            cursor.getInt(cursor.getColumnIndex(AlarmTableInfo.IS_ON_COLUMN)) > 0);

                    alarms.add(alarm);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return alarms;
    }

    public int getCountWhere(String sqlSelection, String[] selectionArgs) {
        SQLiteDatabase db = super.getReadableDatabase();

        String[] projection = {"COUNT(" + AlarmTableInfo._ID + ")"};

        Cursor cursor = db.query(AlarmTableInfo.TABLE_NAME, projection, sqlSelection, selectionArgs, null, null, null);

        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();

            count = cursor.getInt(0);
            cursor.close();
        }

        return count;
    }

    public List<RecyclerViewItemModel> getAllAlarmsAsRecyclerViewItemModel() {
        SQLiteDatabase db = super.getReadableDatabase();

        String[] projection = {AlarmTableInfo._ID, AlarmTableInfo.HOUR_COLUMN,
                AlarmTableInfo.MINUTES_COLUMN, AlarmTableInfo.DAYS_COLUMN, AlarmTableInfo.IS_ON_COLUMN};

        Cursor cursor = db.query(AlarmTableInfo.TABLE_NAME, projection, null, null, null, null, AlarmTableInfo._ID);

        List<RecyclerViewItemModel> viewModels = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() != 0) {
                do {
                    int alarmHour = cursor.getInt(cursor.getColumnIndex(AlarmTableInfo.HOUR_COLUMN));
                    int alarmMinute = cursor.getInt(cursor.getColumnIndex(AlarmTableInfo.MINUTES_COLUMN));
                    String alarmHourStr = alarmHour > 9 ? String.valueOf(alarmHour) : "0" + String.valueOf(alarmHour);
                    String alarmMinuteStr = alarmMinute > 9 ? String.valueOf(alarmMinute) : "0" + String.valueOf(alarmMinute);

                    RecyclerViewItemModel viewModel = new RecyclerViewItemModel(cursor.getInt(cursor.getColumnIndex(AlarmTableInfo._ID)),
                            alarmHourStr + ":" + alarmMinuteStr,
                            cursor.getString(cursor.getColumnIndex(AlarmTableInfo.DAYS_COLUMN)),
                            cursor.getInt(cursor.getColumnIndex(AlarmTableInfo.IS_ON_COLUMN)) > 0);

                    viewModels.add(viewModel);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return viewModels;
    }

    public List<NotificationViewModelExtended> getActiveAndNeedOfNetAlarms() {
        String sqlSelection = AlarmTableInfo.IS_ON_COLUMN + " = 1 AND " + AlarmTableInfo.CONDITION_CODES_COLUMN + " IS NOT NULL";

        return this.getNotificationViewWhere(sqlSelection, null);
    }

    private List<NotificationViewModelExtended> getNotificationViewWhere(String sqlSelection, String[] selectionArgs) {
        SQLiteDatabase db = super.getReadableDatabase();

        String[] projection = {AlarmTableInfo._ID, AlarmTableInfo.HOUR_COLUMN,
                AlarmTableInfo.MINUTES_COLUMN, AlarmTableInfo.DAYS_COLUMN, AlarmTableInfo.CONDITION_CODES_COLUMN};

        Cursor cursor = db.query(AlarmTableInfo.TABLE_NAME, projection, sqlSelection, selectionArgs, null, null, null);

        List<NotificationViewModelExtended> viewModels = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() != 0) {
                do {
                    NotificationViewModelExtended viewModel = new NotificationViewModelExtended(cursor.getInt(cursor.getColumnIndex(AlarmTableInfo._ID)),
                            cursor.getInt(cursor.getColumnIndex(AlarmTableInfo.HOUR_COLUMN)),
                            cursor.getInt(cursor.getColumnIndex(AlarmTableInfo.MINUTES_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(AlarmTableInfo.DAYS_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(AlarmTableInfo.CONDITION_CODES_COLUMN)),
                            this.mContext);

                    viewModels.add(viewModel);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return viewModels;
    }

    public List<NotificationViewModelExtended> getAllActiveAlarmAsNotificationViewModelExtended() {
        SQLiteDatabase db = super.getReadableDatabase();

        String[] projection = {AlarmTableInfo._ID, AlarmTableInfo.HOUR_COLUMN,
                AlarmTableInfo.MINUTES_COLUMN, AlarmTableInfo.DAYS_COLUMN, AlarmTableInfo.CONDITION_CODES_COLUMN};

            Cursor cursor = db.query(AlarmTableInfo.TABLE_NAME, projection, AlarmTableInfo.IS_ON_COLUMN + " = 1", null, null, null, null);

            List<NotificationViewModelExtended> viewModels = new ArrayList<>();
            if (cursor != null) {
                cursor.moveToFirst();

                if (cursor.getCount() != 0) {
                    do {
                        NotificationViewModelExtended viewModel = new NotificationViewModelExtended(cursor.getInt(cursor.getColumnIndex(AlarmTableInfo._ID)),
                                cursor.getInt(cursor.getColumnIndex(AlarmTableInfo.HOUR_COLUMN)),
                                cursor.getInt(cursor.getColumnIndex(AlarmTableInfo.MINUTES_COLUMN)),
                                cursor.getString(cursor.getColumnIndex(AlarmTableInfo.DAYS_COLUMN)),
                                cursor.getString(cursor.getColumnIndex(AlarmTableInfo.CONDITION_CODES_COLUMN)),
                                this.mContext);

                        viewModels.add(viewModel);
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }

        return viewModels;
    }

    public RecyclerViewItemModel insertAlarm(AlarmModel alarm) {
        SQLiteDatabase db = super.getWritableDatabase();

        ContentValues args = this.getSqlAlarmArgs(alarm);

        int id = (int) db.insert(AlarmTableInfo.TABLE_NAME, null, args);
        db.close();
        if (id != -1) {
            alarm.setId(id);
            int alarmHour = alarm.getHourOfDay();
            int alarmMinute = alarm.getMinutes();
            String alarmHourStr = alarmHour > 9 ? String.valueOf(alarmHour) : "0" + String.valueOf(alarmHour);
            String alarmMinuteStr = alarmMinute > 9 ? String.valueOf(alarmMinute) : "0" + String.valueOf(alarmMinute);

            return new RecyclerViewItemModel(id, alarmHourStr + ":" + alarmMinuteStr, null, alarm.isOn());
        }

        return null;
    }

    public boolean updateAlarm(AlarmModel alarm) {
        SQLiteDatabase db = super.getWritableDatabase();

        ContentValues args = this.getSqlAlarmArgs(alarm);
        int affectedRows = db.update(AlarmTableInfo.TABLE_NAME, args, AlarmTableInfo._ID + " = ?", new String[]{String.valueOf(alarm.getId())});

        return affectedRows != -1;
    }

    private ContentValues getSqlAlarmArgs(AlarmModel alarm) {
        ContentValues args = new ContentValues();
        args.put(AlarmTableInfo.HOUR_COLUMN, alarm.getHourOfDay());
        args.put(AlarmTableInfo.MINUTES_COLUMN, alarm.getMinutes());
        args.put(AlarmTableInfo.DAYS_COLUMN, StringUtil.join(", ", alarm.getDays()));
        args.put(AlarmTableInfo.CONDITION_CODES_COLUMN, StringUtil.join(", ", alarm.getConditionCodes()));
        args.put(AlarmTableInfo.RINGTONE_NAME_COLUMN, alarm.getRingtoneName());
        args.put(AlarmTableInfo.RINGTONE_URL_COLUMN, alarm.getRingtoneUli());
        args.put(AlarmTableInfo.IS_ON_COLUMN, alarm.isOn() ? 1 : 0);

        return args;
    }

    public long deleteAlarm(long alarmId) {
        SQLiteDatabase db = super.getWritableDatabase();

        return (long) db.delete(AlarmTableInfo.TABLE_NAME, AlarmTableInfo._ID + " = ?", new String[]{String.valueOf(alarmId)});
    }

    public boolean isExistsInDb(AlarmModel alarm) {
        SQLiteDatabase db = super.getWritableDatabase();

        Cursor cursor = db.query(AlarmTableInfo.TABLE_NAME, new String[]{AlarmTableInfo._ID, AlarmTableInfo.DAYS_COLUMN, AlarmTableInfo.CONDITION_CODES_COLUMN},
                AlarmTableInfo.HOUR_COLUMN + " = " + String.valueOf(alarm.getHourOfDay()) + " AND " +
                        AlarmTableInfo.MINUTES_COLUMN + " = " + String.valueOf(alarm.getHourOfDay()) + " AND " +
                        AlarmTableInfo.IS_ON_COLUMN + " = 1 ",
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    String alarmDaysStr = cursor.getString(cursor.getColumnIndex(AlarmTableInfo.DAYS_COLUMN));
                    TreeSet<String> alarmDays = new TreeSet<>();
                    if (alarmDaysStr != null) {
                        alarmDays = new TreeSet<>(Arrays.asList(alarmDaysStr.split(", ")));
                    }

                    String conditionCodesStr = cursor.getString(cursor.getColumnIndex(AlarmTableInfo.CONDITION_CODES_COLUMN));
                    TreeSet<String> conditionCodes = new TreeSet<>();
                    if (conditionCodesStr != null) {
                        conditionCodes = new TreeSet<>(Arrays.asList(conditionCodesStr.split(", ")));
                    }
                    if (SetUtil.intersection(alarm.getDays(), alarmDays).size() > 0 && SetUtil.intersection(alarm.getConditionCodes(), conditionCodes).size() > 0) {
                        return true;
                    }
                } while (cursor.moveToFirst());
            }

            cursor.close();
        }

        return false;
    }
}