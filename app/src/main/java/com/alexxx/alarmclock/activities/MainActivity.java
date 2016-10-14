package com.alexxx.alarmclock.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.fragments.AlarmFragment;
import com.alexxx.alarmclock.fragments.MainFragment;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int alarmId = super.getIntent().getIntExtra(Constants.ALARM_ID_BK, -1);
        if (alarmId == -1) {
            super.getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new MainFragment(), Constants.MAIN_FRAGMENT_TAG).commit();
        } else {
            AlarmFragment alarmFragment = new AlarmFragment();
            Bundle args = new Bundle();
            args.putLong(Constants.ALARM_ID_BK, alarmId);
            alarmFragment.setArguments(args);

            super.getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, alarmFragment, Constants.ALARM_FRAGMENT_TAG).commit();
        }
    }
}
