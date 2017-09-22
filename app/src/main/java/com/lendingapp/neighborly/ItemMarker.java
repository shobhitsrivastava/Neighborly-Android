package com.lendingapp.neighborly;

/**
 * Created by 2shob on 4/19/2017.
 */

public class ItemMarker {
    private String userId;
    private double lat;
    private double lon;

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLat() {
        return lat;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }
    public double getLon() {
        return lon;
    }
}
