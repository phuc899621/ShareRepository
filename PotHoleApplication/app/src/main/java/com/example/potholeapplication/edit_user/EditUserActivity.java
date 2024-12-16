package com.example.potholeapplication.edit_user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.potholeapplication.R;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.response.User;
import com.example.potholeapplication.class_pothole.request.EditInfoReq;
import com.example.potholeapplication.Retrofit2.RetrofitServices;
import com.example.potholeapplication.class_pothole.response.ApiResponse;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.databinding.ActivityEditUserBinding;
import com.example.potholeapplication.Retrofit2.APIInterface;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {
    ActivityEditUserBinding binding;
    final int PICK_IMAGE=1;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityEditUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context=this;
        setClickEvent();
        CallGetImageAPI();
        setImageInfo();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //cai dat thong tin user
        setUserInfo();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateLanguage(newBase));
    }
    public void setImageInfo(){
        binding.imaUser.setImageBitmap(LocalDataManager.getImageBitmapFromSharePreferences(context));
    }

    public void setClickEvent() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSaveInfoAPI();
            }
        });
        binding.etEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditUserActivity.this,EditEmailActivity.class);
                startActivity(intent);
            }
        });
        binding.etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditUserActivity.this,EditPasswordActivity.class);
                startActivity(intent);
            }
        });
        binding.btnChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                binding.imaUser.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private byte[] getImageBytesFromImageView() {
        Bitmap bitmap = ((BitmapDrawable) binding.imaUser.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public void setUserInfo(){
        SharedPreferences sharedPreferences=getSharedPreferences("user_info",MODE_PRIVATE);
        binding.etName.setText(sharedPreferences.getString("name",""));
        binding.etEmail.setText(sharedPreferences.getString("email",""));
        binding.etUsername.setText(sharedPreferences.getString("username",""));
    }
    public void callSaveInfoAPI(){
        SharedPreferences sharedPreferences=getSharedPreferences("user_info",MODE_PRIVATE);
        String email=sharedPreferences.getString("email","");

        //lay du lieu
        String newUsername=binding.etUsername.getText().toString().trim();
        String newName=binding.etName.getText().toString().trim();
        if(newUsername.isEmpty() || newName.isEmpty()){
            DialogManager.showDialogErrorString(context,getString(R.string.str_enter_name_and_username));
            return;
        }
        EditInfoReq editInfoReq =new EditInfoReq(newUsername,newName);
        APIInterface apiService = RetrofitServices.getApiService();

        Call<ApiResponse> call = apiService.callEditInfo(email, editInfoReq);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    //luu thong tin vao dien thoai
                    LocalDataManager.saveUsernameName(context,newUsername,newName);
                    CallSaveImageAPI();
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
    public void CallSaveImageAPI() {
        byte[] imageBytes = getImageBytesFromImageView();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestBody);
        String email= LocalDataManager.getEmail(context);
        if(email.isEmpty()){
            DialogManager.showDialogErrorString(context,getString(R.string.str_email_not_found));
            return;
        }
        // Tạo RequestBody cho email và task
        RequestBody emailReq = RequestBody.create(MediaType.parse("text/plain"), email);
        APIInterface apiServices=RetrofitServices.getApiService();
        Call<ApiResponse> call = apiServices.callSaveImage(emailReq,imagePart);

        // Gửi yêu cầu
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    LocalDataManager.saveImageBytesToSharedPreferences(context,imageBytes);
                    DialogManager.showDialogOkeThenFinish(context,getString(R.string.str_save_successfully));
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
    public void CallGetImageAPI() {
        String email= LocalDataManager.getEmail(context);
        if(email.isEmpty()){
            DialogManager.showDialogErrorString(context,getString(R.string.str_email_not_found));
            return;
        }
        EmailReq emailReq=new EmailReq(email);
        APIInterface apiServices=RetrofitServices.getApiService();
        Call<ApiResponse> call = apiServices.callFindImage(emailReq);
        // Gửi yêu cầu
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    // Kiểm tra nếu có hình ảnh
                    List<User> data = response.body().getData();
                    if (data != null && !data.isEmpty()) {
                        String imageBase64 = data.get(0).getImage();
                        if (imageBase64 != null) {
                            // Giải mã Base64 thành byte[]
                            byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                            LocalDataManager.saveImageBytesToSharedPreferences(context,decodedBytes);
                        }
                    }
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