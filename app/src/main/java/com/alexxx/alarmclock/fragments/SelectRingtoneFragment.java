package com.alexxx.alarmclock.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alexxx.alarmclock.AlarmClockApp;
import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.adapters.PaperAdapter;
import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.contracts.OnDataPasseListener;
import com.alexxx.alarmclock.models.RingtoneItem;

import java.util.ArrayList;
import java.util.List;

public class SelectRingtoneFragment extends Fragment implements OnDataPasseListener, View.OnClickListener {
    RingtoneItem mSelectedRingtone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_select_ringtone, container, false);

        String selectedRingtoneUrl = super.getArguments().getString(Constants.RINGTONE_URL_BK);

        ArrayList<String> headers = new ArrayList<>();
        headers.add("All");
        headers.add("Internal Storage");
        headers.add("External Storage");

        Uri[] storageUrls = {null, MediaStore.Audio.Media.INTERNAL_CONTENT_URI, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI};

        Button saveBtn = (Button) layout.findViewById(R.id.saveRingtoneBtn);
        Button closeBtn = (Button) layout.findViewById(R.id.closeRingtoneFragmentBtn);

        saveBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);

        ViewPager viewPager = (ViewPager) layout.findViewById(R.id.pager);
        viewPager.setAdapter(new PaperAdapter(super.getActivity().getSupportFragmentManager(), headers, storageUrls, selectedRingtoneUrl));

        return layout;
    }

    private void onSaveClick() {
        if (this.mSelectedRingtone != null) {
            ((AlarmClockApp) super.getActivity().getApplication()).setSelectedRingtone(this.mSelectedRingtone);
        }

        this.cleanChildFragments();
        super.getActivity().getSupportFragmentManager()
                .popBackStack();
    }

    private void onCancelClick() {
        this.cleanChildFragments();
        super.getActivity().getSupportFragmentManager().popBackStack();
    }

    private void cleanChildFragments(){
        List<Fragment> children = super.getActivity().getSupportFragmentManager().getFragments();
        for (Fragment child : children) {
            if (child instanceof PaperContentFragment){
                super.getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(child)
                        .commit();
            }
        }
    }

    @Override
    public void setData(Object data) {
        this.mSelectedRingtone = (RingtoneItem) data;
    }

    @Override
    public Object getData() {
        return this.mSelectedRingtone;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveRingtoneBtn:
                this.onSaveClick();
                break;
            case R.id.closeRingtoneFragmentBtn:
                this.onCancelClick();
                break;
        }
    }
}