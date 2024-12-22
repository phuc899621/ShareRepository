package com.example.potholeapplication.class_pothole.manager;

import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.Retrofit2.APIInterface;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.class_pothole.other.Pothole;
import com.example.potholeapplication.class_pothole.other.PotholeCountByMonth;
import com.example.potholeapplication.class_pothole.other.Ranking;
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
import com.example.potholeapplication.class_pothole.request.ResetPasswordReq;
import com.example.potholeapplication.class_pothole.request.SaveDistanceReq;
import com.example.potholeapplication.class_pothole.request.UserVerificationReq;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIManager {
    private static APIInterface apiInterface;
    private static void getApiInterface(){
        if(apiInterface==null){
            apiInterface= RetrofitServices.getApiService();
        }
    }
    private static <T> void setAPIReturn(Type clazz,Call<T> call, APICallBack<T> apiCallBack){
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    apiCallBack.onSuccess(response);
                }
                else{
                    String errorString;
                    T tResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        tResponse =gson.fromJson(errorString, clazz);
                        apiCallBack.onError(tResponse);

                    } catch (IOException e) {
                        apiCallBack.onFailure(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<T> call, Throwable t) {
                apiCallBack.onFailure(t);
            }
        });
    }
    //--------------------API Đăng nhập------------------------------
    public static void callLogin(LoginReq loginReq,APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callLogin(loginReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    //-----------------------API danh cho dang ki-----------------------------
    public static void callVerifyBeforeRegister(UserVerificationReq userVerificationReq,
                                                APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callVerifyBeforeRegister(userVerificationReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    public static void callRegisterCode(EmailReq emailReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callRegisterCode(emailReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    public static void callRegister(RegisterReq registerReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callRegister(registerReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    //----------------------API cho forgot password------------------------
    public static void callFindEmail(EmailReq emailReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callFindEmail(emailReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    public static void callResetPassCode(EmailReq emailReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callResetPassCode(emailReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    public static void callResetPassword(ResetPasswordReq resetPasswordReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callResetPassword(resetPasswordReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    //------------------------API cho edit email-------------
    public static void callFindEmailNonExists(EmailReq emailReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callFindEmailNonExists(emailReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    public static void callEmailCode(EmailReq emailReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callEmailCode(emailReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    public static void callEditEmail(String email,EmailReq emailReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callEditEmail(email,emailReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    //---------------------API cho edit username va name--------
    public static void callEditInfo(String email, EditInfoReq editInfoReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callEditInfo(email,editInfoReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    //---------------------API cho edit password---------------------
    public static void callChangePassword(EditPasswordReq editPasswordReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callChangePassword(editPasswordReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    //---------------------API lấy hinh anh-----------------------
    public static void callSaveImage(RequestBody email, MultipartBody.Part image, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callSaveImage(email,image);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    public static void callFindImage(EmailReq emailReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callFindImage(emailReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    //--------------------API lưu pothole-------------------
    public static void callAddPothole(AddPotholeReq addPotholeReq, APICallBack<APIResponse<User>> userApiCallBack){
        getApiInterface();
        Call<APIResponse<User>> call = apiInterface.callAddPothole(addPotholeReq);
        Type type = new TypeToken<APIResponse<User>>() {}.getType();
        setAPIReturn(type,call, userApiCallBack);
    }
    public static void callGetPothole(APICallBack<APIResponse<Pothole>> potholeAPICallBack){
        getApiInterface();
        Call<APIResponse<Pothole>> call = apiInterface.callGetPothole();
        Type type = new TypeToken<APIResponse<Pothole>>() {}.getType();
        setAPIReturn(type,call, potholeAPICallBack);
    }
    //--------------------API cho analystic-----------------------

    public static void callGetPotholeCountByMonth(
            DayReq dayReq, APICallBack<APIResponse<PotholeCountByMonth>> countAPICallBack){
        getApiInterface();
        Call<APIResponse<PotholeCountByMonth>> call = apiInterface.callGetPotholeCountByMonth(dayReq);
        Type type = new TypeToken<APIResponse<PotholeCountByMonth>>() {}.getType();
        setAPIReturn(type,call, countAPICallBack);
    }
    public static void callGetPotholeBySeverity(APICallBack<APIResponse<SeverityCount>> severityAPICallBack){
        getApiInterface();
        Call<APIResponse<SeverityCount>> call = apiInterface.callGetPotholeBySeverity();
        Type type = new TypeToken<APIResponse<SeverityCount>>() {}.getType();
        setAPIReturn(type,call, severityAPICallBack);
    }
    //--------------------API cho subinfo--------------------------
    public static void callGetSubinfo(EmailReq emailReq, APICallBack<APIResponse<Subinfo>> subinfoAPICallBack){
        getApiInterface();
        Call<APIResponse<Subinfo>> call = apiInterface.callGetSubinfo(emailReq);
        Type type = new TypeToken<APIResponse<Subinfo>>() {}.getType();
        setAPIReturn(type,call, subinfoAPICallBack);
    }
    public static void callSaveDistances(SaveDistanceReq saveDistanceReq, APICallBack<APIResponse<Subinfo>> subinfoAPICallBack){
        getApiInterface();
        Call<APIResponse<Subinfo>> call=apiInterface.callSaveDistances(saveDistanceReq);
        Type type = new TypeToken<APIResponse<Subinfo>>() {}.getType();
        setAPIReturn(type,call,subinfoAPICallBack);
    }
    //------------------API cho ranking
    public static void callGetRanking(APICallBack<APIResponse<Ranking>> rankingAPICallBack){
        getApiInterface();
        Call<APIResponse<Ranking>> call = apiInterface.callGetRanking();
        Type type = new TypeToken<APIResponse<Ranking>>() {}.getType();
        setAPIReturn(type,call, rankingAPICallBack);
    }
}
