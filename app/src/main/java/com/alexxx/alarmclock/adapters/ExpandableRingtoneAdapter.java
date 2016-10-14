package com.alexxx.alarmclock.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.contracts.OnDataPasseListener;
import com.alexxx.alarmclock.models.RingtoneItem;

import java.util.HashMap;
import java.util.List;

public class ExpandableRingtoneAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener {
    private final OnDataPasseListener mDataPasseListener;
    private Context mContext;
    private List<String> mExpandableListTitle;
    private HashMap<String, List<String[]>> mExpandableListDetail;
    private Ringtone mRingtoneManager;
    private RingtoneItem mSelectedRingtone;
    private String mLastRingtoneUrl;

    public ExpandableRingtoneAdapter(Context context, List<String> expandableListTitle,
                                     HashMap<String, List<String[]>> expandableListDetail, String lastRingtoneUrl) {
        this.mContext = context;
        this.mExpandableListTitle = expandableListTitle;
        this.mExpandableListDetail = expandableListDetail;
        this.mLastRingtoneUrl = lastRingtoneUrl;

        this.mDataPasseListener = (OnDataPasseListener) ((FragmentActivity)mContext)
                .getSupportFragmentManager()
                .findFragmentByTag(Constants.SELECT_RINGTONE_FRAGMENT_TAG);
    }

    private void setSelectedRingtone(RingtoneItem ringtone) {
        this.mSelectedRingtone = ringtone;
        this.mSelectedRingtone.getRadioButton().setChecked(true);
        this.mLastRingtoneUrl = ringtone.getUrl().toString();
        this.notifyDataSetChanged();
        this.mDataPasseListener.setData(ringtone);
    }

    public Ringtone getRingtoneManager() {
        return mRingtoneManager;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.mExpandableListDetail.get(this.mExpandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View view, ViewGroup parent) {
        final String[] ringtoneInfo = (String[]) getChild(listPosition, expandedListPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.expandable_list_select_ringtone_item, null);
        }

        if (ringtoneInfo.length == 2) {
            RingtoneItem ringtone = new RingtoneItem();
            ringtone.setTitleView((TextView) view.findViewById(R.id.ringtoneTextView));
            ringtone.setRadioButton((RadioButton) view.findViewById(R.id.radioButton));

            String ringtoneName = ringtoneInfo[0];
            String ringtoneUrl = ringtoneInfo[1];

            ringtone.getTitleView().setText(ringtoneName);
            ringtone.setUrl(Uri.parse(ringtoneUrl));
            ringtone.getRadioButton().setChecked(false);
            ringtone.getRadioButton().setVisibility(View.VISIBLE);
            RingtoneItem prevRingtone = (RingtoneItem) this.mDataPasseListener.getData();
            if ((prevRingtone != null && ringtone.getUrl().toString().equals(prevRingtone.getUrl().toString())) ||
                    (ringtoneUrl.equals(this.mLastRingtoneUrl))) {
                this.setSelectedRingtone(ringtone);
                this.mLastRingtoneUrl = "";
            }

            view.setTag(ringtone);
        } else {
            TextView ringtoneTextView = (TextView) view.findViewById(R.id.ringtoneTextView);
            ringtoneTextView.setText(ringtoneInfo[0]);

            RadioButton ringtoneRadioButton = (RadioButton) view.findViewById(R.id.radioButton);
            ringtoneRadioButton.setVisibility(View.GONE);
            view.setTag(null);
        }

        return view;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.mExpandableListDetail.get(this.mExpandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.mExpandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mExpandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_list_select_ringtone_header, null);
        }

        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleTextView.setGravity(Gravity.START);
        convertView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.white));
        if (isExpanded) {
            listTitleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_green_light));
        }

        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View view,
                             int groupPosition, int childPosition, long id) {
        RingtoneItem ringtone = (RingtoneItem)view.getTag();
        if (ringtone != null) {
            if (this.mSelectedRingtone != null) {
                this.mSelectedRingtone.getRadioButton().setChecked(false);
            }

            if (this.mRingtoneManager != null) {
                this.mRingtoneManager.stop();
            }

            this.mRingtoneManager = RingtoneManager.getRingtone(this.mContext, ringtone.getUrl());
            this.mRingtoneManager.play();

            this.setSelectedRingtone(ringtone);
        }

        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        if (this.mRingtoneManager != null) {
            this.mRingtoneManager.stop();
        }

        super.onGroupExpanded(groupPosition);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        if (this.mRingtoneManager != null) {
            this.mRingtoneManager.stop();
        }

        super.onGroupCollapsed(groupPosition);
    }
}