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
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.databinding.ActivityEditEmailVerificationBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEmailVerificationActivity extends AppCompatActivity {

    ActivityEditEmailVerificationBinding binding;
    Context context;
    Button btnConfirm;
    Dialog dialogError,dialogOke;
    TextView tvErrorTitle,tvOkeTitle;
    String oldEmail,newEmail,code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityEditEmailVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        getOldEmail();
        getNewEmail();
        SetupDialog();
        setClickEvent();
        callSendCodeApi();
    }
    public void saveEmail(String email){
        SharedPreferences sharedPreferences=getSharedPreferences(
                "user_info",MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("email",email);
        editor.apply();
    }
    public void getOldEmail(){
        SharedPreferences sharedPreferences=getSharedPreferences(
                "user_info",MODE_PRIVATE
        );
        oldEmail=sharedPreferences.getString("email","");
    }
    public void getNewEmail(){
        Intent intent=getIntent();
        newEmail= intent.getStringExtra("email");
    }
    public void callSendCodeApi(){
        UserAPIInterface apiService = RetrofitServices.getApiService();
        EmailReq emailReq=new EmailReq(newEmail);
        Call<ApiResponse> call = apiService.callEmailCode(emailReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if(response.isSuccessful()&&response.body()!=null){
                    code=response.body().getMessage().toString().trim();
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
    public void callEditEmailApi(){
        UserAPIInterface apiService = RetrofitServices.getApiService();
        EmailReq emailReq=new EmailReq(newEmail);
        Call<ApiResponse> call = apiService.callEditEmail(oldEmail,emailReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if(response.isSuccessful()&&response.body()!=null){
                    saveEmail(newEmail);
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
        dialogOke=new Dialog(EditEmailVerificationActivity.this);
        dialogOke.setContentView(R.layout.custom_dialog_oke);
        dialogOke.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogOke.setCancelable(false);
        dialogOke.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        tvOkeTitle=dialogOke.findViewById(R.id.tvTitle);

        dialogError=new Dialog(EditEmailVerificationActivity.this);
        dialogError.setContentView(R.layout.custom_dialog_error);
        dialogError.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogError.setCancelable(true);
        dialogError.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnConfirm=dialogError.findViewById(R.id.btnConfirm);
        tvErrorTitle=dialogError.findViewById(R.id.tvTitle);

    }
    public void showDialogOke(){
        tvOkeTitle.setText(R.string.str_sign_up_successful);
        dialogOke.show();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(EditEmailVerificationActivity.this,
                        EditUserActivity.class);
                startActivity(intent);
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
    public void setClickEvent(){
        binding.btnVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEditEmailApi();
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}