package com.example.potholeapplication.edit_user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.potholeapplication.class_pothole.ApiResponse;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.request.EditPasswordReq;
import com.example.potholeapplication.class_pothole.request.RegisterReq;
import com.example.potholeapplication.class_pothole.request.UserVerificationReq;
import com.example.potholeapplication.databinding.ActivityEditPasswordBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.example.potholeapplication.user_auth.signup.SignupActivity;
import com.example.potholeapplication.user_auth.signup.VerificationActivity;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPasswordActivity extends AppCompatActivity {
    ActivityEditPasswordBinding binding;
    Context context;
    Button btnConfirm;
    Dialog dialogError,dialogOke;
    TextView tvErrorTitle,tvOkeTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityEditPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        SetupDialog();
        setClickEvent();
    }
    public String getEmail(){
        SharedPreferences sharedPreferences=getSharedPreferences("user_info",MODE_PRIVATE);
        return sharedPreferences.getString("email","");
    }
    public void callEditPasswordApi(){
        String email=getEmail();
        String oldPassword=binding.etOldPassword.getText().toString().trim();
        String newPassword=binding.etNewPassword.getText().toString().trim();
        String confirmPassword=binding.etConfirmPassword.getText().toString().trim();

        if(email.isEmpty()){
            showDialogErrorString(getString(R.string.str_email_not_found));
            return;
        }
        if(!newPassword.equals(confirmPassword)){
            showDialogErrorString(getString(R.string.str_password_does_not_match));
            return;
        }

        EditPasswordReq editPasswordReq=new EditPasswordReq(email,oldPassword,newPassword);
        UserAPIInterface apiService = RetrofitServices.getApiService();
        Call<ApiResponse> call = apiService.callChangePassword(editPasswordReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if(response.isSuccessful()&&response.body()!=null){
                    showDialogOke();
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
        dialogOke=new Dialog(EditPasswordActivity.this);
        dialogOke.setContentView(R.layout.custom_dialog_oke);
        dialogOke.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogOke.setCancelable(false);
        dialogOke.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        tvOkeTitle=dialogOke.findViewById(R.id.tvTitle);

        dialogError=new Dialog(EditPasswordActivity.this);
        dialogError.setContentView(R.layout.custom_dialog_error);
        dialogError.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogError.setCancelable(true);
        dialogError.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnConfirm=dialogError.findViewById(R.id.btnConfirm);
        tvErrorTitle=dialogError.findViewById(R.id.tvTitle);

    }
    public void showDialogOke(){
        tvOkeTitle.setText(R.string.str_change_password_successful);
        dialogOke.show();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                dialogOke.dismiss();
            }
        },2000);
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
    public void setClickEvent(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEditPasswordApi();
            }
        });
    }
}