package com.example.potholeapplication.class_pothole.request;

import com.google.gson.annotations.SerializedName;

public class SaveDistanceReq {
    @SerializedName("email")
    private String email;
    @SerializedName("totalDistances")
    private float totalDistances;

    public SaveDistanceReq(String email, float totalDistances) {
        this.email = email;
        this.totalDistances = totalDistances;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getTotalDistances() {
        return totalDistances;
    }

    public void setTotalDistances(float totalDistances) {
        this.totalDistances = totalDistances;
    }
}
