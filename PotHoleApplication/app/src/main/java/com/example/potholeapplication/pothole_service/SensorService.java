package com.example.potholeapplication.pothole_service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.class_pothole.manager.APIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.NetworkManager;
import com.example.potholeapplication.class_pothole.other.Pothole;
import com.example.potholeapplication.class_pothole.other.Subinfo;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.request.SaveDistanceReq;
import com.example.potholeapplication.class_pothole.response.APIResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class SensorService extends Service {

    Vibrator vibrator;//rung ung dung

   private SensorManager sensorManager;
    private Sensor accelerometer;
    private LocationManager locationManager;
    private AccelerometerListener accelerometerListener;
    private GPSListener gpsListener;
    private Handler handler;
    private Handler saveDistanceHandler;

    private Location lastLocation;
    private float addingDistance = 0;
    Context context;
    NetworkManager networkManager;
    List<Pothole> potholes;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;

    }
    public void saveTotalDistanceToAPI(float data){
        if(!networkManager.isNetworkAvailable()){
            return;
        }
        APIManager.callSaveDistances(
                new SaveDistanceReq(LocalDataManager.getEmail(context), data),
                new APICallBack<APIResponse<Subinfo>>() {
                    @Override
                    public void onSuccess(Response<APIResponse<Subinfo>> response) {

                    }

                    @Override
                    public void onError(APIResponse<Subinfo> errorResponse) {

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                }
        );
    }
    public void callGetListPothole(){
        APIManager.callGetPothole(new APICallBack<APIResponse<Pothole>>() {
            @Override
            public void onSuccess(Response<APIResponse<Pothole>> response) {
                if (response.body() != null) {
                    potholes=response.body().getData();
                }else potholes=new ArrayList<>();
            }

            @Override
            public void onError(APIResponse<Pothole> errorResponse) {
                Log.d("GetPothole","error");
                potholes=new ArrayList<>();


            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("GetPothole","error");
                potholes=new ArrayList<>();
            }
        });
    }
    @SuppressLint({"ForegroundServiceType", "ServiceCast"})
    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo các đối tượng SensorManager và LocationManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        handler = new Handler();
        networkManager=new NetworkManager(this);
        LocalDataManager.saveTotalDistances(this,0);
        callGetListPothole();
        setupNetworkReceiver();
        /*networkManager.startMonitoring(new NetworkManager.NetworkStatusListener() {
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
        });*/
        // Khởi tạo listener
        accelerometerListener = new AccelerometerListener();
        gpsListener = new GPSListener(this);
        context=this;
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        // Đăng ký listener cho cảm biến gia tốc
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Đăng ký listener cho GPS
        requestLocationPermission();

    }

    //broadcast receiver network
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void setupNetworkReceiver(){
        IntentFilter filter = new IntentFilter("com.example.NETWORK");
        registerReceiver(networkReceiver, filter);
    }
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.NETWORK".equals(intent.getAction())) {
                if(intent.getBooleanExtra("connected",false)) {
                    saveTotalDistanceToAPI(LocalDataManager.getTotalDistances(context));
                }
            }
        }
    };



    //permission
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Nếu quyền chưa được cấp, yêu cầu cấp quyền
            Log.e("Permission", "Error No permission");
            Intent intent = new Intent("REQUEST_LOCATION_PERMISSION");
            sendBroadcast(intent);

        } else {
            // Nếu quyền đã được cấp, bắt đầu yêu cầu vị trí
            startLocationUpdates();
            startPotholeDetection();
            startPotholeWarning();
            callAPIGetPotholeDistance();
        }
    }
    public void callAPIGetPotholeDistance(){
        APIManager.callGetSubinfo(new EmailReq(LocalDataManager.getEmail(context)), new APICallBack<APIResponse<Subinfo>>() {
            @Override
            public void onSuccess(Response<APIResponse<Subinfo>> response) {
                addingDistance=response.body().getData().get(0).getTotalDistances();
                LocalDataManager.saveTotalDistances(context,addingDistance);
                startDistanceTracking();
                startHandlerSaveDistance();
            }

            @Override
            public void onError(APIResponse<Subinfo> errorResponse) {
                addingDistance=0;
                LocalDataManager.saveTotalDistances(context,addingDistance);
            }

            @Override
            public void onFailure(Throwable t) {
                addingDistance=0;
                LocalDataManager.saveTotalDistances(context,addingDistance);
            }
        });
    }


    //---location update
    private void startLocationUpdates() {
        try {

            // Đăng ký nhận updates từ GPS
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    500, // minimum time interval between updates (ms)
                    1,    // minimum distance between updates (meters)
                    gpsListener
            );

            // Đăng ký nhận updates từ Network provider
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    500,
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
                handler.postDelayed(this, 800);  // Quét lại sau mỗi giây
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
        if(lastLinear>20) return "large";
        if(lastLinear>15) return "medium";
        return "small";
    }
    private void startPotholeWarning() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Kiểm tra dữ liệu cảm biến và GPS
                if(potholes!=null) checkForNearByPothole();
                handler.postDelayed(this, 1000);  // Quét lại sau mỗi giây
            }
        };
        handler.post(runnable);
    }
    public void checkForNearByPothole(){
        for(int i=0;i<potholes.size();i++){
            Location potholeLocation = new Location("gps");
            potholeLocation.setLatitude(potholes.get(i).getLocationClass().getCoordinates().get(1));
            potholeLocation.setLongitude(potholes.get(i).getLocationClass().getCoordinates().get(0));
            if(gpsListener.lastLocation.distanceTo(potholeLocation)<10) {
                vibratePhone();
                return;
            }
        }

    }
    public void startHandlerSaveDistance(){
        Handler handler1=new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                saveTotalDistanceToAPI(LocalDataManager.getTotalDistances(context));
                handler1.postDelayed(this,20000);
            }
        },1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(accelerometerListener);
        locationManager.removeUpdates(gpsListener);
        saveTotalDistanceToAPI(LocalDataManager.getTotalDistances(context));
        networkManager.stopMonitoring();
        handler.removeCallbacksAndMessages(null);

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
            if (currentLocation != null && lastLocation != null && lastLocation.distanceTo(currentLocation)>20) {
                float distance = lastLocation.distanceTo(currentLocation);
                addingDistance += distance;
                LocalDataManager.saveTotalDistances(this, addingDistance);
                Log.d("DISTANCE_TRACKING", "Distance: " + distance + " meters");
            }
            lastLocation = currentLocation;
    }


}