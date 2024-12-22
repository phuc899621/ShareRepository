package com.example.potholeapplication.class_pothole.other;

import com.example.potholeapplication.R;
import com.google.gson.annotations.SerializedName;

public class Ranking {
    @SerializedName("totalReport")
    private int totalReport;
    @SerializedName("username")
    private String username;
    @SerializedName("name")
    private String name;

    private boolean isNew=true;

    public int getColorID() {
        switch (ranking){
            case 1:{
                return R.color.light_yellow;
            }
            case 2:{
                return R.color.gray;
            }
            case 3:{
                return R.color.copper;
            }
        }
        return R.color.white;
    }
    public int getWidth(String usernameCurrent){
        if(username.equals(usernameCurrent)){
            return 5;
        }
        return 1;
    }
    public int getWidthColor(String usernameCurrent){
        if(username.equals(usernameCurrent)){
            return R.color.green;
        }
        return R.color.gray;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ranking(int totalReport, String username, String name) {
        this.totalReport = totalReport;
        this.username = username;
        this.name = name;
    }

    private int ranking;

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public Ranking(int totalReport, String username) {
        this.totalReport = totalReport;
        this.username = username;
    }

    public int getTotalReport() {
        return totalReport;
    }

    public void setTotalReport(int totalReport) {
        this.totalReport = totalReport;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
