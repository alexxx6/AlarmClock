package com.alexxx.alarmclock.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.helpers.StringUtil;
import com.alexxx.alarmclock.models.DaysOfWeek;

import java.util.ArrayList;
import java.util.TreeSet;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private String mHeader;
    private TreeSet<String> mSelectedDayEnAbbreviations;
    private TextView mSelectedDaysTextView;
    private String[] mDayOfWeekAbbreviations;
    private boolean mIsFirstRotation;

    public ExpandableListViewAdapter(Context context, String header, TreeSet<String> selectedDayEnAbbreviations) {
        this.mContext = context;
        this.mHeader = header;
        this.mSelectedDayEnAbbreviations = selectedDayEnAbbreviations;
        this.mDayOfWeekAbbreviations = this.mContext.getResources().getStringArray(R.array.day_of_week_abbreviation);
        this.mIsFirstRotation = true;
    }

    public TreeSet<String> getSelectedDays() {
        if (this.mSelectedDayEnAbbreviations.isEmpty()){
            return null;
        }

        return mSelectedDayEnAbbreviations;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return "";
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.repeatability_list_item, null);
        }

        for (DaysOfWeek dayOfWeek : DaysOfWeek.values()) {
            String dayName = dayOfWeek.toString().toLowerCase();
            String idStr = dayName + "CheckBox";
            int id = mContext.getResources().getIdentifier(idStr, "id", mContext.getPackageName());
            CheckBox checkBox = (CheckBox) view.findViewById(id);
            checkBox.setText(String.format("%s.", this.mDayOfWeekAbbreviations[dayOfWeek.ordinal()]));
            checkBox.setOnCheckedChangeListener(this);
            String enAbbreviation = dayName.substring(0, 1).toUpperCase() + dayName.substring(1, 3);
            checkBox.setTag(enAbbreviation);
            if (this.mSelectedDayEnAbbreviations != null && this.mSelectedDayEnAbbreviations.contains(enAbbreviation)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }

        return view;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.mHeader;
    }


    @Override
    public int getGroupCount() {
        return 1;
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
            convertView = layoutInflater.inflate(R.layout.repeatability_list_header, null);
        }

        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.repeatabilityListTitle);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.arrowIndicatorImageView);
        mSelectedDaysTextView = (TextView) convertView.findViewById(R.id.selectedDaysTextView);

        listTitleTextView.setText(listTitle);
        changeSelectedDaysTextView();
        imageView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.expand_icon_24dp));
        if (isExpanded) {
            imageView.setRotation(90);
            this.mIsFirstRotation = false;
        } else if (!this.mIsFirstRotation){
            imageView.setRotation(360);
        }

        return convertView;
    }

    private void changeSelectedDaysTextView() {
        ArrayList<String> selectedDayAbbreviations = new ArrayList<>();
        for (DaysOfWeek daysOfWeek : DaysOfWeek.values()) {
            String dayName = daysOfWeek.toString().toLowerCase();
            String dayAbbreviation = dayName.substring(0, 1).toUpperCase() + dayName.substring(1, 3);
            if (this.mSelectedDayEnAbbreviations.contains(dayAbbreviation)) {
                selectedDayAbbreviations.add(this.mDayOfWeekAbbreviations[daysOfWeek.ordinal()]);
            }
        }

        mSelectedDaysTextView.setText(StringUtil.join(", ", selectedDayAbbreviations));
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        String dayName = (String) compoundButton.getTag();
        if (isChecked) {
            this.mSelectedDayEnAbbreviations.add(dayName);
        } else {
            this.mSelectedDayEnAbbreviations.remove(dayName);
        }

        this.changeSelectedDaysTextView();
    }
}