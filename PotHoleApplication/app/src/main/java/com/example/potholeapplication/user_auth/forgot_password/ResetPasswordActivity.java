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
import com.example.potholeapplication.class_pothole.other.User;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.example.potholeapplication.class_pothole.request.ResetPasswordReq;
import com.example.potholeapplication.databinding.ActivityResetPasswordBinding;

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
            DialogManager.showDialogErrorString(this,getString(R.string.str_please_enter_your_password));
            return;
        }
        if(!passwordConfirm.equals(password)) {
            DialogManager.showDialogErrorString(this,getString(R.string.str_password_does_not_match));
            return;
        }
        APIManager.callResetPassword(new ResetPasswordReq(emailForResetPassword,password)
                ,new APICallBack<APIResponse<User>>() {
                    @Override
                    public void onSuccess(Response<APIResponse<User>> response) {
                        Intent intent=new Intent(ResetPasswordActivity.this,
                                ResetPasswordSuccessActivity.class);
                        startActivity(intent);                    }
                    @Override
                    public void onError(APIResponse<User> errorResponse) {
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