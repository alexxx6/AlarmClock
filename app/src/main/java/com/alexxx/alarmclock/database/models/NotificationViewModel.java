package com.alexxx.alarmclock.database.models;

import android.content.Context;

import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.helpers.StringUtil;
import com.alexxx.alarmclock.models.DaysOfWeek;

import java.util.ArrayList;

public class NotificationViewModel {
    private long mAlarmId;
    private int mHourOfDay;
    private int mMinutes;
    private String mAlarmDays;
    private Context mContext;

    public NotificationViewModel(long alarmId, int hourOfDay, int minutes, String alarmDays, Context context) {
        this.mAlarmId = alarmId;
        this.mHourOfDay = hourOfDay;
        this.mMinutes = minutes;
        this.mAlarmDays = alarmDays;
        this.mContext = context;
    }

    public long getAlarmId() {
        return this.mAlarmId;
    }

    public int getHourOfDay() {
        return this.mHourOfDay;
    }

    public int getMinutes() {
        return this.mMinutes;
    }

    private String getAlarmTime() {
        String alarmHourStr = this.getHourOfDay() > 9 ? String.valueOf(this.getHourOfDay()) : "0" + String.valueOf(this.getHourOfDay());
        String alarmMinuteStr = this.getMinutes() > 9 ? String.valueOf(this.getMinutes()) : "0" + String.valueOf(this.getMinutes());

        return alarmHourStr + ":" + alarmMinuteStr;
    }

    public String getAlarmDays() {
        return this.mAlarmDays;
    }

    @Override
    public String toString() {
        return this.getAlarmTime() + " " + getSelectedDayAbbrsForCurrLanguage(getAlarmDays());
    }

    private String getSelectedDayAbbrsForCurrLanguage( String selectedDayEnAbbreviations) {
        ArrayList<String> selectedDayAbbrsForCurrLanguage = new ArrayList<>();
        if (selectedDayEnAbbreviations != null) {
            String[] dayOfWeekAbbreviations = this.mContext.getResources().getStringArray(R.array.day_of_week_abbreviation);
            for (DaysOfWeek daysOfWeek : DaysOfWeek.values()) {
                String dayName = daysOfWeek.toString().toLowerCase();
                String dayAbbreviation = dayName.substring(0, 1).toUpperCase() + dayName.substring(1, 3);
                if (selectedDayEnAbbreviations.contains(dayAbbreviation)) {
                    selectedDayAbbrsForCurrLanguage.add(dayOfWeekAbbreviations[daysOfWeek.ordinal()]);
                }
            }
        }

        return StringUtil.join(", ", selectedDayAbbrsForCurrLanguage);
    }
}
