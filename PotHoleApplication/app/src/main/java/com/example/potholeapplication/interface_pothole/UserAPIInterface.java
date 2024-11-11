package com.example.potholeapplication.interface_pothole;

import com.example.potholeapplication.class_pothole.EditInfoRequest;
import com.example.potholeapplication.class_pothole.LoginRequest;
import com.example.potholeapplication.class_pothole.RegisterRequest;
import com.example.potholeapplication.class_pothole.UserApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserAPIInterface {
    @POST("api/user/auth/register")
    Call<UserApiResponse> callRegisterApi(@Body RegisterRequest registerRequest);

    @POST("api/user/auth/login")
    Call<UserApiResponse> callLoginAPI(@Body LoginRequest loginRequest);
    @POST("api/user/auth/register/email/code")
    Call<UserApiResponse> callSendCodeRegisterAPI(@Body RegisterRequest registerRequest);
    @POST("api/user/auth/register/add")
    Call<UserApiResponse> callAddUserAPI(@Body RegisterRequest registerRequest);
    @POST("api/user/edit/password/email")
    Call<UserApiResponse> callEmailCheckAPI(@Body RegisterRequest registerRequest);
    @POST("api/user/edit/password/email/code")
    Call<UserApiResponse> callSendCodeResetPasswordAPI(@Body RegisterRequest registerRequest);
    @PUT("api/user/edit/password")
    Call<UserApiResponse> callResetPasswordAPI(@Body RegisterRequest registerRequest);
    @PUT("api/user/edit/info/email/:email")
    Call<UserApiResponse> callChangeInfoAPI(@Path ("email") String email,
                                         EditInfoRequest editUsernameNameRequest);

}
