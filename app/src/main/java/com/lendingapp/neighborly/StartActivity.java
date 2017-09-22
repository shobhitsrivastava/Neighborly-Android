package com.lendingapp.neighborly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kishan on 4/13/17.
 */

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean loggedin = preferences.getBoolean("logged-in", false);
        if (loggedin) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            StartActivity.this.startActivity(intent);
        } else {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            StartActivity.this.startActivity(intent);
        }
    }
}
