package com.alexxx.alarmclock.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alexxx.alarmclock.AlarmClockApp;
import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.activities.ConditionSelectActivity;
import com.alexxx.alarmclock.activities.MainActivity;
import com.alexxx.alarmclock.adapters.ExpandableListViewAdapter;
import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.constants.Messages;
import com.alexxx.alarmclock.customViews.NonScrollExpandableListView;
import com.alexxx.alarmclock.database.DatabaseManager;
import com.alexxx.alarmclock.database.models.AlarmModel;
import com.alexxx.alarmclock.helpers.InternetChecker;
import com.alexxx.alarmclock.helpers.NotificationBuilder;
import com.alexxx.alarmclock.helpers.PermissionUtil;
import com.alexxx.alarmclock.helpers.StringUtil;
import com.alexxx.alarmclock.models.DaysOfWeek;
import com.alexxx.alarmclock.models.RingtoneItem;
import com.alexxx.alarmclock.receivers.AlarmReceiver;
import com.alexxx.alarmclock.services.AlarmNotifyProgressService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TreeSet;

public class AlarmFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmReceiverPendingIntent;
    private TimePicker mAlarmTimePicker;
    private ExpandableListViewAdapter mExpandableListViewAdapter;
    private ToggleButton mToggleButton;
    private DatabaseManager mDatabaseManager;
    private AlarmModel mAlarm;
    private AlarmClockApp mAlarmClockApp;
    private boolean mIsOpenFromNotification;
    private boolean mPermissionIsAllowed;
    private boolean mReadExternalStoragePermissionIsAllowed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_alarm, container, false);

        this.mAlarmClockApp = ((AlarmClockApp) super.getActivity().getApplication());
        this.mDatabaseManager = this.mAlarmClockApp.getDatabaseManager();
        Long alarmId = this.mAlarmClockApp.getAlarmId();

        this.mPermissionIsAllowed = false;
        this.mReadExternalStoragePermissionIsAllowed = false;
        this.mIsOpenFromNotification = false;

        if (super.getArguments() != null && !super.getArguments().isEmpty()) {
            alarmId = super.getArguments().getLong(Constants.ALARM_ID_BK);
            this.mIsOpenFromNotification = true;
        }

        this.mAlarm = this.mDatabaseManager.getAlarmById(alarmId);
        RingtoneItem selectedRingtone = this.mAlarmClockApp.getSelectedRingtone();
        if (selectedRingtone != null) {
            this.mAlarm.setRingtoneName(selectedRingtone.getTitleView().getText().toString());
            this.mAlarm.setRingtoneUli(selectedRingtone.getUrl().toString());
        }

        TreeSet<String> selectedDays = this.mAlarmClockApp.getSelectedDays();
        if (selectedDays != null) {
            this.mAlarm.setDays(selectedDays);
        }

        this.mAlarmTimePicker = (TimePicker) layout.findViewById(R.id.alarmTimePicker);
        this.mToggleButton = (ToggleButton) layout.findViewById(R.id.alarmToggle);
        Button selectConditionsBtn = (Button) layout.findViewById(R.id.selectConditionsBtn);

        this.mToggleButton.setOnClickListener(this);
        selectConditionsBtn.setOnClickListener(this);

        this.mAlarmManager = (AlarmManager) super.getActivity().getSystemService(Context.ALARM_SERVICE);

        String ringtoneMame = this.mAlarm.getRingtoneName();
        String ringtoneUrl = this.mAlarm.getRingtoneUli() != null ? this.mAlarm.getRingtoneUli() : "";
        int lastAlarmHour = this.mAlarm.getHourOfDay();
        int lastArmMinute = this.mAlarm.getMinutes();

        File file = new File(ringtoneUrl);
        if (ringtoneMame == null || !file.exists()) {
            ringtoneMame = super.getResources().getString(R.string.default_str);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.mAlarmTimePicker.setHour(lastAlarmHour);
        } else {
            //noinspection deprecation
            this.mAlarmTimePicker.setCurrentHour(lastAlarmHour);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.mAlarmTimePicker.setMinute(lastArmMinute);
        } else {
            //noinspection deprecation
            this.mAlarmTimePicker.setCurrentMinute(lastArmMinute);
        }

        this.mToggleButton.setChecked(this.mAlarm.isOn());

        String[] ringtoneNames = new String[]{ringtoneMame};
        ArrayAdapter selectedRingtoneAdapter = new ArrayAdapter<>(super.getActivity(), R.layout.selectedd_ringtone_list_item, ringtoneNames);
        ListView listView = (ListView) layout.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setAdapter(selectedRingtoneAdapter);

        NonScrollExpandableListView repeatabilityListView = (NonScrollExpandableListView) layout.findViewById(R.id.repeatabilityExpandableListView);
        this.mExpandableListViewAdapter = new ExpandableListViewAdapter(super.getActivity(), super.getResources().getString(R.string.repeatability), this.mAlarm.getDays());
        repeatabilityListView.setAdapter(mExpandableListViewAdapter);

        return layout;
    }

    public void onToggleClicked(View view) {
        view.setEnabled(false);

        Intent myIntent = new Intent(AlarmFragment.super.getActivity(), AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ALARM_ID_BK, this.mAlarm.getId());
        myIntent.putExtras(bundle);
        this.mAlarmReceiverPendingIntent = PendingIntent.getBroadcast(super.getActivity(), this.mAlarm.getId(), myIntent, 0);

        if (((ToggleButton) view).isChecked()) {
            if (ActivityCompat.checkSelfPermission(super.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(super.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                super.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.ACCESS_LOCATION_PERMISSION_REQUEST_CODE);

                return;
            }

            this.turnOnAlarm();
        } else {
            this.turnOffAlarm();
        }
    }

    private void turnOffAlarm() {
        this.mAlarmManager.cancel(this.mAlarmReceiverPendingIntent);

        this.mAlarm.setOn(false);
        boolean isUpdated = this.mDatabaseManager.updateAlarm(this.mAlarm);
        if (!isUpdated) {
            Toast.makeText(super.getActivity(), super.getString(R.string.db_error_msg), Toast.LENGTH_SHORT).show();
            this.mToggleButton.setEnabled(true);

            return;
        }

        NotificationManager notificationManager = (NotificationManager) super.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(this.mAlarm.getId());

        Log.d(Constants.ALARM_ACTIVITY_LOG_TAG, Messages.ALARM_OFF_LOG_MSG);

        if (this.mIsOpenFromNotification){
            Intent i = new Intent(AlarmFragment.super.getActivity(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        }

        super.getActivity().getSupportFragmentManager().popBackStack();
    }

    private void turnOnAlarm() {
        TreeSet<String> selectedDays = this.mExpandableListViewAdapter.getSelectedDays();
        if (selectedDays == null) {
            this.mToggleButton.setChecked(false);
            Toast.makeText(super.getActivity(), super.getActivity().getResources().getString(R.string.repeatability_requirement), Toast.LENGTH_SHORT).show();
            this.mToggleButton.setEnabled(true);

            return;
        }

        Log.d(Constants.ALARM_ACTIVITY_LOG_TAG, Messages.ALARM_ON_LOG_MSG);

        Integer currHour;
        Integer currMinute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            currHour = mAlarmTimePicker.getHour();
            currMinute = mAlarmTimePicker.getMinute();
        } else {
            //noinspection deprecation
            currHour = mAlarmTimePicker.getCurrentHour();
            //noinspection deprecation
            currMinute = mAlarmTimePicker.getCurrentMinute();
        }


        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, currHour);
        calendar.set(Calendar.MINUTE, currMinute);
        calendar.set(Calendar.SECOND, 0);

        this.mAlarm.setHourOfDay(calendar.get(Calendar.HOUR_OF_DAY));
        this.mAlarm.setMinutes(calendar.get(Calendar.MINUTE));
        this.mAlarm.setDays(selectedDays);
        this.mAlarm.setOn(true);

        if (this.mDatabaseManager.isExistsInDb(this.mAlarm)){
            Toast.makeText(super.getActivity(), super.getString(R.string.exists_msg), Toast.LENGTH_SHORT).show();
            this.mToggleButton.setEnabled(true);

            return;
        }

        boolean isUpdated = this.mDatabaseManager.updateAlarm(this.mAlarm);
        if (!isUpdated) {
            Toast.makeText(super.getActivity(), super.getString(R.string.db_error_msg), Toast.LENGTH_SHORT).show();
            this.mToggleButton.setEnabled(true);

            return;
        }

        Date currDate = new Date();
        SimpleDateFormat df_output = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH);
        String currDay = df_output.format(currDate);
        int daysToNextAlarm;
        if (this.mAlarm.getDays().contains(currDay) && calendar.after(now)){
             daysToNextAlarm = 0;
        } else {
            daysToNextAlarm = getDaysToNextAlarm(this.mAlarm, currDay);
        }

        calendar.add(Calendar.DAY_OF_MONTH, daysToNextAlarm);

        this.mAlarmManager.cancel(this.mAlarmReceiverPendingIntent);
        this.mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), this.mAlarmReceiverPendingIntent);

        if (AlarmNotifyProgressService.isRunning()) {
            if (InternetChecker.isOnline(this.getContext()) || (this.mAlarm.getConditionCodes() == null || this.mAlarm.getConditionCodes().isEmpty())) {
                String alarmHourStr = this.mAlarm.getHourOfDay() > 9 ? String.valueOf(this.mAlarm.getHourOfDay()) : "0" + String.valueOf(this.mAlarm.getHourOfDay());
                String alarmMinuteStr = this.mAlarm.getMinutes() > 9 ? String.valueOf(this.mAlarm.getMinutes()) : "0" + String.valueOf(this.mAlarm.getMinutes());
                double progress = ((double)(calendar.getTimeInMillis() - now.getTimeInMillis())) / ((double)calendar.getTimeInMillis()) * 100;
                int percentages = 100 - Integer.valueOf(String.valueOf(progress).split("\\D")[0]);
                String notificationMsg = alarmHourStr + ":" + alarmMinuteStr + " " + this.getSelectedDayAbbrsForCurrLanguage(this.mAlarm.getDays());

                NotificationBuilder.build(this.getContext(), notificationMsg, R.drawable.alarm_notify, this.mAlarm.getId(), percentages);
            } else {
                NotificationBuilder.build(this.getContext(), this.getResources().getString(R.string.no_internet), R.drawable.alarm_disabled, this.mAlarm.getId());
            }
        } else {
            super.getActivity().startService(new Intent(AlarmFragment.super.getContext(), AlarmNotifyProgressService.class));
        }

        super.getActivity().getSupportFragmentManager().popBackStack();
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

    private String getSelectedDayAbbrsForCurrLanguage(TreeSet<String> selectedDayEnAbbreviations) {
        String[] dayOfWeekAbbreviations = this.getActivity().getResources().getStringArray(R.array.day_of_week_abbreviation);
        ArrayList<String> selectedDayAbbrsForCurrLanguage = new ArrayList<>();
        for (DaysOfWeek daysOfWeek : DaysOfWeek.values()) {
            String dayName = daysOfWeek.toString().toLowerCase();
            String dayAbbreviation = dayName.substring(0, 1).toUpperCase() + dayName.substring(1, 3);
            if (selectedDayEnAbbreviations.contains(dayAbbreviation)) {
                selectedDayAbbrsForCurrLanguage.add(dayOfWeekAbbreviations[daysOfWeek.ordinal()]);
            }
        }

        return StringUtil.join(", ", selectedDayAbbrsForCurrLanguage);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.listView) {
            if (ActivityCompat.checkSelfPermission(super.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                super.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);

                return;
            }

            this.startSelectRingtoneFragment();
        }
    }

    private void startSelectRingtoneFragment() {
        this.mAlarmClockApp.setSelectedDays(this.mExpandableListViewAdapter.getSelectedDays());

        SelectRingtoneFragment selectRingtoneFragment = new SelectRingtoneFragment();
        Bundle args = new Bundle();
        args.putString(Constants.RINGTONE_URL_BK, this.mAlarm.getRingtoneUli());
        selectRingtoneFragment.setArguments(args);

        super.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, selectRingtoneFragment, Constants.SELECT_RINGTONE_FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    private void startSelectWeatherConditionsActivity() {
        this.mAlarmClockApp.setSelectedDays(this.mExpandableListViewAdapter.getSelectedDays());

        Intent selectConditions = new Intent(AlarmFragment.super.getActivity(), ConditionSelectActivity.class);
        selectConditions.putExtra(Constants.SELECTED_CONDITIONS_KEY, this.mAlarm.getConditionCodes());
        super.startActivityForResult(selectConditions, Constants.CONDITION_SELECT_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.mReadExternalStoragePermissionIsAllowed = true;
                } else {
                    Toast.makeText(super.getActivity(), R.string.read_external_storage_permission_denied_msg, Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case Constants.ACCESS_LOCATION_PERMISSION_REQUEST_CODE: {
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    this.mPermissionIsAllowed = true;
                } else {
                    this.mToggleButton.setChecked(true);
                    Toast.makeText(super.getActivity(), R.string.access_location_permission_denied_msg, Toast.LENGTH_SHORT).show();
                }
            }

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectConditionsBtn:
                this.startSelectWeatherConditionsActivity();
                break;
            case R.id.alarmToggle:
                this.onToggleClicked(view);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == Constants.CONDITION_SELECT_ACTIVITY_REQUEST_CODE) {
            @SuppressWarnings("unchecked")
            TreeSet<String> codes = (TreeSet<String>) data.getSerializableExtra(Constants.SELECTED_CONDITIONS_KEY);
            this.mAlarm.setConditionCodes(codes);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.mPermissionIsAllowed){
            this.mPermissionIsAllowed = false;
            this.turnOnAlarm();
        }

        if (this.mReadExternalStoragePermissionIsAllowed){
            this.mReadExternalStoragePermissionIsAllowed = false;
            this.startSelectRingtoneFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.mAlarmClockApp.setSelectedRingtone(null);
        this.mAlarmClockApp.setSelectedDays(null);
    }
}