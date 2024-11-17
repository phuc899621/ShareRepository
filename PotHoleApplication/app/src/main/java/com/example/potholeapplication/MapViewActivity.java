package com.example.potholeapplication;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.databinding.ActivityMapViewBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapViewActivity extends AppCompatActivity {
    ActivityMapViewBinding binding;
    List<GeoPoint> markersList=new ArrayList<>();
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;
    SettingsClient settingsClient;
    LocationResult locationResult;
    LocationSettingsRequest locationSettingsRequest;
    LocationRequest locationRequest;
    Location lastLocation;
    double d_long,d_lat;
    String address;

    Context context;
    int time;
    Marker userMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMapViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        time=0;
        setClickEvent();

        Configuration.getInstance().load(this, getApplicationContext().getSharedPreferences("osmdroid", MODE_PRIVATE));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding.mapView.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapView.setZoomLevel(15);
        binding.mapView.setMultiTouchControls(true);
        binding.mapView.getController().setZoom(15.0);
        binding.mapView.getController().setCenter(new GeoPoint(10.762622, 106.660172));

        checkLocationPermisson();
        userMarker = new Marker(binding.mapView);
        binding.mapView.getOverlays().add(userMarker);
        init();
    }
    public void checkLocationPermisson(){
        Log.d(TAG,"Inside");
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MapViewActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MapViewActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else {
                ActivityCompat.requestPermissions(MapViewActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(context,"Permission granted..",Toast.LENGTH_LONG).show();
                        init();
                    }
                }
                return;
        }
    }
    @SuppressLint("MissingPermission")
    public void startLocationUpdate(){
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    Log.d(TAG,"Location setting are oke");
                    fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
                }).addOnFailureListener(e->{
                    int statusCode=((ApiException) e).getStatusCode();
                    Log.d(TAG,"inside error-->"+statusCode);
                });
    }
    public void stopLocationUpdate(){
        fusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(task->{
                    Log.d(TAG, "Stop location updates");
                });
    }
    private void receiveLocation(LocationResult locationResult){
        lastLocation=locationResult.getLastLocation();
        Log.d(TAG, "Latitude: "+lastLocation.getLatitude());
        Log.d(TAG, "Longitude: "+lastLocation.getLongitude());
        Log.d(TAG, "Altitude: "+lastLocation.getAltitude());


        d_lat=lastLocation.getLatitude();
        d_long=lastLocation.getLongitude();
        // Cập nhật vị trí Marker
        GeoPoint userGeoPoint = new GeoPoint(d_lat, d_long);
        userMarker.setPosition(userGeoPoint); // Di chuyển Marker tới vị trí mới
        userMarker.setTitle("MARKER");
        binding.mapView.getController().setCenter(userGeoPoint);
        binding.mapView.invalidate();

        // Cập nhật trung tâm bản đồ và làm mới bản đồ
        binding.mapView.getController().setCenter(userGeoPoint);
        binding.mapView.invalidate();
        try{
            Geocoder geocoder=new Geocoder(context,Locale.getDefault());
            List<Address> addresses=geocoder.getFromLocation(d_lat,d_long,1);
            address=addresses.get(0).getAddressLine(0);
            Log.d(TAG,"  "+address);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void init(){
        fusedLocationClient=LocationServices.getFusedLocationProviderClient(context);
        settingsClient=LocationServices.getSettingsClient(context);
        locationCallback=new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                receiveLocation(locationResult);
            }
        };
        locationRequest= LocationRequest.create()
                .setInterval(1000).setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);
        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest=builder.build();
        startLocationUpdate();
    }
    public void setClickEvent(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdate();
    }
}