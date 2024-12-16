package com.example.potholeapplication.class_pothole.request;

import com.google.gson.annotations.SerializedName;

public class DayReq {
    @SerializedName("month")
    private int month;
    @SerializedName("year")
    private int year;

    public DayReq(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
