package com.example.potholeapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.Retrofit2.SubinfoAPICallBack;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.manager.SubinfoAPIManager;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.response.SubinfoResponse;
import com.example.potholeapplication.databinding.ActivityHomeScreenBinding;
import com.example.potholeapplication.pothole_service.SensorService;
import com.github.mikephil.charting.charts.LineChart;

import retrofit2.Response;

public class HomeScreenActivity extends AppCompatActivity {
    Context context;
    ActivityHomeScreenBinding binding;
    boolean isAPIReturn=false;//false neu lay api khong thanh cong
    boolean isResume; //kiem tra trang thai activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        setClickEvent();
        setDisplay();
        setReceivePotholeAlert();
    }
    @Override
    protected void onResume() {
        super.onResume();
        callGetSubinfoAPI();
        if(isAPIReturn){
            binding.tvTotalDistances.setText(LocalDataManager.getTotalDistances(context)+"");
            binding.tvFixedPothole.setText(LocalDataManager.getTotalFixedPothole(context)+"");
            binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
            isAPIReturn=false;
        }
        isResume=true;

    }
    @Override
    protected void onStart() {
        super.onStart();
        setDisplay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isResume=false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }

    //nhan thong bao ve pothole tu services
    private final BroadcastReceiver potholeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.SHOW_DIALOG".equals(intent.getAction())) {
                double latitude = intent.getDoubleExtra("latitude", 0);
                double longitude = intent.getDoubleExtra("longitude", 0);
                String severity= intent.getStringExtra("severity");
                Toast.makeText(context, severity, Toast.LENGTH_SHORT).show();
                if(!DialogManager.isIsDialogShowing() && isResume) {
                    DialogManager.showDialogSavePothole(context,
                            longitude, latitude, severity);
                }
            }
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public void setReceivePotholeAlert(){
        IntentFilter filter = new IntentFilter("com.example.SHOW_DIALOG");
        registerReceiver(potholeReceiver, filter);
    }

    public void setClickEvent(){
        binding.maplayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,MapViewActivity.class);
                startActivity(intent);
            }
        });
        binding.analyticslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,AnalyticsActivity.class);
                startActivity(intent);
            }
        });
        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
        binding.rankinglayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,RankingActivity.class);
                startActivity(intent);
            }
        });
    }
    public void setDisplay(){
        binding.tvName.setText(LocalDataManager.getName(this));
        binding.imaUserIcon.setImageBitmap(LocalDataManager.getImageBitmap(context));

    }
    public void callGetSubinfoAPI(){
        SubinfoAPIManager.callGetSubinfo(
                new EmailReq(LocalDataManager.getEmail(this)),
                new SubinfoAPICallBack() {
                    @Override
                    public void onSuccess(Response<SubinfoResponse> response) {

                        LocalDataManager.saveSubinfo(
                                context,
                                Math.round(response.body().getData().get(0).getTotalDistances()*100f)/100f,
                                response.body().getData().get(0).getTotalReport(),
                                response.body().getData().get(0).getTotalFixedPothole()
                        );
                        binding.tvTotalDistances.setText(LocalDataManager.getTotalDistances(context)+"");
                        binding.tvFixedPothole.setText(LocalDataManager.getTotalFixedPothole(context)+"");
                        binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
                        isAPIReturn=true;
                    }

                    @Override
                    public void onError(SubinfoResponse errorResponse) {
                        LocalDataManager.saveSubinfo(context,0,0,0);
                        binding.tvTotalDistances.setText(LocalDataManager.getTotalDistances(context)+"");
                        binding.tvFixedPothole.setText(LocalDataManager.getTotalFixedPothole(context)+"");
                        binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
                        isAPIReturn=true;
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        LocalDataManager.saveSubinfo(context,0,0,0);
                        binding.tvTotalDistances.setText(LocalDataManager.getTotalDistances(context)+"");
                        binding.tvFixedPothole.setText(LocalDataManager.getTotalFixedPothole(context)+"");
                        binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
                        Log.e("API Error", "Failure: " + t.getMessage());
                        DialogManager.showDialogErrorString(context,
                                getString(R.string.str_please_check_your_internet_connection));
                    }
                }
        );
    }

}