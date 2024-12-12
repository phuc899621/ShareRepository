package com.example.potholeapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.databinding.ActivityAnalyticsBinding;
import com.example.potholeapplication.databinding.ActivityHomeScreenBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;

public class AnalyticsActivity extends AppCompatActivity {

    ActivityAnalyticsBinding binding;
    BarChart barChart;
    BarDataSet barDataSet1, barDataSet2;
    // ArrayList for storing entries
    ArrayList<BarEntry> barEntries;
    // Creating a string array for displaying days
    String[] days = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    PieChart pieChart;
    TextView tvLarge, tvMedium, tvSmall;
    // Variables for bar data sets

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        barChart = binding.idBarChart;

        pieChart = binding.idPieChart;
        tvLarge = binding.tvLargePercentage;
        tvMedium = binding.tvMediumPercentage;
        tvSmall = binding.tvSmallPercentage;

        //BAR CHART
        // Creating a new bar data set*
        barDataSet1 = new BarDataSet(getBarEntriesOne(), "First Set");
        barDataSet1.setColor(getApplicationContext().getResources().getColor(R.color.large));
        barDataSet2 = new BarDataSet(getBarEntriesTwo(), "Second Set");
        barDataSet2.setColor(getApplicationContext().getResources().getColor(R.color.medium));

        // Adding bar data sets to the bar data
        BarData data = new BarData(barDataSet1, barDataSet2);

        // Setting the data to the bar chart
        barChart.setData(data);

        // Removing the description label of the bar chart
        barChart.getDescription().setEnabled(false);

        // Getting the x-axis of the bar chart
        XAxis xAxis = barChart.getXAxis();

        // Setting value formatter to the x-axis
        // and adding the days to the x-axis labels
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));

        // Setting center axis labels for the bar chart
        xAxis.setCenterAxisLabels(true);

        // Setting the position of the x-axis to bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Setting granularity for the x-axis labels
        xAxis.setGranularity(1);

        // Enabling granularity for the x-axis
        xAxis.setGranularityEnabled(true);

        // Making the bar chart draggable
        barChart.setDragEnabled(true);

        // Setting the visible range for the bar chart
        barChart.setVisibleXRangeMaximum(3);

        // Adding bar space to the chart
        float barSpace = 0.1f;

        // Adding group spacing to the chart
        float groupSpace = 0.5f;

        // Setting the width of the bars
        data.setBarWidth(0.15f);

        // Setting the minimum axis value for the chart
        barChart.getXAxis().setAxisMinimum(0);

        // Animating the chart
        barChart.animate();

        // Grouping bars and adding spacing to them
        barChart.groupBars(0, groupSpace, barSpace);

        // Invalidating the bar chart
        barChart.invalidate();
        setData();
        pieChart.invalidate();
    }

    // ArrayList for the first set of bar entries
    private ArrayList<BarEntry> getBarEntriesOne() {
        // Creating a new ArrayList
        barEntries = new ArrayList<>();

        // Adding entries to the ArrayList for the first set
        barEntries.add(new BarEntry(1f, 4));
        barEntries.add(new BarEntry(2f, 6));
        barEntries.add(new BarEntry(3f, 8));
        barEntries.add(new BarEntry(4f, 2));
        barEntries.add(new BarEntry(5f, 4));
        barEntries.add(new BarEntry(6f, 1));

        return barEntries;
    }

    // ArrayList for the second set of bar entries
    private ArrayList<BarEntry> getBarEntriesTwo() {
        // Creating a new ArrayList
        barEntries = new ArrayList<>();

        // Adding entries to the ArrayList for the second set
        barEntries.add(new BarEntry(1f, 8));
        barEntries.add(new BarEntry(2f, 12));
        barEntries.add(new BarEntry(3f, 4));
        barEntries.add(new BarEntry(4f, 1));
        barEntries.add(new BarEntry(5f, 7));
        barEntries.add(new BarEntry(6f, 3));

        return barEntries;
    }

    // PIE CHART
    private void setData(){
        tvLarge.setText(Integer.toString(40));
        tvMedium.setText(Integer.toString(30));
        tvSmall.setText(Integer.toString(30));

        pieChart.addPieSlice(new PieModel("Large", Integer.parseInt(tvLarge.getText().toString()), Color.parseColor("#3B1E54")));
        pieChart.addPieSlice(new PieModel("Medium", Integer.parseInt(tvMedium.getText().toString()), Color.parseColor("#9B7EBD")));
        pieChart.addPieSlice(new PieModel("Small", Integer.parseInt(tvSmall.getText().toString()), Color.parseColor("#D4BEE4")));;
        pieChart.startAnimation();
    }


}