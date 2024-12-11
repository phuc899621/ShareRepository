package com.example.potholeapplication.class_pothole.request;

import com.google.gson.annotations.SerializedName;

public class PotholeSizeReq
{
    @SerializedName("size")
    String size;

    public PotholeSizeReq(String size) {
        this.size = size;
    }
}
