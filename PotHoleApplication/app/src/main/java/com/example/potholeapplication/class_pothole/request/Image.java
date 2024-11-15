package com.example.potholeapplication.class_pothole.request;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("image")
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Image(String image) {
        this.image = image;
    }
}
