package com.example.potholeapplication.pothole_service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.potholeapplication.class_pothole.AccelerometerListener;
import com.example.potholeapplication.class_pothole.GPSListener;

public class SensorService extends Service {

    Vibrator vibrator;//rung ung dung

   private SensorManager sensorManager;
    private Sensor accelerometer;
    private LocationManager locationManager;
    private AccelerometerListener accelerometerListener;
    private GPSListener gpsListener;
    private Handler handler;
    private Location lastLocation;
    private double totalDistance = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;

    }

    @SuppressLint({"ForegroundServiceType", "ServiceCast"})
    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo các đối tượng SensorManager và LocationManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Khởi tạo listener
        accelerometerListener = new AccelerometerListener();
        gpsListener = new GPSListener();

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // Đăng ký listener cho cảm biến gia tốc
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Đăng ký listener cho GPS
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        500, 0, gpsListener);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // Tạo một Handler để xử lý các hành động định kỳ
        handler = new Handler();
        startPotholeDetection();
        startDistanceTracking();

    }
    private void startPotholeDetection() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Kiểm tra dữ liệu cảm biến và GPS
                checkForPotholes();
                handler.postDelayed(this, 1000);  // Quét lại sau mỗi giây
            }
        };
        handler.post(runnable);
    }
    private void checkForPotholes() {
        double lastLinear = accelerometerListener.lastLinear;
        if (lastLinear > 10) {
            double latitude = gpsListener.latitude;
            double longitude = gpsListener.longitude;
            Intent intent = new Intent("com.example.SHOW_DIALOG");
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("severity",getPotholeSeverity(lastLinear));
            sendBroadcast(intent);
            vibratePhone();
            System.out.println("Pothole detected"+lastLinear+ " at: " + latitude + ", " + longitude);
        }
    }
    private String getPotholeSeverity(double lastLinear){
        if(lastLinear>30) return "large";
        if(lastLinear>20) return "medium";
        return "small";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(accelerometerListener);
        locationManager.removeUpdates(gpsListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("ServiceCast")
    private void vibratePhone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Đối với Android 12 trở lên
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } else {

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(500); // Rung trong 500ms
            }
        }
    }
    private void startDistanceTracking() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDistance();
                handler.postDelayed(this, 5000); // Cập nhật mỗi 20s
            }
        }, 5000);
    }
    private boolean isConnectedToWifi() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() &&
                networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }
    private void updateDistance() {
            Location currentLocation = gpsListener.lastLocation;
        Log.d("DISTANCE_TRACKING", "Distance: " + currentLocation + " meters");
            if (currentLocation != null && lastLocation != null) {
                float distance = lastLocation.distanceTo(currentLocation);
                totalDistance += distance;
                Log.d("DISTANCE_TRACKING", "Distance: " + distance + " meters");

                // Lưu dữ liệu vào cơ sở dữ liệu hoặc file tại đây
                if (isConnectedToWifi()) {
                    saveDistanceToStorage(totalDistance);
                }
                else Log.e("WIFI", "No wifi");
            }
            lastLocation = currentLocation;
    }
    private void saveDistanceToStorage(double distance) {
        Log.d("DATA_STORAGE", "Distance saved: " + distance + " meters");
    }


}