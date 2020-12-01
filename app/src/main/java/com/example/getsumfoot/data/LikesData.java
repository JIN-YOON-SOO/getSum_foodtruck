package com.example.getsumfoot.data;

public class LikesData {
    public String name;
    public String address;
    public String time;
    public String menu;
    public String image;
    public String isOpen;

    public LikesData(){}
    public LikesData(String name, String address, String menu, String isOpen, String time, String image){
        this.name = name;
        this.address = address;
        this.menu = menu;
        this.time = time;
        this.image = image;
        this.isOpen = isOpen;
    }
    public String getName(){return name;}
    public String getAddress(){return address;}
    public String getTime(){return time;}
    public String getMenu(){return menu;}
    public String getImage(){return image;}
    public String getIsOpen(){return isOpen;}
    public void setName(String name){ this.name = name;}
    public void setAddress(String address){ this.address = address;}
    public void setTime(String time){ this.time = time;}
    public void setMenu(String menu){ this.menu = menu;}
    public void setImage(String image){ this.image = image;}
    public void setIsOpen(String isOpen){this.isOpen = isOpen;}
}
