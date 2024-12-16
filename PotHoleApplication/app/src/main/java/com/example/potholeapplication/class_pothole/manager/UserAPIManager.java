package com.example.potholeapplication.class_pothole.manager;

import com.example.potholeapplication.Retrofit2.UserAPICallBack;
import com.example.potholeapplication.Retrofit2.APIInterface;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.class_pothole.request.AddPotholeReq;
import com.example.potholeapplication.class_pothole.request.EditInfoReq;
import com.example.potholeapplication.class_pothole.request.EditPasswordReq;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.LoginReq;
import com.example.potholeapplication.class_pothole.request.RegisterReq;
import com.example.potholeapplication.class_pothole.request.ResetPasswordReq;
import com.example.potholeapplication.class_pothole.request.UserVerificationReq;
import com.example.potholeapplication.class_pothole.response.UserResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAPIManager {
    private static APIInterface apiInterface;
    private static void getApiInterface(){
        if(apiInterface==null){
            apiInterface= RetrofitServices.getApiService();
        }
    }
    private static void setUserAPIReturn(Call<UserResponse> call, UserAPICallBack userApiCallBack){
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    userApiCallBack.onSuccess(response);
                }
                else{
                    String errorString;
                    UserResponse userResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        userResponse =gson.fromJson(errorString, UserResponse.class);
                        userApiCallBack.onError(userResponse);

                    } catch (IOException e) {
                        userApiCallBack.onFailure(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                userApiCallBack.onFailure(t);
            }
        });
    }

    //--------------------API Đăng nhập------------------------------
    public static void callLogin(LoginReq loginReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callLogin(loginReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    //-----------------------API danh cho dang ki-----------------------------
    public static void callVerifyBeforeRegister(UserVerificationReq userVerificationReq,
                                                UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callVerifyBeforeRegister(userVerificationReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    public static void callRegisterCode(EmailReq emailReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callRegisterCode(emailReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    public static void callRegister(RegisterReq registerReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callRegister(registerReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    //----------------------API cho forgot password------------------------
    public static void callFindEmail(EmailReq emailReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callFindEmail(emailReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    public static void callResetPassCode(EmailReq emailReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callResetPassCode(emailReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    public static void callResetPassword(ResetPasswordReq resetPasswordReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callResetPassword(resetPasswordReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    //------------------------API cho edit email-------------
    public static void callFindEmailNonExists(EmailReq emailReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callFindEmailNonExists(emailReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    public static void callEmailCode(EmailReq emailReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callEmailCode(emailReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    public static void callEditEmail(String email,EmailReq emailReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callEditEmail(email,emailReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    //---------------------API cho edit username va name--------
    public static void callEditInfo(String email, EditInfoReq editInfoReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callEditInfo(email,editInfoReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    //---------------------API cho edit password---------------------
    public static void callChangePassword(EditPasswordReq editPasswordReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callChangePassword(editPasswordReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    //---------------------API lấy hinh anh-----------------------
    public static void callSaveImage(RequestBody email, MultipartBody.Part image, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callSaveImage(email,image);
        setUserAPIReturn(call, userApiCallBack);
    }
    public static void callFindImage(EmailReq emailReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callFindImage(emailReq);
        setUserAPIReturn(call, userApiCallBack);
    }
    //--------------------API lưu pothole-------------------
    public static void callAddPothole(AddPotholeReq addPotholeReq, UserAPICallBack userApiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callAddPothole(addPotholeReq);
        setUserAPIReturn(call, userApiCallBack);
    }

}
