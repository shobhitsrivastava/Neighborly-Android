package com.lendingapp.neighborly;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    private ArrayList<String> filters;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        filters = new ArrayList<String>();
        filters.add("lawnmower");
        filters.add("screwdriver");
        filters.add("paintbrush");
        filters.add("spoon");
        filters.add("pressure washer");
        filters.add("speaker");
        filters.add("ladder");
        filters.add("hammer");
        filters.add("wrench");
        filters.add("vacuum cleaner");
        filters.add("cooler");
        filters.add("fan");
        filters.add("chair");
        filters.add("table");
        filters.add("drill");
        filters.add("sander");
        filters.add("ski");

        listView = (ListView) findViewById(R.id.List);
        StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, filters);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FilterActivity.this, MapActivity.class);
                AppCompatTextView test = (AppCompatTextView) view;
                String toAdd = (String) test.getText();
                intent.putExtra("filter", toAdd);
                FilterActivity.this.startActivity(intent);
            }
        });


    }

    private class StableArrayAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
