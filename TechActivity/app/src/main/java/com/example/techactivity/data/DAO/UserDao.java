package com.example.techactivity.data.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.techactivity.data.Entities.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM User")//chon tat ca user (*) trong bang user
    List<User> getAll();
    
}
