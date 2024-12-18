package com.example.potholeapplication.class_pothole.response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potholeapplication.R;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        // Bind data to the view holder if needed
    }

    @Override
    public int getItemCount() {
        return 20; // Number of items to display
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        FeedViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}