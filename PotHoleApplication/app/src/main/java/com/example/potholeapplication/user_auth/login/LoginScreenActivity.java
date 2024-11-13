package com.example.potholeapplication.user_auth.login;

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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.HomeScreenActivity;
import com.example.potholeapplication.R;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.LoginReq;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.User;
import com.example.potholeapplication.class_pothole.ApiResponse;
import com.example.potholeapplication.databinding.ActivityLoginScreenBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.example.potholeapplication.user_auth.forgot_password.ForgotPasswordActivity;
import com.example.potholeapplication.user_auth.signup.SignupActivity;
import com.example.potholeapplication.user_auth.signup.VerificationActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreenActivity extends AppCompatActivity {
    ActivityLoginScreenBinding binding;
    Context context;
    Button btnConfirm;
    Dialog dialogError,dialogOke;
    TextView tvErrorTitle,tvOkeTitle;
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
        SetupDialog();
        setClickEvent(); //cài su kien click

    }
    public void callLoginAPI(){
        UserAPIInterface apiService = RetrofitServices.getApiService();
        String username=binding.etUsername.getText().toString().trim();
        String password=binding.etPassword.getText().toString().trim();
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(context,"Vui lòng nhập đầu đủ username và password"
                    ,Toast.LENGTH_LONG).show();
            return;
        }

        LoginReq loginReq =new LoginReq(username,password);
        Call<ApiResponse> call = apiService.callLogin(loginReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                 if(response.isSuccessful()&&response.body()!=null){
                     //luu thong tin vao dien thoai
                     saveUserInfo(response.body().getData());
                     showDialogOke();
                 }
                 else{
                     String errorString;
                     JsonObject errorJson;
                     ApiResponse apiResponse;
                     try {
                         //lay chuoi json va chuyen thanh UserAPIResponse
                         errorString=response.errorBody().string();
                         errorJson= JsonParser.parseString(errorString).getAsJsonObject();
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

    public void saveUserInfo(List<User> userList){
        User user=userList.get(0);
        SharedPreferences sharedPreferences=getSharedPreferences(
                "user_info",MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",user.getUsername());
        editor.putString("email",user.getEmail());
        editor.putString("name",user.getName());
        editor.putBoolean("login",true);
        editor.apply();
    }
    public void SetupDialog(){
        dialogOke=new Dialog(LoginScreenActivity.this);
        dialogOke.setContentView(R.layout.custom_dialog_oke);
        dialogOke.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogOke.setCancelable(false);
        dialogOke.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        tvOkeTitle=dialogOke.findViewById(R.id.tvTitle);

        dialogError=new Dialog(LoginScreenActivity.this);
        dialogError.setContentView(R.layout.custom_dialog_error);
        dialogError.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogError.setCancelable(true);
        dialogError.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnConfirm=dialogError.findViewById(R.id.btnConfirm);
        tvErrorTitle=dialogError.findViewById(R.id.tvTitle);

    }
    public void showDialogOke(){
        tvOkeTitle.setText(R.string.str_login_successful);
        dialogOke.show();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //luu thong tin dang nhap vao file
                Intent intent = new Intent(LoginScreenActivity.this, HomeScreenActivity.class);
                startActivity(intent);
                dialogOke.dismiss();
                finish();
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


    //su kien click
    public void setClickEvent(){
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLoginAPI();

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
                Intent intent=new Intent(LoginScreenActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}