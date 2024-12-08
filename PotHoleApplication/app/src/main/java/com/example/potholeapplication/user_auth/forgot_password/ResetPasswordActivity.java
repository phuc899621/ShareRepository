package com.example.potholeapplication.user_auth.forgot_password;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.R;
import com.example.potholeapplication.class_pothole.LocaleManager;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.response.ApiResponse;
import com.example.potholeapplication.class_pothole.request.ResetPasswordReq;
import com.example.potholeapplication.databinding.ActivityResetPasswordBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    ActivityResetPasswordBinding binding;
    Context context;
    String emailForResetPassword;
    Button btnConfirm;
    Dialog dialogError;
    TextView tvErrorTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        SetupDialog();
        getStringEmail();
        setClickEvent();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }

    public void setClickEvent(){
        binding.btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callResetPasswordAPI();
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
    public void callResetPasswordAPI(){
        //kiem tra du lieu nhap vao
        String password=binding.etNewPassword.getText().toString().trim();
        String passwordConfirm=binding.etConfirmPassword.getText().toString().trim();
        if(passwordConfirm.isEmpty()||password.isEmpty()){
            showDialogErrorString(getString(R.string.str_please_enter_your_password));
            return;
        }
        if(!passwordConfirm.equals(password)) {
            showDialogErrorString(getString(R.string.str_password_does_not_match));
            return;
        }

        UserAPIInterface apiService= RetrofitServices.getApiService();
        ResetPasswordReq resetPasswordReq =new ResetPasswordReq(emailForResetPassword,password);
        Call<ApiResponse> call = apiService.callResetPassword(resetPasswordReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    Intent intent=new Intent(ResetPasswordActivity.this,
                            ResetPasswordSuccessActivity.class);
                    startActivity(intent);
                }
                else{
                    String errorString;
                    ApiResponse apiResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        apiResponse=gson.fromJson(errorString, ApiResponse.class);
                        showDialogError(apiResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API Error", "Failure: " + t.getMessage());
            }
        });
    }
    public void SetupDialog(){
        dialogError=new Dialog(ResetPasswordActivity.this);
        dialogError.setContentView(R.layout.custom_dialog_error);
        dialogError.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogError.setCancelable(true);
        dialogError.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnConfirm=dialogError.findViewById(R.id.btnConfirm);
        tvErrorTitle=dialogError.findViewById(R.id.tvTitle);
    }
    public void showDialogError(ApiResponse apiResponse){
        tvErrorTitle.setText(apiResponse.getMessage());
        dialogError.show();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogError.dismiss();
            }
        });
    }
    public void showDialogErrorString(String error){
        tvErrorTitle.setText(error);
        dialogError.show();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogError.dismiss();
            }
        });
    }

}