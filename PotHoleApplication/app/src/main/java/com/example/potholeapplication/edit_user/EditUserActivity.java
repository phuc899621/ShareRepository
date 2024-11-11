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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.HomeScreenActivity;
import com.example.potholeapplication.R;
import com.example.potholeapplication.class_pothole.EditInfoRequest;
import com.example.potholeapplication.class_pothole.LoginRequest;
import com.example.potholeapplication.class_pothole.RegisterRequest;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.User;
import com.example.potholeapplication.class_pothole.UserApiResponse;
import com.example.potholeapplication.databinding.ActivityEditUserBinding;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.example.potholeapplication.user_auth.login.LoginScreenActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {
    ActivityEditUserBinding binding;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityEditUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setClickEvent();
        setUserInfo();
    }
    public void setClickEvent() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSaveInfoAPI();
            }
        });
    }
    public void setUserInfo(){
        SharedPreferences sharedPreferences=getSharedPreferences("user_info",MODE_PRIVATE);
        binding.etName.setText(sharedPreferences.getString("name",""));
        binding.etEmail.setText(sharedPreferences.getString("email",""));
        binding.etUsername.setText(sharedPreferences.getString("username",""));
    }
    public void callSaveInfoAPI(){
        SharedPreferences sharedPreferences=getSharedPreferences("user_info",MODE_PRIVATE);
        String email=sharedPreferences.getString("email","");


        //lay du lieu
        String username=binding.etUsername.getText().toString().trim();
        String name=binding.etName.getText().toString().trim();
        if(username.isEmpty() || name.isEmpty()){
            Toast.makeText(context,"Please enter username and full name"
                    ,Toast.LENGTH_LONG).show();
            return;
        }
        EditInfoRequest editInfoRequest=new EditInfoRequest(username,name);
        UserAPIInterface apiService = RetrofitServices.getApiService();
        // Call API login
        Call<UserApiResponse> call = apiService.callChangeInfoAPI(email,editInfoRequest);
        // call API bất đồng bộ
        call.enqueue(new Callback<UserApiResponse>() {
            @Override
            public void onResponse(Call<UserApiResponse> call, Response<UserApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    //luu thong tin vao dien thoai
                    saveUserInfo(username,name);
                    showDialogOke();
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
    public void showDialogOke(){
        Dialog dialog=new Dialog(EditUserActivity.this);
        dialog.setContentView(R.layout.custom_dialog_oke);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        TextView tvTitle=dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.str_save_successfully);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //luu thong tin dang nhap vao file
                finish();
            }
        },1500);
    }
    public void saveUserInfo(String username,String name){
        SharedPreferences sharedPreferences=getSharedPreferences(
                "user_info",MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",username);
        editor.putString("name",name);
        editor.apply();
    }
    public void showDialogError(UserApiResponse apiResponse){
        Dialog dialog=new Dialog(EditUserActivity.this);
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