package com.example.pavelevtesttask;


public class TelemetricDate {
    private int sinhromarker;
    private int counterPackages;
    private double time;
    private double date;
    int CheckSumm;

    public void setSinhromarker(int sinhromarker) {
        this.sinhromarker = sinhromarker;
    }

    public void setCounterPackages(int counterPackages) {
        this.counterPackages = counterPackages;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setDate(double date) {
        this.date = date;
    }

    public void setCheckSumm(int checkSumm) {
        CheckSumm = checkSumm;
    }

    public int getSinhromarker() {
        return sinhromarker;
    }

    public int getCounterPackages() {
        return counterPackages;
    }

    public double getTime() {
        return time;
    }

    public double getDate() {
        return date;
    }

    public int getCheckSumm() {
        return CheckSumm;
    }
    public TelemetricDate(int sinhromarker, int counterPackages, double time, double date, int checkSumm) {
        this.sinhromarker = sinhromarker;
        this.counterPackages = counterPackages;
        this.time = time;
        this.date = date;
        CheckSumm = checkSumm;
    }
    public TelemetricDate() {
    }
}