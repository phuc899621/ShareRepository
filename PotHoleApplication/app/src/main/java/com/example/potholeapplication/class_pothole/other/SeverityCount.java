package com.example.potholeapplication.class_pothole.other;

import com.google.gson.annotations.SerializedName;

public class SeverityCount {
    @SerializedName("total")
    private int total;
    @SerializedName("small")
    private int small;
    @SerializedName("medium")
    private int medium;
    @SerializedName("large")
    private int large;

    public SeverityCount(int total, int small, int medium, int large) {
        this.total = total;
        this.small = small;
        this.medium = medium;
        this.large = large;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSmall() {
        return small;
    }

    public void setSmall(int small) {
        this.small = small;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getLarge() {
        return large;
    }

    public void setLarge(int large) {
        this.large = large;
    }
}
