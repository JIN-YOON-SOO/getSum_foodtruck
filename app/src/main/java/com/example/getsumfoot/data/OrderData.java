package com.example.getsumfoot.data;

public class OrderData {
    public String date;
    public String seller_name;
    public String menu_name;
    public String seller_address;
    public int menu_count;
    public int menu_sum;

    public OrderData(){}

    public OrderData(String date, String seller_name, String seller_address, String menu_name, int menu_count, int menu_sum){
        this.date = date;
        this.seller_name = seller_name;
        this.menu_name = menu_name;
        this.seller_address = seller_address;
        this.menu_count = menu_count;
        this.menu_sum = menu_sum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getSeller_address() {
        return seller_address;
    }

    public void setSeller_address(String seller_address) {
        this.seller_address = seller_address;
    }

    public int getMenu_count() {
        return menu_count;
    }

    public void setMenu_count(int menu_count) {
        this.menu_count = menu_count;
    }

    public int getMenu_sum() {
        return menu_sum;
    }

    public void setMenu_sum(int menu_sum) {
        this.menu_sum = menu_sum;
    }

}
