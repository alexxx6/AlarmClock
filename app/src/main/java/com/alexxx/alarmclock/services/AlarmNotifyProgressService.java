package com.alexxx.alarmclock.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alexxx.alarmclock.AlarmClockApp;
import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.database.DatabaseManager;
import com.alexxx.alarmclock.database.models.NotificationViewModelExtended;
import com.alexxx.alarmclock.helpers.InternetChecker;
import com.alexxx.alarmclock.helpers.NotificationBuilder;
import com.alexxx.alarmclock.models.DaysOfWeek;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlarmNotifyProgressService extends Service {

    private static boolean mIsRunning = false;
    private Thread mProgressUpdater;

    public static boolean isRunning() {
        return AlarmNotifyProgressService.mIsRunning;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AlarmNotifyProgressService.mIsRunning = true;
        this.mProgressUpdater = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() || AlarmNotifyProgressService.isRunning()){
                    DatabaseManager databaseManager = ((AlarmClockApp) AlarmNotifyProgressService.this.getApplicationContext()).getDatabaseManager();
                    List<NotificationViewModelExtended> models = databaseManager.getAllActiveAlarmAsNotificationViewModelExtended();
                    boolean isOnline = InternetChecker.isOnline(AlarmNotifyProgressService.super.getBaseContext());

                    if (models.isEmpty()){
                        break;
                    }

                    for (NotificationViewModelExtended model : models) {
                        if (isOnline || model.getConditionCodes() == null) {
                            Calendar now = Calendar.getInstance();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, model.getHourOfDay());
                            calendar.set(Calendar.MINUTE, model.getMinutes());
                            calendar.set(Calendar.SECOND, 0);

                            Date currDate = new Date();
                            SimpleDateFormat df_output = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH);
                            String currDay = df_output.format(currDate);

                            int daysToNextAlarm;
                            if (model.getAlarmDays().contains(currDay) && calendar.after(now)){
                                daysToNextAlarm = 0;
                            } else {
                                daysToNextAlarm = getDaysToNextAlarm(model, currDay);
                            }

                            calendar.add(Calendar.DAY_OF_MONTH, daysToNextAlarm);

                            double progress = ((double)(calendar.getTimeInMillis() - now.getTimeInMillis())) / ((double)calendar.getTimeInMillis()) * 100;
                            int percentages = 100 - Integer.valueOf(String.valueOf(progress).split("\\D")[0]);
                            if (percentages >= 100){
                                percentages = 0;
                            }

                            NotificationBuilder.build(AlarmNotifyProgressService.super.getBaseContext(), model.toString(), R.drawable.alarm_notify, (int) model.getAlarmId(), percentages);
                        } else {
                            NotificationBuilder.build(AlarmNotifyProgressService.super.getBaseContext(), AlarmNotifyProgressService.super.getBaseContext().getResources().getString(R.string.no_internet),
                                    R.drawable.alarm_disabled,
                                    (int) model.getAlarmId());
                        }
                    }

                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        Log.i("I", "Thread stopped");
                    }
                }

                AlarmNotifyProgressService.mIsRunning = false;
                AlarmNotifyProgressService.this.stopService(new Intent(AlarmNotifyProgressService.this.getApplication(), AlarmNotifyProgressService.class));
            }
        });

        this.mProgressUpdater.start();
    }

    private int getDaysToNextAlarm(NotificationViewModelExtended alarmToStart, String currDay) {
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


    @Override
    public void onDestroy() {
        AlarmNotifyProgressService.mIsRunning = false;
        if(this.mProgressUpdater != null) {
            this.mProgressUpdater.interrupt();
            this.mProgressUpdater = null;
        }

        super.onDestroy();
    }
}
