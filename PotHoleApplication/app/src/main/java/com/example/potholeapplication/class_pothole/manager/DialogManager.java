package com.example.potholeapplication.class_pothole.manager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.potholeapplication.R;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.SplashScreenActivity;
import com.example.potholeapplication.class_pothole.request.AddPotholeReq;
import com.example.potholeapplication.class_pothole.response.ApiResponse;
import com.example.potholeapplication.class_pothole.response.LocationClass;
import com.example.potholeapplication.Retrofit2.APIInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogManager {
    private static boolean isDialogShowing=false;

    //hàm tao dialog
    private static Dialog createDialog(Context context, int layoutID,boolean isCancelable){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(layoutID);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(isCancelable);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }
    //tạo dialog xong đóng activity
    public static void showDialogOkeThenFinish(Context context, String title){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_oke,false);
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

    //tạo dialog lỗi, in ra lôi từ sever trả ve
    public static void showDialogError(Context context, ApiResponse apiResponse){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_error,true);

        Button btnConfirm=dialog.findViewById(R.id.btnConfirm);
        TextView tvErrorTitle=dialog.findViewById(R.id.tvTitle);
        switch (apiResponse.getMessage().trim()){
            case "Server error":
                tvErrorTitle.setText(R.string.str_server_error);
                break;
            case "Username not found":
                tvErrorTitle.setText(R.string.str_username_not_found);
                break;
            case "Wrong Password":
                tvErrorTitle.setText(R.string.str_wrong_password);
                break;
            case "User not found":
                tvErrorTitle.setText(R.string.str_user_not_found);
                break;
            case "Username already exists":
                tvErrorTitle.setText(R.string.str_username_already_exists);
                break;
            case "Email not found":
                tvErrorTitle.setText(R.string.str_email_not_found);
                break;
            case "Email already exists":
                tvErrorTitle.setText(R.string.str_email_already_exists);
                break;
            case "Error save pothole":
                tvErrorTitle.setText(R.string.str_error_save_pothole);
                break;
            default: break;
        }
        dialog.show();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //tao dialog lỗi v in ra 1 chuỗi string
    public static void showDialogErrorString(Context context, String message){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_error,true);

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

    //tao dialog oke và chuyển dđến 1 activity khác
    public static void showDialogOkeNavigation(Context context, String title,
                                               Class<?> next){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_oke,false);
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

    //tạo dialog oke, chuyển đến 1 activity mới, xoóa activity truước nó
    public static void showDialogOkeNavigationClear(Context context, String title,
                                               Class<?> next){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_oke,false);
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

    //gọi dialog đổi ngon ngữ
    public static void showDialogLanguage(Context context){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_language,true);

        MaterialCardView cvEnglish=dialog.findViewById(R.id.cvEnglish);
        MaterialCardView cvVietnamese=dialog.findViewById(R.id.cvVietnamese);
        dialog.show();
        cvEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleManager.updateNewLanguageThenReload(context,"en", SplashScreenActivity.class);
                dialog.dismiss();
            }
        });
        cvVietnamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleManager.updateNewLanguageThenReload(context,"vi", SplashScreenActivity.class);
                dialog.dismiss();
            }
        });
    }

    //Goi dialog hien thi thong tin lên piechart
    public static void showDialogPieChartDetail(Context context, int large,int medium, int small){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_pie_chart,true);

        TextView tvLargePercentage=dialog.findViewById(R.id.tvLargePercentage);
        TextView tvMediumPercentage=dialog.findViewById(R.id.tvMediumPercentage);
        TextView tvSmallPercentage=dialog.findViewById(R.id.tvSmallPercentage);

        tvLargePercentage.setText(large+R.string.str_percent);
        tvMediumPercentage.setText(medium+R.string.str_percent);
        tvSmallPercentage.setText(small+R.string.str_percent);

        dialog.show();
        setIsDialogShowing(true);

    }

    public static boolean isIsDialogShowing() {
        return isDialogShowing;
    }

    public static void setIsDialogShowing(boolean isDialogShowing) {
        DialogManager.isDialogShowing = isDialogShowing;
    }
    public static void showDialogSavePothole(Context context,double longtitude,
                                             double latitude,String severity){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_save_pothole,true);

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
                        LocalDataManager.getEmail(context),
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

        APIInterface apiService = RetrofitServices.getApiService();
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
                        DialogManager.showDialogError(context,apiResponse);
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
