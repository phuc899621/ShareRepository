package com.example.potholeapplication.class_pothole;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

public class GPSListener implements LocationListener {
    public double latitude =0;
    public double longitude =0;
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}
