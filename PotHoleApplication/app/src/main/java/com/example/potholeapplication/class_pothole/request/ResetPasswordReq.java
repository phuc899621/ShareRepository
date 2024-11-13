package com.example.potholeapplication.class_pothole.request;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordReq {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ResetPasswordReq(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
