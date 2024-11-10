package com.example.potholeapplication.class_pothole;

import com.google.gson.annotations.SerializedName;

public class EmailCodeResponse {
    @SerializedName("code")
    private String code;

    public EmailCodeResponse(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
