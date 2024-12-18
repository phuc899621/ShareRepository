package com.example.potholeapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.databinding.ActivitySplashScreenBinding;
import com.example.potholeapplication.pothole_service.SensorService;
import com.example.potholeapplication.user_auth.login.LoginScreenActivity;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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
        Log.d("Permission",LocalDataManager.getEnableRealTimeDetection(this)+"");
        requestLocationPermission();
        registerReceiver(potholeReceiver, new IntentFilter("REQUEST_LOCATION_PERMISSION"));

    }
    public void checkRealtimePothole(){
        Intent serviceIntent = new Intent(this, SensorService.class);
        Log.d("Permission",LocalDataManager.getEnableRealTimeDetection(this)+"");
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
    private final BroadcastReceiver potholeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("REQUEST_LOCATION_PERMISSION".equals(intent.getAction())) {
                requestLocationPermission();
            }
        }
    };
    public void requestLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            checkRealtimePothole();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp
                checkRealtimePothole();
                Log.d("Permission", "Quyền truy cập oke.");

            } else {
                // Quyền bị từ chối
                Log.d("Permission", "Quyền truy cập vị trí bị từ chối. Không thể phát hiện ổ gà.");
            }
        }
    }
}