package com.example.potholeapplication.Retrofit2;

import com.example.potholeapplication.class_pothole.response.UserResponse;

import retrofit2.Response;

public interface APICallBack {
    void onSuccess(Response<UserResponse> response);
    void onError(UserResponse errorResponse);
    void onFailure(Throwable t);
}
