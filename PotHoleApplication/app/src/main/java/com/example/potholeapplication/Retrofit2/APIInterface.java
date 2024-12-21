package com.example.potholeapplication.Retrofit2;

import com.example.potholeapplication.class_pothole.other.Pothole;
import com.example.potholeapplication.class_pothole.other.PotholeCountByMonth;
import com.example.potholeapplication.class_pothole.other.SeverityCount;
import com.example.potholeapplication.class_pothole.other.Subinfo;
import com.example.potholeapplication.class_pothole.other.User;
import com.example.potholeapplication.class_pothole.request.AddPotholeReq;
import com.example.potholeapplication.class_pothole.request.DayReq;
import com.example.potholeapplication.class_pothole.request.EditInfoReq;
import com.example.potholeapplication.class_pothole.request.EditPasswordReq;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.LoginReq;
import com.example.potholeapplication.class_pothole.request.RegisterReq;
import com.example.potholeapplication.class_pothole.request.SaveDistanceReq;
import com.example.potholeapplication.class_pothole.response.APIResponse;
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
    Call<APIResponse<User>> callLogin(@Body LoginReq loginReq);
    //-----------------------API danh cho dang ki-----------------------------
    @POST("api/find/register/non")//kiem tra username va email co ton tai chua
    Call<APIResponse<User>> callVerifyBeforeRegister(@Body UserVerificationReq userVerificationReq);
    @POST("api/code/register")//gui code
    Call<APIResponse<User>> callRegisterCode(@Body EmailReq emailReq);
    @POST("api/auth/register")//dang ky
    Call<APIResponse<User>> callRegister(@Body RegisterReq registerReq);
    //----------------------API cho forgot password------------------------
    @POST("api/find/email")//kiem tra email co trong csdl ko
    Call<APIResponse<User>> callFindEmail(@Body EmailReq emailReq);
    @POST("api/code/password")//gui code
    Call<APIResponse<User>> callResetPassCode(@Body EmailReq emailReq);
    @PUT("api/edit/password")//reset password
    Call<APIResponse<User>> callResetPassword(@Body ResetPasswordReq resetPasswordReq);
    //------------------------API cho edit email-------------
    @POST("api/find/email/non")//kiem tra email da ton tai trong csdl
    Call<APIResponse<User>> callFindEmailNonExists(@Body EmailReq emailReq);
    @POST("api/code/email")//gui code email
    Call<APIResponse<User>> callEmailCode(@Body EmailReq emailReq);
    @PUT("api/edit/email/{email}")//edit email
    Call<APIResponse<User>> callEditEmail(@Path("email") String email, @Body EmailReq emailReq);
    //---------------------API cho edit username va name--------
    @PUT("api/edit/info/{email}")//edit username va name
    Call<APIResponse<User>> callEditInfo(@Path("email") String email, @Body EditInfoReq editInfoReq);
    //---------------------API cho edit password---------------------
    @PUT("api/edit/password/change")//edit password
    Call<APIResponse<User>> callChangePassword(@Body EditPasswordReq editPasswordReq);
    //---------------------API lấy hinh anh-----------------------
    @Multipart
    @PUT("api/edit/image")//save image
    Call<APIResponse<User>> callSaveImage(@Part("email")RequestBody email, @Part MultipartBody.Part image);
    @POST("api/find/image")//lay image
    Call<APIResponse<User>> callFindImage(@Body EmailReq emailReq);
    //--------------------API pothole-------------------
    @POST("api/pothole/add")//luu pothole
    Call<APIResponse<User>> callAddPothole(@Body AddPotholeReq addPotholeReq);
    @GET("api/pothole/")//lay danh sach pothole
    Call<APIResponse<Pothole>> callGetPothole();
    //--------------------API cho analystic-----------------------
    @POST("api/pothole/find/year")//goi so luong pothole/fixed pothole theo tung thang
    Call<APIResponse<PotholeCountByMonth>> callGetPotholeCountByMonth(@Body DayReq dayReq);
    @GET("api/pothole/find/severity")//goi lay so luong pothole theo kích thước
    Call<APIResponse<SeverityCount>> callGetPotholeBySeverity();

    //--------------------API cho subinfo
    @POST("api/pothole/subinfo")//goi api tra ve subinfo
    Call<APIResponse<Subinfo>> callGetSubinfo(@Body EmailReq emailReq);
    @POST("api/pothole/save/distance")//luu quang duong di chuyen duoc khi bat pothole detection
    Call<APIResponse<Subinfo>> callSaveDistances(@Body SaveDistanceReq saveDistanceReq);

}
