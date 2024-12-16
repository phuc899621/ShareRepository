package com.example.potholeapplication.Retrofit2;

import com.example.potholeapplication.class_pothole.other.SeverityCount;
import com.example.potholeapplication.class_pothole.response.CountResponse;
import com.example.potholeapplication.class_pothole.response.SeverityResponse;

import retrofit2.Response;

public interface SeverityAPICallBack {
    void onSuccess(Response<SeverityResponse> response);
    void onError(SeverityResponse errorResponse);
    void onFailure(Throwable t);
}
