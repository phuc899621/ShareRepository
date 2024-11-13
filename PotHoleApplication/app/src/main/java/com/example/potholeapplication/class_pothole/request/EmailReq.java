package com.example.potholeapplication.class_pothole.request;

import com.google.gson.annotations.SerializedName;

public class EmailReq {
    @SerializedName("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailReq(String email) {
        this.email = email;
    }
}
