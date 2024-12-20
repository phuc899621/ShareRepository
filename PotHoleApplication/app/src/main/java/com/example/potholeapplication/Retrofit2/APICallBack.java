package com.example.potholeapplication.Retrofit2;

import com.example.potholeapplication.class_pothole.response.CountResponse;

import retrofit2.Response;

public interface APICallBack<T> {
    void onSuccess(Response<T> response);
    void onError(T errorResponse);
    void onFailure(Throwable t);
}
