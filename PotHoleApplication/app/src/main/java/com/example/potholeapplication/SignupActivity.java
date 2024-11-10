package com.example.potholeapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.example.potholeapplication.class_pothole.LoginRequest;
import com.example.potholeapplication.class_pothole.RegisterRequest;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.User;
import com.example.potholeapplication.class_pothole.UserApiResponse;
import com.example.potholeapplication.databinding.ActivitySignupBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    Context context;
    Button btnConfirm;
    TextView tvErrorTitle,tvOkeTitle;
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
        setClickEvent();
        context=this;
    }
    public void callAPI(){
        UserAPIInterface apiService = RetrofitServices.getApiService();
        //lay du lieu
        String username=binding.etUsername.getText().toString().trim();
        String password=binding.etPassword.getText().toString().trim();
        String name=binding.etUsername.getText().toString().trim();
        String email=binding.etEmail.getText().toString().trim();
        String confirmPassword=binding.etConfirmPassword.getText().toString().trim();
        if(username.isEmpty() || password.isEmpty()||name.isEmpty()||email.isEmpty()||confirmPassword.isEmpty()){
            Toast.makeText(context,"Please enter your information"
                    ,Toast.LENGTH_LONG).show();
            return;
        }
        if(!password.equals(confirmPassword)){
            Toast.makeText(context,"Password does not match"
                    ,Toast.LENGTH_LONG).show();
            return;
        }
        RegisterRequest registerRequest=new RegisterRequest(username,name,email,password);
        // Call API login
        Call<UserApiResponse> call = apiService.callRegisterApi(registerRequest);
        // call API bất đồng bộ
        call.enqueue(new Callback<UserApiResponse>() {
            @Override
            public void onResponse(Call<UserApiResponse> call, Response<UserApiResponse> response) {

                if(response.isSuccessful()&&response.body()!=null){
                    showDialogOke(registerRequest);
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
    public void showDialogOke(RegisterRequest registerRequest){
        Dialog dialog=new Dialog(SignupActivity.this);
        dialog.setContentView(R.layout.custom_dialog_oke);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        tvOkeTitle=dialog.findViewById(R.id.tvTitle);
        tvOkeTitle.setText(R.string.str_sign_up_successful);
        dialog.show();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SignupActivity.this, VerificationActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("email",registerRequest.getEmail().trim());
                bundle.putString("name",registerRequest.getName().trim());
                bundle.putString("username",registerRequest.getUsername().trim());
                bundle.putString("password",registerRequest.getPassword().trim());
                intent.putExtra("sendEmail", bundle);
                startActivity(intent);
                dialog.dismiss();
            }
        },2000);
    }
    public void showDialogError(UserApiResponse apiResponse){
        Dialog dialog=new Dialog(SignupActivity.this);
        dialog.setContentView(R.layout.custom_dialog_error);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnConfirm=dialog.findViewById(R.id.btnConfirm);
        tvErrorTitle=dialog.findViewById(R.id.tvTitle);
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
        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPI();
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