package com.example.potholeapplication.user_auth.signup;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.potholeapplication.class_pothole.User;
import com.example.potholeapplication.class_pothole.UserApiResponse;
import com.example.potholeapplication.databinding.ActivityVerificationBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {
    ActivityVerificationBinding binding;
    String code;
    Bundle bundle;
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
        setClickEvent();
        callAPISendEmail();
    }
    public void callAPISendEmail(){
        UserAPIInterface apiService = RetrofitServices.getApiService();
        //lay du lieu
        Intent intent=getIntent();
        bundle=intent.getBundleExtra("sendEmail");
        if (bundle == null) {
            Toast.makeText(VerificationActivity.this,
                    "Null Bundle",Toast.LENGTH_LONG).show();
            return;
        }
        RegisterRequest registerRequest=new RegisterRequest(
          bundle.getString("username"),bundle.getString("name"),
          bundle.getString("email"),bundle.getString("password")
        );

        // Call API login
        Call<UserApiResponse> call = apiService.callSendCodeRegisterAPI(registerRequest);
        // call API bất đồng bộ
        call.enqueue(new Callback<UserApiResponse>() {
            @Override
            public void onResponse(Call<UserApiResponse> call, Response<UserApiResponse> response) {

                if(response.isSuccessful()&&response.body()!=null){
                    code=response.body().getMessage().trim();
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
    public void callAPIAddUser(){
        UserAPIInterface apiService = RetrofitServices.getApiService();
        //lay du lieu
        if (bundle == null) {
            Toast.makeText(VerificationActivity.this,
                    "Null Bundle",Toast.LENGTH_LONG).show();
            return;
        }
        RegisterRequest registerRequest=new RegisterRequest(
                bundle.getString("username"),bundle.getString("name"),
                bundle.getString("email"),bundle.getString("password")
        );
        // Call API login
        Call<UserApiResponse> call = apiService.callAddUserAPI(registerRequest);
        // call API bất đồng bộ
        call.enqueue(new Callback<UserApiResponse>() {
            @Override
            public void onResponse(Call<UserApiResponse> call, Response<UserApiResponse> response) {

                if(response.isSuccessful()&&response.body()!=null){
                    Intent intent=new Intent(VerificationActivity.this, VerificationSuccessActivity.class);
                    startActivity(intent);
                    finish();
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
        Dialog dialog=new Dialog(VerificationActivity.this);
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
                String codeEntered=binding.etCodeInput.getText().toString().trim();
                if(!codeEntered.equals(code)) {
                    Toast.makeText(VerificationActivity.this, "Wrong Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                callAPIAddUser();
            }
        });
    }
}