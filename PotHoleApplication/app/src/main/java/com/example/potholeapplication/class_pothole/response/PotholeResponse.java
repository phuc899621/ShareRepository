package com.example.potholeapplication.class_pothole.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PotholeResponse {

   @SerializedName("success")
   private boolean success=false;
   @SerializedName("message")
   private String Message;
   @SerializedName("data")
   private List<Pothole> data;
   public PotholeResponse() {
      this.success = false;
      this.Message = "";
      this.data = new ArrayList<>();
   }

   public PotholeResponse(boolean success, String message, List<Pothole> data) {
      this.success = success;
      Message = message;
      this.data = data;
   }

   public boolean isSuccess() {
      return success;
   }

   public void setSuccess(boolean success) {
      this.success = success;
   }

   public String getMessage() {
      return Message;
   }

   public void setMessage(String message) {
      Message = message;
   }

   public List<Pothole> getData() {
      return data;
   }

   public void setData(List<Pothole> data) {
      this.data = data;
   }
}
