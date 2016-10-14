package com.alexxx.alarmclock.fragments;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alexxx.alarmclock.AlarmClockApp;
import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.adapters.RecyclerViewAdapter;
import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.contracts.RecycleViewLongClickedListener;
import com.alexxx.alarmclock.contracts.RecycleViewSelectedElementListener;
import com.alexxx.alarmclock.database.DatabaseManager;
import com.alexxx.alarmclock.database.models.AlarmModel;
import com.alexxx.alarmclock.database.models.RecyclerViewItemModel;
import com.alexxx.alarmclock.helpers.StringUtil;
import com.alexxx.alarmclock.models.DaysOfWeek;
import com.alexxx.alarmclock.receivers.AlarmReceiver;

import java.util.ArrayList;

public class MainFragment extends Fragment implements View.OnClickListener, RecycleViewSelectedElementListener,
        RecycleViewLongClickedListener {

    private ArrayList<RecyclerViewItemModel> mData;
    private RecyclerViewAdapter mAdapter;
    private DatabaseManager mDatabaseManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);

        this.mDatabaseManager = ((AlarmClockApp) super.getActivity().getApplication()).getDatabaseManager();

        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.recycleView);
        Button addButton = (Button) layout.findViewById(R.id.addButton);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(super.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        this.mData = new ArrayList<>(this.mDatabaseManager.getAllAlarmsAsRecyclerViewItemModel());

        for (RecyclerViewItemModel model : this.mData) {
            if (model.getAlarmDays() != null) {
                model.setAlarmDays(this.getSelectedDayAbbrsForCurrLanguage(model.getAlarmDays()));
            }
        }

        if (this.mData.isEmpty()){
            RecyclerViewItemModel insertedModel = this.mDatabaseManager.insertAlarm(new AlarmModel(0, 0, null, null, null, null, false));
            this.mData.add(insertedModel);
        }

        this.mAdapter = new RecyclerViewAdapter(this.mData);
        this.mAdapter.setRecycleViewSelectedElementListener(this);
        this.mAdapter.seRecycleViewLongClickedListener(this);

        recyclerView.setAdapter(this.mAdapter);

        ItemTouchHelper itemTouchHelper = this.createItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(recyclerView);

        addButton.setOnClickListener(this);

        return layout;
    }

    private ItemTouchHelper createItemTouchHelper(){

        return new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int elementPosition = viewHolder.getAdapterPosition();

                        MainFragment.this.removeAlarm(elementPosition);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addButton) {
            view.setEnabled(false);
            RecyclerViewItemModel insertedModel = this.mDatabaseManager.insertAlarm(new AlarmModel(0, 0, null, null, null, null, false));
            if (insertedModel == null){
                Toast.makeText(super.getActivity(), super.getString(R.string.db_error_msg), Toast.LENGTH_SHORT).show();

                return;
            }

            this.mData.add(insertedModel);
            this.mAdapter.notifyItemInserted(this.mData.size() - 1);
            view.setEnabled(true);
        }
    }

    @Override
    public void onItemSelected(int position) {

        long alarmId = this.mData.get(position).getAlarmId();
        Fragment alarmFragment = new AlarmFragment();
        ((AlarmClockApp) super.getActivity().getApplication()).setAlarmId(alarmId);

        super.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, alarmFragment, Constants.ALARM_FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onItemLongClicked(int position) {
        this.removeAlarm(position);
    }

    private void removeAlarm(int position){
        long alarmId = this.mData.get(position).getAlarmId();
        this.mDatabaseManager.deleteAlarm(alarmId);

        Intent myIntent = new Intent(MainFragment.super.getActivity(), AlarmReceiver.class);
        PendingIntent alarmReceiverPendingIntent = PendingIntent.getBroadcast(super.getActivity(), (int) alarmId, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) super.getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmReceiverPendingIntent);

        NotificationManager notificationManager = (NotificationManager) super.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel((int) alarmId);

        this.mData.remove(position);
        this.mAdapter.notifyDataSetChanged();
    }

    private String getSelectedDayAbbrsForCurrLanguage(String selectedDayEnAbbreviations) {
        String[] dayOfWeekAbbreviations = this.getActivity().getResources().getStringArray(R.array.day_of_week_abbreviation);
        ArrayList<String> selectedDayAbbrsForCurrLanguage = new ArrayList<>();
        for (DaysOfWeek daysOfWeek : DaysOfWeek.values()) {
            String dayName = daysOfWeek.toString().toLowerCase();
            String dayAbbreviation = dayName.substring(0, 1).toUpperCase() + dayName.substring(1, 3);
            if (selectedDayEnAbbreviations.contains(dayAbbreviation)) {
                selectedDayAbbrsForCurrLanguage.add(dayOfWeekAbbreviations[daysOfWeek.ordinal()]);
            }
        }

        return StringUtil.join(", ", selectedDayAbbrsForCurrLanguage);
    }
}
