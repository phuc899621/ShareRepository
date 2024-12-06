package com.example.potholeapplication.pothole_service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class SensorService extends Service implements SensorEventListener {
    private final float LARGE_THRESHOLE=35.0f;
    private final float MEDIUM_THRESHOLE=25.0f;
    private final float SMALL_THRESHOLE=15.0f;
    private long lastPotholeTime = 0; // Thời gian lần cuối phát hiện ổ gà
    private static final long DETECTION_COOLDOWN = 2000; // 2 giây
    private static final String TAG = "PotholeService";
    private static final String CHANNEL_ID = "PotholeDetectionChannel";
    Vibrator vibrator;//rung ung dung
    private boolean isStart;
    float DeltaZ;
    private SensorManager sensorManager;
    private float[] accelerationData=new float[3];

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;

    }

    @SuppressLint({"ForegroundServiceType", "ServiceCast"})
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getNotification());
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        Log.d("Services","Service started");

    }

    public SensorService() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        sensorManager.unregisterListener(this);
        Log.d("Services","Service stopped");
    }
    /*    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerationData = event.values.clone();
            float x=accelerationData[0];
            float y=accelerationData[1];
            float z=accelerationData[2];
            /*Log.d(TAG, "Accelerometer: Z=" +
                    accelerationData[2]);*/
            DeltaZ= (float) Math.sqrt(x*x+y*y+z*z);
            DeltaZ=Math.abs(DeltaZ);
            // Lấy thời gian hiện tại
            long currentTime = System.currentTimeMillis();

            // Phát hiện ổ gà
            if (DeltaZ > SMALL_THRESHOLE &&(currentTime - lastPotholeTime) > DETECTION_COOLDOWN) {
                lastPotholeTime = currentTime; // Cập nhật thời gian phát hiện
                vibratePhone();
                Log.d("POTHOLE_DETECTION", "Pothole detected! "
                        +potholeSeverity(DeltaZ));
            }
        }
    }
    private String potholeSeverity(float data){
        if(data > LARGE_THRESHOLE) return "large";
        if(data > MEDIUM_THRESHOLE) return "medium";
        return "small";
    }
    private Notification getNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pothole Detection")
                .setContentText("Monitoring road conditions...")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pothole Detection Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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

}