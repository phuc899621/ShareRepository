package com.example.potholeapplication.user_auth.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.HomeScreenActivity;
import com.example.potholeapplication.R;
import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.class_pothole.manager.APIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.manager.NetworkManager;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.LoginReq;
import com.example.potholeapplication.class_pothole.other.User;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.example.potholeapplication.databinding.ActivityLoginScreenBinding;
import com.example.potholeapplication.user_auth.forgot_password.ForgotPasswordActivity;
import com.example.potholeapplication.user_auth.signup.SignupActivity;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class LoginScreenActivity extends AppCompatActivity {
    ActivityLoginScreenBinding binding;
    Context context;
    NetworkManager networkManager;
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
        networkManager=new NetworkManager(this);
        setClickEvent();
    }

    public void callLoginAPI(){
        if(!networkManager.isNetworkAvailable()){
            return;
        }
        String username=binding.etUsername.getText().toString().trim();
        String password=binding.etPassword.getText().toString().trim();
        if(username.isEmpty() || password.isEmpty()){
            DialogManager.showDialogErrorString(context,getString(R.string.str_please_enter_username_and_password));
            return;
        }
        APIManager.callLogin(new LoginReq(username,password), new APICallBack<APIResponse<User>>() {
            @Override
            public void onSuccess(Response<APIResponse<User>> response) {
                LocalDataManager.saveUser(context,response.body().getData());
                CallGetImageAPI();
            }

            @Override
            public void onError(APIResponse<User> errorResponse) {
                DialogManager.showDialogError(context,errorResponse);

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API Error", "Failure: " + t.getMessage());
                throw new RuntimeException(t);
            }
        });


    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }


    //lay hinh anh tu server
    public void CallGetImageAPI() {
        String email= LocalDataManager.getEmail(context);
        if(email.isEmpty()){
            DialogManager.showDialogErrorString(context,getString(R.string.str_email_not_found));
            return;
        }
        APIManager.callFindImage(new EmailReq(email), new APICallBack<APIResponse<User>>() {
            @Override
            public void onSuccess(Response<APIResponse<User>> response) {
                List<User> data = response.body().getData();
                if (data != null && !data.isEmpty()) {
                    String imageBase64 = data.get(0).getImage();
                    if (!imageBase64.isEmpty()) {
                        //neu co thi luu va den homescreen
                        byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                        LocalDataManager.saveImageBytes(context,decodedBytes);
                        DialogManager.showDialogOkeNavigation(context,getString(R.string.str_login_successful),
                                HomeScreenActivity.class);
                    } else {
                        //neu chua co thi lay anh mac dinh
                        LocalDataManager.saveImageBytes(context,
                                LocalDataManager.drawableToByteArray(context,R.drawable.default_user)
                        );
                        CallSaveImageAPI(LocalDataManager.drawableToByteArray(context,R.drawable.default_user));
                    }
                }
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
    //Luu anh mac dinh len server neu chua co
    public void CallSaveImageAPI(byte[] imageBytes) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestBody);
        String email= LocalDataManager.getEmail(context);
        if(email.isEmpty()){
            DialogManager.showDialogErrorString(context,getString(R.string.str_email_not_found));
            return;
        }
        // Tạo RequestBody cho email và task
        RequestBody emailReq = RequestBody.create(MediaType.parse("text/plain"), email);
        APIManager.callSaveImage(emailReq, imagePart, new APICallBack<APIResponse<User>>() {
            @Override
            public void onSuccess(Response<APIResponse<User>> response) {
                LocalDataManager.saveImageBytes(context,imageBytes);
                DialogManager.showDialogOkeNavigation(context,getString(R.string.str_login_successful),
                        HomeScreenActivity.class);
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
        binding.imaBtnChooseLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.showDialogLanguage(context);
            }
        });
    }

}