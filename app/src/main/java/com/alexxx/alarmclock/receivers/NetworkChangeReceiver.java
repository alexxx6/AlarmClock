package com.alexxx.alarmclock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alexxx.alarmclock.AlarmClockApp;
import com.alexxx.alarmclock.database.DatabaseManager;
import com.alexxx.alarmclock.database.models.NotificationViewModelExtended;
import com.alexxx.alarmclock.services.AlarmNotifyProgressService;

import java.util.List;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseManager databaseManager = ((AlarmClockApp) context.getApplicationContext()).getDatabaseManager();
        List<NotificationViewModelExtended> models = databaseManager.getActiveAndNeedOfNetAlarms();

        if (!models.isEmpty()) {
            Intent intentService = new Intent(context, AlarmNotifyProgressService.class);
            context.stopService(intentService);
            context.startService(intentService);
        }
    }
}
