package com.example.potholeapplication.Retrofit2;

import com.example.potholeapplication.class_pothole.request.AddPotholeReq;
import com.example.potholeapplication.class_pothole.request.DayReq;
import com.example.potholeapplication.class_pothole.request.EditInfoReq;
import com.example.potholeapplication.class_pothole.request.EditPasswordReq;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.LoginReq;
import com.example.potholeapplication.class_pothole.request.RegisterReq;
import com.example.potholeapplication.class_pothole.request.SaveDistanceReq;
import com.example.potholeapplication.class_pothole.response.CountResponse;
import com.example.potholeapplication.class_pothole.response.SeverityResponse;
import com.example.potholeapplication.class_pothole.response.SubinfoResponse;
import com.example.potholeapplication.class_pothole.response.UserResponse;
import com.example.potholeapplication.class_pothole.request.ResetPasswordReq;
import com.example.potholeapplication.class_pothole.request.UserVerificationReq;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIInterface {
    //-----------------------API danh cho dang nhap-----------------------
    @POST("api/auth/login")//dang nhap
    Call<UserResponse> callLogin(@Body LoginReq loginReq);
    //-----------------------API danh cho dang ki-----------------------------
    @POST("api/find/register/non")//kiem tra username va email co ton tai chua
    Call<UserResponse> callVerifyBeforeRegister(@Body UserVerificationReq userVerificationReq);
    @POST("api/code/register")//gui code
    Call<UserResponse> callRegisterCode(@Body EmailReq emailReq);
    @POST("api/auth/register")//dang ky
    Call<UserResponse> callRegister(@Body RegisterReq registerReq);
    //----------------------API cho forgot password------------------------
    @POST("api/find/email")//kiem tra email co trong csdl ko
    Call<UserResponse> callFindEmail(@Body EmailReq emailReq);
    @POST("api/code/password")//gui code
    Call<UserResponse> callResetPassCode(@Body EmailReq emailReq);
    @PUT("api/edit/password")//reset password
    Call<UserResponse> callResetPassword(@Body ResetPasswordReq resetPasswordReq);
    //------------------------API cho edit email-------------
    @POST("api/find/email/non")//kiem tra email da ton tai trong csdl
    Call<UserResponse> callFindEmailNonExists(@Body EmailReq emailReq);
    @POST("api/code/email")//gui code email
    Call<UserResponse> callEmailCode(@Body EmailReq emailReq);
    @PUT("api/edit/email/{email}")//edit email
    Call<UserResponse> callEditEmail(@Path("email") String email, @Body EmailReq emailReq);
    //---------------------API cho edit username va name--------
    @PUT("api/edit/info/{email}")//edit username va name
    Call<UserResponse> callEditInfo(@Path("email") String email, @Body EditInfoReq editInfoReq);
    //---------------------API cho edit password---------------------
    @PUT("api/edit/password/change")//edit password
    Call<UserResponse> callChangePassword(@Body EditPasswordReq editPasswordReq);
    //---------------------API lấy hinh anh-----------------------
    @Multipart
    @PUT("api/edit/image")//save image
    Call<UserResponse> callSaveImage(@Part("email")RequestBody email, @Part MultipartBody.Part image);
    @POST("api/find/image")//lay image
    Call<UserResponse> callFindImage(@Body EmailReq emailReq);
    //--------------------API lưu pothole-------------------
    @POST("api/pothole/add")//luu pothole
    Call<UserResponse> callAddPothole(@Body AddPotholeReq addPotholeReq);
    //--------------------API cho analystic-----------------------
    @POST("api/pothole/find/year")//goi so luong pothole/fixed pothole theo tung thang
    Call<CountResponse> callGetPotholeCountByMonth(@Body DayReq dayReq);
    @GET("api/pothole/find/severity")//goi lay so luong pothole theo kích thước
    Call<SeverityResponse> callGetPotholeBySeverity();

    //--------------------API cho subinfo
    @POST("api/pothole/subinfo")//goi api tra ve subinfo
    Call<SubinfoResponse> callGetSubinfo(@Body EmailReq emailReq);
    @POST("api/pothole/save/distance")//luu quang duong di chuyen duoc khi bat pothole detection
    Call<SubinfoResponse> callSaveDistances(@Body SaveDistanceReq saveDistanceReq);

}
