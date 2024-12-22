package com.example.potholeapplication.class_pothole.manager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import com.example.potholeapplication.class_pothole.other.Pothole;
import com.example.potholeapplication.class_pothole.other.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocalDataManager {

    /*File: user_info
    __image
    __name
    __username
    __email
    __login [true/false]
    File: language
    __language [vi/en]
    File: realtime_detection
    __enable [true/false]
    File: subinfo
    __totalReport
    __totalDistances
    __totalFixedPothole
    */
    /*------------------------------------------------------------------------|
    |--------------------------------Image------------------------------------|
    | ------------------------------------------------------------------------*/
    private static final String userPef="user_info";
    private static final String langPef="language";
    private static final String subinfoPef="subinfo";
    private static final String realtimePef="realtime_detection";
    private static final String potholePef="pothole";
    public static byte[] getImageBytes(Context context) {
        SharedPreferences sharedPreferences=context.getSharedPreferences(userPef, MODE_PRIVATE);
        String base64Image = sharedPreferences.getString("image", null);
        if (base64Image != null) {
            return Base64.decode(base64Image, Base64.DEFAULT);
        }
        return null;
    }
    public static void saveImageBytes(Context context, byte[] imageBytes) {
        SharedPreferences sharedPreferences=context.getSharedPreferences(userPef,MODE_PRIVATE);
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("image", base64Image);
        editor.apply();
    }
    public static Bitmap getImageBitmap(Context context){
        byte[] imageBytes= getImageBytes(context);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
    public static byte[] drawableToByteArray(Context context, int drawableId) {
        // Get drawable bang ID sau do chuyen thanh bitmap
        Drawable drawable = context.getResources().getDrawable(drawableId, null);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Chuyen thanh byte
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    /*------------------------------------------------------------------------|
    |--------------------------------user_info--------------------------------|
    | ------------------------------------------------------------------------*/
    public static void saveUsernameName(Context context, String username,String name){
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                userPef,MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",username);
        editor.putString("name",name);
        editor.apply();
    }



    public static String getEmail(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                userPef,MODE_PRIVATE
        );
        return sharedPreferences.getString("email","");
    }
    public static String getUsername(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                userPef,MODE_PRIVATE
        );
        return sharedPreferences.getString("username","");
    }
    public static String getName(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                userPef,MODE_PRIVATE
        );
        return sharedPreferences.getString("name","");
    }
    public  static void saveEmail(Context context,String email){
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                userPef,MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("email",email);
        editor.apply();
    }
    public static void saveUser(Context context, List<User> userList){
        User user=userList.get(0);
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                userPef,MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",user.getUsername());
        editor.putString("email",user.getEmail());
        editor.putString("name",user.getName());
        editor.putBoolean("login",true);
        editor.apply();
    }
    public static void saveLogin(Context context,boolean isLogin){
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                userPef,MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("login",isLogin);
        editor.apply();
    }
    /*------------------------------------------------------------------------|
    |--------------------------------language---------------------------------|
    | ------------------------------------------------------------------------*/
    public static String getLanguage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                langPef, MODE_PRIVATE
        );
        if (!sharedPreferences.contains("language")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language", "en");
            editor.apply();
            return "en";
        }
        return sharedPreferences.getString("language","en");

    }
    public static void saveLanguage(Context context, String language) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                langPef, MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.apply();

    }
    /*------------------------------------------------------------------------|
    |---------------------------realtime_detection----------------------------|
    | ------------------------------------------------------------------------*/
    public static boolean getEnableRealTimeDetection(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                realtimePef, MODE_PRIVATE
        );
        if (!sharedPreferences.contains("enable")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("enable", "false");
            editor.apply();
            Log.d("Enable",sharedPreferences.getString("enable", "false"));
            return false;
        }
        if(sharedPreferences.getString("enable", "false").equals("true")){
            Log.d("Enable",sharedPreferences.getString("enable", "false"));
            return true;
        }else{
            Log.d("Enable",sharedPreferences.getString("enable", "false"));
            return  false;
        }

    }
    public static boolean setEnableRealTimeDetection(Context context,Boolean enable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                realtimePef, MODE_PRIVATE
        );
        if (!sharedPreferences.contains("enable")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("enable", "false");
            editor.apply();
            return false;
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("enable", enable.toString());
            editor.apply();
            return enable;
        }
    }
    /*------------------------------------------------------------------------|
    |--------------------------------subinfo----------------------------------|
    | ------------------------------------------------------------------------*/
    public static void saveSubinfo(
            Context context,float totalDistances,int totalReport,int totalFixedPothole
    ){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                subinfoPef, MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putFloat("totalDistances",totalDistances);
        editor.putInt("totalReport",totalReport);
        editor.putInt("totalFixedPothole",totalFixedPothole);
        editor.apply();
    }
    public static void saveUserRank(
            Context context,int ranking
    ){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                subinfoPef, MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("rank",ranking);
        editor.apply();
    }
    public static int getUserRank(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                subinfoPef, MODE_PRIVATE
        );
        return sharedPreferences.getInt("rank",0);
    }
    public static void saveTotalDistances(Context context,float totalDistances){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                subinfoPef, MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putFloat("totalDistances",totalDistances);
        editor.apply();
    }
    public static float getTotalDistances(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                subinfoPef, MODE_PRIVATE
        );
        return sharedPreferences.getFloat("totalDistances",0);
    }
    public static int getTotalReport(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                subinfoPef, MODE_PRIVATE
        );
        return sharedPreferences.getInt("totalReport",0);
    }
    public static int getTotalFixedPothole(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                subinfoPef, MODE_PRIVATE
        );
        return sharedPreferences.getInt("totalFixedPothole",0);
    }
    /*------------------------------------------------------------------------|
    |--------------------------------pothole----------------------------------|
    | ------------------------------------------------------------------------*/
    public static void savePotholeList(Context context, List<Pothole> potholes) {
        Gson gson = new Gson();
        String json = gson.toJson(potholes);
        // Lấy SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(potholePef, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Chuyển JSONArray thành String
        editor.putString("pothole", json);
        editor.apply(); // Lưu vào SharedPreferences
    }
    public static List<Pothole> getPotholeList(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(potholePef, Context.MODE_PRIVATE);
        if(!sharedPreferences.contains("pothole")){
            return new ArrayList<>();
        }
        String json=sharedPreferences.getString("pothole","");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Pothole>>() {}.getType();
        return gson.fromJson(json, type);
    }

}
