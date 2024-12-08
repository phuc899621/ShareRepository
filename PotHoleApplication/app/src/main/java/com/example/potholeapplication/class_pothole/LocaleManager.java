package com.example.potholeapplication.class_pothole;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class LocaleManager {
    public static void updateNewLanguage(Context context,String language){
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        DataEditor.saveLanguagePreferences(context,language);
    }
    public static Context updateLanguage(Context context){
        String language=DataEditor.getLanguagePreferences(context);
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,dm);
        return context;
    }
}
