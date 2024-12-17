package com.example.potholeapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.potholeapplication.Retrofit2.CountAPICallBack;
import com.example.potholeapplication.Retrofit2.SeverityAPICallBack;
import com.example.potholeapplication.class_pothole.manager.CountAPIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.SeverityAPIManager;
import com.example.potholeapplication.class_pothole.other.PotholeCountByMonth;
import com.example.potholeapplication.class_pothole.request.DayReq;
import com.example.potholeapplication.class_pothole.response.CountResponse;
import com.example.potholeapplication.class_pothole.response.SeverityResponse;
import com.example.potholeapplication.databinding.ActivityAnalyticsBinding;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Response;

public class AnalyticsActivity extends AppCompatActivity {

    ActivityAnalyticsBinding binding;
    
    BarDataSet barDataSet1, barDataSet2;
    // ArrayList for storing entries
    ArrayList<BarEntry> barEntriesOne,barEntriesTwo;
    // Creating a string array for displaying days
    String[] days;
    float piechartSmallData=0,piechartMediumData=0,piechartLargeData=0;
    float smallCount=0,mediumCount=0,largeCount=0,totalCount=0;
    // Variables for bar data sets
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        days=getResources().getStringArray(R.array.months_array);
        binding.tvDayMonthBarChart.setText(Calendar.getInstance().get(Calendar.YEAR)+
                "-"+(Calendar.getInstance().get(Calendar.YEAR)+1));
        context = this;
        setClickEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        callAPIGetPotholeCountByMonth();
        callAPIGetPotholeBySeverity();
    }

    public void callAPIGetPotholeCountByMonth(){
        Calendar calendar=Calendar.getInstance();
        DayReq dayReq=new DayReq(calendar.get(Calendar.MONTH)-1,calendar.get(Calendar.YEAR));
        CountAPIManager.callGetPotholeCountByMonth(
                dayReq,
                new CountAPICallBack() {
                    @Override
                    public void onSuccess(Response<CountResponse> response) {
                        setBarEntriesOne(true,response.body());
                        setBarEntriesTwo(true,response.body());
                        setBarChart();
                    }

                    @Override
                    public void onError(CountResponse errorResponse) {
                        setBarEntriesOne(false,errorResponse);
                        setBarEntriesTwo(false,errorResponse);
                        setBarChart();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API Error", "Failure: " + t.getMessage());
                        setBarEntriesOne(false,null);
                        setBarEntriesTwo(false,null);
                        setBarChart();

                    }
                }
        );
    }
    public void callAPIGetPotholeBySeverity(){
        SeverityAPIManager.callGetPotholeBySeverity(new SeverityAPICallBack() {
            @Override
            public void onSuccess(Response<SeverityResponse> response) {
                largeCount=response.body().getData().get(0).getLarge();
                mediumCount=response.body().getData().get(0).getMedium();
                smallCount=response.body().getData().get(0).getSmall();
                totalCount=response.body().getData().get(0).getTotal();
                setPiechartData();

            }

            @Override
            public void onError(SeverityResponse errorResponse) {
                largeCount=2;
                mediumCount=2;
                smallCount=2;
                totalCount=6;
                setPiechartData();
            }

            @Override
            public void onFailure(Throwable t) {
                largeCount=2;
                mediumCount=2;
                smallCount=2;
                totalCount=6;
                setPiechartData();
                Log.e("API Error", "Failure: " + t.getMessage());
                throw new RuntimeException(t);

            }
        });
    }
    private void setClickEvent() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.idPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogManager.showDialogPieChartDetail(context,
                        piechartLargeData, piechartMediumData, piechartSmallData);
            }
        });
    }
    
    private void setBarChart() {
        barDataSet1 = new BarDataSet(barEntriesOne, "Pothole");
        barDataSet1.setColor(getApplicationContext().getResources().getColor(R.color.large));
        barDataSet2 = new BarDataSet(barEntriesTwo, "Fixed Pothole");
        barDataSet2.setColor(getApplicationContext().getResources().getColor(R.color.medium));

        // Adding bar data sets to the bar data
        BarData data = new BarData(barDataSet1, barDataSet2);

        // Setting the data to the bar chart
        binding.idBarChart.setData(data);

        // Removing the description label of the bar chart
        binding.idBarChart.getDescription().setEnabled(false);

        // Getting the x-axis of the bar chart
        XAxis xAxis = binding.idBarChart.getXAxis();

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
        binding.idBarChart.setDragEnabled(true);

        // Setting the visible range for the bar chart
        binding.idBarChart.setVisibleXRangeMaximum(3);

        // Adding bar space to the chart
        float barSpace = 0.1f;

        // Adding group spacing to the chart
        float groupSpace = 0.5f;

        // Setting the width of the bars
        data.setBarWidth(0.15f);

        // Setting the minimum axis value for the chart
        binding.idBarChart.getXAxis().setAxisMinimum(0);

        // Animating the chart
        binding.idBarChart.animate();

        // Grouping bars and adding spacing to them
        binding.idBarChart.groupBars(0, groupSpace, barSpace);

        // Invalidating the bar chart
        binding.idBarChart.invalidate();
    }



    // ArrayList for the first set of bar entries
    private void setBarEntriesOne(boolean isSuccess, CountResponse countResponse) {
        barEntriesOne = new ArrayList<>();
        if(!isSuccess){
            // Adding entries to the ArrayList for the first set
            barEntriesOne.add(new BarEntry(1f, 0));
            barEntriesOne.add(new BarEntry(2f, 0));
            barEntriesOne.add(new BarEntry(3f, 0));
            barEntriesOne.add(new BarEntry(4f, 0));
            barEntriesOne.add(new BarEntry(5f, 0));
            barEntriesOne.add(new BarEntry(6f, 0));
            return;
        }
        List<PotholeCountByMonth> data=countResponse.getData();
        barEntriesOne.add(new BarEntry(1f, data.get(0).getJan().getPothole()));
        barEntriesOne.add(new BarEntry(2f, data.get(0).getFeb().getPothole()));
        barEntriesOne.add(new BarEntry(3f, data.get(0).getMar().getPothole()));
        barEntriesOne.add(new BarEntry(4f, data.get(0).getApr().getPothole()));
        barEntriesOne.add(new BarEntry(5f, data.get(0).getMay().getPothole()));
        barEntriesOne.add(new BarEntry(6f, data.get(0).getJun().getPothole()));
        barEntriesOne.add(new BarEntry(7f, data.get(0).getJul().getPothole()));
        barEntriesOne.add(new BarEntry(8f, data.get(0).getAug().getPothole()));
        barEntriesOne.add(new BarEntry(9f, data.get(0).getSep().getPothole()));
        barEntriesOne.add(new BarEntry(10f, data.get(0).getOct().getPothole()));
        barEntriesOne.add(new BarEntry(11f, data.get(0).getNov().getPothole()));
        barEntriesOne.add(new BarEntry(12f, data.get(0).getDec().getPothole()));


    }

    // ArrayList for the second set of bar entries
    private void setBarEntriesTwo(boolean isSuccess, CountResponse countResponse) {
        barEntriesTwo = new ArrayList<>();
        if(!isSuccess){
            // Adding entries to the ArrayList for the first set
            barEntriesTwo.add(new BarEntry(1f, 0));
            barEntriesTwo.add(new BarEntry(2f, 0));
            barEntriesTwo.add(new BarEntry(3f, 0));
            barEntriesTwo.add(new BarEntry(4f, 0));
            barEntriesTwo.add(new BarEntry(5f, 0));
            barEntriesTwo.add(new BarEntry(6f, 0));
            return;
        }
        List<PotholeCountByMonth> data=countResponse.getData();
        barEntriesTwo.add(new BarEntry(1f, data.get(0).getJan().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(2f, data.get(0).getFeb().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(3f, data.get(0).getMar().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(4f, data.get(0).getApr().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(5f, data.get(0).getMay().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(6f, data.get(0).getJun().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(7f, data.get(0).getJul().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(8f, data.get(0).getAug().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(9f, data.get(0).getSep().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(10f, data.get(0).getOct().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(11f, data.get(0).getNov().getFixed_pothole()));
        barEntriesTwo.add(new BarEntry(12f, data.get(0).getDec().getFixed_pothole()));
    }

    // PIE CHART
    private void setPiechartData(){
        binding.tvLarge.setText(R.string.str_large);
        binding.tvMedium.setText(R.string.str_medium);
        binding.tvSmall.setText(R.string.str_small);
        piechartSmallData= Math.round(((smallCount*100f)/totalCount) * 100) / 100.0f;
        piechartMediumData= Math.round(((mediumCount*100f)/totalCount) * 100) / 100.0f;
        piechartLargeData= Math.round(((largeCount*100f)/totalCount) * 100) / 100.0f;
        binding.tvDayMonthPieChart.setText(Calendar.getInstance().get(Calendar.YEAR)+
                "-"+(Calendar.getInstance().get(Calendar.YEAR)+1));

        binding.idPieChart.addPieSlice(new PieModel(getString(R.string.str_large), piechartLargeData, Color.parseColor("#793FDF")));
        binding.idPieChart.addPieSlice(new PieModel(getString(R.string.str_medium), piechartMediumData, Color.parseColor("#7091F5")));
        binding.idPieChart.addPieSlice(new PieModel(getString(R.string.str_small), piechartSmallData, Color.parseColor("#97FFF4")));;
        binding.idPieChart.startAnimation();
        final int[] i = {0};
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.idPieChart.setCurrentItem(i[0]);
                i[0] =(i[0] +1)%3;
                handler.postDelayed(this,3000);
            }
        },3000);
        binding.idPieChart.invalidate();
    }




}