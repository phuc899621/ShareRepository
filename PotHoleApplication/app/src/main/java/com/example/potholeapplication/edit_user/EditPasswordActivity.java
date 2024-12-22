package com.example.potholeapplication.edit_user;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.other.User;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.request.EditPasswordReq;
import com.example.potholeapplication.databinding.ActivityEditPasswordBinding;

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

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkReceiver, new IntentFilter("com.example.NETWORK"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
    public void callEditPasswordApi(){
        String email= LocalDataManager.getEmail(context);
        String oldPassword=binding.etOldPassword.getText().toString().trim();
        String newPassword=binding.etNewPassword.getText().toString().trim();
        String confirmPassword=binding.etConfirmPassword.getText().toString().trim();

        if(email.isEmpty()){
            DialogManager.showDialogErrorString(context,getString(R.string.str_email_not_found));
            return;
        }
        if(!newPassword.equals(confirmPassword)){
            DialogManager.showDialogErrorString(context,getString(R.string.str_password_does_not_match));
            return;
        }
        APIManager.callChangePassword(
                new EditPasswordReq(email,oldPassword,newPassword)
                ,new APICallBack<APIResponse<User>>() {
                    @Override
                    public void onSuccess(Response<APIResponse<User>> response) {
                        DialogManager.showDialogOkeThenFinish(context,getString(R.string.str_change_password_successful));
                    }

                    @Override
                    public void onError(APIResponse<User> errorResponse) {
                        DialogManager.showDialogError(context, errorResponse);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API Error", "Failure: " + t.getMessage());
                        throw new RuntimeException(t);
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
    //----------------NETWORK-------------------
    public BroadcastReceiver networkReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("com.example.NETWORK".equals(intent.getAction())){
                boolean isConnected=intent.getBooleanExtra("connected",false);
                if(!isConnected){
                    DialogManager.showDialogWarningThenFinish(context);
                }
            }
        }
    };
}