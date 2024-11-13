package com.example.potholeapplication.class_pothole.request;

import com.google.gson.annotations.SerializedName;

public class EditEmailRequest {
    @SerializedName("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EditEmailRequest(String email) {
        this.email = email;
    }
}
