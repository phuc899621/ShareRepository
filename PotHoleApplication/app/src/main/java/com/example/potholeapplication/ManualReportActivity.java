package com.example.potholeapplication;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.class_pothole.manager.APIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.other.LocationClass;
import com.example.potholeapplication.class_pothole.other.Pothole;
import com.example.potholeapplication.class_pothole.request.ReportReq;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.color.utilities.Contrast;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class ManualReportActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_PERMISSION_REQUEST = 2;

    private boolean isImageReady=false;

    private EditText descriptionEditText;
    private Spinner issueTypeSpinner;
    private Button addPhotoButton, getLocationButton, submitButton;
    Bitmap imageBitmap;
    Context context;
    private String photoPath;
    private Location currentLocation;
    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_report);

        // Initialize views
        descriptionEditText = findViewById(R.id.etDescription);
        issueTypeSpinner = findViewById(R.id.spinnerPotholeType);
        addPhotoButton = findViewById(R.id.btnAddPhoto);
        getLocationButton = findViewById(R.id.btnGetLocation);
        submitButton = findViewById(R.id.btnSubmit);
        context=this;
        // Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.pothole_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issueTypeSpinner.setAdapter(adapter);

        // Initialize location provider
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Add photo button listener
        addPhotoButton.setOnClickListener(v -> openCamera());

        // Get location button listener
        getLocationButton.setOnClickListener(v -> fetchCurrentLocation());

        // Submit button listener
        submitButton.setOnClickListener(v -> callAPISaveReport());
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        locationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    currentLocation = task.getResult();
                    Toast.makeText(ManualReportActivity.this, "Location fetched successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManualReportActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private byte[] getImageBytesFromImageView() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void callAPISaveReport() {
        String description = descriptionEditText.getText().toString();
        String severity = issueTypeSpinner.getSelectedItem().toString();
        byte[] imageBytes = getImageBytesFromImageView();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestBody);
        if(imageBitmap==null||!isImageReady){
            Toast.makeText(this, "Please provide image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please provide a description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentLocation == null) {
            Toast.makeText(this, "Please fetch your location", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(currentLocation.getLongitude());
        coordinates.add(currentLocation.getLatitude());
        ReportReq reportReq=new ReportReq(LocalDataManager.getEmail(this)
                , description, severity, new LocationClass(coordinates));
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(reportReq);

        RequestBody reqBody = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
        APIManager.callSaveReport(reqBody,
                imagePart, new APICallBack<APIResponse<Pothole>>() {
                    @Override
                    public void onSuccess(Response<APIResponse<Pothole>> response) {
                        DialogManager.showDialogOkeThenFinish(context,getString(R.string.str_report_submitted_successfully));
                    }

                    @Override
                    public void onError(APIResponse<Pothole> errorResponse) {
                        DialogManager.showDialogErrorString(context, getString(R.string.str_server_error));
                        Log.d("ErrorAPIReport",errorResponse.getMessage());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(ManualReportActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                // Submit data (this is just a placeholder, replace with your backend submission logic)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // You can handle saving or displaying the photo here
            imageBitmap = (Bitmap) data.getExtras().get("data");
            isImageReady=true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
}