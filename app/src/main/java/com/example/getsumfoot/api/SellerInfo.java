package com.example.getsumfoot.api;

import retrofit2.http.Url;

public class SellerInfo {
    private double Lat;

    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    private double Lng;

    private boolean is_open;
    private String keyword;

    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }
}
