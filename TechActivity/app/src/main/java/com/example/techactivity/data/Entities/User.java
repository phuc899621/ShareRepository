package com.example.techactivity.data.Entities;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.auto.value.AutoValue;

@AutoValue
@Entity
public abstract class User {
    // Supported annotations must include `@CopyAnnotations`.
    //Tạo bằng User user=User.create(id,firstName,lastName);
    @AutoValue.CopyAnnotations
    @PrimaryKey
    public abstract long getId();//gọi bằng user.getId()

    public abstract String getFirstName();
    public abstract String getLastName();

    // Phải có 1 phương thức tao
    //Lớp mới kế thừa User tên AutoValue_[ten lớp abstract] tự đông
    public static User create(long id, String firstName, String lastName) {
        return new AutoValue_User(id, firstName, lastName);
    }
}