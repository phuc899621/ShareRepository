package com.example.potholeapplication;
import static com.mapbox.maps.plugin.annotation.AnnotationsUtils.getAnnotations;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.databinding.ActivityMapViewBinding;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.bindgen.Expected;
import com.mapbox.common.location.Location;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.trip.model.RouteProgress;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.core.trip.session.RouteProgressObserver;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi;
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineUpdateValue;
import com.mapbox.search.SearchOptions;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteResult;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.common.IsoCountryCode;
import com.mapbox.search.common.IsoLanguageCode;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;

public class MapViewActivity extends AppCompatActivity {
    ActivityMapViewBinding binding;
    Location currentLocation;
    Point currentPoint;//luu tru vi tri hien tai
    boolean firstTime=true;//lan dau mo ung dung map
    PermissionsManager permissionsManager;//xin quyen
    //Bien xu ly lay danh sach dia chi
    PlaceAutocomplete autocomplete;
    boolean ignoreNextQueryUpdate=false,isOnTracking=false,isListening=false;
    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxNavigation mapboxNavigation;
    PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    //Marker;
    PointAnnotationManager pointAnnotationManager;
    Bitmap iconImage;
    Context context;
    boolean isTracking=true;
    //Camera
    CameraOptions cameraForBearing,cameraForLocation,cameraForPosition;
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
        if (checkLocationPermissions()) {
            setupMapbox();
            //enableLocationComponent();
            settingSearch();
        } else {
            requestLocationPermission();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
    public void init(){
        context=this;
        //cai dat icon cho Marker
        iconImage = BitmapFactory.decodeResource(getResources(),R.drawable.ic_puck);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.fabLocationTracking.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(context,R.color.pink)));
        binding.fabUserTracking.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(context,R.color.pink)));
        binding.fabReport.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(context,R.color.pink)));
        binding.fabLocationTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOnTracking){
                    binding.SearchViewLayout.setVisibility(View.GONE);
                    binding.fromViewLayout.setVisibility(View.VISIBLE);
                    binding.toViewLayout.setVisibility(View.VISIBLE);
                    binding.btnCurrentLocation.setVisibility(View.VISIBLE);
                    binding.fabLocationTracking.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(context,R.color.green)));
                    binding.searchView.setText("");
                    isOnTracking=true;
                }else{
                    binding.SearchViewLayout.setVisibility(View.VISIBLE);
                    binding.fromViewLayout.setVisibility(View.GONE);
                    binding.toViewLayout.setVisibility(View.GONE);
                    binding.btnCurrentLocation.setVisibility(View.GONE);
                    binding.fabLocationTracking.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(context,R.color.pink)));
                    binding.fromView.setText("");
                    binding.toView.setText("");
                    isOnTracking=false;
                }
            }
        });
    }
    private void updateCamera(Location location) {
        if(firstTime){
            CameraOptions cameraOptions = new CameraOptions.Builder()
                    .center(Point.fromLngLat(location.getLongitude(), location.getLatitude()))
                    .zoom(17.0)
                    .build();
            binding.mapView.getMapboxMap().setCamera(cameraOptions);
            firstTime=false;
        }else{
            CameraOptions cameraOptions = new CameraOptions.Builder()
                    .center(Point.fromLngLat(location.getLongitude(), location.getLatitude()))
                    .zoom(binding.mapView.getMapboxMap().getCameraState().getZoom())
                    .padding(new EdgeInsets(500.0, 0.0, 0.0, 0.0))
                    .build();
            CameraAnimationsUtils.flyTo(binding.mapView.getMapboxMap(),
                    cameraOptions,
                    new MapAnimationOptions.Builder().duration(2000).build());
            binding.mapView.getMapboxMap().setCamera(cameraOptions);

    }
    }

    MapboxNavigation navigation;
    private void setupMapbox(){
        getGestures(binding.mapView).addOnMoveListener(onMoveListener);
        Log.d("LocationUpdate",
                "Current Position");
        //cai dat mapbox navigation app
        if (!MapboxNavigationApp.isSetup()) {
            MapboxNavigationApp.setup(new NavigationOptions.Builder(context).build());

        }
        //gan lifecycle cua mapbox
        MapboxNavigationApp.attach(this);
        MapboxNavigationApp.registerObserver(mapboxNavigationObserver);
        Log.d("LocationUpdate",
                "Current Position");
        navigation = MapboxNavigationApp.current();
        getLocationComponent(binding.mapView).setLocationProvider(navigationLocationProvider);
        getLocationComponent(binding.mapView).setEnabled(true);
        setupRecenterButton();
        //navigation

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapboxNavigationApp.detach(this);
        MapboxNavigationApp.unregisterObserver(mapboxNavigationObserver);
        MapboxNavigation navigation = MapboxNavigationApp.current();
        if (navigation != null) {
            navigation.stopTripSession();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void settingSearch(){
        //cai dat
        autocomplete= PlaceAutocomplete.create();

        binding.searchResultView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
        placeAutocompleteUiAdapter=new PlaceAutocompleteUiAdapter(
                binding.searchResultView,autocomplete
        );
        AnnotationPlugin annotationPlugin= getAnnotations(binding.mapView);
        pointAnnotationManager= (PointAnnotationManager) annotationPlugin.createAnnotationManager(
                AnnotationType.PointAnnotation,null
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

    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                },
                1
        );
    }
    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
                setupMapbox();
                //enableLocationComponent();
                settingSearch();
            } else {
                // Permissions denied
                Toast.makeText(this,
                        "Location permissions are required for navigation",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    LocationObserver myLocationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location rawLocation) {
            // Xử lý vị trí thô
            Log.d("Location", "Raw Location: " + rawLocation.toString());
        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            // Xử lý vị trí đã khớp với bản đồ
            Location enhancedLocation = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(
                    enhancedLocation,
                    locationMatcherResult.getKeyPoints(), new Function1<ValueAnimator, Unit>() {
                        @Override
                        public Unit invoke(ValueAnimator valueAnimator) {
                            return null;
                        }
                    }, new Function1<ValueAnimator, Unit>() {
                        @Override
                        public Unit invoke(ValueAnimator valueAnimator) {
                            return null;
                        }
                    }
            );
            if (isTracking) {
                updateCamera(enhancedLocation);
            }
        }
    };
    private final MapboxNavigationObserver mapboxNavigationObserver = new MapboxNavigationObserver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onAttached(@NonNull MapboxNavigation mapboxNavigation) {
            mapboxNavigation.registerLocationObserver(myLocationObserver);
            mapboxNavigation.startTripSession();
        }

        @Override
        public void onDetached(@NonNull MapboxNavigation mapboxNavigation) {
            mapboxNavigation.unregisterLocationObserver(myLocationObserver);
        }
    };

