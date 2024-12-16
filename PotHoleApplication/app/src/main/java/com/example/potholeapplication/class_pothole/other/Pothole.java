package com.example.potholeapplication.class_pothole.other;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Pothole {
    @SerializedName("location")
    LocationClass locationClass;
    @SerializedName("reportedBy")
    ReportedByClass reportedByClass;
    @SerializedName("reportedAt")
    Date reportedAt;
    @SerializedName("image")
    String image;
    @SerializedName("severity")
    String severity;
    @SerializedName("status")
    String status;

    public Pothole(LocationClass locationClass,
                       ReportedByClass reportedByClass, Date reportedAt,
                       String image, String severity, String status) {
        this.locationClass = locationClass;
        this.reportedByClass = reportedByClass;
        this.reportedAt = reportedAt;
        this.image = image;
        this.severity = severity;
        this.status = status;
    }

    public LocationClass getLocationClass() {
        return locationClass;
    }

    public void setLocationClass(LocationClass locationClass) {
        this.locationClass = locationClass;
    }

    public ReportedByClass getReportedByClass() {
        return reportedByClass;
    }

    public void setReportedByClass(ReportedByClass reportedByClass) {
        this.reportedByClass = reportedByClass;
    }

    public Date getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(Date reportedAt) {
        this.reportedAt = reportedAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
