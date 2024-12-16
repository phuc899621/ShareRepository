package com.example.potholeapplication.class_pothole.other;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationClass {
    @SerializedName("type")
    String type;
    @SerializedName("coordinates")
    List<Double> coordinates;

    public LocationClass(List<Double> coordinates) {
        this.type = "Point";
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }
}
