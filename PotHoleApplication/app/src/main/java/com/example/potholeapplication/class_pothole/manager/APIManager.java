package com.example.potholeapplication.class_pothole.manager;

import android.util.Log;

import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.Retrofit2.APIInterface;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.class_pothole.request.LoginReq;
import com.example.potholeapplication.class_pothole.response.ApiResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIManager {
    private static APIInterface apiInterface;
    private static void getApiInterface(){
        if(apiInterface==null){
            apiInterface= RetrofitServices.getApiService();
        }
    }
    //--------------------API Đăng nhập------------------------------
    public static void callLogin(LoginReq loginReq, APICallBack apiCallBack){
        getApiInterface();
        Call<ApiResponse> call = apiInterface.callLogin(loginReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    apiCallBack.onSuccess(response);
                }
                else{
                    String errorString;
                    ApiResponse apiResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        apiResponse=gson.fromJson(errorString, ApiResponse.class);
                        apiCallBack.onError(apiResponse);

                    } catch (IOException e) {
                        apiCallBack.onFailure(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                apiCallBack.onFailure(t);
            }
        });
    }
    //-----------------------API danh cho dang ki-----------------------------
    //----------------------API cho forgot password------------------------
    //------------------------API cho edit email-------------
    //---------------------API cho edit username va name--------
    //---------------------API cho edit password---------------------
    //---------------------API lấy hinh anh-----------------------
    //--------------------API lưu pothole-------------------


}
