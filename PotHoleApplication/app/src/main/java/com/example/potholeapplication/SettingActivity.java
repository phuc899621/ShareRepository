package com.example.potholeapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.class_pothole.CustomDialog;
import com.example.potholeapplication.class_pothole.DataEditor;
import com.example.potholeapplication.class_pothole.LocaleManager;
import com.example.potholeapplication.databinding.ActivitySettingBinding;
import com.example.potholeapplication.edit_user.EditUserActivity;
import com.example.potholeapplication.pothole_service.SensorService;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivitySettingBinding.inflate(getLayoutInflater());
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
    protected void onResume() {
        super.onResume();
        setData();
        setSwitchRealtime();
    }
    public void setData(){
        binding.tvUsername.setText(DataEditor.getNameFromSharePreferences(context));
        binding.imaPicture.setImageBitmap(DataEditor.getImageBitmapFromSharePreferences(context));
    }
    public void setSwitchRealtime(){
        boolean isCheck=DataEditor.getEnableRealTimeDetection(this);
        binding.switchRealtime.setChecked(isCheck);
    }

    public void setClickEvent(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.tvEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingActivity.this, EditUserActivity.class);
                startActivity(intent);
            }
        });
        binding.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingActivity.this,EditUserActivity.class);
                startActivity(intent);
            }
        });
        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //xoa cac activity khác, quay lại nhu cũ
                SharedPreferences sharedPreferences=getSharedPreferences(
                        "user_info",MODE_PRIVATE
                );
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("login",false);
                editor.apply();
                Intent intent = new Intent(SettingActivity.this, SplashScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });
        binding.tvLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.showDialogLanguage(context);
            }
        });
        binding.switchRealtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DataEditor.setEnableRealTimeDetection(context,isChecked);
                Intent serviceIntent = new Intent(context, SensorService.class);
                if(isChecked){
                    startService(serviceIntent);
                }else{
                    stopService(serviceIntent);
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
}