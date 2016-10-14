package com.alexxx.alarmclock.fragments;

import android.database.Cursor;
import android.database.MergeCursor;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.adapters.ExpandableRingtoneAdapter;
import com.alexxx.alarmclock.constants.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaperContentFragment extends Fragment {
    ExpandableRingtoneAdapter mExpandableRingtoneAdapter;
    ExpandableListView mExpandableListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paper_content, container, false);

        Uri[] storageUrls = (Uri[]) super.getArguments().getParcelableArray(Constants.EXPANDABLE_LIST_DETAILS_BK);
        String lastRingtoneUrl = super.getArguments().getString(Constants.RINGTONE_URL_BK);
        Uri storageUrl = null;
        if (storageUrls != null) {
            storageUrl = storageUrls[0];
        }
        HashMap<String, List<String[]>> expandableListDetail = this.getContentData(storageUrl);
        if (view != null) {
            mExpandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        }

        if (this.mExpandableListView != null && expandableListDetail != null) {
            List<String> expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
            this.mExpandableRingtoneAdapter = new ExpandableRingtoneAdapter(super.getActivity(), expandableListTitle, expandableListDetail, lastRingtoneUrl);
            this.mExpandableListView.setAdapter(this.mExpandableRingtoneAdapter);
            this.mExpandableListView.setOnChildClickListener(this.mExpandableRingtoneAdapter);
        }

        return view;
    }

    @Override
    public void onStop() {
        this.stopRingtoneManager();

        super.onStop();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser){
            this.stopRingtoneManager();
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    private void stopRingtoneManager(){
        if (this.mExpandableRingtoneAdapter == null){
            return;
        }

        Ringtone ringtoneManager = this.mExpandableRingtoneAdapter.getRingtoneManager();
        if (ringtoneManager != null) {
            ringtoneManager.stop();
        }
    }

    private HashMap<String, List<String[]>> getContentData(Uri storageUrl) {
        HashMap<String, List<String[]>> audioMedia = new HashMap<>();
        String[] mediaTypes = {MediaStore.Audio.Media.IS_MUSIC, MediaStore.Audio.Media.IS_RINGTONE, MediaStore.Audio.Media.IS_ALARM, MediaStore.Audio.Media.IS_NOTIFICATION};

        for (String mediaType : mediaTypes) {
            String listHeader = mediaType.substring(3).substring(0, 1).toUpperCase() + mediaType.substring(4);
            audioMedia.put(listHeader, new ArrayList<String[]>());
            Cursor audioMediaCursor;
            if (storageUrl == null){
                Cursor internalAudioMediaCursor = this.geAudioMediaCursor(mediaType, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
                Cursor externalAudioMediaCursor = this.geAudioMediaCursor(mediaType, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                audioMediaCursor = new MergeCursor(new Cursor[]{internalAudioMediaCursor, externalAudioMediaCursor});
            } else {
                audioMediaCursor = this.geAudioMediaCursor(mediaType, storageUrl);
            }

            while (audioMediaCursor.moveToNext()) {
                String[] data = new String[2];
                data[0] = audioMediaCursor.getString(0);
                data[1] = audioMediaCursor.getString(1);
                audioMedia.get(listHeader).add(data);
            }

            audioMediaCursor.close();

            if (audioMedia.get(listHeader).size() == 0) {
                audioMedia.get(listHeader).add(new String[]{super.getResources().getString(R.string.no_items)});
            }
        }

        return audioMedia;
    }

    private Cursor geAudioMediaCursor(String mediaType, Uri storage) {
        String sqlSelection = mediaType + " != 0";

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
        };

        return super.getActivity().getContentResolver().query(storage, projection, sqlSelection, null, null);
    }
}
