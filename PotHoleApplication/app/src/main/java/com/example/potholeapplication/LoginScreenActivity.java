package com.example.potholeapplication;

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

import com.example.potholeapplication.class_pothole.LoginRequest;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.User;
import com.example.potholeapplication.class_pothole.UserApiResponse;
import com.example.potholeapplication.databinding.ActivityForgotPasswordBinding;
import com.example.potholeapplication.databinding.ActivityLoginScreenBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.google.android.material.badge.BadgeUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreenActivity extends AppCompatActivity {
    ActivityLoginScreenBinding binding;
    Context context;
    Button btnConfirm;
    Dialog dialogOke,dialogError;
    TextView tvErrorTitle;
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
        setClickEvent(); //cài su kien click

    }
    public void callAPI(){
        // Khoi tai UerAPIInterface
        UserAPIInterface apiService = RetrofitServices.getApiService();
        //lay du lieu
        String username=binding.etUsername.getText().toString().trim();
        String password=binding.etPassword.getText().toString().trim();
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(context,"Vui lòng nhập đầu đủ username và password"
                    ,Toast.LENGTH_LONG).show();
            return;
        }


        LoginRequest loginRequest=new LoginRequest(username,password);
        // Call API login
        Call<UserApiResponse> call = apiService.callLoginAPI(loginRequest);

        // call API bất đồng bộ
        call.enqueue(new Callback<UserApiResponse>() {
            @Override
            public void onResponse(Call<UserApiResponse> call, Response<UserApiResponse> response) {
                 if(response.isSuccessful()&&response.body()!=null){
                     showDialogOke();
                 }
                 else{
                     String errorString;
                     JsonObject errorJson;
                     UserApiResponse apiResponse;
                     try {
                         //lay chuoi json va chuyen thanh UserAPIResponse
                         errorString=response.errorBody().string();
                         errorJson= JsonParser.parseString(errorString).getAsJsonObject();
                         Gson gson=new Gson();
                         apiResponse=gson.fromJson(errorString,UserApiResponse.class);
                         showDialogError(apiResponse);

                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                 }
           }

            @Override
            public void onFailure(Call<UserApiResponse> call, Throwable t) {
                Log.e("API Error", "Failure: " + t.getMessage());
            }
        });

    }
    public void showDialogOke(){
        Dialog dialog=new Dialog(LoginScreenActivity.this);
        dialog.setContentView(R.layout.custom_dialog_oke);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginScreenActivity.this, HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
    public void showDialogError(UserApiResponse apiResponse){
        Dialog dialog=new Dialog(LoginScreenActivity.this);
        dialog.setContentView(R.layout.custom_dialog_error);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnConfirm=dialog.findViewById(R.id.btnConfirm);
        tvErrorTitle=dialog.findViewById(R.id.tvTitle);
        tvErrorTitle.setText(apiResponse.getMessage());
        dialog.show();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    //su kien click
    public void setClickEvent(){
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPI();
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
                Intent intent=new Intent(LoginScreenActivity.this, ActivityForgotPasswordBinding.class);
                startActivity(intent);
            }
        });
    }
}