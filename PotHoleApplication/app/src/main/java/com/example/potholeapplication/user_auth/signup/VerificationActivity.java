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
import com.example.potholeapplication.Retrofit2.UserAPICallBack;
import com.example.potholeapplication.class_pothole.manager.UserAPIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.RegisterReq;
import com.example.potholeapplication.class_pothole.response.UserResponse;
import com.example.potholeapplication.databinding.ActivityVerificationBinding;

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
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }

    //goi api gui mail ngay khi vao activity
    public void callAPISendEmail(){
        //lay cac thong tin dang ky tu activity signup
        Intent intent=getIntent();
        bundle=intent.getBundleExtra("sendEmail");
        if (bundle == null) {
            DialogManager.showDialogErrorString(context,getString(R.string.str_null_bundle));
            return;
        }

        //call api gui mail, kem theo thong tin email can gui
        UserAPIManager.callRegisterCode(
                new EmailReq(bundle.getString("email"))
                , new UserAPICallBack() {
                    @Override
                    public void onSuccess(Response<UserResponse> response) {
                        code=response.body().getMessage().trim();
                    }

                    @Override
                    public void onError(UserResponse errorResponse) {
                        DialogManager.showDialogError(context, errorResponse);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API Error", "Failure: " + t.getMessage());
                        throw new RuntimeException(t);
                    }
                });
    }
    public void callAPIAddUser(){
        if (bundle == null) {
            DialogManager.showDialogErrorString(context,getString(R.string.str_null_bundle));
            return;
        }
        RegisterReq registerReq =new RegisterReq(
                bundle.getString("username"),bundle.getString("name"),
                bundle.getString("email"),bundle.getString("password")
        );
        // Call API login
        UserAPIManager.callRegister(registerReq
                ,new UserAPICallBack() {
                    @Override
                    public void onSuccess(Response<UserResponse> response) {
                        Intent intent=new Intent(VerificationActivity.this, VerificationSuccessActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(UserResponse errorResponse) {
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
        binding.btnVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kiem tra code hop le
                String codeEntered=binding.etCodeInput.getText().toString().trim();
                if(!codeEntered.equals(code)) {
                    DialogManager.showDialogErrorString(context,getString(R.string.str_wrong_code));
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