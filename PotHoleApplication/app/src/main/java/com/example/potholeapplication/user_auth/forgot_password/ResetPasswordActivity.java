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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.R;
import com.example.potholeapplication.class_pothole.RegisterRequest;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.UserApiResponse;
import com.example.potholeapplication.databinding.ActivityResetPasswordBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    ActivityResetPasswordBinding binding;
    Context context;
    String emailForResetPassword;

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
        getStringEmail();
        setClickEvent();
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
            Toast.makeText(context,"Please enter password",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if(!passwordConfirm.equals(password)) {
            Toast.makeText(context,"Password does not match",
                    Toast.LENGTH_LONG).show();
            return;
        }

        UserAPIInterface apiService= RetrofitServices.getApiService();
        RegisterRequest registerRequest=new RegisterRequest(
                "","",emailForResetPassword,password);
        // Call API kiem tra email
        Call<UserApiResponse> call = apiService.callResetPasswordAPI(registerRequest);
        // call API bất đồng bộ
        call.enqueue(new Callback<UserApiResponse>() {
            @Override
            public void onResponse(Call<UserApiResponse> call, Response<UserApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    Intent intent=new Intent(ResetPasswordActivity.this,
                            ResetPasswordSuccessActivity.class);
                    startActivity(intent);
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
    public void showDialogError(UserApiResponse apiResponse){
        Dialog dialog=new Dialog(ResetPasswordActivity.this);
        dialog.setContentView(R.layout.custom_dialog_error);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnConfirm=dialog.findViewById(R.id.btnConfirm);
        TextView tvErrorTitle=dialog.findViewById(R.id.tvTitle);
        tvErrorTitle.setText(apiResponse.getMessage());
        dialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}