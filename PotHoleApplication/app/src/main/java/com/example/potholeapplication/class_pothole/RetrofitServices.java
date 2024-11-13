package com.example.potholeapplication.class_pothole;

import com.example.potholeapplication.interface_pothole.UserAPIInterface;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServices {
    private static final String BASE_URL = "http://149.28.153.49:8000/";
    private static Retrofit retrofit = null;

    public static UserAPIInterface getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(UserAPIInterface.class);
    }
}
