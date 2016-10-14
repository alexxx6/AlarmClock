package com.alexxx.alarmclock.constants;

import com.alexxx.alarmclock.fragments.AlarmFragment;
import com.alexxx.alarmclock.fragments.MainFragment;
import com.alexxx.alarmclock.fragments.SelectRingtoneFragment;
import com.alexxx.alarmclock.providers.LocationProvider;
import com.alexxx.alarmclock.providers.WeatherConditionProvider;
import com.alexxx.alarmclock.receivers.AlarmReceiver;

public class Constants {

    //region Default Values
    public static final String EMPTY_STRING = "";
    //endregion

    //region Intent Keys
    public static final String RINGTONE_URL_KEY = "ringtoneUrl";

    public static final String SELECTED_CONDITIONS_KEY = "selectedConditions";
    //endregion

    //region Log Tags
    public static final String ALARM_ACTIVITY_LOG_TAG = AlarmFragment.class.getName();

    public static final String LOCATION_PROVIDER_LOG_TAG = LocationProvider.class.getName();

    public static final String ALARM_RECEIVER_LOG_TAG = AlarmReceiver.class.getName();

    public static final String WEATHER_CONDITION_PROVIDER_LOG_TAG = WeatherConditionProvider.class.getName();
    //endregion

    //region Intent Request Codes
    public static final int CONDITION_SELECT_ACTIVITY_REQUEST_CODE = 2;
    //endregion

    //region Permission Request Codes
    public static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;

    public static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 2;
    //endregion

    //region Bundle Keys
    public static final String EXPANDABLE_LIST_DETAILS_BK = "expandableListDetail";

    public static final String ALARM_ID_BK = "id";

    public static final String RINGTONE_URL_BK = "ringtoneUrl";
    //endregion

    //region Fragment Tags
    public static final String ALARM_FRAGMENT_TAG = AlarmFragment.class.getName();

    public static final String SELECT_RINGTONE_FRAGMENT_TAG = SelectRingtoneFragment.class.getName();

    public static final String MAIN_FRAGMENT_TAG = MainFragment.class.getName();
    //endregion

    public final static int MAX_VOLUME = 100;

    public final static String DATE_FORMAT = "EEE";
}
