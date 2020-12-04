package com.example.getsumfoot.data;

import java.io.Serializable;

public class OrderInfo implements Serializable {
    private String menu_name;//(주문하는 메뉴 중 하나)
    public int  menu_count; //(총 개수)
    public String menu_price[];
    public int menu_num[];
    public int tot_price;

    public OrderInfo() {
    }

    public String[] getMenu_price() {
        return menu_price;
    }

    public int getTot_price() {
        return tot_price;
    }

    public void setTot_price(int tot_price) {
        this.tot_price = tot_price;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getMenu_count() {
        return menu_count;
    }

    public void setMenu_count(int menu_count) {
        this.menu_count = menu_count;
    }
}
