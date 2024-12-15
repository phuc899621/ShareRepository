package com.example.potholeapplication.class_pothole;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.potholeapplication.R;
import com.example.potholeapplication.SplashScreenActivity;
import com.example.potholeapplication.class_pothole.request.AddPotholeReq;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.class_pothole.response.ApiResponse;
import com.example.potholeapplication.class_pothole.response.LocationClass;
import com.example.potholeapplication.edit_user.EditEmailActivity;
import com.example.potholeapplication.edit_user.EditEmailVerificationActivity;
import com.example.potholeapplication.interface_pothole.UserAPIInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomDialog {
    private static boolean isDialogShowing=false;
    public static void showDialogOkeThenFinish(Context context, String title){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_oke);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        TextView tvTitle=dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = (Activity) context;
                activity.finish();
                dialog.dismiss();
            }
        },1500);
    }

    public static void showDialogError(Context context, ApiResponse apiResponse){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_error);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btnConfirm=dialog.findViewById(R.id.btnConfirm);
        TextView tvErrorTitle=dialog.findViewById(R.id.tvTitle);
        if(apiResponse.getMessage().trim().equals("Server error")){
            tvErrorTitle.setText(R.string.str_server_error);
        }
        if(apiResponse.getMessage().trim().equals("Username not found")){
            tvErrorTitle.setText(R.string.str_username_not_found);
        }
        if(apiResponse.getMessage().trim().equals("Wrong Password")){
            tvErrorTitle.setText(R.string.str_wrong_password);
        }
        if(apiResponse.getMessage().trim().equals("User not found")){
            tvErrorTitle.setText(R.string.str_user_not_found);
        }
        if(apiResponse.getMessage().trim().equals("Username already exists")){
            tvErrorTitle.setText(R.string.str_username_already_exists);
        }
        if(apiResponse.getMessage().trim().equals("Email not found")){
            tvErrorTitle.setText(R.string.str_email_not_found);
        }
        if(apiResponse.getMessage().trim().equals("Email already exists")){
            tvErrorTitle.setText(R.string.str_email_already_exists);
        }
        if(apiResponse.getMessage().trim().equals("Error save pothole")){
            tvErrorTitle.setText(R.string.str_error_save_pothole);
        }
        dialog.show();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public static void showDialogErrorString(Context context, String message){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_error);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnConfirm=dialog.findViewById(R.id.btnConfirm);
        TextView tvErrorTitle=dialog.findViewById(R.id.tvTitle);
        tvErrorTitle.setText(message);
        dialog.show();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public static void showDialogOkeNavigation(Context context, String title,
                                               Class<?> next){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_oke);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        TextView tvTitle=dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, next);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        },1500);
    }
    public static void showDialogOkeNavigationClear(Context context, String title,
                                               Class<?> next){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_oke);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        TextView tvTitle=dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, next);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                dialog.dismiss();
                ((Activity) context).finish();
            }
        },1500);
    }
    public static void showDialogLanguage(Context context){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_language);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MaterialCardView cvEnglish=dialog.findViewById(R.id.cvEnglish);
        MaterialCardView cvVietnamese=dialog.findViewById(R.id.cvVietnamese);
        dialog.show();
        cvEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleManager.updateNewLanguage(context,"en");
                Resources resources = context.getResources();
                @SuppressLint({"NewApi", "LocalSuppress"}) String config =
                        resources.getConfiguration().getLocales().get(0).getLanguage();
                Log.d("Language",config);
                dialog.dismiss();
                Intent intent = new Intent(context, SplashScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        cvVietnamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleManager.updateNewLanguage(context,"vi");
                Resources resources = context.getResources();
                @SuppressLint({"NewApi", "LocalSuppress"}) String config =
                        resources.getConfiguration().getLocales().get(0).getLanguage();
                Log.d("Language",config);
                dialog.dismiss();
                Intent intent = new Intent(context, SplashScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }
    public static void showDialogPieChartDetail(Context context, int large,int medium, int small){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_pie_chart);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvLargePercentage=dialog.findViewById(R.id.tvLargePercentage);
        TextView tvMediumPercentage=dialog.findViewById(R.id.tvMediumPercentage);
        TextView tvSmallPercentage=dialog.findViewById(R.id.tvSmallPercentage);

        tvLargePercentage.setText(large+"%");
        tvMediumPercentage.setText(medium+"%");
        tvSmallPercentage.setText(small+"%");



        dialog.show();
        setIsDialogShowing(true);

    }

    public static boolean isIsDialogShowing() {
        return isDialogShowing;
    }

    public static void setIsDialogShowing(boolean isDialogShowing) {
        CustomDialog.isDialogShowing = isDialogShowing;
    }
    public static void showDialogSavePothole(Context context,double longtitude,
                                             double latitude,String severity){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_save_pothole);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvLatitude=dialog.findViewById(R.id.tvLatitude);
        TextView tvLongtitude=dialog.findViewById(R.id.tvLongtitude);
        TextView tvSeverity=dialog.findViewById(R.id.tvSeverity);
        MaterialButton btnYes=dialog.findViewById(R.id.btnYes);
        MaterialButton btnNo=dialog.findViewById(R.id.btnNo);

        tvLatitude.setText(context.getString(R.string.str_latitude)+": "+
                latitude);
        tvLongtitude.setText(context.getString(R.string.str_longtitude)+": "+
                longtitude);
        tvSeverity.setText(context.getString(R.string.str_severity)+": "+
                severity);
        dialog.show();

        setIsDialogShowing(true);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                List<Double> coordinates=new ArrayList<>();
                coordinates.add(longtitude);
                coordinates.add(latitude);

                callSavePotholeAPI(context, new AddPotholeReq(
                        new LocationClass(coordinates),
                        DataEditor.getEmail(context),
                        severity
                ));
                setIsDialogShowing(false);

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                setIsDialogShowing(false);
            }
        });
    }
    public static void callSavePotholeAPI(Context context, AddPotholeReq addPotholeReq){

        UserAPIInterface apiService = RetrofitServices.getApiService();
        Call<ApiResponse> call = apiService.callAddPothole(addPotholeReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){

                }
                else{
                    String errorString;
                    ApiResponse apiResponse;
                    try {
                        errorString=response.errorBody().string();
                        Gson gson=new Gson();
                        apiResponse=gson.fromJson(errorString, ApiResponse.class);
                        CustomDialog.showDialogError(context,apiResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API Error", "Failure: " + t.getMessage());
            }
        });
    }
}
