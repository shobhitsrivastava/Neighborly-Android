package com.lendingapp.neighborly;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kishan on 4/17/17.
 */

public class GetRequest extends JsonArrayRequest{
    private String token;

    public GetRequest(String address, String token, Response.Listener<JSONArray> listener, JSONArray request, Response.ErrorListener errorListener) {
        super(0, address, request, listener, errorListener);
        this.token = token;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("x-auth", token);
        return headers;
    }
}
