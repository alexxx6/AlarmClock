package com.alexxx.alarmclock.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.contracts.RecycleViewLongClickedListener;
import com.alexxx.alarmclock.contracts.RecycleViewSelectedElementListener;
import com.alexxx.alarmclock.database.models.RecyclerViewItemModel;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<RecyclerViewItemModel> mAdapterData;
    private static RecycleViewSelectedElementListener mRecycleViewSelectedElementListener;
    private static RecycleViewLongClickedListener mRecycleViewLongClickedListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mAlarmTimeTextView;
        TextView mAlarmDaysTextView;
        Switch mSwitch;

        public ViewHolder(View itemView) {
            super(itemView);

            this.mAlarmTimeTextView = (TextView) itemView.findViewById(R.id.alarmTimeTextView);
            this.mAlarmDaysTextView = (TextView) itemView.findViewById(R.id.alarmDaysTextView);
            this.mSwitch = (Switch) itemView.findViewById(R.id.alarmSwitch);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecyclerViewAdapter.mRecycleViewSelectedElementListener.onItemSelected(ViewHolder.this.getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {
                    RecyclerViewAdapter.mRecycleViewLongClickedListener.onItemLongClicked(ViewHolder.this.getAdapterPosition());

                    return true;
                }
            });
        }
    }

    public RecyclerViewAdapter(ArrayList<RecyclerViewItemModel> data) {
        this.mAdapterData = data;
    }

    public void setRecycleViewSelectedElementListener(RecycleViewSelectedElementListener recycleViewSelectedElementListener) {
        RecyclerViewAdapter.mRecycleViewSelectedElementListener = recycleViewSelectedElementListener;
    }

    public void seRecycleViewLongClickedListener(RecycleViewLongClickedListener recycleViewLongClickedListener) {
        RecyclerViewAdapter.mRecycleViewLongClickedListener = recycleViewLongClickedListener;
    }

    @Override
    public int getItemCount() {
        return mAdapterData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder != null) {
            RecyclerViewItemModel item = mAdapterData.get(position);
            holder.mAlarmTimeTextView.setText(item.getAlarmTime());
            holder.mAlarmDaysTextView.setText(item.getAlarmDays());
            holder.mSwitch.setChecked(item.isAlarmOn());
        }
    }
}
