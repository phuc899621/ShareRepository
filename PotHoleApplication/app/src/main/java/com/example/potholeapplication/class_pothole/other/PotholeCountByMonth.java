package com.example.potholeapplication.class_pothole.other;

import com.google.gson.annotations.SerializedName;

public class PotholeCountByMonth {
    @SerializedName("jan")
    private PotholeCount jan;
    @SerializedName("feb")
    private PotholeCount feb;
    @SerializedName("mar")
    private PotholeCount mar;
    @SerializedName("apr")
    private PotholeCount apr;
    @SerializedName("may")
    private PotholeCount may;
    @SerializedName("jun")
    private PotholeCount jun;
    @SerializedName("jul")
    private PotholeCount jul;
    @SerializedName("aug")
    private PotholeCount aug;
    @SerializedName("sep")
    private PotholeCount sep;
    @SerializedName("oct")
    private PotholeCount oct;
    @SerializedName("nov")
    private PotholeCount nov;
    @SerializedName("dec")
    private PotholeCount dec;

    public PotholeCountByMonth(
            PotholeCount jan, PotholeCount feb, PotholeCount mar,
            PotholeCount apr, PotholeCount may, PotholeCount jun,
            PotholeCount jul, PotholeCount aug, PotholeCount sep,
            PotholeCount oct, PotholeCount nov, PotholeCount dec) {
        this.jan = jan;
        this.feb = feb;
        this.mar = mar;
        this.apr = apr;
        this.may = may;
        this.jun = jun;
        this.jul = jul;
        this.aug = aug;
        this.sep = sep;
        this.oct = oct;
        this.nov = nov;
        this.dec = dec;
    }

    public PotholeCount getJan() {
        return jan;
    }

    public void setJan(PotholeCount jan) {
        this.jan = jan;
    }

    public PotholeCount getFeb() {
        return feb;
    }

    public void setFeb(PotholeCount feb) {
        this.feb = feb;
    }

    public PotholeCount getMar() {
        return mar;
    }

    public void setMar(PotholeCount mar) {
        this.mar = mar;
    }

    public PotholeCount getApr() {
        return apr;
    }

    public void setApr(PotholeCount apr) {
        this.apr = apr;
    }

    public PotholeCount getMay() {
        return may;
    }

    public void setMay(PotholeCount may) {
        this.may = may;
    }

    public PotholeCount getJun() {
        return jun;
    }

    public void setJun(PotholeCount jun) {
        this.jun = jun;
    }

    public PotholeCount getJul() {
        return jul;
    }

    public void setJul(PotholeCount jul) {
        this.jul = jul;
    }

    public PotholeCount getAug() {
        return aug;
    }

    public void setAug(PotholeCount aug) {
        this.aug = aug;
    }

    public PotholeCount getSep() {
        return sep;
    }

    public void setSep(PotholeCount sep) {
        this.sep = sep;
    }

    public PotholeCount getOct() {
        return oct;
    }

    public void setOct(PotholeCount oct) {
        this.oct = oct;
    }

    public PotholeCount getNov() {
        return nov;
    }

    public void setNov(PotholeCount nov) {
        this.nov = nov;
    }

    public PotholeCount getDec() {
        return dec;
    }

    public void setDec(PotholeCount dec) {
        this.dec = dec;
    }
}
