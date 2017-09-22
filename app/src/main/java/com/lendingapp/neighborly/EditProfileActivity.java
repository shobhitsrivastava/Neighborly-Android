package com.lendingapp.neighborly;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by kishan on 4/15/17.
 */

public class EditProfileActivity extends AppCompatActivity {

    private String firstName;
    private String lastName;
    private String token;
    private TextView name;
    private ImageView pic;
    private Button address;
    private String actualAddress;
    private TextView shownAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        firstName = preferences.getString("firstname", "");
        lastName = preferences.getString("lastname", "");
        token = preferences.getString("token", "");
        name = (TextView) findViewById(R.id.name);
        name.setText(firstName + " " + lastName, TextView.BufferType.EDITABLE);
        pic = (ImageView) findViewById(R.id.profPic);
        address = (Button) findViewById(R.id.address);
        shownAddress = (TextView) findViewById(R.id.currentAddress);
        shownAddress.setText(preferences.getString("address", "No current address found"));

        //update address
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle("Update Address");
                final EditText input = new EditText(EditProfileActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String inputtedAddress = input.getText().toString();
                        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    actualAddress = response.getString("address");
                                    shownAddress.setText(actualAddress, TextView.BufferType.EDITABLE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("address",actualAddress);
                                    editor.commit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
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
                                Toast.makeText(EditProfileActivity.this, toPrint, Toast.LENGTH_LONG).show();
                            }
                        };
                        JSONObject request_data = new JSONObject();
                        try {
                            request_data.put("address", inputtedAddress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        XAuthRequest request = new XAuthRequest(7,
                                "https://lending-legend.herokuapp.com/users/me/location",
                                 responseListener, request_data, errorListener, token);
                        System.out.println(request_data);
                        RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
                        queue.add(request);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

    }

}
