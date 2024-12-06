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
import com.example.potholeapplication.class_pothole.request.RegisterReq;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.response.ApiResponse;
import com.example.potholeapplication.class_pothole.request.UserVerificationReq;
import com.example.potholeapplication.databinding.ActivitySignupBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.example.potholeapplication.user_auth.login.LoginScreenActivity;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        setClickEvent();
    }
    public void callUserVerificationAPI(){
        UserAPIInterface apiService = RetrofitServices.getApiService();
        //lay du lieu
        String username=binding.etUsername.getText().toString().trim();
        String password=binding.etPassword.getText().toString().trim();
        String name=binding.etName.getText().toString().trim();
        String email=binding.etEmail.getText().toString().trim();
        String confirmPassword=binding.etConfirmPassword.getText().toString().trim();

        //kiem tra du lieu nhap vao
        if(username.isEmpty() || password.isEmpty()||name.isEmpty()||email.isEmpty()||confirmPassword.isEmpty()){
            CustomDialog.showDialogErrorString(context,getString(R.string.str_please_enter_your_information));
            return;
        }
        if(!password.equals(confirmPassword)){
            CustomDialog.showDialogErrorString(context,getString(R.string.str_password_does_not_match));
            return;
        }
        //tao doi tuong chua email va username de call api kiem tra
        UserVerificationReq userVerificationReq=new UserVerificationReq(username,email);
        //call api kiem tra username va email co ton tai chua
        Call<ApiResponse> call = apiService.callVerifyBeforeRegister(userVerificationReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if(response.isSuccessful()&&response.body()!=null){
                    //Verify email
                    RegisterReq registerReq=new RegisterReq(username,name,email,password);
                    NavigateToEmailVerification(registerReq);
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
    public void NavigateToEmailVerification(RegisterReq registerReq){
        Intent intent = new Intent(SignupActivity.this, VerificationActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("email", registerReq.getEmail().trim());
        bundle.putString("name", registerReq.getName().trim());
        bundle.putString("username", registerReq.getUsername().trim());
        bundle.putString("password", registerReq.getPassword().trim());
        intent.putExtra("sendEmail", bundle);
        startActivity(intent);
    }

    public void setClickEvent(){
        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUserVerificationAPI();
            }
        });
        binding.tvHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this, LoginScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}