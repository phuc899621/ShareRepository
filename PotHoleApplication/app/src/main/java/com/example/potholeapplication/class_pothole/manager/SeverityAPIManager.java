package com.example.potholeapplication.class_pothole.manager;

import android.util.Log;

import com.example.potholeapplication.Retrofit2.APIInterface;
import com.example.potholeapplication.Retrofit2.CountAPICallBack;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.Retrofit2.SeverityAPICallBack;
import com.example.potholeapplication.class_pothole.request.DayReq;
import com.example.potholeapplication.class_pothole.response.CountResponse;
import com.example.potholeapplication.class_pothole.response.SeverityResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeverityAPIManager {
    private static APIInterface apiInterface;
    private static void getApiInterface(){
        if(apiInterface==null){
            apiInterface= RetrofitServices.getApiService();
        }
    }
    private static void setSeverityAPIReturn(Call<SeverityResponse> call, SeverityAPICallBack severityAPICallBack){
        call.enqueue(new Callback<SeverityResponse>() {
            @Override
            public void onResponse(Call<SeverityResponse> call, Response<SeverityResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    severityAPICallBack.onSuccess(response);
                }
                else{
                    String errorString;
                    SeverityResponse severityResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        severityResponse=gson.fromJson(errorString, SeverityResponse.class);
                        severityAPICallBack.onError(severityResponse);

                    } catch (IOException e) {
                        severityAPICallBack.onFailure(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<SeverityResponse> call, Throwable t) {
                severityAPICallBack.onFailure(t);
            }
        });
    }

    public static void callGetPotholeBySeverity(SeverityAPICallBack severityAPICallBack){
        getApiInterface();
        Call<SeverityResponse> call = apiInterface.callGetPotholeBySeverity();
        setSeverityAPIReturn(call, severityAPICallBack);
    }
}
