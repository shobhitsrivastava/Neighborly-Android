package com.lendingapp.neighborly;

import android.app.Application;
import android.content.Context;

/**
 * Created by kishan on 4/13/17.
 */

public class Neighborly extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        Neighborly.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Neighborly.context;
    }
}
