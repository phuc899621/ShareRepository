package com.example.potholeapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.Retrofit2.SavePotholeSatusCallBack;
import com.example.potholeapplication.class_pothole.manager.APIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.manager.NetworkManager;
import com.example.potholeapplication.class_pothole.other.Subinfo;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.example.potholeapplication.databinding.ActivityHomeScreenBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Response;

public class HomeScreenActivity extends AppCompatActivity {
    Context context;
    ActivityHomeScreenBinding binding;
    boolean isAPIReturn=false;//false neu lay api khong thanh cong
    boolean isResume; //kiem tra trang thai activity
    NetworkManager networkManager;

    Snackbar snackBar;
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
        setNetworkMonitor();
        setDisplay();
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkAcceleronmeter();
        setReceivePotholeAlert();
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
        unregisterReceiver(potholeReceiver);
        isResume=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkManager.stopMonitoring();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
    //kiem tra xem co acceleronmeter ko
    public void checkAcceleronmeter(){
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Snackbar snackbar;
        // Kiểm tra accelerometer
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                snackbar=Snackbar.make(binding.main,
                        "Thiết bị có hỗ trợ Accelerometer", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Đóng", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();

            } else {
                snackbar=Snackbar.make(binding.main,
                        "Thiết bị khong co hỗ trợ Accelerometer", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Đóng", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();

            }
        } else {
            snackbar=Snackbar.make(binding.main,
                    "Thiết bị khong the truy cap Sensor", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Đóng", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();

        }
    }
    //hien thi thong bao khi network mat ket noi hoac ket noi lai
    private void setNetworkMonitor() {
        networkManager=new NetworkManager(this);
        if(networkManager.isNetworkAvailable()){
            Intent intent = new Intent("com.example.NETWORK");
            intent.putExtra("connected", true);
            sendBroadcast(intent);
            snackBar=Snackbar.make(binding.main,R.string.str_network_available,Snackbar.LENGTH_LONG);
            //snackBar.show();
        }
        else {
            Intent intent = new Intent("com.example.NETWORK");
            intent.putExtra("connected", false);
            sendBroadcast(intent);
            snackBar=Snackbar.make(binding.main,R.string.str_network_unavailable,Snackbar.LENGTH_LONG);
            //snackBar.show();

        }
        networkManager.startMonitoring(new NetworkManager.NetworkStatusListener() {
            @Override
            public void onConnected() {
                Intent intent = new Intent("com.example.NETWORK");
                intent.putExtra("connected", true);
                sendBroadcast(intent);
                snackBar=Snackbar.make(binding.main,R.string.str_network_available,Snackbar.LENGTH_LONG);
                // snackBar.show();
            }

            @Override
            public void onDisconnected() {
                Intent intent = new Intent("com.example.NETWORK");
                intent.putExtra("connected", false);
                sendBroadcast(intent);
                snackBar=Snackbar.make(binding.main,R.string.str_network_unavailable,Snackbar.LENGTH_LONG);
                //snackBar.show();

            }
        });


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
                            longitude, latitude, severity, new SavePotholeSatusCallBack() {
                                @Override
                                public void onComplete(boolean isSuccess) {

                                }
                            });
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
                if(networkManager.isNetworkAvailable()){
                    Intent intent=new Intent(HomeScreenActivity.this,RankingActivity.class);
                    startActivity(intent);
                }
                else {
                    snackBar = Snackbar.make(binding.main, R.string.str_network_unavailable, Snackbar.LENGTH_LONG);
                    snackBar.show();
                }
            }
        });
    }
    public void setDisplay(){
        binding.tvName.setText(LocalDataManager.getName(this));
        binding.imaUserIcon.setImageBitmap(LocalDataManager.getImageBitmap(context));
    }
    public void callGetSubinfoAPI(){
        if(!networkManager.isNetworkAvailable()){
            snackBar=Snackbar.make(binding.main,R.string.str_network_unavailable,Snackbar.LENGTH_LONG);
            snackBar.show();
            float distance=LocalDataManager.getTotalDistances(context)/1000.0f;
            binding.tvTotalDistances.setText(String.format("%.2f", distance));
            binding.tvFixedPothole.setText(LocalDataManager.getTotalFixedPothole(context)+"");
            binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
            return;
        }
        APIManager.callGetSubinfo(
                new EmailReq(LocalDataManager.getEmail(this)),
                new APICallBack<APIResponse<Subinfo>>() {
                    @Override
                    public void onSuccess(Response<APIResponse<Subinfo>> response) {

                        LocalDataManager.saveSubinfo(
                                context,
                                Math.round(response.body().getData().get(0).getTotalDistances()*100f)/100f,
                                response.body().getData().get(0).getTotalReport(),
                                response.body().getData().get(0).getTotalFixedPothole()
                        );
                        float distance=LocalDataManager.getTotalDistances(context)/1000.0f;
                        binding.tvTotalDistances.setText(String.format("%.2f", distance));
                        binding.tvFixedPothole.setText(LocalDataManager.getTotalFixedPothole(context)+"");
                        binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
                        isAPIReturn=true;
                    }

                    @Override
                    public void onError(APIResponse<Subinfo> errorResponse) {
                        LocalDataManager.saveSubinfo(context,0,0,0);
                        float distance=LocalDataManager.getTotalDistances(context)/1000.0f;
                        binding.tvTotalDistances.setText(String.format("%.2f", distance));
                        binding.tvFixedPothole.setText(LocalDataManager.getTotalFixedPothole(context)+"");
                        binding.tvTotalReport.setText(LocalDataManager.getTotalReport(context)+"");
                        isAPIReturn=true;
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        LocalDataManager.saveSubinfo(context,0,0,0);
                        float distance=LocalDataManager.getTotalDistances(context)/1000.0f;
                        binding.tvTotalDistances.setText(String.format("%.2f", distance));
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