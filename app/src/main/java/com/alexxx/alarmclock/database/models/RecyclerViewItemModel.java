package com.alexxx.alarmclock.database.models;

public class RecyclerViewItemModel {
    private long mAlarmId;
    private String mAlarmTime;
    private String mAlarmDays;
    private boolean mIsAlarmOn;


    public RecyclerViewItemModel(long alarmId, String alarmTime, String alarmDays, boolean isAlarmOn) {
        this.mAlarmId = alarmId;
        this.mAlarmTime = alarmTime;
        this.mAlarmDays = alarmDays;
        this.mIsAlarmOn = isAlarmOn;
    }

    public long getAlarmId() {
        return this.mAlarmId;
    }

    public String getAlarmTime() {
        return this.mAlarmTime;
    }

    public String getAlarmDays() {
        return this.mAlarmDays;
    }

    public void setAlarmDays(String mAlarmDays) {
        this.mAlarmDays = mAlarmDays;
    }

    public boolean isAlarmOn() {
        return this.mIsAlarmOn;
    }
}
