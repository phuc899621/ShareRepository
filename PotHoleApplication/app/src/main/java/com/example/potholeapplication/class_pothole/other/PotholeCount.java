package com.example.potholeapplication.class_pothole.other;

import com.google.gson.annotations.SerializedName;

public class PotholeCount {
    @SerializedName("pothole")
    private int pothole;
    @SerializedName("fixed_pothole")
    private int fixed_pothole;

    public PotholeCount(int pothole, int fixed_pothole) {
        this.pothole = pothole;
        this.fixed_pothole = fixed_pothole;
    }

    public int getPothole() {
        return pothole;
    }

    public void setPothole(int pothole) {
        this.pothole = pothole;
    }

    public int getFixed_pothole() {
        return fixed_pothole;
    }

    public void setFixed_pothole(int fixed_pothole) {
        this.fixed_pothole = fixed_pothole;
    }
}
