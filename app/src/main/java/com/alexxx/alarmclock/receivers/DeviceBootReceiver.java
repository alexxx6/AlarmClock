package com.alexxx.alarmclock.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alexxx.alarmclock.AlarmClockApp;
import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.database.DatabaseManager;
import com.alexxx.alarmclock.database.models.NotificationViewModel;
import com.alexxx.alarmclock.database.models.NotificationViewModelExtended;
import com.alexxx.alarmclock.models.DaysOfWeek;
import com.alexxx.alarmclock.services.AlarmNotifyProgressService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            DatabaseManager databaseManager = ((AlarmClockApp) context.getApplicationContext()).getDatabaseManager();
            List<NotificationViewModelExtended> alarms = databaseManager.getAllActiveAlarmAsNotificationViewModelExtended();

            Date currDate = new Date();
            SimpleDateFormat df_output = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH);
            String currDay = df_output.format(currDate);
            for (NotificationViewModel alarm : alarms) {
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.ALARM_ID_BK, (int) alarm.getAlarmId());
                alarmIntent.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) alarm.getAlarmId(), alarmIntent, 0);

                Calendar now = Calendar.getInstance();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, alarm.getHourOfDay());
                calendar.set(Calendar.MINUTE, alarm.getMinutes());
                calendar.set(Calendar.SECOND, 0);

                int daysToNextAlarm;
                if (alarm.getAlarmDays().contains(currDay) && calendar.after(now)){
                    daysToNextAlarm = 0;
                } else {
                    daysToNextAlarm = getDaysToNextAlarm(alarm, currDay);
                }

                calendar.add(Calendar.DAY_OF_MONTH, daysToNextAlarm);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            context.startService(new Intent(context, AlarmNotifyProgressService.class));
        }
    }

    private int getDaysToNextAlarm(NotificationViewModel alarmToStart, String currDay) {
        boolean isFound = false;
        int daysToNextAlarm = 0;
        for (DaysOfWeek day : DaysOfWeek.values()) {
            String dayName = day.toString().toLowerCase();
            String enAbbreviation = dayName.substring(0, 1).toUpperCase() + dayName.substring(1, 3);
            if (currDay.equals(enAbbreviation)) {
                isFound = true;
            }

            if (!currDay.equals(enAbbreviation) && alarmToStart.getAlarmDays().contains(enAbbreviation) && isFound) {
                isFound = false;
                break;
            }

            if (isFound) {
                daysToNextAlarm++;
            }
        }

        if (isFound) {
            for (DaysOfWeek day : DaysOfWeek.values()) {
                String dayName = day.toString().toLowerCase();
                String enAbbreviation = dayName.substring(0, 1).toUpperCase() + dayName.substring(1, 3);
                if (currDay.equals(enAbbreviation) || alarmToStart.getAlarmDays().contains(enAbbreviation)) {
                    break;
                }

                daysToNextAlarm++;
            }
        }

        return daysToNextAlarm;
    }
}
