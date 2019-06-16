package com.example.selenatabbara.group8;

public class AccessPoint {
    double x;
    double y;
    int rssi;
    String bssid;
    String ssid;
    int configparameter;// RSSI value when distance = 1m;
    double evmfactor;
    double distance;

    public AccessPoint(double x, double y, int rssi, String bssid, String ssid, int configparameter, double distance, double evmfactor){
        this.x=x;
        this.y=y;
        this.rssi=rssi;
        this.bssid=bssid;
        this.ssid=ssid;
        this.configparameter=configparameter;
        this.distance=distance;
        this.evmfactor=evmfactor;
    }

    public void setRSSI (int rssi) {this.rssi=rssi;}
    public void setDistance (double distance) {this.distance=distance;}

    public int getRSSI() { return rssi; }
    public int getConfigParameter() { return configparameter; }

    public double getEvmfactor() { return evmfactor; }

    public double getDistance() { return distance; }
    public String getBSSID() { return bssid; }
    public String getSSID() { return ssid; }

    public double getX() { return x; }

    public double getY() { return y; }
}
