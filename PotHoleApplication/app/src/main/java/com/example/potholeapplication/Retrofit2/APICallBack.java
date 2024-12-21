package com.example.potholeapplication.Retrofit2;

import retrofit2.Response;

public interface APICallBack<T> {
    void onSuccess(Response<T> response);
    void onError(T errorResponse);
    void onFailure(Throwable t);
}
