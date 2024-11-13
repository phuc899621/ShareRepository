package com.example.potholeapplication.class_pothole.request;

import com.google.gson.annotations.SerializedName;

public class EditInfoReq {
    @SerializedName("username")
    private String username;
    @SerializedName("name")
    private String name;

    public EditInfoReq(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
