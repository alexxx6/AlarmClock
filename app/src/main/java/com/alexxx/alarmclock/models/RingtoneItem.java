package com.alexxx.alarmclock.models;

import android.net.Uri;
import android.widget.RadioButton;
import android.widget.TextView;

public class RingtoneItem {
    private TextView titleView;
    private Uri url;
    private RadioButton radioButton;

    public TextView getTitleView() {
        return titleView;
    }

    public void setTitleView(TextView title) {
        this.titleView = title;
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public void setRadioButton(RadioButton radioButton) {
        this.radioButton = radioButton;
    }

    public Uri getUrl() {
        return url;
    }

    public void setUrl(Uri url) {
        this.url = url;
    }
}
