package com.example.getsumfoot.data;

public class SellerInfo {
    private double Lat;
    private double Lng;
    private boolean is_open;
    private String keyword;
    private String time_close;
    private String time_open;

    private Seller_Image sellerImage; //이미지 max 3개
    private Seller_Menu sellerMenu;


    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }

    public boolean isIs_open() {
        return is_open;
    }

    public void setIs_open(boolean is_open) {
        this.is_open = is_open;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTime_close() {
        return time_close;
    }

    public void setTime_close(String time_close) {
        this.time_close = time_close;
    }

    public String getTime_open() {
        return time_open;
    }

    public void setTime_open(String time_open) {
        this.time_open = time_open;
    }

    public Seller_Image getSellerImage() {
        return sellerImage;
    }

    public void setSellerImage(Seller_Image sellerImage) {
        this.sellerImage = sellerImage;
    }

    public Seller_Menu getSellerMenu() {
        return sellerMenu;
    }

    public void setSellerMenu(Seller_Menu sellerMenu) {
        this.sellerMenu = sellerMenu;
    }
}
