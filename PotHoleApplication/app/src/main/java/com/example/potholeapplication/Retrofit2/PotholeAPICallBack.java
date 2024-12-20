package com.example.potholeapplication.Retrofit2;

import com.example.potholeapplication.class_pothole.response.CountResponse;
import com.example.potholeapplication.class_pothole.response.PotholeResponse;

import retrofit2.Response;

public interface PotholeAPICallBack {
    void onSuccess(Response<PotholeResponse> response);
    void onError(PotholeResponse errorResponse);
    void onFailure(Throwable t);
}
