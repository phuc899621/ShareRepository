package com.example.potholeapplication;
import static com.mapbox.maps.plugin.annotation.AnnotationsUtils.getAnnotations;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.databinding.ActivityMapViewBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.bindgen.Expected;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.search.SearchEngine;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteResult;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class MapViewActivity extends AppCompatActivity {
    ActivityMapViewBinding binding;
    Point currentPoint;//luu tru vi tri hien tai
    boolean firstTime=true;//lan dau mo ung dung map
    PermissionsManager permissionsManager;//xin quyen
    //Bien xu ly lay danh sach dia chi
    PlaceAutocomplete autocomplete;
    boolean ignoreNextQueryUpdate=false;
    PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    //Marker;
    PointAnnotationManager pointAnnotationManager;
    Bitmap iconImage;

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
        init();
        //xin quyen
        permissionChecking();
        Log.d("hi",binding.searchView+"");

    }
    public void init(){
        //cai dat icon cho Marker
        iconImage = BitmapFactory.decodeResource(getResources(),R.drawable.ic_puck);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void settingSearch(){
        //cai dat
        autocomplete= PlaceAutocomplete.create();
        binding.searchResultView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
        placeAutocompleteUiAdapter=new PlaceAutocompleteUiAdapter(
                binding.searchResultView,autocomplete
        );
        //Lay toa do khi nguoi dung chon dia chi
        placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
            @Override
            public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {

            }

            @Override
            public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                autocomplete.select(placeAutocompleteSuggestion, new Continuation<Expected<Exception, PlaceAutocompleteResult>>() {
                    @NonNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NonNull Object o) {
                        //lay toa do va dia chi
                        Expected<Exception, PlaceAutocompleteResult> result=
                                (Expected<Exception, PlaceAutocompleteResult>) o;
                        result.onValue(new Expected.Action<PlaceAutocompleteResult>() {
                            @Override
                            public void run(@NonNull PlaceAutocompleteResult input) {
                                ignoreNextQueryUpdate=true;

                                //gan marker ngay vi tri do
                                AnnotationPlugin annotationPlugin= getAnnotations(binding.mapView);
                                pointAnnotationManager= (PointAnnotationManager) annotationPlugin.createAnnotationManager(
                                        AnnotationType.PointAnnotation,null
                                );
                                binding.searchView.setText(placeAutocompleteSuggestion.getFormattedAddress());
                                binding.searchResultView.setVisibility(View.GONE);
                                pointAnnotationManager.deleteAll();
                                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                        .withTextAnchor(TextAnchor.CENTER)
                                        .withIconImage(iconImage)
                                        .withPoint(input.getCoordinate());
                                pointAnnotationManager.create(pointAnnotationOptions);
                                //update camera lai vi tri do
                                updateCamera(input.getCoordinate(), binding.mapView.getMapboxMap().getCameraState().getBearing());
                            }
                        }).onError(new Expected.Action<Exception>() {
                            @Override
                            public void run(@NonNull Exception input) {
                                Log.e("Error",input.toString()+"");
                            }
                        });
                    }
                });
            }

            @Override
            public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {

            }

            @Override
            public void onError(@NonNull Exception e) {

            }
        });
        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(ignoreNextQueryUpdate) ignoreNextQueryUpdate=false;
                else {
                    Log.d("name",s.toString());
                    placeAutocompleteUiAdapter.search(s.toString(),
                            new Continuation<Unit>() {
                                @NonNull
                                @Override
                                public CoroutineContext getContext() {
                                    return EmptyCoroutineContext.INSTANCE;
                                }

                                @Override
                                public void resumeWith(@NonNull Object o) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.searchResultView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                    );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void permissionChecking(){
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            //Nếu đã cấp quyền thì gọi đến hàm xử lý
            enableLocationComponent();
            settingSearch();
        } else {
            // Yêu cầu quyền nếu chưa được cấp
            permissionsManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                    // Giải thích lý do cần quyền (tùy chọn)
                    Toast.makeText(MapViewActivity.this, "Ứng dụng cần quyền vị trí để hoạt động", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        // Người dùng đãđược cấp quyền, gọi ham xử lý
                        enableLocationComponent();
                        settingSearch();
                    } else {
                        // Người dùng bị từ chối quyền
                        Toast.makeText(MapViewActivity.this, "Quyền vị trí bị từ chối", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissionsManager!=null){
            permissionsManager.onRequestPermissionsResult(
                    requestCode,permissions,grantResults
            );
        }
    }

    private void enableLocationComponent() {
        // Kích hoạt logic để hiển thị vị trí trên bản đồ
        Toast.makeText(this, "Kích hoạt hiển thị vị trí người dùng", Toast.LENGTH_SHORT).show();

        //cho phep hien thi huong di chuyen
        getLocationComponent(binding.mapView).setPulsingEnabled(true);
        //cho phep hien thi vi tri nguoi dung
        getLocationComponent(binding.mapView).setEnabled(true);

        getLocationComponent(binding.mapView).
                addOnIndicatorBearingChangedListener(indicatorBearingChangedListener);
        getLocationComponent(binding.mapView).
                addOnIndicatorPositionChangedListener(indicatorPositionChangedListener);
        getGestures(binding.mapView).addOnMoveListener(onMoveListener);
        binding.fabUserTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMapboxListener();
                getLocationComponent(binding.mapView).
                        addOnIndicatorBearingChangedListener(indicatorBearingChangedListener);
                getLocationComponent(binding.mapView).
                        addOnIndicatorPositionChangedListener(indicatorPositionChangedListener);
                getGestures(binding.mapView).addOnMoveListener(onMoveListener);
            }
        });
    }
    //Nếu huoướng người dùng thayđổi thì thay đổi camera theo
    OnIndicatorBearingChangedListener indicatorBearingChangedListener=new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            binding.mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
        }
    };


    //nêu vị tr thay đổi
    OnIndicatorPositionChangedListener indicatorPositionChangedListener=new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            if(firstTime){
                CameraOptions cameraOptions = new CameraOptions.Builder()
                        .center(point) // Đặt vị trí trung tâm là vị trí người dùng
                        .zoom(17.0) // Đặt mức zoom của bản đồ hện tại
                        .build();
                binding.mapView.getMapboxMap().setCamera(cameraOptions);
                firstTime=false;
            }

            CameraOptions cameraOptions = new CameraOptions.Builder()
                    .center(point) // Đặt vị trí trung tâm là vị trí người dùng
                    .zoom(binding.mapView.getMapboxMap().getCameraState().getZoom()) // Đặt mức zoom của bản đồ hện tại
                    .build();
            CameraAnimationsUtils.flyTo(binding.mapView.getMapboxMap(),
                    cameraOptions,
                    new MapAnimationOptions.Builder().duration(1000).build());

            //giu nguoi dung o vi tri hien tai khi zoom
            getGestures(binding.mapView).setFocalPoint(binding.mapView.getMapboxMap().pixelForCoordinate(point));
            currentPoint=point;//luu vi tri hien tai
        }
    };
    //lang nghe viec nguoi dung move trong man hinh
    OnMoveListener onMoveListener=new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            removeMapboxListener();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };
    //ham xoa cac su kien lang nghe tu mapbox
    public void removeMapboxListener(){
        getLocationComponent(binding.mapView)
                .removeOnIndicatorBearingChangedListener(indicatorBearingChangedListener);
        getLocationComponent(binding.mapView)
                .removeOnIndicatorPositionChangedListener(indicatorPositionChangedListener);
        getGestures(binding.mapView).removeOnMoveListener(onMoveListener);
    }
    public void updateCamera(Point point,double bearing){
        removeMapboxListener();
        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(point) // Đặt vị trí trung tâm là vị trí người dùng
                .zoom(binding.mapView.getMapboxMap().getCameraState().getZoom()) // Đặt mức zoom của bản đồ hện tại
                .bearing(bearing)
                .build();
        //cai animation
        CameraAnimationsUtils.flyTo(binding.mapView.getMapboxMap(),
                cameraOptions,
                new MapAnimationOptions.Builder().duration(2000).build());
        binding.mapView.getMapboxMap().setCamera(cameraOptions);
    }


}