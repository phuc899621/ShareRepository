package com.example.potholeapplication.class_pothole;

import android.app.Application;
import android.content.Context;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potholeapplication.R;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.other.Ranking;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder>{
    List<Ranking> data;
    Context context;
    StringBuilder stringBuilder=new StringBuilder();
    List<String> usernames;
    String usernameCurrent;
    boolean isSetCurrent=false;

    public RankingAdapter(List<Ranking> data, Context context) {
        this.context=context;
        this.data = data;
        usernames=new ArrayList<>();
        usernameCurrent=LocalDataManager.getUsername(context);
        setRankingData();
    }
    public List<Ranking> getData() {
        return data;
    }

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public void setRankingData(){
        int ranking=1;
        for(int i=0;i<data.size();i++){
            if(i==0) {
                data.get(i).setRanking(ranking);
                continue;
            }
            if(data.get(i).getTotalReport()==data.get(i-1).getTotalReport()){
                data.get(i).setRanking(ranking);
                continue;
            }
            ranking++;
            data.get(i).setRanking(ranking);
        }


        Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(data.get(position).getName());
        holder.tvRanking.setText(String.valueOf(data.get(position).getRanking()));
        holder.tvPoints.setText(String.valueOf(data.get(position).getTotalReport() * 10));

        holder.cardView.setStrokeWidth(data.get(position).getWidth(usernameCurrent));
        holder.cardView.setStrokeColor(ContextCompat.getColor(context,
                data.get(position).getWidthColor(usernameCurrent)));
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,
                data.get(position).getColorID()));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvRanking,tvPoints;
        MaterialCardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRanking = itemView.findViewById(R.id.tvRanking);
            tvPoints=itemView.findViewById(R.id.tvPoints);
            cardView=itemView.findViewById(R.id.main);
        }
    }

}
