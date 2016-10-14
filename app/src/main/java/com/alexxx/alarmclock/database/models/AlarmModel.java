package com.alexxx.alarmclock.database.models;

import java.util.TreeSet;

public class AlarmModel {
    private int mId;
    private int mHourOfDay;
    private int mMinutes;
    private TreeSet<String> mDays;
    private TreeSet<String> mConditionCodes;
    private String mRingtoneName;
    private String mRingtoneUli;
    private boolean mIsOn;

    public AlarmModel(int id, int hourOfDay, int minutes, TreeSet<String> days,
                      TreeSet<String> conditionCodes, String ringtoneName, String ringtoneUli, boolean isOn) {
        this.setId(id);
        this.setHourOfDay(hourOfDay);
        this.setMinutes(minutes);
        this.setDays(days);
        this.setConditionCodes(conditionCodes);
        this.setRingtoneName(ringtoneName);
        this.setRingtoneUli(ringtoneUli);
        this.setOn(isOn);
    }

    public AlarmModel(int hourOfDay, int minutes, TreeSet<String> days,
                      TreeSet<String> conditionCodes, String ringtoneName, String ringtoneUli, boolean isOn) {
        this(-1, hourOfDay, minutes, days, conditionCodes, ringtoneName, ringtoneUli, isOn);
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getHourOfDay() {
        return this.mHourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.mHourOfDay = hourOfDay;
    }

    public int getMinutes() {
        return this.mMinutes;
    }

    public void setMinutes(int minutes) {
        this.mMinutes = minutes;
    }

    public TreeSet<String> getDays() {
        return this.mDays;
    }

    public void setDays(TreeSet<String> days) {
        this.mDays = days;
    }

    public TreeSet<String> getConditionCodes() {
        return this.mConditionCodes;
    }

    public void setConditionCodes(TreeSet<String> conditionCodes) {
        this.mConditionCodes = conditionCodes;
    }

    public String getRingtoneName() {
        return this.mRingtoneName;
    }

    public void setRingtoneName(String ringtoneName) {
        this.mRingtoneName = ringtoneName;
    }

    public String getRingtoneUli() {
        return this.mRingtoneUli;
    }

    public void setRingtoneUli(String ringtoneUli) {
        this.mRingtoneUli = ringtoneUli;
    }

    public boolean isOn() {
        return this.mIsOn;
    }

    public void setOn(boolean on) {
        this.mIsOn = on;
    }
}
