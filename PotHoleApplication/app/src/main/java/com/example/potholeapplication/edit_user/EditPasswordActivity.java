package com.example.potholeapplication.edit_user;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.R;
import com.example.potholeapplication.class_pothole.LocaleManager;
import com.example.potholeapplication.class_pothole.response.ApiResponse;
import com.example.potholeapplication.class_pothole.CustomDialog;
import com.example.potholeapplication.class_pothole.DataEditor;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.request.EditPasswordReq;
import com.example.potholeapplication.databinding.ActivityEditPasswordBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPasswordActivity extends AppCompatActivity {
    ActivityEditPasswordBinding binding;
    Context context;
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
        setClickEvent();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
    public void callEditPasswordApi(){
        String email= DataEditor.getEmail(context);
        String oldPassword=binding.etOldPassword.getText().toString().trim();
        String newPassword=binding.etNewPassword.getText().toString().trim();
        String confirmPassword=binding.etConfirmPassword.getText().toString().trim();

        if(email.isEmpty()){
            CustomDialog.showDialogErrorString(context,getString(R.string.str_email_not_found));
            return;
        }
        if(!newPassword.equals(confirmPassword)){
            CustomDialog.showDialogErrorString(context,getString(R.string.str_password_does_not_match));
            return;
        }

        EditPasswordReq editPasswordReq=new EditPasswordReq(email,oldPassword,newPassword);
        UserAPIInterface apiService = RetrofitServices.getApiService();
        Call<ApiResponse> call = apiService.callChangePassword(editPasswordReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if(response.isSuccessful()&&response.body()!=null){
                    CustomDialog.showDialogOkeThenFinish(context,getString(R.string.str_change_password_successful));
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
        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEditPasswordApi();
            }
        });
    }
}