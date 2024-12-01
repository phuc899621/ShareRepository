package com.example.potholeapplication.class_pothole;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("type")
    private String type;

    @SerializedName("data")
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Image(String type, String data) {
        this.type = type;
        this.data = data;
    }

}
