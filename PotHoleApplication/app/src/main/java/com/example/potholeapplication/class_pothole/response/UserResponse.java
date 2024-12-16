package com.example.potholeapplication.class_pothole.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserResponse {
    @SerializedName("success")
    private boolean success=false;
    @SerializedName("message")
    private String Message;
    @SerializedName("data")
    private List<User> data;
    public UserResponse() {
        this.success = false;
        this.Message = "";
        this.data = new ArrayList<>();
    }

    public UserResponse(boolean success, String message, List<User> data) {
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

    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
        this.data = data;
    }
}
