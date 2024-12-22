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
import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.class_pothole.manager.APIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.manager.NetworkManager;
import com.example.potholeapplication.class_pothole.other.User;
import com.example.potholeapplication.class_pothole.request.RegisterReq;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.example.potholeapplication.class_pothole.request.UserVerificationReq;
import com.example.potholeapplication.databinding.ActivitySignupBinding;
import com.example.potholeapplication.Retrofit2.APIInterface;
import com.example.potholeapplication.user_auth.login.LoginScreenActivity;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    Context context;
    NetworkManager networkManager;

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
        networkManager=new NetworkManager(this);
        setClickEvent();

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
    public void callUserVerificationAPI(){
        //lay du lieu
        String username=binding.etUsername.getText().toString().trim();
        String password=binding.etPassword.getText().toString().trim();
        String name=binding.etName.getText().toString().trim();
        String email=binding.etEmail.getText().toString().trim();
        String confirmPassword=binding.etConfirmPassword.getText().toString().trim();

        //kiem tra du lieu nhap vao
        if(username.isEmpty() || password.isEmpty()||name.isEmpty()||email.isEmpty()||confirmPassword.isEmpty()){
            DialogManager.showDialogErrorString(context,getString(R.string.str_please_enter_your_information));
            return;
        }
        if(!password.equals(confirmPassword)){
            DialogManager.showDialogErrorString(context,getString(R.string.str_password_does_not_match));
            return;
        }
        //call api kiem tra username va email co ton tai chua
        APIManager.callVerifyBeforeRegister(new UserVerificationReq(username,email)
                , new APICallBack<APIResponse<User>>() {
            @Override
            public void onSuccess(Response<APIResponse<User>> response) {
                RegisterReq registerReq=new RegisterReq(username,name,email,password);
                NavigateToEmailVerification(registerReq);
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
                if(networkManager.isNetworkAvailable()){
                    callUserVerificationAPI();
                }else{
                    Snackbar.make(binding.main,getString(R.string.str_network_unavailable),Snackbar.LENGTH_LONG).show();
                }
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