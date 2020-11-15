package com.example.getsumfoot.data;

public class ImageData {
    public String image_uri;
    public String image_id;

    public ImageData(){}
    public ImageData(String image_uri, String image_id){
        this.image_uri = image_uri;
        this.image_id = image_id;
    }

    public String getImage_id() {
        return image_id;
    }
    public String getImage_uri(){
        return image_uri;
    }
}
