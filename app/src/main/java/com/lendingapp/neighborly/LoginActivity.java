package com.lendingapp.neighborly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Kishan on 3/2/17.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private Button login_button;
    private Button register_button;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.email);
        pass=(EditText)findViewById(R.id.password);
        login_button = (Button) findViewById(R.id.email_sign_in_button);
        register_button = (Button) findViewById(R.id.registerButton);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable((Color.parseColor("#ffffff"))));
        getSupportActionBar().setTitle(Html.fromHtml("<font color ='#e12929'>Sign In</font>"));

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Email = email.getText().toString();
                final String Password = pass.getText().toString();

                if(CheckFieldValidation()) {
                    Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            String FirstName = "firstName";
                            String LastName = "lastName";
                            String token = "token";
                            boolean logged_in = false;
                            try {
                                FirstName = response.getString("firstName");
                                LastName = response.getString("lastName");
                                token = response.getJSONObject("headers").getString("X-Auth");
                                logged_in = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                address = response.getJSONObject("location").getString("address");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                address = "No current address found";
                            }
                            editor.putBoolean("logged-in", logged_in);
                            editor.putString("token", token);
                            editor.putString("firstname", FirstName);
                            editor.putString("lastname", LastName);
                            editor.putString("email", Email);
                            editor.putString("address", address);
                            editor.commit();
                            LoginActivity.this.startActivity(intent);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(toPrint)
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }
                    };
                    JSONObject request = new JSONObject();
                    try {
                        request.put("email", Email);
                        request.put("password", Password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LoginRequest loginRequest = new LoginRequest(responseListener, request, errorListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);
                }
            }
        });
    }

    private boolean CheckFieldValidation(){

        boolean valid = true;
        if(email.getText().toString().equals("")){
            email.setError("Please enter your email.");
            valid = false;
        } else if (pass.getText().toString().equals("")) {
            pass.setError("Please enter your password.");
            valid = false;
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        boolean loggedout = intent.getBooleanExtra("loggedout", false);
        if(loggedout) {
            Intent newintent = new Intent(Intent.ACTION_MAIN);
            newintent.addCategory(Intent.CATEGORY_HOME);
            startActivity(newintent);
        } else {
            super.onBackPressed();
        }
    }
}
