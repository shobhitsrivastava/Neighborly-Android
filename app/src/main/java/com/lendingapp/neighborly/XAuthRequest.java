package com.lendingapp.neighborly;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kishan on 4/16/17.
 */

public class XAuthRequest extends JsonObjectRequest {
    private String token;
    private int mStatusCode;

    public XAuthRequest(int type,String address, Response.Listener<JSONObject> listener, JSONObject request, Response.ErrorListener errorListener, String token) {
        super(type, address, request, listener, errorListener);
        this.token = token;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("x-auth", token);
        return headers;
    }
}