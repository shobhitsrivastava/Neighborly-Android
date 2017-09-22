package com.lendingapp.neighborly;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kishan on 4/18/17.
 */

public class EditItemActivity extends AppCompatActivity {

    private String name;
    private String description;
    private String id;
    private boolean active;
    private Button update;
    private Button cancel;
    private Button delete;
    private SharedPreferences preferences;
    private Intent intent;
    private EditText namebox;
    private EditText descriptionbox;
    private boolean answer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edititem);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        intent = getIntent();
        name = intent.getStringExtra("name");
        description = intent.getStringExtra("description");
        id = intent.getStringExtra("id");
        active = intent.getBooleanExtra("active", true);
        namebox = (EditText) findViewById(R.id.name);
        descriptionbox = (EditText) findViewById(R.id.description);
        final RadioButton yes = (RadioButton) findViewById(R.id.yes);
        final RadioButton no = (RadioButton) findViewById(R.id.no);
        if(active) {
            yes.setChecked(true);
            no.setChecked(false);
        } else {
            yes.setChecked(false);
            no.setChecked(true);
        }
        namebox.setText(name);
        descriptionbox.setText(description);

        //find buttons
        update = (Button) findViewById(R.id.update);
        cancel = (Button) findViewById(R.id.cancel);
        delete = (Button) findViewById(R.id.deleteItem);

        //update listener
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(EditItemActivity.this, "Successfully updated", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EditItemActivity.this, ManageItemsActivity.class);
                        EditItemActivity.this.startActivity(intent);
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
                        Toast.makeText(EditItemActivity.this, "Update item failed", Toast.LENGTH_LONG).show();
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
                String address = "https://lending-legend.herokuapp.com/listings/" + id;
                XAuthRequest request = new XAuthRequest(Request.Method.PATCH, address, responseListener, request_data, errorListener, preferences.getString("token", ""));
                RequestQueue queue = Volley.newRequestQueue(EditItemActivity.this);
                queue.add(request);
            }
        });

        //cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditItemActivity.this, ManageItemsActivity.class);
                EditItemActivity.this.startActivity(intent);
            }
        });

        //delete button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditItemActivity.this);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(EditItemActivity.this, "Successfully deleted", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditItemActivity.this, ManageItemsActivity.class);
                                EditItemActivity.this.startActivity(intent);
                            }
                        };
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Toast.makeText(EditItemActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        };
                        JSONObject request_data = new JSONObject();
                        String address = "https://lending-legend.herokuapp.com/listings/" + id;
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, address, request_data, responseListener, errorListener) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("x-auth", preferences.getString("token", ""));
                                return headers;
                            }
                        };
                        RequestQueue queue = Volley.newRequestQueue(EditItemActivity.this);
                        queue.add(request);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
