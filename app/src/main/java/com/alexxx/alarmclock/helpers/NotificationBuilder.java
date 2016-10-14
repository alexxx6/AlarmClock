package com.alexxx.alarmclock.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.activities.MainActivity;
import com.alexxx.alarmclock.constants.Constants;

public class NotificationBuilder {
    public static void build(Context context, String text, int icon, int id) {
        NotificationBuilder.build(context, text, icon, id, -1);
    }

    public static void build(Context context, String text, int icon, int id, int progress){
            NotificationManager alarmNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.ALARM_ID_BK, id);

        PendingIntent contentIntent = PendingIntent.getActivity(context, id,
                intent, 0);

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setSmallIcon(icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentText(text)
                .setOngoing(true);

        if (progress != -1){
            alarmNotificationBuilder.setProgress(100, progress, false);
        }

        alarmNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(id, alarmNotificationBuilder.build());
    }
}
