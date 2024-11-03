package com.example.potholeapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity {
    LineChart lineChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lineChart = findViewById(R.id.lineChart);

        // Tạo dữ liệu cho LineChart
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 5));
        entries.add(new Entry(1, 10));
        entries.add(new Entry(2, 7));
        entries.add(new Entry(3, 12));
        entries.add(new Entry(4, 6));
        entries.add(new Entry(5, 6));
        entries.add(new Entry(6, 6));

        LineDataSet lineDataSet = new LineDataSet(entries, "Performance");
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);  // Tạo đường cong
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setColor(getResources().getColor(R.color.purple));  // Màu cho phần tă
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(R.color.purple);
        lineDataSet.setFillAlpha(80);

        lineDataSet.setDrawFilled(true); // Bật fill
        lineDataSet.setFillDrawable(getResources().getDrawable(R.drawable.gradient_fill)); // Sử dụng gradient
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);;
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false); // Tắt đường chỉ định trục Y trái
        lineChart.getAxisRight().setDrawGridLines(false); // Tắt đường chỉ định trục Y phải
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.animateX(1000);
        final String[] daysOfWeek = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        // Thiết lập trục X với nhãn tùy chỉnh
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Đảm bảo mỗi nhãn cách đều nhau
        xAxis.setLabelCount(daysOfWeek.length); // Số lượng nhãn
        // Tùy chỉnh biểu đồ
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate(); // Refresh biểu đồ
    }


}