package com.example.potholeapplication.class_pothole.other;

import com.google.gson.annotations.SerializedName;

public class ReportedByClass {
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private String image;

    public ReportedByClass(String username, String email, String name, String image) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
