package com.example.potholeapplication.class_pothole.request;

import com.example.potholeapplication.class_pothole.response.LocationClass;
import com.google.gson.annotations.SerializedName;

public class AddPotholeReq {
    @SerializedName("location")
    LocationClass locationClass;
    @SerializedName("email")
    String email;
    @SerializedName("severity")
    String severity;

    public AddPotholeReq(LocationClass locationClass, String email, String severity) {
        this.locationClass = locationClass;
        this.email = email;
        this.severity = severity;
    }

    public LocationClass getLocationClass() {
        return locationClass;
    }

    public void setLocationClass(LocationClass locationClass) {
        this.locationClass = locationClass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
