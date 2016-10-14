package com.alexxx.alarmclock.database.models;

import android.content.Context;

public class NotificationViewModelExtended extends NotificationViewModel {
    private String mConditionCodes;

    public NotificationViewModelExtended(long alarmId, int hourOfDay, int minutes, String alarmDays, String conditionCodes, Context context) {
        super(alarmId, hourOfDay, minutes, alarmDays, context);

        this.mConditionCodes = conditionCodes;
    }

    public String getConditionCodes() {
        return this.mConditionCodes;
    }
}
