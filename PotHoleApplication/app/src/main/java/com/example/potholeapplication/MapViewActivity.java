package com.example.potholeapplication;
import static com.mapbox.maps.plugin.annotation.AnnotationsUtils.getAnnotations;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.Retrofit2.SavePotholeSatusCallBack;
import com.example.potholeapplication.class_pothole.manager.APIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.manager.NetworkManager;
import com.example.potholeapplication.class_pothole.other.Pothole;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.example.potholeapplication.databinding.ActivityMapViewBinding;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.bindgen.Expected;
import com.mapbox.common.location.Location;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.trip.model.RouteProgress;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.core.trip.session.RouteProgressObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineUpdateValue;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteResult;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;
import retrofit2.Response;

public class MapViewActivity extends AppCompatActivity {
    //------------------Khởi tạo bieến cơ bản-------------------
    ActivityMapViewBinding binding;
    Context context;
    Point currentPoint;//luu tru vi tri hien tai
    boolean isFirstTime = true;//lan dau mo ung dung map
    boolean ignoreNextQueryUpdate = false, isOnTracking = false,
            isListening = false,isOnRouting=false
            ,isExpanded=false;
    List<Pothole> potholes;
    NetworkManager networkManager;
    //-------------Biến tọađộ,camera,icon-----------------------
    Point thisLocation;
    Bitmap iconImage,iconPotholeLarge,iconPotholeSmall,iconPotholeMedium;
    CameraOptions cameraForBearing, cameraForLocation, cameraForPosition;
    PointAnnotationManager pointAnnotationManager;

    //------------------Biến search view-------------------
    PlaceAutocomplete autocomplete;
    PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    //--------------Biến current location tracking-----------------------

    MapboxNavigation mapboxNavigation;
    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
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
            Expected<RouteLineError, RouteLineUpdateValue> routeLineUpdate =
                    routeLineApi.updateTraveledRouteLine(
                            Point.fromLngLat(enhancedLocation.getLongitude(), enhancedLocation.getLatitude())
                    );

