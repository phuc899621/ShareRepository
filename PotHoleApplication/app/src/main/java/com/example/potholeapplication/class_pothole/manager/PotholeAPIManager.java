package com.example.potholeapplication.class_pothole.manager;

import com.example.potholeapplication.Retrofit2.APIInterface;
import com.example.potholeapplication.Retrofit2.PotholeAPICallBack;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.Retrofit2.SeverityAPICallBack;
import com.example.potholeapplication.class_pothole.response.PotholeResponse;
import com.example.potholeapplication.class_pothole.response.SeverityResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotholeAPIManager {
    private static APIInterface apiInterface;
    private static void getApiInterface(){
        if(apiInterface==null){
            apiInterface= RetrofitServices.getApiService();
        }
    }
    private static void setPotholeAPIReturn(Call<PotholeResponse> call, PotholeAPICallBack potholeAPICallBack){
        call.enqueue(new Callback<PotholeResponse>() {
            @Override
            public void onResponse(Call<PotholeResponse> call, Response<PotholeResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    potholeAPICallBack.onSuccess(response);
                }
                else{
                    String errorString;
                    PotholeResponse potholeResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        potholeResponse=gson.fromJson(errorString, PotholeResponse.class);
                        potholeAPICallBack.onError(potholeResponse);

                    } catch (IOException e) {
                        potholeAPICallBack.onFailure(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<PotholeResponse> call, Throwable t) {
                potholeAPICallBack.onFailure(t);
            }
        });
    }

    public static void callGetPothole(PotholeAPICallBack potholeAPICallBack){
        getApiInterface();
        Call<PotholeResponse> call = apiInterface.callGetPothole();
        setPotholeAPIReturn(call, potholeAPICallBack);
    }
}
