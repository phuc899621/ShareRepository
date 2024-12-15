package com.example.potholeapplication.class_pothole;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.potholeapplication.R;
import com.example.potholeapplication.SplashScreenActivity;
import com.example.potholeapplication.class_pothole.response.ApiResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class CustomDialog {
    private static boolean isDialogShowing=false;

    //ham tao 1 dialog
    private static Dialog createDialog(Context context,int layoutID,boolean isCancelable){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(layoutID);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(isCancelable);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }
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
    public static void showDialogPieChartDetail(Context context, String title){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_pie_chart);

    }
    public static void showDialogError(Context context, ApiResponse apiResponse){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_error,true);

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
        dialog.show();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
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
    public static void showDialogLanguage(Context context){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_language,true);

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

    public static boolean isIsDialogShowing() {
        return isDialogShowing;
    }

    public static void setIsDialogShowing(boolean isDialogShowing) {
        CustomDialog.isDialogShowing = isDialogShowing;
    }
}
