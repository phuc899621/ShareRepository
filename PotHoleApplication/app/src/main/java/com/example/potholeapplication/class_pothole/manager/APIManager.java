package com.example.potholeapplication.class_pothole.manager;

import com.example.potholeapplication.Retrofit2.APICallBack;
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
import retrofit2.http.Multipart;

public class APIManager {
    private static APIInterface apiInterface;
    private static void getApiInterface(){
        if(apiInterface==null){
            apiInterface= RetrofitServices.getApiService();
        }
    }
    private static void setAPIReturn(Call<UserResponse> call, APICallBack apiCallBack){
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    apiCallBack.onSuccess(response);
                }
                else{
                    String errorString;
                    UserResponse userResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        userResponse =gson.fromJson(errorString, UserResponse.class);
                        apiCallBack.onError(userResponse);

                    } catch (IOException e) {
                        apiCallBack.onFailure(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                apiCallBack.onFailure(t);
            }
        });
    }
    //--------------------API Đăng nhập------------------------------
    public static void callLogin(LoginReq loginReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callLogin(loginReq);
        setAPIReturn(call,apiCallBack);
    }
    //-----------------------API danh cho dang ki-----------------------------
    public static void callVerifyBeforeRegister(UserVerificationReq userVerificationReq,
                                                APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callVerifyBeforeRegister(userVerificationReq);
        setAPIReturn(call,apiCallBack);
    }
    public static void callRegisterCode(EmailReq emailReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callRegisterCode(emailReq);
        setAPIReturn(call,apiCallBack);
    }
    public static void callRegister(RegisterReq registerReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callRegister(registerReq);
        setAPIReturn(call,apiCallBack);
    }
    //----------------------API cho forgot password------------------------
    public static void callFindEmail(EmailReq emailReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callFindEmail(emailReq);
        setAPIReturn(call,apiCallBack);
    }
    public static void callResetPassCode(EmailReq emailReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callResetPassCode(emailReq);
        setAPIReturn(call,apiCallBack);
    }
    public static void callResetPassword(ResetPasswordReq resetPasswordReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callResetPassword(resetPasswordReq);
        setAPIReturn(call,apiCallBack);
    }
    //------------------------API cho edit email-------------
    public static void callFindEmailNonExists(EmailReq emailReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callFindEmailNonExists(emailReq);
        setAPIReturn(call,apiCallBack);
    }
    public static void callEmailCode(EmailReq emailReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callEmailCode(emailReq);
        setAPIReturn(call,apiCallBack);
    }
    public static void callEditEmail(String email,EmailReq emailReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callEditEmail(email,emailReq);
        setAPIReturn(call,apiCallBack);
    }
    //---------------------API cho edit username va name--------
    public static void callEditInfo(String email, EditInfoReq editInfoReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callEditInfo(email,editInfoReq);
        setAPIReturn(call,apiCallBack);
    }
    //---------------------API cho edit password---------------------
    public static void callChangePassword(EditPasswordReq editPasswordReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callChangePassword(editPasswordReq);
        setAPIReturn(call,apiCallBack);
    }
    //---------------------API lấy hinh anh-----------------------
    public static void callSaveImage(RequestBody email, MultipartBody.Part image, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callSaveImage(email,image);
        setAPIReturn(call,apiCallBack);
    }
    public static void callFindImage(EmailReq emailReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callFindImage(emailReq);
        setAPIReturn(call,apiCallBack);
    }
    //--------------------API lưu pothole-------------------
    public static void callAddPothole(AddPotholeReq addPotholeReq, APICallBack apiCallBack){
        getApiInterface();
        Call<UserResponse> call = apiInterface.callAddPothole(addPotholeReq);
        setAPIReturn(call,apiCallBack);
    }

}
