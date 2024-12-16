package com.example.potholeapplication.class_pothole.response;

import com.example.potholeapplication.class_pothole.other.PotholeCountByMonth;
import com.example.potholeapplication.class_pothole.other.Subinfo;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SubinfoResponse {
    @SerializedName("success")
    private boolean success=false;
    @SerializedName("message")
    private String Message;
    @SerializedName("data")
    private List<Subinfo> data;
    public SubinfoResponse() {
        this.success = false;
        this.Message = "";
        this.data = new ArrayList<>();
    }

    public SubinfoResponse(boolean success, String message, List<Subinfo> data) {
        this.success = success;
        Message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public List<Subinfo> getData() {
        return data;
    }

    public void setData(List<Subinfo> data) {
        this.data = data;
    }
}
