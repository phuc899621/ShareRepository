package com.example.potholeapplication.class_pothole.manager;

import com.example.potholeapplication.Retrofit2.APIInterface;
import com.example.potholeapplication.Retrofit2.CountAPICallBack;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.class_pothole.request.DayReq;
import com.example.potholeapplication.class_pothole.response.CountResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountAPIManager {
    private static APIInterface apiInterface;
    private static void getApiInterface(){
        if(apiInterface==null){
            apiInterface= RetrofitServices.getApiService();
        }
    }
    private static void setCountAPIReturn(Call<CountResponse> call, CountAPICallBack countAPICallBack){
        call.enqueue(new Callback<CountResponse>() {
            @Override
            public void onResponse(Call<CountResponse> call, Response<CountResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    countAPICallBack.onSuccess(response);
                }
                else{
                    String errorString;
                    CountResponse countResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        countResponse=gson.fromJson(errorString, CountResponse.class);
                        countAPICallBack.onError(countResponse);

                    } catch (IOException e) {
                        countAPICallBack.onFailure(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<CountResponse> call, Throwable t) {
                countAPICallBack.onFailure(t);
            }
        });
    }

    public static void callGetPotholeCountByMonth(DayReq dayReq, CountAPICallBack countAPICallBack){
        getApiInterface();
        Call<CountResponse> call = apiInterface.callGetPotholeCountByMonth(dayReq);
        setCountAPIReturn(call, countAPICallBack);
    }
}
