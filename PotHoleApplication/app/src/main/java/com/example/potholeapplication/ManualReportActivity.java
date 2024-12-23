package com.example.potholeapplication;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ManualReportActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_PERMISSION_REQUEST = 2;

    private EditText descriptionEditText;
    private Spinner issueTypeSpinner;
    private Button addPhotoButton, getLocationButton, submitButton;
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
        submitButton.setOnClickListener(v -> submitReport());
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

    private void submitReport() {
        String description = descriptionEditText.getText().toString();
        String issueType = issueTypeSpinner.getSelectedItem().toString();

        if (description.isEmpty()) {
            Toast.makeText(this, "Please provide a description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentLocation == null) {
            Toast.makeText(this, "Please fetch your location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Submit data (this is just a placeholder, replace with your backend submission logic)
        Toast.makeText(this, "Report submitted:\n" +
                "Description: " + description + "\n" +
                "Issue Type: " + issueType + "\n" +
                "Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // You can handle saving or displaying the photo here
            Toast.makeText(this, "Photo added successfully", Toast.LENGTH_SHORT).show();
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
}