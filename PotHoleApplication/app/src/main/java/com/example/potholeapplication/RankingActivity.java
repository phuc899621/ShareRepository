package com.example.potholeapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.class_pothole.RankingAdapter;
import com.example.potholeapplication.class_pothole.manager.APIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.NetworkManager;
import com.example.potholeapplication.class_pothole.other.Ranking;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.example.potholeapplication.databinding.ActivityRankingBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class RankingActivity extends AppCompatActivity {
    ActivityRankingBinding binding;
    NetworkManager networkManager;
    Context context;
    List<Ranking> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRankingBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        context=this;
        networkManager=new NetworkManager(this);
        callAPIRanking();

    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkReceiver, new IntentFilter("com.example.NETWORK"));
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkReceiver);
    }
    public void callAPIRanking(){
        if(!networkManager.isNetworkAvailable()){
            DialogManager.showDialogWarningThenFinish(this);
            return;
        }
        APIManager.callGetRanking(new APICallBack<APIResponse<Ranking>>() {

            @Override
            public void onSuccess(Response<APIResponse<Ranking>> response) {
                data=response.body().getData();
                Log.d("data",data.toString());
                setDisplay();
            }

            @Override
            public void onError(APIResponse<Ranking> errorResponse) {
                Log.d("error",errorResponse.getMessage());

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("error",t.toString());
            }
        });
    }
    public void setDisplay(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(
                this,LinearLayoutManager.VERTICAL,false
        );
        RankingAdapter ranking = new RankingAdapter(data,this);
        binding.recycleview.setLayoutManager(linearLayoutManager);
        binding.recycleview.setAdapter(ranking);

        List<Ranking> data=ranking.getData();
        binding.imaUser.setImageBitmap(LocalDataManager.getImageBitmap(context));
        binding.tvRanking.setText(data.get(0).getRanking()+"");
        binding.tvName.setText(data.get(0).getName());
        binding.tvPoints.setText(data.get(0).getTotalReport()*10+"");
        Toast.makeText(context, ranking.getStringBuilder(), Toast.LENGTH_LONG).show();


    }
    public BroadcastReceiver networkReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("com.example.NETWORK".equals(intent.getAction())){
                boolean isConnected=intent.getBooleanExtra("connected",false);
                if(!isConnected){
                    DialogManager.showDialogWarningThenFinish(context);
                }
            }
        }
    };

}