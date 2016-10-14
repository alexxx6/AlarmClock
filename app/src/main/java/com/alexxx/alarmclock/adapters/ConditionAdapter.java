package com.alexxx.alarmclock.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.models.ConditionItem;

import java.util.ArrayList;

public class ConditionAdapter extends ArrayAdapter<ConditionItem> {
    private Activity mContext;
    private int mLayoutResourceId;
    private ArrayList<ConditionItem> mData;

    public ConditionAdapter(Activity context, int layoutResourceId, ArrayList<ConditionItem> data) {
        super(context, layoutResourceId, data);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ConditionHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = mContext.getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, null, true);

            holder = new ConditionHolder();
            holder.img = (ImageView)row.findViewById(R.id.imageView);
            holder.title = (TextView)row.findViewById(R.id.conditionTextView);
            holder.isChecked = (CheckBox) row.findViewById(R.id.checkBox);

            row.setTag(holder);
        }
        else
        {
            holder = (ConditionHolder) row.getTag();
        }

        ConditionItem condition = mData.get(position);
        holder.title.setText(condition.getText());
        holder.img.setImageResource(condition.getImgId());
        holder.isChecked.setChecked(condition.isChecked());

        return row;
    }

    private static class ConditionHolder{
        ImageView img;
        TextView title;
        CheckBox isChecked;
    }
}