package com.lendingapp.neighborly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kishan on 4/4/17.
 */

public class RegisterActivity extends AppCompatActivity {
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName  = (EditText) findViewById(R.id.lastName);
        email     = (EditText) findViewById(R.id.email);
        password  = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        register_button = (Button) findViewById(R.id.registerButton);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredFN = firstName.getText().toString().trim();
                String enteredLN = lastName.getText().toString().trim();
                final String enteredEmail = email.getText().toString().trim();
                String enteredP = password.getText().toString().trim();
                String enteredCP = confirmPassword.getText().toString().trim();

                //check if first name/last name is empty
                if (enteredFN.length() == 0) {
                    firstName.setError("Please enter a valid first name.");
                    return;
                }
                if (enteredLN.length() == 0) {
                    lastName.setError("Please enter a valid last name.");
                    return;
                }

                //check if email is valid.
                if (enteredEmail.length() == 0) {
                    email.setError("Please enter a valid email.");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
                    email.setError("Please enter a valid email.");
                    return;
                }

                //check if password is valid
                if (enteredP.length() == 0) {
                    password.setError("Please enter a valid password.");
                    return;
                }
                if (enteredP.length() < 6) {
                    password.setError("Password length must be at least 6 characters.");
                    return;
                }
                if (enteredCP.length() == 0) {
                    confirmPassword.setError("Please enter your password again.");
                    return;
                }
                if (!enteredCP.equals(enteredP)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.putExtra("email", enteredEmail);
                        RegisterActivity.this.startActivity(intent);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("Registration Failed").setNegativeButton("Retry", null).create().show();
                    }
                };
                JSONObject request = new JSONObject();
                try {
                    request.put("email", enteredEmail);
                    request.put("firstName", enteredFN);
                    request.put("lastName", enteredLN);
                    request.put("password", enteredP);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(request);
                RegisterRequest registerRequest = new RegisterRequest(responseListener, request, errorListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }

        });

    }

}
