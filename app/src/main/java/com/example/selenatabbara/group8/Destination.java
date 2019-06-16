package com.example.selenatabbara.group8;

public class Destination {
    String id;
    //    double [] corner1;
//    double [] corner2;
//    double [] corner3;
//    double [] corner4;
    int []coordinates;
    int beaconindex1;
    int beaconindex2;
    double minRSSIBeacon1;
    double minRSSIBeacon2;



    public Destination(String id){
        this.id=id;
        if(id.equals("Gate1")) init (new int [] {10,24},2, -1);
        if(id.equals("Gate2")) init (new int [] {9,35},1, -1);
        if(id.equals("Gate3")) init (new int [] {16,40},4, -1);
        if(id.equals("Security Check")||id.equals("UK Boarders")) init (new int [] {16,9},3, -1);
        if(id.equals("Luggage1")) init (new int [] {8,1},5, -1);
        if(id.equals("Luggage2")) init (new int [] {8,6},6, -1);
        if(id.equals("Luggage Deposit")) init (new int [] {8,4},5, 6);
        if(id.equals("Exit")) init (new int [] {0,16},0, -1);
    }

    public void initparameters (double configparameterb1, double configparameterb2){
        this.minRSSIBeacon1=configparameterb1;
        this.minRSSIBeacon2=configparameterb2;
    }

    private void init( int[] coordinates, int b1, int b2){
        this.coordinates=coordinates;
        this.beaconindex1=b1;
        this.beaconindex2=b2;
    }

    public boolean isarrived (int RSSI_B1, int RSSI_B2){
        if (RSSI_B1>=minRSSIBeacon1|| RSSI_B2>=minRSSIBeacon2) return true;
        else return false;
    }


    public int getBeaconindex1() { return beaconindex1; }

    public int getBeaconindex2() { return beaconindex2; }

    public String getId() { return id; }

    public int [] getCoordinates() { return coordinates; }

}
