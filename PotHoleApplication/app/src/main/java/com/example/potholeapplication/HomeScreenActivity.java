package com.example.potholeapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.class_pothole.CustomDialog;
import com.example.potholeapplication.class_pothole.DataEditor;
import com.example.potholeapplication.class_pothole.LocaleManager;
import com.example.potholeapplication.databinding.ActivityHomeScreenBinding;
import com.github.mikephil.charting.charts.LineChart;

public class HomeScreenActivity extends AppCompatActivity {
    LineChart lineChart;
    Context context;
    ActivityHomeScreenBinding binding;
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

    }
    @Override
    protected void onStart() {
        super.onStart();
        setDisplay();
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
                if(!CustomDialog.isIsDialogShowing()) {
                    CustomDialog.showDialogSavePothole(context,
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
    }
    public void setDisplay(){
        SharedPreferences sharedPreferences=getSharedPreferences(
                "user_info",MODE_PRIVATE
        );
        String name= sharedPreferences.getString("name","");
        binding.tvName.setText(name);
        binding.imaUserIcon.setImageBitmap(DataEditor.getImageBitmapFromSharePreferences(context));
    }
//    public void chartData(){
//        lineChart = findViewById(R.id.lineChart);
//        ArrayList<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(0, 5));
//        entries.add(new Entry(1, 10));
//        entries.add(new Entry(2, 7));
//        entries.add(new Entry(3, 12));
//        entries.add(new Entry(4, 6));
//        entries.add(new Entry(5, 6));
//        entries.add(new Entry(6, 6));
//
//        LineDataSet lineDataSet = new LineDataSet(entries, "Performance");
//        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        lineDataSet.setCubicIntensity(0.2f);
//        lineDataSet.setLineWidth(2f);
//        lineDataSet.setColor(getResources().getColor(R.color.purple));
//        lineDataSet.setDrawFilled(true);
//        lineDataSet.setFillColor(R.color.purple);
//        lineDataSet.setFillAlpha(80);
//
//
//        lineChart.getXAxis().setDrawGridLines(false);
//        lineChart.animateX(1000);
//        final String[] daysOfWeek = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
//
//
//        lineChart.getDescription().setEnabled(false);
//        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//        lineChart.getAxisRight().setEnabled(false);
//        lineChart.invalidate(); // Refresh biểu đồ
//    }


}