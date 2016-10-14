package com.alexxx.alarmclock.activities;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.fragments.AlarmDialogFragment;

import java.io.File;

public class DialogActivity extends AppCompatActivity {
    private MediaPlayer mPlayer;
    private PowerManager.WakeLock mWake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        KeyguardManager.KeyguardLock lock = ((KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE)).newKeyguardLock(KEYGUARD_SERVICE);
        PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        this.mWake = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");

        lock.disableKeyguard();
        this.mWake.acquire();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        DialogFragment newFragment = new AlarmDialogFragment();
        newFragment.setCancelable(false);
        newFragment.show(super.getSupportFragmentManager(), AlarmDialogFragment.class.getName());

        String path = super.getIntent().getStringExtra(Constants.RINGTONE_URL_KEY);
        path = path != null ? path : Constants.EMPTY_STRING;
        File file = new File(path);
        if (!file.exists()) {
            path = Constants.EMPTY_STRING;
        }

        Uri alarmUri = Uri.parse(path);
        if (alarmUri.toString().equals(Constants.EMPTY_STRING)) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        final float volume = (float) (1 - (Math.log(0) / Math.log(Constants.MAX_VOLUME)));

        mPlayer = MediaPlayer.create(this, alarmUri);
        mPlayer.setLooping(true);
        mPlayer.setVolume(volume, volume);
        mPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        while (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        if ((this.mWake != null) && !this.mWake.isHeld()) {
            this.mWake.acquire();
        }
    }
}
