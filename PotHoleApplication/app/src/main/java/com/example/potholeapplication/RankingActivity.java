package com.example.potholeapplication;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.potholeapplication.class_pothole.RankingAdapter;
import com.example.potholeapplication.databinding.ActivityRankingBinding;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    ActivityRankingBinding binding;
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
        List <String> data = new ArrayList<>();
        data.add("nam");
        data.add("tuan");
        data.add("tuan");
        data.add("tuan");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(
                this,LinearLayoutManager.VERTICAL,false
        );
        RankingAdapter ranking = new RankingAdapter(data);
        binding.recycleview.setLayoutManager(linearLayoutManager);
        binding.recycleview.setAdapter(ranking);
    }

}