package com.example.potholeapplication.user_auth.forgot_password;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.R;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.RegisterReq;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.ApiResponse;
import com.example.potholeapplication.databinding.ActivityForgotPasswordVerificationBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.example.potholeapplication.user_auth.signup.SignupActivity;
import com.example.potholeapplication.user_auth.signup.VerificationActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordVerificationActivity extends AppCompatActivity {
    ActivityForgotPasswordVerificationBinding binding;
    String emailForResetPassword,code;
    Button btnConfirm;
    Dialog dialogError,dialogOke;
    TextView tvErrorTitle,tvOkeTitle;
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
        SetupDialog();
        getStringEmail();
        setClickEvent();
        callAPiSendCode();
    }
    public void setClickEvent(){
        binding.btnVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.etCodeInput.getText().toString().trim().equals(code)){
                    showDialogErrorString(getString(R.string.str_wrong_code));
                }
                Intent intent=new Intent(ForgotPasswordVerificationActivity.this,
                        ResetPasswordActivity.class);
                intent.putExtra("email",emailForResetPassword);
                startActivity(intent);
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
        UserAPIInterface apiService= RetrofitServices.getApiService();
        EmailReq emailReq=new EmailReq(emailForResetPassword);
        Call<ApiResponse> call = apiService.callResetPassCode(emailReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    code=response.body().getMessage().trim();
                }
                else{
                    String errorString;
                    ApiResponse apiResponse;
                    try {
                        //lay chuoi json va chuyen thanh UserAPIResponse
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
        dialogError=new Dialog(ForgotPasswordVerificationActivity.this);
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