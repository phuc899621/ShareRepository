package com.example.potholeapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.databinding.ActivityHomeScreenBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity {
    LineChart lineChart;//chua dung den
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
        setClickEvent();// chỗ này cài su kien click

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
        binding.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,UserActivity.class);
                startActivity(intent);
            }
        });
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