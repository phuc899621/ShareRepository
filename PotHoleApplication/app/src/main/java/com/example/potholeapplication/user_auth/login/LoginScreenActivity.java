package com.example.potholeapplication.user_auth.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.HomeScreenActivity;
import com.example.potholeapplication.R;
import com.example.potholeapplication.class_pothole.CustomDialog;
import com.example.potholeapplication.class_pothole.DataEditor;
import com.example.potholeapplication.class_pothole.LocaleManager;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.LoginReq;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.response.User;
import com.example.potholeapplication.class_pothole.response.ApiResponse;
import com.example.potholeapplication.databinding.ActivityLoginScreenBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.example.potholeapplication.user_auth.forgot_password.ForgotPasswordActivity;
import com.example.potholeapplication.user_auth.signup.SignupActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreenActivity extends AppCompatActivity {
    ActivityLoginScreenBinding binding;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityLoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        setClickEvent();
    }
    @Override
    protected void onResume() {
        super.onResume();


    }
    public void callLoginAPI(){
        UserAPIInterface apiService = RetrofitServices.getApiService();
        String username=binding.etUsername.getText().toString().trim();
        String password=binding.etPassword.getText().toString().trim();
        if(username.isEmpty() || password.isEmpty()){
            CustomDialog.showDialogErrorString(context,getString(R.string.str_please_enter_username_and_password));
            return;
        }

        LoginReq loginReq =new LoginReq(username,password);
        Call<ApiResponse> call = apiService.callLogin(loginReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                 if(response.isSuccessful()&&response.body()!=null){
                     DataEditor.saveUserToSharePreferences(context,response.body().getData());
                     CallGetImageAPI();
                 }
                 else{
                     String errorString;
                     ApiResponse apiResponse;
                     try {
                         errorString=response.errorBody().string();
                         Gson gson=new Gson();
                         apiResponse=gson.fromJson(errorString, ApiResponse.class);
                         CustomDialog.showDialogError(context,apiResponse);
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                 }
           }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API Error", "Failure2: " + t.getMessage());
            }
        });

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }


    //lay hinh anh tu server
    public void CallGetImageAPI() {
        String email= DataEditor.getEmail(context);
        if(email.isEmpty()){
            CustomDialog.showDialogErrorString(context,getString(R.string.str_email_not_found));
            return;
        }
        EmailReq emailReq=new EmailReq(email);
        UserAPIInterface apiServices= RetrofitServices.getApiService();
        Call<ApiResponse> call = apiServices.callFindImage(emailReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    List<User> data = response.body().getData();
                    if (data != null && !data.isEmpty()) {
                        String imageBase64 = data.get(0).getImage();
                        if (!imageBase64.isEmpty()) {
                            //neu co thi luu va den homescreen
                            byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                            DataEditor.saveImageBytesToSharedPreferences(context,decodedBytes);
                            CustomDialog.showDialogOkeNavigation(context,getString(R.string.str_login_successful),
                                    HomeScreenActivity.class);
                        } else {
                            //neu chua co thi lay anh mac dinh
                            DataEditor.saveImageBytesToSharedPreferences(context,
                                    DataEditor.drawableToByteArray(context,R.drawable.default_user)
                            );
                            CallSaveImageAPI(DataEditor.drawableToByteArray(context,R.drawable.default_user));
                        }
                    }
                }
                else{
                    String errorString;
                    ApiResponse apiResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        apiResponse=gson.fromJson(errorString, ApiResponse.class);
                        CustomDialog.showDialogError(context,apiResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API Error", "Failure1: " + t.getMessage());
            }
        });
    }

    //Luu anh mac dinh len server neu chua co
    public void CallSaveImageAPI(byte[] imageBytes) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestBody);
        String email=DataEditor.getEmail(context);
        if(email.isEmpty()){
            CustomDialog.showDialogErrorString(context,getString(R.string.str_email_not_found));
            return;
        }
        // Tạo RequestBody cho email và task
        RequestBody emailReq = RequestBody.create(MediaType.parse("text/plain"), email);
        UserAPIInterface apiServices=RetrofitServices.getApiService();
        Call<ApiResponse> call = apiServices.callSaveImage(emailReq,imagePart);

        // Gửi yêu cầu
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    DataEditor.saveImageBytesToSharedPreferences(context,imageBytes);
                    CustomDialog.showDialogOkeNavigation(context,getString(R.string.str_login_successful),
                            HomeScreenActivity.class);
                }
                else{
                    String errorString;
                    ApiResponse apiResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        apiResponse=gson.fromJson(errorString, ApiResponse.class);
                        CustomDialog.showDialogError(context,apiResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API Error", "Failure3: " + t.getMessage());
            }
        });
    }
    //su kien click
    public void setClickEvent(){
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLoginAPI();
            }
        });
        binding.tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginScreenActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        binding.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginScreenActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        binding.imaBtnChooseLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.showDialogLanguage(context);
            }
        });
    }
}