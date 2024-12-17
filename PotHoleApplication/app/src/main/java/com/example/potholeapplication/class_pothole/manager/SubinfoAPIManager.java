package com.example.potholeapplication.class_pothole.manager;

import com.example.potholeapplication.Retrofit2.APIInterface;
import com.example.potholeapplication.Retrofit2.CountAPICallBack;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.Retrofit2.SubinfoAPICallBack;
import com.example.potholeapplication.class_pothole.request.DayReq;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.SaveDistanceReq;
import com.example.potholeapplication.class_pothole.response.CountResponse;
import com.example.potholeapplication.class_pothole.response.SubinfoResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubinfoAPIManager {
    private static APIInterface apiInterface;
    private static void getApiInterface(){
        if(apiInterface==null){
            apiInterface= RetrofitServices.getApiService();
        }
    }
    private static void setSubinfoAPIReturn(Call<SubinfoResponse> call, SubinfoAPICallBack subinfoAPICallBack){
        call.enqueue(new Callback<SubinfoResponse>() {
            @Override
            public void onResponse(Call<SubinfoResponse> call, Response<SubinfoResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    subinfoAPICallBack.onSuccess(response);
                }
                else{
                    String errorString;
                    SubinfoResponse subinfoResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        subinfoResponse=gson.fromJson(errorString, SubinfoResponse.class);
                        subinfoAPICallBack.onError(subinfoResponse);

                    } catch (IOException e) {
                        subinfoAPICallBack.onFailure(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<SubinfoResponse> call, Throwable t) {
                subinfoAPICallBack.onFailure(t);
            }
        });
    }

    public static void callGetSubinfo(EmailReq emailReq, SubinfoAPICallBack subinfoAPICallBack){
        getApiInterface();
        Call<SubinfoResponse> call = apiInterface.callGetSubinfo(emailReq);
        setSubinfoAPIReturn(call, subinfoAPICallBack);
    }
    public static void callSaveDistances(SaveDistanceReq saveDistanceReq,SubinfoAPICallBack subinfoAPICallBack){
        getApiInterface();
        Call<SubinfoResponse> call=apiInterface.callSaveDistances(saveDistanceReq);
        setSubinfoAPIReturn(call,subinfoAPICallBack);
    }
}
