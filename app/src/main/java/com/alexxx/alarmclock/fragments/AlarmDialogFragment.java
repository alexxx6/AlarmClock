package com.alexxx.alarmclock.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.alexxx.alarmclock.R;

public class AlarmDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.alarm_dialog_text_msg)
                .setPositiveButton(R.string.alarm_dialog_positive_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AlarmDialogFragment.this.getActivity().finish();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}