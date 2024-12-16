package com.example.potholeapplication.class_pothole.response;

import com.example.potholeapplication.class_pothole.other.PotholeCountByMonth;
import com.example.potholeapplication.class_pothole.other.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CountResponse {//lớp chứa danh sách pothole tra ve theo từng thaáng cuủa năm
    @SerializedName("success")
    private boolean success=false;
    @SerializedName("message")
    private String Message;
    @SerializedName("data")
    private List<PotholeCountByMonth> data;
    public CountResponse() {
        this.success = false;
        this.Message = "";
        this.data = new ArrayList<>();
    }

    public CountResponse(boolean success, String message, List<PotholeCountByMonth> data) {
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

    public List<PotholeCountByMonth> getData() {
        return data;
    }

    public void setData(List<PotholeCountByMonth> data) {
        this.data = data;
    }
}
