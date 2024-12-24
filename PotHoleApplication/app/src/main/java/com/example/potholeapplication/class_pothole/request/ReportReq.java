package com.example.potholeapplication.class_pothole.request;

import com.example.potholeapplication.class_pothole.other.LocationClass;
import com.google.gson.annotations.SerializedName;

public class ReportReq {
    @SerializedName("email")
    private String email;
    @SerializedName("description")
    private String description;
    @SerializedName("severity")
    private String severity;
    @SerializedName("location")
    private LocationClass location;

    public ReportReq(String email, String description, String severity, LocationClass location) {
        this.email = email;
        this.description = description;
        this.severity = severity;
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocationClass getLocation() {
        return location;
    }

    public void setLocation(LocationClass location) {
        this.location = location;
    }
}
