package com.lendingapp.neighborly;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kishan on 4/15/17.
 */

public class ProfilePicRequest extends JsonObjectRequest {
    private static final String PROFILEPIC_REQUEST_URL = "https://lending-legend.herokuapp.com/users/me/propic";
    private int type;
    private String token;

    public ProfilePicRequest(String token, int type, Response.Listener<JSONObject> listener, JSONObject request, Response.ErrorListener errorListener) {
        super(type, PROFILEPIC_REQUEST_URL, request, listener, errorListener);
        this.type = type;
        this.token = token;
    }

    @Override
    public String getBodyContentType() {
        if (type == 0) {
            return super.getBodyContentType();
        } else if (type == 1) {
            return "application/x-www-form-urlencoded";
        }
        return super.getBodyContentType();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-auth", token);
        if (type == 1 ) {
            headers.put("Content-Type", "application/x-www-form-urlencoded");
        }
        return headers;
    }
}
