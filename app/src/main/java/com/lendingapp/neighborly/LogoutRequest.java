package com.lendingapp.neighborly;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kishan on 4/13/17.
 */

public class LogoutRequest extends JsonObjectRequest {
    private static final String LOGOUT_REQUEST_URL = "https://lending-legend.herokuapp.com/users/me/token";
    private int mStatusCode;

    public LogoutRequest(Response.Listener<JSONObject> listener, JSONObject request, Response.ErrorListener errorListener) {
        super(3, LOGOUT_REQUEST_URL, request, listener, errorListener);
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Neighborly.getAppContext());
        String token = preferences.getString("token", "no token found");
        headers.put("x-auth", token);
        return headers;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        mStatusCode = response.statusCode;
        if (mStatusCode == 200) {
            return Response.error(null);
        } else {
            return super.parseNetworkResponse(response);
        }
    }
}
