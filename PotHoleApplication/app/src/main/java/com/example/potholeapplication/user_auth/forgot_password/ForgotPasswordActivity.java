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
import com.example.potholeapplication.databinding.ActivityForgotPasswordBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        setClickEvent();
    }
    public void setClickEvent(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPICheckEmail();
            }
        });
    }
    public void callAPICheckEmail(){
        UserAPIInterface apiService = RetrofitServices.getApiService();
        //lay du lieu
        String email=binding.etEmail.getText().toString().trim();
        if(email.isEmpty()){
            Toast.makeText(context,"Please enter email"
                    ,Toast.LENGTH_LONG).show();
            return;
        }
        //chi gui email len api server
        RegisterRequest registerRequest=new RegisterRequest("","",email,"");
        // Call API kiem tra email
        Call<UserApiResponse> call = apiService.callEmailCheckAPI(registerRequest);
        // call API bất đồng bộ
        call.enqueue(new Callback<UserApiResponse>() {
            @Override
            public void onResponse(Call<UserApiResponse> call, Response<UserApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    Intent intent=new Intent(ForgotPasswordActivity.this,
                            ForgotPasswordVerificationActivity.class);
                    intent.putExtra("email",email);
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
        Dialog dialog=new Dialog(ForgotPasswordActivity.this);
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