package com.example.potholeapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.class_pothole.manager.APIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.manager.NetworkManager;
import com.example.potholeapplication.class_pothole.other.Subinfo;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.example.potholeapplication.databinding.ActivitySettingBinding;
import com.example.potholeapplication.edit_user.EditUserActivity;
import com.example.potholeapplication.pothole_service.SensorService;

import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    Context context;
    boolean isAPIReturn=false;
    NetworkManager networkManager;

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
        networkManager=new NetworkManager(this);
        callGetSubinfoAPI();
        if(isAPIReturn){
            binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
            binding.tvPoints.setText(LocalDataManager.getTotalReport(context)*10+"");
            isAPIReturn=false;
        }
        binding.tvRanking.setText(LocalDataManager.getUserRank(context)+"");
        setData();
        setSwitchRealtime();
    }
    public void callGetSubinfoAPI(){
        if(!networkManager.isNetworkAvailable()){
            binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
            binding.tvPoints.setText(LocalDataManager.getTotalReport(context)*10+"");
            return;
        }
        APIManager.callGetSubinfo(
                new EmailReq(LocalDataManager.getEmail(this)),
                new APICallBack<APIResponse<Subinfo>>() {
                    @Override
                    public void onSuccess(Response<APIResponse<Subinfo>> response) {
                        LocalDataManager.saveSubinfo(
                                context,
                                response.body().getData().get(0).getTotalDistances(),
                                response.body().getData().get(0).getTotalReport(),
                                response.body().getData().get(0).getTotalFixedPothole()
                        );
                        binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
                        binding.tvPoints.setText(LocalDataManager.getTotalReport(context)*10+"");
                        isAPIReturn=true;
                    }

                    @Override
                    public void onError(APIResponse<Subinfo> errorResponse) {
                        binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
                        binding.tvPoints.setText(LocalDataManager.getTotalReport(context)*10+"");
                        isAPIReturn=true;
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
                        binding.tvPoints.setText(LocalDataManager.getTotalReport(context)*10+"");
                        Log.e("API Error", "Failure: " + t.getMessage());
                    }
                }
        );
    }
    public void setData(){
        binding.tvUsername.setText(LocalDataManager.getName(context));
        binding.imaPicture.setImageBitmap(LocalDataManager.getImageBitmap(context));
    }
    public void setSwitchRealtime(){
        boolean isCheck= LocalDataManager.getEnableRealTimeDetection(this);
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
                LocalDataManager.saveLogin(context,false);
                Intent intent = new Intent(SettingActivity.this, SplashScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });

        binding.tvLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.showDialogLanguage(context);
            }
        });
        binding.switchRealtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LocalDataManager.setEnableRealTimeDetection(context,isChecked);
                Intent serviceIntent = new Intent(context, SensorService.class);
                if(isChecked){
                    startService(serviceIntent);
                }else{
                    stopService(serviceIntent);
                }
            }
        });
        binding.tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(SettingActivity.this, ManualReportActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("SettingActivity", "Error starting ManualReportActivity", e);
                }
            }
        });
        binding.tvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.showDialogPolicy(context);
            }
        });
        binding.tvCustomerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.showDialogSupport(context);
            }
        });
        binding.tvDeveloperInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.showDialogUs(context);
            }
        });
//
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
    //------------------Network------------------



}