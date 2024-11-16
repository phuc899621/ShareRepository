package com.example.potholeapplication.class_pothole;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class DataEditor {
    public static byte[] getImageBytesFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("user_info", MODE_PRIVATE);
        String base64Image = sharedPreferences.getString("image", null);
        if (base64Image != null) {
            return Base64.decode(base64Image, Base64.DEFAULT);
        }
        return null;
    }
    public static void saveImageBytesToSharedPreferences(Context context,byte[] imageBytes) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("user_info",MODE_PRIVATE);
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("image", base64Image);
        editor.apply();
    }
    public static void saveUsernameName(Context context, String username,String name){
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                "user_info",MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",username);
        editor.putString("name",name);
        editor.apply();
    }
    public static Bitmap getImageBitmapFromSharePreferences(Context context){
        byte[] imageBytes=getImageBytesFromSharedPreferences(context);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public static String getEmail(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                "user_info",MODE_PRIVATE
        );
        return sharedPreferences.getString("email","");
    }
    public  static void saveEmail(Context context,String email){
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                "user_info",MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("email",email);
        editor.apply();
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
    public static void saveUserToSharePreferences(Context context, List<User> userList){
        User user=userList.get(0);
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                "user_info",MODE_PRIVATE
        );
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",user.getUsername());
        editor.putString("email",user.getEmail());
        editor.putString("name",user.getName());
        editor.putBoolean("login",true);
        editor.apply();
    }
}
