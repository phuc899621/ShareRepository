package com.example.potholeapplication.user_auth.forgot_password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.R;
import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.class_pothole.manager.APIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.class_pothole.response.UserResponse;
import com.example.potholeapplication.databinding.ActivityForgotPasswordVerificationBinding;
import com.example.potholeapplication.Retrofit2.APIInterface;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordVerificationActivity extends AppCompatActivity {
    ActivityForgotPasswordVerificationBinding binding;
    String emailForResetPassword,code;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityForgotPasswordVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        getStringEmail();
        setClickEvent();
        callAPiSendCode();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
    public void setClickEvent(){
        binding.btnVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.etCodeInput.getText().toString().trim().equals(code)){
                    DialogManager.showDialogErrorString(context,getString(R.string.str_wrong_code));
                }else{
                    Intent intent=new Intent(ForgotPasswordVerificationActivity.this,
                            ResetPasswordActivity.class);
                    intent.putExtra("email",emailForResetPassword);
                    startActivity(intent);
                }

            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void getStringEmail(){
        Intent intent=getIntent();
        emailForResetPassword=intent.getStringExtra("email");
    }
    public void callAPiSendCode(){
        APIManager.callResetPassCode(new EmailReq(emailForResetPassword)
                ,new APICallBack() {
                    @Override
                    public void onSuccess(Response<UserResponse> response) {
                        code=response.body().getMessage().trim();
                    }
                    @Override
                    public void onError(UserResponse errorResponse) {
                        DialogManager.showDialogError(context, errorResponse);
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API Error", "Failure: " + t.getMessage());
                        throw new RuntimeException(t);
                    }
                });

    }


}