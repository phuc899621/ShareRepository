package com.example.potholeapplication.edit_user;

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
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.class_pothole.response.UserResponse;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.databinding.ActivityEditEmailBinding;
import com.example.potholeapplication.Retrofit2.APIInterface;
import com.example.potholeapplication.user_auth.forgot_password.ForgotPasswordActivity;
import com.example.potholeapplication.user_auth.forgot_password.ForgotPasswordVerificationActivity;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEmailActivity extends AppCompatActivity {
    ActivityEditEmailBinding binding;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityEditEmailBinding.inflate(getLayoutInflater());
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }

    public void setClickEvent(){
        binding.btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCheckEmailAPI();
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void callCheckEmailAPI(){
        String newEmail=binding.etEmail.getText().toString().trim();
        if(newEmail.isEmpty()) {
            DialogManager.showDialogErrorString(context,getString(R.string.str_please_enter_your_new_email));
            return;
        }
        APIManager.callFindEmailNonExists(new EmailReq(newEmail)
                ,new APICallBack() {
                    @Override
                    public void onSuccess(Response<UserResponse> response) {
                        Intent intent=new Intent(EditEmailActivity.this, EditEmailVerificationActivity.class);
                        intent.putExtra("email",newEmail);
                        startActivity(intent);
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