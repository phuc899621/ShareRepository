package com.example.potholeapplication.Retrofit2;

import com.example.potholeapplication.class_pothole.response.CountResponse;
import com.example.potholeapplication.class_pothole.response.UserResponse;

import retrofit2.Response;

public interface CountAPICallBack {
    void onSuccess(Response<CountResponse> response);
    void onError(CountResponse errorResponse);
    void onFailure(Throwable t);
}
