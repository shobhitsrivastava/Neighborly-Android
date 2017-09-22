package com.lendingapp.neighborly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private String userFN;
    private TextView welcomeString;
    private Button logout;
    private Button editProfile;
    private Button viewMap;
    private SharedPreferences preferences;
    private Button manageItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        welcomeString = (TextView) findViewById(R.id.welcome);
        userFN = preferences.getString("firstname", "");
        System.out.println("userFN: " + userFN);
        if (userFN.equals("")) {
            welcomeString.setText("Welcome!", TextView.BufferType.EDITABLE);
        } else {
            welcomeString.setText("Welcome, " + userFN + "!", TextView.BufferType.EDITABLE);
        }

        //all of the buttons
        logout = (Button) findViewById(R.id.logout);
        editProfile = (Button) findViewById(R.id.editProfile);
        viewMap = (Button) findViewById(R.id.viewMap);
        manageItems = (Button) findViewById(R.id.manageItems);


        //set up logout functionality
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("logged-in", false);
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra("loggedout", true);
                        MainActivity.this.startActivity(intent);
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Logout Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                };
                JSONObject request = new JSONObject();
                LogoutRequest logoutRequest = new LogoutRequest(responseListener, request, errorListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(logoutRequest);
            }
        });

        //set up editProfile functionality
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        manageItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("address", "").equals("No current address found")) {
                    Toast.makeText(MainActivity.this, "Need to set address in Edit Profile first", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, ManageItemsActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
