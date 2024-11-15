package com.example.potholeapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.class_pothole.ApiResponse;
import com.example.potholeapplication.class_pothole.CustomDialog;
import com.example.potholeapplication.class_pothole.DataEditor;
import com.example.potholeapplication.class_pothole.RetrofitServices;
import com.example.potholeapplication.class_pothole.User;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.databinding.ActivityHomeScreenBinding;
import com.example.potholeapplication.edit_user.EditUserActivity;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreenActivity extends AppCompatActivity {
    LineChart lineChart;//chua dung den
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
        setClickEvent();// chỗ này cài su kien click
        setDisplay();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setDisplay();
    }



    public void setClickEvent(){
        binding.cvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,MapViewActivity.class);
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
        binding.btnUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this, EditUserActivity.class);
                startActivity(intent);
            }
        });
    }
    public void setDisplay(){
        SharedPreferences sharedPreferences=getSharedPreferences(
                "user_info",MODE_PRIVATE
        );
        String name= sharedPreferences.getString("name","not found");
        binding.tvName.setText(name);
        binding.btnUserIcon.setImageBitmap(DataEditor.getImageBitmapFromSharePreferences(context));
    }
    //chưa xong
    public void chartData(){
        lineChart = findViewById(R.id.lineChart);


        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 5));
        entries.add(new Entry(1, 10));
        entries.add(new Entry(2, 7));
        entries.add(new Entry(3, 12));
        entries.add(new Entry(4, 6));
        entries.add(new Entry(5, 6));
        entries.add(new Entry(6, 6));

        LineDataSet lineDataSet = new LineDataSet(entries, "Performance");
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setColor(getResources().getColor(R.color.purple));
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(R.color.purple);
        lineDataSet.setFillAlpha(80);


        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.animateX(1000);
        final String[] daysOfWeek = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};


        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate(); // Refresh biểu đồ
    }


}