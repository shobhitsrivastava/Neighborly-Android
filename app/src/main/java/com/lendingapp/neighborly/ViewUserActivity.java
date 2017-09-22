package com.lendingapp.neighborly;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import android.widget.ExpandableListAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kishan on 4/20/17.
 */

public class ViewUserActivity extends AppCompatActivity {
    private Intent mIntent;
    private SharedPreferences preferences;
    private String token;
    private TextView name;
    private TextView email;
    final private static String URL = "https://lending-legend.herokuapp.com/";
    private RequestQueue queue;
    private ExpandableListView list;
    private com.lendingapp.neighborly.ExpandableListAdapter adapter;
    private List<String> header;
    private HashMap<String, Item> child;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewprofile);
        mIntent = this.getIntent();
        String id = mIntent.getStringExtra("id");
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = preferences.getString("token", "notfound");
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        list = (ExpandableListView) findViewById(R.id.list);
        getUserData(id);
        getListingData(id);
    }

    private void getUserData(String id) {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    name.setText(response.getString("firstName") + " " + response.getString("lastName"));
                    email.setText(response.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
        JSONObject request_data = new JSONObject();
        String address = URL + "user/" + id;
        XAuthRequest request = new XAuthRequest(Request.Method.GET, address, listener, request_data, errorListener, token);
        queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
        queue.start();
    }

    private void getListingData(String id) {
        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                header = new ArrayList<String>();
                child = new HashMap<String, Item>();
                try {
                    for (int i= 0; i < response.length(); i++) {
                        Item toAdd = new Item();
                        toAdd.setId(response.getJSONObject(i).getString("_id"));
                        toAdd.setActive(response.getJSONObject(i).getBoolean("active"));
                        toAdd.setDescription(response.getJSONObject(i).getString("description"));
                        toAdd.setUserid(response.getJSONObject(i).getString("user"));
                        toAdd.setName(response.getJSONObject(i).getString("name"));
                        header.add(response.getJSONObject(i).getString("name"));
                        child.put(response.getJSONObject(i).getString("name"),toAdd);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                nextStep();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
        JSONArray request_data = new JSONArray();
        String address = URL + "listings/by/" + id;
        GetRequest request = new GetRequest(address,token, listener, request_data, errorListener);
        queue.add(request);
    }


    private void nextStep() {
        adapter = new com.lendingapp.neighborly.ExpandableListAdapter(getApplicationContext(), header, child);
        adapter.setViewOnly(true);
        list.setAdapter(adapter);
    }
}
