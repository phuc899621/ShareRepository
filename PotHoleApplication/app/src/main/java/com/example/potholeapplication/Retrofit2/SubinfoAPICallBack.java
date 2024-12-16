package com.example.potholeapplication.Retrofit2;

import com.example.potholeapplication.class_pothole.response.CountResponse;
import com.example.potholeapplication.class_pothole.response.SubinfoResponse;

import retrofit2.Response;

public interface SubinfoAPICallBack {
    void onSuccess(Response<SubinfoResponse> response);
    void onError(SubinfoResponse errorResponse);
    void onFailure(Throwable t);
}
