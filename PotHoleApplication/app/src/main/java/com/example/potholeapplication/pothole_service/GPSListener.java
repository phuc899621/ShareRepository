package com.example.potholeapplication.pothole_service;

import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

import androidx.annotation.NonNull;

public class GPSListener implements LocationListener {
    public double latitude =0;
    public double longitude =0;
    public Location lastLocation;

    public GPSListener(SensorService sensorService) {
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("locationAAAAAAAA",location+"");
        lastLocation=location;
    }

}
