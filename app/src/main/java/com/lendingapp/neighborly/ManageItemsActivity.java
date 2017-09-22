package com.lendingapp.neighborly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kishan on 4/17/17.
 */

public class ManageItemsActivity extends AppCompatActivity {

    private ExpandableListView list;
    private ArrayList<String> itemNameList;
    private ExpandableListAdapter listAdapter;
    private HashMap<String, Item> child;
    private SharedPreferences preferences;
    private ProgressBar spinner;
    private Button add;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageitems);
        list = (ExpandableListView) findViewById(R.id.list);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        list.setVisibility(View.INVISIBLE);
        add = (Button) findViewById(R.id.addNewItem);
        Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                spinner.setVisibility(View.GONE);
                itemNameList = new ArrayList<String>();
                child = new HashMap<String, Item>();
                try {
                    for (int i= 0; i < response.length(); i++) {
                        Item toAdd = new Item();
                        toAdd.setId(response.getJSONObject(i).getString("_id"));
                        toAdd.setActive(response.getJSONObject(i).getBoolean("active"));
                        toAdd.setDescription(response.getJSONObject(i).getString("description"));
                        toAdd.setUserid(response.getJSONObject(i).getString("user"));
                        toAdd.setName(response.getJSONObject(i).getString("name"));
                        itemNameList.add(response.getJSONObject(i).getString("name"));
                        child.put(response.getJSONObject(i).getString("name"),toAdd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                nextStep();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinner.setVisibility(View.GONE);
                String toPrint = "";
                for (int i = 0; i < error.networkResponse.data.length; i++) {
                    toPrint += (char) error.networkResponse.data[i];
                }
                System.out.println(toPrint);
                Toast.makeText(ManageItemsActivity.this, toPrint, Toast.LENGTH_LONG).show();
            }
        };
        String address = "https://lending-legend.herokuapp.com/listings/me";
        JSONArray request_data = new JSONArray();
        GetRequest request = new GetRequest(address, preferences.getString("token", ""), responseListener, request_data, errorListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

        // add new item
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageItemsActivity.this, NewItemActivity.class);
                ManageItemsActivity.this.startActivity(intent);
            }
        });
    }

    private void nextStep() {
        listAdapter = new ExpandableListAdapter(this, itemNameList, child);
        list.setAdapter(listAdapter);
        list.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManageItemsActivity.this, MainActivity.class);
        ManageItemsActivity.this.startActivity(intent);
    }
}
