package com.example.potholeapplication.class_pothole.request;

import com.google.gson.annotations.SerializedName;

public class UserVerificationReq {
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;

    public UserVerificationReq(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
