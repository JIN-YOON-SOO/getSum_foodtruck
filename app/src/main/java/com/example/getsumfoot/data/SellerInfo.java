package com.example.getsumfoot.data;

import java.io.Serializable;
import java.util.ArrayList;

public class SellerInfo implements Serializable { //img, menu가 list
    private  Double Lat;
    private  Double Lng;
    private boolean is_open;
    private String name;
    private String keyword;
    private String address;
    private String time_close;
    private String time_open;

    private ArrayList<Seller_Image> sellerImage = new ArrayList<>(); //이미지 max 3개
    private ArrayList<Seller_Menu> sellerMenu= new ArrayList<>();

    public SellerInfo(Double lat, Double lng, boolean is_open, String name, String keyword, String address, String time_close, String time_open, ArrayList<Seller_Image> sellerImage, ArrayList<Seller_Menu> sellerMenu) {
        Lat = lat;
        Lng = lng;
        this.is_open = is_open;
        this.name = name;
        this.keyword = keyword;
        this.address = address;
        this.time_close = time_close;
        this.time_open = time_open;
        this.sellerImage = sellerImage;
        this.sellerMenu = sellerMenu;
    }

    public String getCheckOpen(){
        if(this.is_open==true)
            return "영업중";
        else
            return "영업준비중";
    }
    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public Double getLat() {
        return Lat;
    }

    public Double getLng() {
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

    public ArrayList<Seller_Image> getSellerImage() {
        return sellerImage;
    }

    public void setSellerImage(Seller_Image sellerImage) {
        this.sellerImage.add(sellerImage);
    }

    public ArrayList<Seller_Menu> getSellerMenu() {
        return sellerMenu;
    }

    public void setSellerMenu(Seller_Menu sellerMenu) {
        this.sellerMenu.add(sellerMenu);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
