package com.example.potholeapplication.pothole_service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.potholeapplication.Retrofit2.APIInterface;
import com.example.potholeapplication.Retrofit2.SubinfoAPICallBack;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.NetworkManager;
import com.example.potholeapplication.class_pothole.manager.SubinfoAPIManager;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.SaveDistanceReq;
import com.example.potholeapplication.class_pothole.response.SubinfoResponse;

import retrofit2.Response;

public class SensorService extends Service {

    Vibrator vibrator;//rung ung dung

   private SensorManager sensorManager;
    private Sensor accelerometer;
    private LocationManager locationManager;
    private AccelerometerListener accelerometerListener;
    private GPSListener gpsListener;
    private Handler handler;
    private Location lastLocation;
    private float addingDistance = 0;
    Context context;
    NetworkManager networkManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;

    }
    public void saveTotalDistanceToAPI(float data){
        if(!networkManager.isNetworkAvailable()){
            return;
        }
        SubinfoAPIManager.callSaveDistances(
                new SaveDistanceReq(LocalDataManager.getEmail(context), data),
                new SubinfoAPICallBack() {
                    @Override
                    public void onSuccess(Response<SubinfoResponse> response) {

                    }

                    @Override
                    public void onError(SubinfoResponse errorResponse) {

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                }
        );
    }
    @SuppressLint({"ForegroundServiceType", "ServiceCast"})
    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo các đối tượng SensorManager và LocationManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        networkManager=new NetworkManager(this);
        LocalDataManager.saveTotalDistances(this,0);
        networkManager.startMonitoring(new NetworkManager.NetworkStatusListener() {
            @Override
            public void onConnected() {
                float data=LocalDataManager.getTotalDistances(context)+
                        (Math.round(((addingDistance)*100)/100f)/1000f);
                Log.d("DISTANCE_TRACKING2", "Distance: " + data + " kilometers"+addingDistance);
                addingDistance=0;
                LocalDataManager.saveTotalDistances(context,
                        data);
                saveTotalDistanceToAPI(data);
                Log.d("WIFI","ISCONNECTED");
            }

            @Override
            public void onDisconnected() {
                float data=LocalDataManager.getTotalDistances(context)+
                        (Math.round(((addingDistance)*100)/100f)/1000f);
                Log.d("DISTANCE_TRACKING2", "Distance: " + data + " kilometers"+addingDistance);
                addingDistance=0;
                LocalDataManager.saveTotalDistances(context,data);
                Log.d("WIFI","NO WIFI");

            }
        });
        // Khởi tạo listener
        accelerometerListener = new AccelerometerListener();
        gpsListener = new GPSListener(this);
        context=this;
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        // Đăng ký listener cho cảm biến gia tốc
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Đăng ký listener cho GPS
        requestLocationPermission();

        // Tạo một Handler để xử lý các hành động định kỳ
        handler = new Handler();
        startPotholeDetection();
        //startDistanceTracking();

    }
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Nếu quyền chưa được cấp, yêu cầu cấp quyền
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1); // 1 là mã yêu cầu quyền
        } else {
            // Nếu quyền đã được cấp, bắt đầu yêu cầu vị trí
            startLocationUpdates();
        }
    }
    private void startLocationUpdates() {
        try {

            // Đăng ký nhận updates từ GPS
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, // minimum time interval between updates (ms)
                    1,    // minimum distance between updates (meters)
                    gpsListener
            );

            // Đăng ký nhận updates từ Network provider
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    1,
                    gpsListener
            );
        } catch (SecurityException e) {
            Log.e("Location", "Error requesting location updates: " + e.getMessage());
        }
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
        float data=LocalDataManager.getTotalDistances(context)+
                (Math.round(((addingDistance)*100)/100f)/1000f);
        addingDistance=0;
        LocalDataManager.saveTotalDistances(context,
                data);
        saveTotalDistanceToAPI(data);
        networkManager.stopMonitoring();

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
    private void updateDistance() {
        Location currentLocation = gpsListener.lastLocation;
        Log.d("DISTANCE_TRACKING", "Distance: " + currentLocation + " meters");
            if (currentLocation != null && lastLocation != null && lastLocation.distanceTo(currentLocation)>1) {
                float distance = lastLocation.distanceTo(currentLocation);
                addingDistance += distance;
                float data=LocalDataManager.getTotalDistances(context)+
                        (Math.round(((addingDistance)*100)/100f)/1000f);
                Log.d("DISTANCE_TRACKING", "Distance: " + distance + " meters"+addingDistance);
                LocalDataManager.saveTotalDistances(this, data);
                addingDistance=0;
            }
            lastLocation = currentLocation;
    }


}