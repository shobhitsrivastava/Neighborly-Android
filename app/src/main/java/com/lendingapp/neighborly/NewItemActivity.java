package com.lendingapp.neighborly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class NewItemActivity extends AppCompatActivity {

    private String name;
    private String description;
    private boolean active;
    private Button add;
    private Button cancel;
    private SharedPreferences preferences;
    private EditText namebox;
    private EditText descriptionbox;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        namebox = (EditText) findViewById(R.id.name);
        descriptionbox = (EditText) findViewById(R.id.description);
        final RadioButton yes = (RadioButton) findViewById(R.id.yes);
        final RadioButton no = (RadioButton) findViewById(R.id.no);

        //find buttons
        add = (Button) findViewById(R.id.add);
        cancel = (Button) findViewById(R.id.cancel);

        //update listener
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(NewItemActivity.this, "Successfully created", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(NewItemActivity.this, ManageItemsActivity.class);
                        NewItemActivity.this.startActivity(intent);
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String toPrint = "";
                        for (int i = 0; i < error.networkResponse.data.length; i++) {
                            toPrint += (char) error.networkResponse.data[i];
                        }
                        System.out.println(toPrint);
                        Toast.makeText(NewItemActivity.this, "Add item failed", Toast.LENGTH_LONG).show();
                    }
                };
                JSONObject request_data = new JSONObject();
                name = namebox.getText().toString();
                description = descriptionbox.getText().toString();
                if (yes.isChecked() && !no.isChecked()) {
                    active = true;
                } else if (!yes.isChecked() && no.isChecked()) {
                    active = false;
                } else {
                    active = true;
                }
                try {
                    request_data.put("name", name);
                    request_data.put("description", description);
                    request_data.put("active", active);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String address = "https://lending-legend.herokuapp.com/listings/";
                XAuthRequest request = new XAuthRequest(Request.Method.POST, address, responseListener, request_data, errorListener, preferences.getString("token", ""));
                RequestQueue queue = Volley.newRequestQueue(NewItemActivity.this);
                queue.add(request);
            }
        });

        //cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewItemActivity.this, ManageItemsActivity.class);
                NewItemActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
