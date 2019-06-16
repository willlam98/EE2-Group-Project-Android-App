package com.example.selenatabbara.group8;

public class Beacon {
    int x;
    int y;
    String id;
    int rssi;
    double configparameter;
    int test;
    int average;

    public Beacon (int x, int y, String id, int rssi, double configparameter) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.rssi = rssi;
        this.configparameter=configparameter;
        this.test=0;
        average=0;
    }

    public void setRssi (int rssi){
        this.rssi = rssi;
    }

    public int getX() { return x; }

    public int getY() { return y; }

    public int getRssi() { return rssi; }

    public void Testing (int sample) {
        this.test++;
        average=average+sample;
    }

    public double getAverage() {
        if (test==0)return -200;
        else {
            double out = this.average / this.test;
            this.average = 0;
            this.test = 0;
            return (out);
        }
    }

    public int getTest() { return test; }

    public String getId() { return id; }
}
