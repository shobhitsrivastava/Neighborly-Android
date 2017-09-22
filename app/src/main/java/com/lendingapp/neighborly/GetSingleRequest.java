package com.lendingapp.neighborly;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 2shob on 4/19/2017.
 */

public class GetSingleRequest extends JsonObjectRequest {
    private String token;

    public GetSingleRequest(String address, String token, Response.Listener<JSONObject> listener, JSONObject request, Response.ErrorListener errorListener) {
        super(Method.GET, address, request, listener, errorListener);
        this.token = token;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("x-auth", token);
        return headers;
    }
}
