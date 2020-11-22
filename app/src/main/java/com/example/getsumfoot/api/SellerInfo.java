package com.example.getsumfoot.api;

import retrofit2.http.Url;

public class SellerInfo {
    private double Lat;
    private Seller_Image sellerImage;
    private SellerMenu sellerMunu;

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

    public Seller_Image getSellerImage() {
        return sellerImage;
    }

    public void setSellerImage(Seller_Image sellerImage) {
        this.sellerImage = sellerImage;
    }

    public SellerMenu getSellerMunu() {
        return sellerMunu;
    }

    public void setSellerMunu(SellerMenu sellerMunu) {
        this.sellerMunu = sellerMunu;
    }
}
