package com.example.getsumfoot.data;

import java.util.HashMap;

public class ImageData {
    public String image_uri;
    public String image_id;

    public ImageData(){}
    public ImageData(String image_uri, String uid){
        this.image_uri = image_uri;
        this.image_id = uid + "_" + System.currentTimeMillis();
    }

    public String getImageId() {
        return image_id;
    }
    public void setImageId(String image_id){
        this.image_id = image_id;
    }
    public String getImageUri(){
        return image_uri;
    }
    public void setImageUri(String image_uri){
        this.image_uri = image_uri;
    }
    public HashMap<String, Object> getImageHash(){
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("image_uri", this.image_uri);
        hash.put("image_id", this.image_id);
        return hash;
    }
}
