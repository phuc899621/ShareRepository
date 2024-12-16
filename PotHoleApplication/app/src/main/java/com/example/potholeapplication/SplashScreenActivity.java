package com.example.potholeapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.databinding.ActivitySplashScreenBinding;
import com.example.potholeapplication.pothole_service.SensorService;
import com.example.potholeapplication.user_auth.login.LoginScreenActivity;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setClickEvent();
        checkRealtimePothole();

    }
    public void checkRealtimePothole(){
        Intent serviceIntent = new Intent(this, SensorService.class);
        if(LocalDataManager.getEnableRealTimeDetection(this)){
            startService(serviceIntent);
        }else{
            stopService(serviceIntent);
        }
    }
    public void setClickEvent(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences=getSharedPreferences(
                        "user_info",MODE_PRIVATE
                );
                boolean isLogin=sharedPreferences.getBoolean("login",false);
                if(isLogin){
                    Intent intent=new Intent(SplashScreenActivity.this, HomeScreenActivity.class);

                    startActivity(intent);
                    finish();
                    return;
                }
                Intent intent=new Intent(SplashScreenActivity.this, LoginScreenActivity.class);
                startActivity(intent);
                finish();
            }
        },1500);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
}