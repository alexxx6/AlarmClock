package com.alexxx.alarmclock.receivers;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.alexxx.alarmclock.AlarmClockApp;
import com.alexxx.alarmclock.BuildConfig;
import com.alexxx.alarmclock.activities.DialogActivity;
import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.constants.Messages;
import com.alexxx.alarmclock.database.DatabaseManager;
import com.alexxx.alarmclock.database.models.AlarmModel;
import com.alexxx.alarmclock.helpers.InternetChecker;
import com.alexxx.alarmclock.models.DaysOfWeek;
import com.alexxx.alarmclock.providers.LocationProvider;
import com.alexxx.alarmclock.providers.WeatherConditionProvider;
import com.alexxx.alarmclock.providers.WoeidProvider;
import com.alexxx.alarmclock.services.AlarmNotifyProgressService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TreeSet;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        int alarmId = intent.getIntExtra(Constants.ALARM_ID_BK, -1);
        if (alarmId != -1) {
            Date currDate = new Date();
            SimpleDateFormat df_output = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH);
            final String currDay = df_output.format(currDate);

            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(currDate);

            final DatabaseManager databaseManager = ((AlarmClockApp) context.getApplicationContext()).getDatabaseManager();
            final AlarmModel alarm = databaseManager.getAlarmById(alarmId);

            if (alarm.isOn() && alarm.getDays().contains(currDay) && alarm.getHourOfDay() == (calendar.get(Calendar.HOUR_OF_DAY)) && alarm.getMinutes() == (calendar.get(Calendar.MINUTE))) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean toStart = true;
                        try {
                            TreeSet<String> conditionCodes = alarm.getConditionCodes();
                            if (!conditionCodes.isEmpty()) {
                                if (InternetChecker.isOnline(context)) {
                                    Location location = getLocation(context);

                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();

                                    String woeid = WoeidProvider.getWoeiid(latitude, longitude);
                                    String conditionCode = WeatherConditionProvider.getConditionCode(woeid);

                                    if (!conditionCodes.contains(conditionCode)) {
                                        toStart = false;
                                    }
                                } else {
                                    toStart = false;
                                }
                            }

                            if (toStart) {
                                Intent dialogIntent = new Intent(context, DialogActivity.class);
                                dialogIntent.putExtra(Constants.RINGTONE_URL_KEY, alarm.getRingtoneUli());
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(dialogIntent);
                            }

                        } catch (Exception e) {
                            Log.e(Constants.ALARM_RECEIVER_LOG_TAG, e.getMessage());
                        } finally {
                            Intent myIntent = new Intent(context, AlarmReceiver.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(Constants.ALARM_ID_BK, alarm.getId());
                            myIntent.putExtras(bundle);
                            PendingIntent alarmReceiverPendingIntent = PendingIntent.getBroadcast(context, alarm.getId(), myIntent, 0);

                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                            Calendar now = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, alarm.getHourOfDay());
                            calendar.set(Calendar.MINUTE, alarm.getMinutes());
                            calendar.set(Calendar.SECOND, 0);

                            int daysToNextAlarm;
                            if (alarm.getDays().contains(currDay) && calendar.after(now)) {
                                daysToNextAlarm = 0;
                            } else {
                                daysToNextAlarm = getDaysToNextAlarm(alarm, currDay);
                            }

                            calendar.add(Calendar.DAY_OF_MONTH, daysToNextAlarm);

                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmReceiverPendingIntent);

                            Intent intentService = new Intent(context, AlarmNotifyProgressService.class);
                            context.stopService(intentService);
                            context.startService(intentService);
                        }
                    }
                }).start();
            }
        }
    }

    private int getDaysToNextAlarm(AlarmModel alarmToStart, String currDay) {
        boolean isFound = false;
        int daysToNextAlarm = 0;
        for (DaysOfWeek day : DaysOfWeek.values()) {
            String dayName = day.toString().toLowerCase();
            String enAbbreviation = dayName.substring(0, 1).toUpperCase() + dayName.substring(1, 3);
            if (currDay.equals(enAbbreviation)) {
                isFound = true;
            }

            if (!currDay.equals(enAbbreviation) && alarmToStart.getDays().contains(enAbbreviation) && isFound) {
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
                if (currDay.equals(enAbbreviation) || alarmToStart.getDays().contains(enAbbreviation)) {
                    break;
                }

                daysToNextAlarm++;
            }
        }
        return daysToNextAlarm;
    }

    private Location getLocation(final Context context) throws InterruptedException {
        final Location[] location = new Location[1];

        new LocationProvider(context) {
            @Override
            public void onConnected(Bundle bundle) {
                if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                location[0] = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);
            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult result) {
                Log.i(Constants.ALARM_RECEIVER_LOG_TAG, String.format(Messages.ALARM_RECEIVER_CONNECTION_FAILED_LOG_MSG, result.getErrorCode()));
            }
        };

        if (location[0] == null) {
            location[0] = LocationProvider.getLastKnownLocation(context);
        }

        if (location[0] == null && BuildConfig.DEBUG) {
            Location slivenLocation = new Location("dummyprovider");
            slivenLocation.setLatitude(42.68583);
            slivenLocation.setLongitude(26.32917);

            location[0] = slivenLocation;
        }


        return location[0];
    }
}