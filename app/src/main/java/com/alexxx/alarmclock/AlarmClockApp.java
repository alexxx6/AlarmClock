package com.alexxx.alarmclock;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.alexxx.alarmclock.database.DatabaseManager;
import com.alexxx.alarmclock.models.RingtoneItem;

import java.util.TreeSet;

public class AlarmClockApp extends MultiDexApplication {
    private DatabaseManager mDatabaseManager;
    private Long mAlarmId;
    private RingtoneItem mSelectedRingtone;
    private TreeSet<String> mSelectedDays;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.mDatabaseManager = new DatabaseManager(this);
    }

    public DatabaseManager getDatabaseManager() {
        return this.mDatabaseManager;
    }

    public Long getAlarmId() {
        return this.mAlarmId;
    }

    public void setAlarmId(Long mAlarmId) {
        this.mAlarmId = mAlarmId;
    }

    public RingtoneItem getSelectedRingtone() {
        return this.mSelectedRingtone;
    }

    public void setSelectedRingtone(RingtoneItem selectedRingtone) {
        this.mSelectedRingtone = selectedRingtone;
    }

    public TreeSet<String> getSelectedDays() {
        return mSelectedDays;
    }

    public void setSelectedDays(TreeSet<String> selectedDays) {
        this.mSelectedDays = selectedDays;
    }
}
