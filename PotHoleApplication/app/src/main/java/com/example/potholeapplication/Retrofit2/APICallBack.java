package com.example.potholeapplication.Retrofit2;

import com.example.potholeapplication.class_pothole.response.ApiResponse;

import retrofit2.Response;

public interface APICallBack {
    void onSuccess(Response<ApiResponse> response);
    void onError(ApiResponse errorResponse);
    void onFailure(Throwable t);
}
