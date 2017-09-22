package com.lendingapp.neighborly;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kishan on 4/4/17.
 */

public class RegisterRequest extends JsonObjectRequest {
    private static final String REGISTER_REQUEST_URL = "https://lending-legend.herokuapp.com/users";

    public RegisterRequest(Response.Listener<JSONObject> listener, JSONObject request, Response.ErrorListener errorListener) {
        super(1, REGISTER_REQUEST_URL, request, listener, errorListener);
    }

    @Override
    public String getBodyContentType() { return "application/json"; }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
