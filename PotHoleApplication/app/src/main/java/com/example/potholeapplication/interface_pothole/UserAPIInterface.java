package com.example.potholeapplication.interface_pothole;

import com.example.potholeapplication.class_pothole.EmailCodeResponse;
import com.example.potholeapplication.class_pothole.LoginRequest;
import com.example.potholeapplication.class_pothole.RegisterRequest;
import com.example.potholeapplication.class_pothole.User;
import com.example.potholeapplication.class_pothole.UserApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserAPIInterface {
    @POST("api/user/auth/register")
    Call<UserApiResponse> callRegisterApi(@Body RegisterRequest registerRequest);

    @POST("api/user/auth/login")
    Call<UserApiResponse> callLoginAPI(@Body LoginRequest loginRequest);
    @POST("api/user/auth/register/email/code")
    Call<UserApiResponse> callSendEmail(@Body RegisterRequest registerRequest);
    @POST("api/user/auth/register/add")
    Call<UserApiResponse> callAddUser(@Body RegisterRequest registerRequest);
}
