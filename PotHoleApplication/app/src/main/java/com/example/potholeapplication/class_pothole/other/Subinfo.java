package com.example.potholeapplication.class_pothole.other;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;

public class Subinfo {
    @SerializedName("totalDistances")
    private float totalDistances;
    @SerializedName("totalReport")
    private int totalReport;
    @SerializedName("totalFixedPothole")
    private int totalFixedPothole;

    public Subinfo(float totalDistances, int totalReport, int totalFixedPothole) {
        this.totalDistances = totalDistances;
        this.totalReport = totalReport;
        this.totalFixedPothole = totalFixedPothole;
    }

    public float getTotalDistances() {
        return totalDistances;
    }

    public void setTotalDistances(float totalDistances) {
        this.totalDistances = totalDistances;
    }

    public int getTotalReport() {
        return totalReport;
    }

    public void setTotalReport(int totalReport) {
        this.totalReport = totalReport;
    }

    public int getTotalFixedPothole() {
        return totalFixedPothole;
    }

    public void setTotalFixedPothole(int totalFixedPothole) {
        this.totalFixedPothole = totalFixedPothole;
    }
}
