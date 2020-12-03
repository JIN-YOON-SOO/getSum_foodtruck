package com.example.getsumfoot.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;

public class Seller_Menu implements Serializable {
    public String menu_name;
    public String menu_description;
    public String menu_price;
    public String menu_id;

    public Seller_Menu(){}
    public Seller_Menu(String menu_name, String menu_description, String menu_price, String menu_id){
        this.menu_name = menu_name;
        this.menu_description = menu_description;
        this.menu_price = menu_price;
        this.menu_id = menu_id;
    }

    protected Seller_Menu(Parcel in) {
        menu_name = in.readString();
        menu_description = in.readString();
        menu_price = in.readString();
        menu_id = in.readString();
    }

    public String getMenuName() {
        return menu_name;
    }

    public void setMenuName(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getMenuDescription() {
        return menu_description;
    }

    public void setMenuDescription(String menuDescription) {
        this.menu_description = menuDescription;
    }

    public String getMenuPrice() {
        return menu_price;
    }

    public void setMenuPrice(String menuPrice) {
        this.menu_price = menuPrice;
    }

    public String getMenuId() {
        return menu_id;
    }
    public void setMenuId(String menuId) {
        this.menu_id = menuId;
    }
    public HashMap<String, Object> getMenuHash(){
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("menu_name",this.menu_name);
        hash.put("menu_description", this.menu_description);
        hash.put("menu_price", this.menu_price);
        hash.put("menu_id", this.menu_id);
        return hash;
    }
}
