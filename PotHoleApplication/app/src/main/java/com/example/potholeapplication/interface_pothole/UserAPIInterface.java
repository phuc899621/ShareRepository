package com.example.potholeapplication.interface_pothole;

import com.example.potholeapplication.class_pothole.request.EditInfoReq;
import com.example.potholeapplication.class_pothole.request.EditPasswordReq;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.LoginReq;
import com.example.potholeapplication.class_pothole.request.RegisterReq;
import com.example.potholeapplication.class_pothole.ApiResponse;
import com.example.potholeapplication.class_pothole.request.ResetPasswordReq;
import com.example.potholeapplication.class_pothole.request.UserVerificationReq;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserAPIInterface {
    //-----------------------API danh cho dang nhap-----------------------
    @POST("api/auth/login")//dang nhap
    Call<ApiResponse> callLogin(@Body LoginReq loginReq);
    //-----------------------API danh cho dang ki-----------------------------
    @POST("api/find/register/non")//kiem tra username va email co ton tai chua
    Call<ApiResponse> callVerifyBeforeRegister(@Body UserVerificationReq userVerificationReq);
    @POST("api/code/register")//gui code
    Call<ApiResponse> callRegisterCode(@Body EmailReq emailReq);
    @POST("api/auth/register")//dang ky
    Call<ApiResponse> callRegister(@Body RegisterReq registerReq);
    //----------------------API cho forgot password------------------------
    @POST("api/find/email")//kiem tra email co trong csdl ko
    Call<ApiResponse> callFindEmail(@Body EmailReq emailReq);
    @POST("api/code/password")//gui code
    Call<ApiResponse> callResetPassCode(@Body EmailReq emailReq);
    @PUT("api/edit/password")//reset password
    Call<ApiResponse> callResetPassword(@Body ResetPasswordReq resetPasswordReq);
    //------------------------API cho edit email-------------
    @POST("api/find/email/non")//kiem tra email da ton tai trong csdl
    Call<ApiResponse> callFindEmailNonExists(@Body EmailReq emailReq);
    @POST("api/code/email")//gui code email
    Call<ApiResponse> callEmailCode(@Body EmailReq emailReq);
    @PUT("api/edit/email/{email}")//edit email
    Call<ApiResponse> callEditEmail(@Path("email") String email, @Body EmailReq emailReq);
    //---------------------API cho edit username va name--------
    @PUT("api/edit/info/{email}")//edit username va name
    Call<ApiResponse> callEditInfo(@Path("email") String email, @Body EditInfoReq editInfoReq);
    //---------------------API cho edit password---------------------
    @PUT("api/edit/password/change")//edit password
    Call<ApiResponse> callChangePassword(@Body EditPasswordReq editPasswordReq);
    //---------------------API lấy hinh anh-----------------------
    @Multipart
    @PUT("api/edit/image")//save image
    Call<ApiResponse> callSaveImage(@Part("email")RequestBody email, @Part MultipartBody.Part image);
    @POST("api/find/Image")//kiem tra email co trong csdl ko
    Call<ApiResponse> callFindImage(@Body EmailReq emailReq);


}
