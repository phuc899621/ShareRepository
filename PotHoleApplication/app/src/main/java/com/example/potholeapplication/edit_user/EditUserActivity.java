package com.example.potholeapplication.edit_user;

import android.content.Context;
import android.content.Intent;
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
import com.example.potholeapplication.Retrofit2.UserAPICallBack;
import com.example.potholeapplication.class_pothole.manager.UserAPIManager;
import com.example.potholeapplication.class_pothole.manager.DialogManager;
import com.example.potholeapplication.class_pothole.manager.LocalDataManager;
import com.example.potholeapplication.class_pothole.manager.LocaleManager;
import com.example.potholeapplication.class_pothole.other.User;
import com.example.potholeapplication.class_pothole.request.EditInfoReq;
import com.example.potholeapplication.class_pothole.response.UserResponse;
import com.example.potholeapplication.class_pothole.request.EmailReq;
import com.example.potholeapplication.databinding.ActivityEditUserBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
        binding.etName.setText(LocalDataManager.getName(this));
        binding.etEmail.setText(LocalDataManager.getEmail(this));
        binding.etUsername.setText(LocalDataManager.getUsername(this));
    }
    public void callSaveInfoAPI(){

        //lay du lieu
        String newUsername=binding.etUsername.getText().toString().trim();
        String newName=binding.etName.getText().toString().trim();
        if(newUsername.isEmpty() || newName.isEmpty()){
            DialogManager.showDialogErrorString(context,getString(R.string.str_enter_name_and_username));
            return;
        }
        UserAPIManager.callEditInfo(
                LocalDataManager.getEmail(this)
                ,new EditInfoReq(newUsername,newName)
                ,new UserAPICallBack() {
                    @Override
                    public void onSuccess(Response<UserResponse> response) {
                        LocalDataManager.saveUsernameName(context,newUsername,newName);
                        CallSaveImageAPI();
                    }

                    @Override
                    public void onError(UserResponse errorResponse) {
                        DialogManager.showDialogError(context, errorResponse);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API Error", "Failure: " + t.getMessage());
                        throw new RuntimeException(t);
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
        UserAPIManager.callSaveImage(emailReq,imagePart
                ,new UserAPICallBack() {
                    @Override
                    public void onSuccess(Response<UserResponse> response) {
                        LocalDataManager.saveImageBytesToSharedPreferences(context,imageBytes);
                        DialogManager.showDialogOkeThenFinish(context,getString(R.string.str_save_successfully));
                    }

                    @Override
                    public void onError(UserResponse errorResponse) {
                        DialogManager.showDialogError(context, errorResponse);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API Error", "Failure: " + t.getMessage());
                        throw new RuntimeException(t);
                    }
                });
    }
    public void CallGetImageAPI() {
        String email= LocalDataManager.getEmail(context);
        if(email.isEmpty()){
            DialogManager.showDialogErrorString(context,getString(R.string.str_email_not_found));
            return;
        }

        UserAPIManager.callFindImage(new EmailReq(email)
                ,new UserAPICallBack() {
                    @Override
                    public void onSuccess(Response<UserResponse> response) {
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

                    @Override
                    public void onError(UserResponse errorResponse) {
                        DialogManager.showDialogError(context, errorResponse);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API Error", "Failure: " + t.getMessage());
                        throw new RuntimeException(t);
                    }
                });
    }


}