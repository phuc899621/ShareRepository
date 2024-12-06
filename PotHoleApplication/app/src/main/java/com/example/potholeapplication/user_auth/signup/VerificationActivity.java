package com.example.potholeapplication.user_auth.signup;

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
import com.example.potholeapplication.class_pothole.CustomDialog;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.RegisterReq;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.response.ApiResponse;
import com.example.potholeapplication.databinding.ActivityVerificationBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {
    ActivityVerificationBinding binding;
    String code;
    Bundle bundle;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        setClickEvent();
        callAPISendEmail();
    }

    //goi api gui mail ngay khi vao activity
    public void callAPISendEmail(){
        //lay cac thong tin dang ky tu activity signup
        Intent intent=getIntent();
        bundle=intent.getBundleExtra("sendEmail");
        if (bundle == null) {
            CustomDialog.showDialogErrorString(context,getString(R.string.str_null_bundle));
            return;
        }

        EmailReq emailReq =new EmailReq(
          bundle.getString("email")
        );

        //call api gui mail, kem theo thong tin email can gui
        UserAPIInterface apiService = RetrofitServices.getApiService();
        Call<ApiResponse> call = apiService.callRegisterCode(emailReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    //luu lai code de so sanh voi code nguoi dung nhap vao
                    code=response.body().getMessage().trim();
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
                Log.e("API Error", "Failure: " + t.getMessage());
            }
        });
    }
    public void callAPIAddUser(){
        if (bundle == null) {
            CustomDialog.showDialogErrorString(context,getString(R.string.str_null_bundle));
            return;
        }
        UserAPIInterface apiService = RetrofitServices.getApiService();
        RegisterReq registerReq =new RegisterReq(
                bundle.getString("username"),bundle.getString("name"),
                bundle.getString("email"),bundle.getString("password")
        );
        // Call API login
        Call<ApiResponse> call = apiService.callRegister(registerReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if(response.isSuccessful()&&response.body()!=null){
                    Intent intent=new Intent(VerificationActivity.this, VerificationSuccessActivity.class);
                    startActivity(intent);
                    finish();
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
                Log.e("API Error", "Failure: " + t.getMessage());
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
        binding.btnVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kiem tra code hop le
                String codeEntered=binding.etCodeInput.getText().toString().trim();
                if(!codeEntered.equals(code)) {
                    CustomDialog.showDialogErrorString(context,getString(R.string.str_wrong_code));
                    return;
                }
                callAPIAddUser();
            }
        });
        binding.tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPISendEmail();
            }
        });
    }
}