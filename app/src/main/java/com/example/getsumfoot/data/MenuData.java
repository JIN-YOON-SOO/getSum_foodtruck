package com.example.getsumfoot.data;

public class MenuData {
    public String menu_name;
    public String menu_description;
    public int menu_price;

    public MenuData(){}
    public MenuData(String menu_name, String menu_description, int menu_price){
        this.menu_name = menu_name;
        this.menu_description = menu_description;
        this.menu_price = menu_price;
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

    public int getMenuPrice() {
        return menu_price;
    }

    public void setMenuPrice(int menuPrice) {
        this.menu_price = menuPrice;
    }
}
