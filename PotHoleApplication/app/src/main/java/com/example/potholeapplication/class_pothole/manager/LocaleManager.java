package com.example.potholeapplication.class_pothole.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleManager {
    public static void updateNewLanguage(Context context,String language){
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        LocalDataManager.saveLanguage(context,language);
    }
    public static Context updateLanguage(Context context){
        String language= LocalDataManager.getLanguage(context);
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,dm);
        return context;
    }
    public static void updateNewLanguageThenReload(
            Context context,String newLanguage,Class<?> next
    ){
        LocaleManager.updateNewLanguage(context,newLanguage);
        Resources resources = context.getResources();
        @SuppressLint({"NewApi", "LocalSuppress"}) String config =
                resources.getConfiguration().getLocales().get(0).getLanguage();

        Intent intent = new Intent(context, next);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity)context).finish();
    }
}
