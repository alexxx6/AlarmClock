package com.alexxx.alarmclock.adapters;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.fragments.PaperContentFragment;

import java.util.ArrayList;

public class PaperAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> mHeaders;
    private Uri[] mStorageUrls;
    private String mSelectedRingtoneUrl;

    public PaperAdapter(FragmentManager fragmentManager, ArrayList<String> headers, Uri[] storageUrls, String selectedRingtoneUrl) {
        super(fragmentManager);
        this.mHeaders = headers;
        this.mStorageUrls = storageUrls;
        this.mSelectedRingtoneUrl = selectedRingtoneUrl;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new PaperContentFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(Constants.EXPANDABLE_LIST_DETAILS_BK, new Uri[]{this.mStorageUrls[position]});
        args.putString(Constants.RINGTONE_URL_BK, this.mSelectedRingtoneUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return this.mHeaders.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.mHeaders.get(position);
    }
}