// Đăng ký observer với MapboxNavigation

    private void enableLocationComponent() {
        // Kích hoạt logic để hiển thị vị trí trên bản đồ
        Toast.makeText(this, "Kích hoạt hiển thị vị trí người dùng", Toast.LENGTH_SHORT).show();
        //cho phep hien thi huong di chuyen
        getLocationComponent(binding.mapView).setPulsingEnabled(true);
        getLocationComponent(binding.mapView).setPulsingEnabled(true);
        //cho phep hien thi vi tri nguoi dung
        getLocationComponent(binding.mapView).setEnabled(true);
        getLocationComponent(binding.mapView).
                addOnIndicatorBearingChangedListener(indicatorBearingChangedListener);
        getLocationComponent(binding.mapView).
                addOnIndicatorPositionChangedListener(indicatorPositionChangedListener);
        getGestures(binding.mapView).addOnMoveListener(onMoveListener);
        isListening=true;
        binding.fabUserTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMapboxListener();
                getLocationComponent(binding.mapView).
                        addOnIndicatorBearingChangedListener(indicatorBearingChangedListener);
                getLocationComponent(binding.mapView).
                        addOnIndicatorPositionChangedListener(indicatorPositionChangedListener);
                getGestures(binding.mapView).addOnMoveListener(onMoveListener);
                isListening=true;
            }
        });
    }
    private void setupRecenterButton() {
        binding.fabUserTracking.setOnClickListener(v -> {
            if (!isTracking) {
                startLocationTracking();
                binding.fabUserTracking.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                );
            }
        });
    }
    //Nếu huoướng người dùng thayđổi thì thay đổi camera theo
    OnIndicatorBearingChangedListener indicatorBearingChangedListener=new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            cameraForBearing=new CameraOptions.Builder().bearing(v).build();
            binding.mapView.getMapboxMap().setCamera(cameraForBearing);
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

            cameraForPosition=new CameraOptions.Builder()
                    .center(point) // Đặt vị trí trung tâm là vị trí người dùng
                    .zoom(binding.mapView.getMapboxMap().getCameraState().getZoom()) // Đặt mức zoom của bản đồ hện tại
                    .build();
            CameraAnimationsUtils.flyTo(binding.mapView.getMapboxMap(),
                    cameraForPosition,
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
            if (isTracking) {
                removeMapboxListener();
                binding.fabLocationTracking.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context,R.color.pink)));
            }
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
        if(isTracking){
            /*getLocationComponent(binding.mapView)
                    .removeOnIndicatorBearingChangedListener(indicatorBearingChangedListener);
            getLocationComponent(binding.mapView)
                    .removeOnIndicatorPositionChangedListener(indicatorPositionChangedListener);*/
            if (mapboxNavigation != null) {
                mapboxNavigation.unregisterLocationObserver(myLocationObserver);
            }
            isTracking=false;

        }
    }
    private void startLocationTracking() {
        if (!isTracking) {
            // 1. Start location updates
            if (mapboxNavigation != null) {
                mapboxNavigation.registerLocationObserver(myLocationObserver);
            }

            isTracking = true;
            isListening = true;
        }
    }
    public void updateCamera(Point point,double bearing){
        removeMapboxListener();
        cameraForLocation=new CameraOptions.Builder()
                .center(point) // Đặt vị trí trung tâm là vị trí người dùng
                .zoom(binding.mapView.getMapboxMap().getCameraState().getZoom()) // Đặt mức zoom của bản đồ hện tại
                .bearing(bearing)
                .build();
        //cai animation
        CameraAnimationsUtils.flyTo(binding.mapView.getMapboxMap(),
                cameraForLocation,
                new MapAnimationOptions.Builder().duration(2000).build());
        binding.mapView.getMapboxMap().setCamera(cameraForLocation);
    }
    //navigation




}