            Style style = binding.mapView.getMapboxMap().getStyle();
            if (style != null) {
                routeLineView.renderRouteLineUpdate(style, routeLineUpdate);
            }
            if (isOnTracking) {
                updateCamera(enhancedLocation);
            }
        }
    };
    private final MapboxNavigationObserver mapboxNavigationObserver = new MapboxNavigationObserver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onAttached(@NonNull MapboxNavigation mapboxNavigation) {
            mapboxNavigation.registerRoutesObserver(routesObserver);
            mapboxNavigation.registerLocationObserver(myLocationObserver);
            mapboxNavigation.registerRouteProgressObserver(routeProgressObserver);
            mapboxNavigation.startTripSession();
        }

        @Override
        public void onDetached(@NonNull MapboxNavigation mapboxNavigation) {
            mapboxNavigation.unregisterLocationObserver(myLocationObserver);
            mapboxNavigation.registerRoutesObserver(routesObserver);
            mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver);

        }
    };
    //---------------các hàm lay thong tin map--------------
    public MapboxMap getMapboxMap() {
        return binding.mapView.getMapboxMap();
    }
    public double getMapboxZoom(){
        return binding.mapView.getMapboxMap().getCameraState().getZoom();
    }
    //----------------------Các biến listening----------------
    OnIndicatorBearingChangedListener indicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            cameraForBearing = new CameraOptions.Builder().bearing(v).build();
            binding.mapView.getMapboxMap().setCamera(cameraForBearing);
        }
    };


    OnIndicatorPositionChangedListener indicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            if (isFirstTime) {
                CameraOptions cameraOptions = new CameraOptions.Builder()
                        .center(point) // Đặt vị trí trung tâm là vị trí người dùng
                        .zoom(17.0) // Đặt mức zoom của bản đồ hện tại
                        .build();
                getMapboxMap().setCamera(cameraOptions);
                isFirstTime = false;
            }

            cameraForPosition = new CameraOptions.Builder()
                    .center(point) // Đặt vị trí trung tâm là vị trí người dùng
                    .zoom(binding.mapView.getMapboxMap().getCameraState().getZoom()) // Đặt mức zoom của bản đồ hện tại
                    .build();
            CameraAnimationsUtils.flyTo(binding.mapView.getMapboxMap(),
                    cameraForPosition,
                    new MapAnimationOptions.Builder().duration(1000).build());

            //giu nguoi dung o vi tri hien tai khi zoom
            getGestures(binding.mapView).setFocalPoint(binding.mapView.getMapboxMap().pixelForCoordinate(point));
            currentPoint = point;//luu vi tri hien tai
        }
    };
    OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            if (isOnTracking) {
                removeLocationTracking();
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
    //---------------Biến navigation route-----------
    MapboxRouteLineApi routeLineApi;
    MapboxRouteLineView routeLineView;
    MapboxRouteLineViewOptions routeLineViewOptions;
    MapboxRouteLineApiOptions routeLineApiOptions;
    private final RoutesObserver routesObserver = new RoutesObserver() {
        @Override
        public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
            routeLineApi.setNavigationRoutes(routesUpdatedResult.getNavigationRoutes(), new MapboxNavigationConsumer<Expected<RouteLineError, RouteSetValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) {
                    Style style = getMapboxMap().getStyle();
                    if (style != null) {
                        routeLineView.renderRouteDrawData(style, routeLineErrorRouteSetValueExpected);
                    }
                }
            });
        }
    };
    // Route progress observer để cập nhật tiến trình
    private final RouteProgressObserver routeProgressObserver = new RouteProgressObserver() {
        @Override
        public void onRouteProgressChanged(@NonNull RouteProgress routeProgress) {
            routeLineApi.updateWithRouteProgress(routeProgress, new MapboxNavigationConsumer<Expected<RouteLineError, RouteLineUpdateValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteLineUpdateValue> routeLineErrorRouteLineUpdateValueExpected) {
                    Style style = binding.mapView.getMapboxMap().getStyle();
                    if (style != null) {
                        routeLineView.renderRouteLineUpdate(style, routeLineErrorRouteLineUpdateValueExpected);
                    }
                }
            });
        }
    };



    //------------------các hàm vòng đời activity------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivityMapViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int navigationBarHeight = insets.getInsets(
                    WindowInsetsCompat.Type.navigationBars()
            ).bottom;
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, navigationBarHeight);
            return insets;
        });
        context=this;
        networkManager=new NetworkManager(this);
        init();
        if (checkLocationPermissions()) {
            setupLocationTracking();
            //enableLocationComponent();
            setupClickMap();
            settingSearch();
        } else {
            requestLocationPermission();
        }
    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(potholeReceiver, new IntentFilter("com.example.SHOW_DIALOG"));
        callGetPothole();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(potholeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapboxNavigationApp.detach(this);
        MapboxNavigationApp.unregisterObserver(mapboxNavigationObserver);
        mapboxNavigation.unregisterRoutesObserver(routesObserver);
        MapboxNavigation navigation = MapboxNavigationApp.current();
        if (navigation != null) {
            navigation.stopTripSession();
        }

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }

    //-----------------Hàm permission----------------------
    public void requestLocationPermission() {
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
                setupLocationTracking();
                setupClickMap();
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

    //---------------Hàm cài đặt co7 bản-----------------
    public void init() {
        context = this;
        setupIcon();
        setClickListener();
        binding.fabLocationTracking.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink)));
        binding.fabUserTracking.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink)));

    }

    public void setupIcon(){
        iconImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_puck);
        iconPotholeLarge = createBitmapFromDrawable(R.drawable.ic_pothole_large);
        iconPotholeMedium = createBitmapFromDrawable(R.drawable.ic_pothole_medium);
        iconPotholeSmall = createBitmapFromDrawable(R.drawable.ic_pothole_small);;
    }
    public Bitmap createBitmapFromDrawable(int drawableID){
        Drawable drawable = ContextCompat.getDrawable(this, drawableID);
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        // Áp dụng kích thước và vẽ vào Bitmap
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawable.draw(new Canvas(bitmap));
        return bitmap;
    }
    public void setClickListener() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.fabLocationTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnRouting){
                    clearRoute();
                    isOnRouting=false;
                }else{
                    fetchRoute(thisLocation);
                    isOnRouting=true;
                }
            }
        });
    }
    //-------------------Hàm cài đặt location tracking----------------
    private void setupLocationTracking() {
        setupNavigationRoute();
        if (!MapboxNavigationApp.isSetup()) {
             MapboxNavigationApp.setup(new NavigationOptions.Builder(context).build());
        }
        MapboxNavigationApp.attach(this);
        //wait cho NavigationApp set up xong
        getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onStart(@NonNull LifecycleOwner owner) {
                MapboxNavigationApp.registerObserver(mapboxNavigationObserver);
                mapboxNavigation = MapboxNavigationApp.current();
                if (mapboxNavigation != null) {
                    startLocationTracking();
                }
            }
        });
        //gan lifecycle cua mapbox
        getLocationComponent(binding.mapView).setLocationProvider(navigationLocationProvider);
        getLocationComponent(binding.mapView).setEnabled(true);
        //cai dat button de khi click la quay tro lai tracking
        binding.fabUserTracking.setOnClickListener(v -> {
            if (!isOnTracking) {
                startLocationTracking();
            }
            else {
                removeLocationTracking();
             }});

    }
    public void removeLocationTracking() {
        if (isOnTracking) {
            /*getLocationComponent(binding.mapView)
                    .removeOnIndicatorBearingChangedListener(indicatorBearingChangedListener);
            getLocationComponent(binding.mapView)
                    .removeOnIndicatorPositionChangedListener(indicatorPositionChangedListener);*/
            getGestures(binding.mapView).removeOnMoveListener(onMoveListener);
            if (mapboxNavigation != null) {
                mapboxNavigation.unregisterLocationObserver(myLocationObserver);
            }
            binding.fabUserTracking.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink)));
            isOnTracking = false;
        }
    }

    private void startLocationTracking() {
        if (!isOnTracking) {
            getGestures(binding.mapView).addOnMoveListener(onMoveListener);
            if (mapboxNavigation != null) {
                mapboxNavigation.registerLocationObserver(myLocationObserver);
            }
            binding.fabUserTracking.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
            isOnTracking = true;
        }
    }
    //-----------------Haàm cài đặt search view-------------------------

    public void settingSearch() {
        //cai dat
        autocomplete = PlaceAutocomplete.create();

        binding.searchResultView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(
                binding.searchResultView, autocomplete
        );
        AnnotationPlugin annotationPlugin = getAnnotations(binding.mapView);
        pointAnnotationManager = (PointAnnotationManager) annotationPlugin.createAnnotationManager(
                AnnotationType.PointAnnotation, null
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
                        Expected<Exception, PlaceAutocompleteResult> result =
                                (Expected<Exception, PlaceAutocompleteResult>) o;
                        result.onValue(new Expected.Action<PlaceAutocompleteResult>() {
                            @Override
                            public void run(@NonNull PlaceAutocompleteResult input) {
                                hideKeyboard(binding.searchView);
                                ignoreNextQueryUpdate = true;

                                //gan marker ngay vi tri do
                                binding.searchView.setText(placeAutocompleteSuggestion.getFormattedAddress());
                                binding.searchResultView.setVisibility(View.GONE);
                                pointAnnotationManager.deleteAll();
                                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                        .withTextAnchor(TextAnchor.CENTER)
                                        .withIconImage(iconImage)
                                        .withPoint(input.getCoordinate());
                                pointAnnotationManager.create(pointAnnotationOptions);
                                thisLocation = input.getCoordinate();
                                //update camera lai vi tri do
                                updateCamera(input.getCoordinate(), getMapboxMap().getCameraState().getBearing());
                            }
                        }).onError(new Expected.Action<Exception>() {
                            @Override
                            public void run(@NonNull Exception input) {
                                Log.e("Error", input.toString() + "");
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
                if (ignoreNextQueryUpdate) ignoreNextQueryUpdate = false;
                else {
                    Log.d("name", s.toString());
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
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
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
        isListening = true;
        binding.fabUserTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLocationTracking();
                getLocationComponent(binding.mapView).
                        addOnIndicatorBearingChangedListener(indicatorBearingChangedListener);
                getLocationComponent(binding.mapView).
                        addOnIndicatorPositionChangedListener(indicatorPositionChangedListener);
                getGestures(binding.mapView).addOnMoveListener(onMoveListener);
                isListening = true;
            }
        });
    }
    //------------------setupClickMap----------------
    public void setupClickMap(){
        AnnotationPlugin annotationPlugin = getAnnotations(binding.mapView);
        pointAnnotationManager = (PointAnnotationManager) annotationPlugin.createAnnotationManager(
                AnnotationType.PointAnnotation, null
        );
        getGestures(binding.mapView).addOnMapClickListener(
                new OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull Point point) {
                        removeLocationTracking();
                        pointAnnotationManager.deleteAll();

                        // cài marker
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                .withPoint(point) // điểm click
                                .withIconImage(iconImage) // icon cho marker
                                .withIconSize(1.0) // kích thước icon
                                .withDraggable(false); // có cho phép kéo marker không
                        pointAnnotationManager.create(pointAnnotationOptions);
                        isOnRouting=false;
                        thisLocation=point;
                        //su kien click de tracking
                        /*binding.fabLocationTracking.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(isOnRouting){
                                    clearRoute();
                                    isOnRouting=false;
                                }else{
                                    fetchRoute(thisLocation);
                                    isOnRouting=true;
                                }
                            }
                        });*/


                        // Tùy chọn: Di chuyển camera đến điểm được click
                        CameraOptions cameraPosition = new CameraOptions.Builder()
                                .center(point)
                                .zoom(getMapboxZoom())
                                .build();
                        CameraAnimationsUtils.flyTo(getMapboxMap(),
                                cameraPosition,
                                new MapAnimationOptions.Builder().duration(2000).build());
                        getMapboxMap().setCamera(cameraPosition);
                        return true;
                    }
                }
        );
    }
    //-----------------------hàm navigation route-----------------
    private void setupNavigationRoute() {
        routeLineViewOptions=new MapboxRouteLineViewOptions.Builder(this)
                .routeLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER)
                .routeLineColorResources(new RouteLineColorResources.Builder().build())
                .build();
        routeLineApiOptions=new MapboxRouteLineApiOptions.Builder()
                .vanishingRouteLineEnabled(true)
                .build();
        routeLineApi=new MapboxRouteLineApi(routeLineApiOptions);
        routeLineView=new MapboxRouteLineView(routeLineViewOptions);
    }
    private void fetchRoute(Point point){
        Location currentLocation = navigationLocationProvider.getLastLocation();

        if (currentLocation == null) {
            Toast.makeText(this, "Waiting for location...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button và hiển thị loading
        binding.fabLocationTracking.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));

        // Tạo điểm bắt đầu từ vị trí hiện tại
        Point origin = Point.fromLngLat(
                currentLocation.getLongitude(),
                currentLocation.getLatitude()
        );
        Log.d("Location",origin+"/"+point+"/"+currentLocation.getLatitude()+"/"+currentLocation.getLongitude());
        // Tạo route options
        RouteOptions routeOptions=createRouteOptions(origin,point,currentLocation);
        // Request route
        mapboxNavigation.requestRoutes(
                routeOptions,
                new NavigationRouterCallback() {
                    @Override
                    public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull String s) {
                        if (!list.isEmpty()) {
                            mapboxNavigation.setNavigationRoutes(list);

                            // Focus vào vị trí hiện tại
                            binding.fabUserTracking.performClick();

                            // Reset button state
                            binding.fabLocationTracking.setBackgroundTintList(
                                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                            );
                            isOnRouting=true;
                        } else {
                            Toast.makeText(
                                    MapViewActivity.this,
                                    "No routes found",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull String s) {
                        binding.fabLocationTracking.setBackgroundTintList(
                                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink)));
                    }

                    @Override
                    public void onFailure(
                            @NonNull List<RouterFailure> failures,
                            @NonNull RouteOptions routeOptions
                    ) {
                        // Xử lý lỗi
                        binding.fabLocationTracking.setBackgroundTintList(
                                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink)));
                        Toast.makeText(
                                MapViewActivity.this,
                                "Route request failed: " + failures.get(0).getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }


                }
        );
    }
    private void clearRoute() {
        routeLineApi.clearRouteLine(value -> {
            Style style = getMapboxMap().getStyle();
            if (style != null) {
                routeLineView.renderClearRouteLineValue(style, value);
            }
        });
        binding.fabLocationTracking.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink)));
    }
    private RouteOptions createRouteOptions(Point origin, Point destination,Location currentLocation) {
        return RouteOptions.builder()
                // Required
                .coordinatesList(Arrays.asList(origin, destination))
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .user(DirectionsCriteria.PROFILE_DEFAULT_USER)
                // Important fields for steps
                .steps(true)                                    // Bắt buộc để có steps
                .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .alternatives(false)

                // Navigation related
                .voiceInstructions(true)
                .bannerInstructions(true)
                .voiceUnits(DirectionsCriteria.METRIC)

                // Optional bearing
                .bearingsList(createBearingsList(currentLocation))

                // Ensure valid coordinates
                .radiusesList(Arrays.asList(null, null))       // Tìm điểm gần nhất có thể đi được
                .build();
    }
    private List<Bearing> createBearingsList(Location location) {
        List<Bearing> bearings = new ArrayList<>();
        if (location != null && location.getBearing()!=null) {
            bearings.add(Bearing.builder()
                    .angle(location.getBearing())
                    .degrees(45.0)
                    .build());
        } else {
            bearings.add(null);
        }
        bearings.add(null);
        return bearings;
    }
    //-----------------------Update camera---------------------
    private void updateCamera(Location location) {
        if (isFirstTime) {
            CameraOptions cameraOptions = new CameraOptions.Builder()
                    .center(Point.fromLngLat(location.getLongitude(), location.getLatitude()))
                    .zoom(17.0)
                    .build();
            getMapboxMap().setCamera(cameraOptions);
            isFirstTime = false;
        } else {
            CameraOptions cameraOptions = new CameraOptions.Builder()
                    .center(Point.fromLngLat(location.getLongitude(), location.getLatitude()))
                    .zoom(getMapboxZoom())
                    .padding(new EdgeInsets(500.0, 0.0, 0.0, 0.0))
                    .build();
            CameraAnimationsUtils.flyTo(getMapboxMap(),
                    cameraOptions,
                    new MapAnimationOptions.Builder().duration(2000).build());
            getMapboxMap().setCamera(cameraOptions);

        }
    }
    public void updateCamera(Point point, double bearing) {
        removeLocationTracking();
        cameraForLocation = new CameraOptions.Builder()
                .center(point) // Đặt vị trí trung tâm là vị trí người dùng
                .zoom(getMapboxZoom()) // Đặt mức zoom của bản đồ hện tại
                .bearing(bearing)
                .build();
        //cai animation
        CameraAnimationsUtils.flyTo(binding.mapView.getMapboxMap(),
                cameraForLocation,
                new MapAnimationOptions.Builder().duration(2000).build());
        getMapboxMap().setCamera(cameraForLocation);
    }

    //---------------call get pothole-----------
    public void callGetPothole(){
        if(!networkManager.isNetworkAvailable()){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            potholes= LocalDataManager.getPotholeList(context);
            return;
        }
        APIManager.callGetPothole(new APICallBack<APIResponse<Pothole>>() {
            @Override
            public void onSuccess(Response<APIResponse<Pothole>> response) {
                setupIcon();
                potholes=response.body().getData();
                LocalDataManager.savePotholeList(context,potholes);
                updatePotholeMarker();
            }

            @Override
            public void onError(APIResponse<Pothole> errorResponse) {
                Log.d("GetPothole","error");


            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("GetPothole","error");
            }
        });

    }
    PointAnnotationManager potholeAnnotationManager;
    public void updatePotholeMarker(){
        AnnotationPlugin annotationPlugin = getAnnotations(binding.mapView);
        potholeAnnotationManager = (PointAnnotationManager) annotationPlugin.createAnnotationManager(
                AnnotationType.PointAnnotation, null
        );
        potholeAnnotationManager.deleteAll();
        Log.d("LOG",iconPotholeSmall+"");
        for(int i=0;i<potholes.size();i++){
            switch(potholes.get(i).getSeverity()){
                case "small":{
                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                            .withTextAnchor(TextAnchor.CENTER)
                            .withIconImage(iconPotholeSmall)
                            .withPoint(Point.fromLngLat(potholes.get(i).getLocationClass().getCoordinates().get(0),
                                    potholes.get(i).getLocationClass().getCoordinates().get(1)));
                    potholeAnnotationManager.create(pointAnnotationOptions);
                    break;
                }
                case "medium":{
                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                            .withTextAnchor(TextAnchor.CENTER)
                            .withIconImage(iconPotholeMedium)
                            .withPoint(Point.fromLngLat(potholes.get(i).getLocationClass().getCoordinates().get(0),
                                    potholes.get(i).getLocationClass().getCoordinates().get(1)));
                    potholeAnnotationManager.create(pointAnnotationOptions);
                    break;
                }
                case "large":{
                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                            .withTextAnchor(TextAnchor.CENTER)
                            .withIconImage(iconPotholeLarge)
                            .withPoint(Point.fromLngLat(potholes.get(i).getLocationClass().getCoordinates().get(0),
                                    potholes.get(i).getLocationClass().getCoordinates().get(1)));
                    potholeAnnotationManager.create(pointAnnotationOptions);
                    break;
                }
            }

        }

    }
    //----------------------NETWORK--------------------------
    private final BroadcastReceiver potholeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.SHOW_DIALOG".equals(intent.getAction())) {
                double latitude = intent.getDoubleExtra("latitude", 0);
                double longitude = intent.getDoubleExtra("longitude", 0);
                String severity= intent.getStringExtra("severity");
                Toast.makeText(context, severity, Toast.LENGTH_SHORT).show();
                if(!DialogManager.isIsDialogShowing()) {
                    DialogManager.showDialogSavePothole(context,
                            longitude, latitude, severity, new SavePotholeSatusCallBack() {
                                @Override
                                public void onComplete(boolean isSuccess) {
                                    if (isSuccess) {
                                        callGetPothole();
                                    }
                                }
                            });
                }
            }
        }
    };



}
