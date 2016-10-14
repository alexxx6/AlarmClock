package com.alexxx.alarmclock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alexxx.alarmclock.R;
import com.alexxx.alarmclock.adapters.ConditionAdapter;
import com.alexxx.alarmclock.constants.Constants;
import com.alexxx.alarmclock.models.ConditionItem;

import java.util.ArrayList;
import java.util.TreeSet;

public class ConditionSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ConditionAdapter mAdapter;
    private TreeSet<String> mSelectedConditions;
    private ArrayList<ConditionItem> mConditionItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition_select);

        mConditionItems = new ArrayList<>();
        mSelectedConditions = new TreeSet<>();

        String[] arr = getResources().getStringArray(R.array.conditions);

        @SuppressWarnings("unchecked")
        TreeSet<String> conditionCodes = (TreeSet<String>) super.getIntent().getSerializableExtra(Constants.SELECTED_CONDITIONS_KEY);
        if (conditionCodes != null) {
            mSelectedConditions = conditionCodes;
        }

        int index = 0;
        for (String txt : arr) {
            int id = getResources().getIdentifier("a" + index, "mipmap",
                getPackageName());
            ConditionItem conditionItem = new ConditionItem(id, txt, index);
            if (conditionCodes != null && conditionCodes.contains(String.valueOf(index))) {
                conditionItem.setChecked(true);
            }

            mConditionItems.add(conditionItem);
            index++;
        }

        mAdapter = new ConditionAdapter(this, R.layout.condition_list_item, mConditionItems);
        ListView listView = (ListView) findViewById(R.id.conditions_listView);
        listView.setOnItemClickListener(this);

        listView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ConditionItem condition = mConditionItems.get(position);

        if (condition.isChecked()) {
            condition.setChecked(false);
            ConditionSelectActivity.this.mSelectedConditions.remove(condition.getCode().toString());
        } else {
            condition.setChecked(true);
            ConditionSelectActivity.this.mSelectedConditions.add(condition.getCode().toString());
        }

        mAdapter.notifyDataSetChanged();
    }

    public void onSaveConditionsClick(View view) {
        if (this.mSelectedConditions != null) {
            Intent intent = this.getIntent();
            intent.putExtra(Constants.SELECTED_CONDITIONS_KEY, this.mSelectedConditions);
            this.setResult(AppCompatActivity.RESULT_OK, intent);
        }

        this.finish();
    }

    public void onCancelConditionsClick(View view) {
        this.finish();
    }
}
