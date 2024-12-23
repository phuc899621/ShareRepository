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
import com.example.potholeapplication.Retrofit2.APICallBack;
import com.example.potholeapplication.Retrofit2.SavePotholeSatusCallBack;
import com.example.potholeapplication.Retrofit2.StopShowDialog;
import com.example.potholeapplication.SplashScreenActivity;
import com.example.potholeapplication.class_pothole.other.User;
import com.example.potholeapplication.class_pothole.request.AddPotholeReq;
import com.example.potholeapplication.class_pothole.response.APIResponse;
import com.example.potholeapplication.class_pothole.other.LocationClass;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class DialogManager {
    private static boolean isDialogSavingPothole =false;
    private static boolean isDialogWarningPothole =false;

    //hàm tao dialog
    private static Dialog createDialog(Context context, int layoutID,boolean isCancelable){
        Dialog dialog = new Dialog(context);
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
    public static <T> void showDialogError(Context context, APIResponse<T> apiResponse){
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
    public static void showDialogPieChartDetail(Context context, float large,float medium, float small){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_pie_chart,true);

        TextView tvLargePercentage=dialog.findViewById(R.id.tvLargePercentage);
        TextView tvMediumPercentage=dialog.findViewById(R.id.tvMediumPercentage);
        TextView tvSmallPercentage=dialog.findViewById(R.id.tvSmallPercentage);

        tvLargePercentage.setText(String.format("%.2f%%", large));
        tvMediumPercentage.setText(String.format("%.2f%%", medium));
        tvSmallPercentage.setText(String.format("%.2f%%", small));

        dialog.show();
        setIsDialogSavingPothole(true);

    }

    public static boolean isIsDialogSavingPothole() {
        return isDialogSavingPothole;
    }

    public static void setIsDialogSavingPothole(boolean isDialogSavingPothole) {
        DialogManager.isDialogSavingPothole = isDialogSavingPothole;
    }
    public static void showDialogSavePothole(Context context,double longtitude,
                                             double latitude,String severity,SavePotholeSatusCallBack callBack){
        Dialog dialog=createDialog(context,R.layout.custom_dialog_save_pothole,true);

        NetworkManager networkManager=new NetworkManager(context);

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

        setIsDialogSavingPothole(true);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!networkManager.isNetworkAvailable()){
                    Snackbar.make(dialog.findViewById(R.id.main),
                            context.getString(R.string.str_network_unavailable),
                            Snackbar.LENGTH_LONG).show();
                    dialog.dismiss();
                    setIsDialogSavingPothole(false);
                    return ;
                }
                dialog.dismiss();
                List<Double> coordinates=new ArrayList<>();
                coordinates.add(longtitude);
                coordinates.add(latitude);

                APIManager.callAddPothole(new AddPotholeReq(
                        new LocationClass(coordinates),
                        LocalDataManager.getEmail(context),
                        severity)
                        ,new APICallBack<APIResponse<User>>() {
                            @Override
                            public void onSuccess(Response<APIResponse<User>> response) {
                                callBack.onComplete(true);
                                Intent intent=new Intent("com.example.SAVE_POTHOLE");
                                context.sendBroadcast(intent);
                            }

                            @Override
                            public void onError(APIResponse<User> errorResponse) {
                                DialogManager.showDialogError(context, errorResponse);
                                callBack.onComplete(false);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.e("API Error", "Failure: " + t.getMessage());
                                callBack.onComplete(false);
                                DialogManager.showDialogErrorString(context,context.getString(R.string.str_cannot_save_pothole_due_to_poor_connection));

                            }
                        });
                setIsDialogSavingPothole(false);

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callBack.onComplete(false);
                setIsDialogSavingPothole(false);
            }
        });
    }
    public static void showDialogWarningThenFinish(Context context) {
        Dialog dialog = createDialog(context, R.layout.custom_dialog_warning, false);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = (Activity) context;
                activity.finish();
            }
        },2000);
    }
    public static void showDialogPotholeWarning(Context context, StopShowDialog stopShowDialog) {
        Dialog dialog = createDialog(context, R.layout.custom_dialog_pothole_warning, false);
        Button button=dialog.findViewById(R.id.btnConfirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopShowDialog.onStopShowDialog(true);
                isDialogWarningPothole =false;
                dialog.dismiss();
            }
        });
        isDialogWarningPothole =true;
        dialog.show();

    }

    public static boolean isIsDialogWarningPothole() {
        return isDialogWarningPothole;
    }

    public static void setIsDialogWarningPothole(boolean isDialogWarningPothole) {
        DialogManager.isDialogWarningPothole = isDialogWarningPothole;
    }
}
