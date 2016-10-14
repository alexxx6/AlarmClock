package com.alexxx.alarmclock.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

public class InternetChecker {
    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
}
