package com.example.selenatabbara.group8;
import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;


import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.Arrays;

public class location extends AppCompatActivity {




    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    int largest = -200;
    int larger = -200;
    int large= -200;
    int indb0 = 8;
    int indb1 = 8;
    int indb2 = 8;
    double currDistance = 0;
    double[] location = new double[]{0,0};
    double[][] locationPoints = new double [2][5];
    double[] finalPosition = new double[] {0,0};
    double[] outfinalPosition = new double[] {0,0};

    int counter = 0;
    public double getx() { return  Math.round(10 * (outfinalPosition[0])) / 10; }

    public double gety() { return  Math.round(10 * (outfinalPosition[1])) / 10; }



    public void init (BluetoothManager bm, BluetoothAdapter ba, BluetoothLeScanner bs) {

        this.btManager = bm;
        this.btAdapter = ba;
        this.btScanner = bs;

    }
    public void Scan() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }


    class beaconDetails {
        double x;
        double y;
        String id;
        int rssi;
        double r;

        beaconDetails(double x, double y, String id, int rssi, int r) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.rssi = rssi;
            this.r = r;
        }

        public void setRssi (int rssi){
            this.rssi = rssi;
        }

        public void  setDistance (double r){
            this.r = r;
        }
    }
//    beaconDetails beacon0 = new beaconDetails(0,0, "90",-100);
//    beaconDetails beacon1 = new beaconDetails(-2.5, 5,"90",-100);
//    beaconDetails beacon2 = new beaconDetails(2.5,5,"88",-100);
//    beaconDetails beacon3 = new beaconDetails(5,0,"",-100);
//    beaconDetails beacon4 = new beaconDetails(2.5,-5,"",-100);
//    beaconDetails beacon5 = new beaconDetails(-2.5,-5, "", -100);
//    beaconDetails beacon6 = new beaconDetails(-5,0,"",-100);

    beaconDetails[] beacons = new beaconDetails[]{new beaconDetails(5, 5, "90", -100,0), new beaconDetails(2.5, 10, "96", -100,0), new beaconDetails(7.5, 10, "88", -100,0),  new beaconDetails(10,0,"69",-100,0), new beaconDetails(7.5,0,"A5",-100,0),new beaconDetails(2.5,0, "BA", -100,0),new beaconDetails(0,5,"9E",-100, 0)};


    public double distance (int rssiValue){
        return Math.pow(10, (-59-rssiValue)/(10*2.55));
    }



    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String[] beaconAd = new String[]{"90", "96", "88", "69", "A5", "BA", "9E"};
            String currentId = result.getDevice().getAddress().substring(15, 17);
            System.out.println(result.getDevice().getAddress().substring(0, 14));
            if ((result.getDevice().getAddress().substring(0, 14)).equals("0C:F3:EE:B8:0D")) {
                if (Arrays.asList(beaconAd).contains(result.getDevice().getAddress().substring(15, 17))) {
                    for (int i = 0; i < 7; i++) {
                        if (currentId.equals(beacons[i].id)) {
                            beacons[i].setRssi(result.getRssi());
                            currDistance = distance(result.getRssi());
                            beacons[i].setDistance(currDistance);

                            //   peripheralTextView.append("Device Address: " + result.getDevice().getAddress() + " rssi: " + result.getRssi() + "\n");
                        }
                        //  peripheralTextView.append("Device Address: " + result.getDevice().getAddress() + " rssi: " + result.getRssi() + "\n");
                    }
                    for (int i = 0; i < 7; i++) {
                        if (beacons[i].rssi > largest) {
                            largest = beacons[i].rssi;
                            indb0 = i;
                        }
                    }

                    for (int j = 0; j < 7; j++) {
                        if ((beacons[j].rssi > larger) && (j != indb0)) {
                            larger = beacons[j].rssi;
                            indb1 = j;
                        }
                    }
                    for (int k = 0; k < 7; k++) {
                        if ((beacons[k].rssi > large) && (k != indb1) && (k != indb0)) {
                            large = beacons[k].rssi;
                            indb2 = k;
                        }
                    }
                    if (largest > -200 && larger > -200 && large > -200) {
                        double[][] positions = new double[][]{{beacons[indb0].x, beacons[indb0].y}, {beacons[indb1].x, beacons[indb1].y}, {beacons[indb2].x, beacons[indb2].y}};
                        double[] distances = new double[]{beacons[indb0].r, beacons[indb1].r, beacons[indb2].r};
                        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
                        LeastSquaresOptimizer.Optimum optimum = solver.solve();
                        location = optimum.getPoint().toArray();
                    }
                    if (largest > -200 && larger > -200 && large > -200 && location[0] >= 0 && (location[0] <= 10) && location[1] <= 10 && location[1] >= 0) {
                        if (Arrays.asList(beaconAd).contains("88")) {
                            if (Arrays.asList(beaconAd).contains("96")) {
                                if (location[0] <= 7.5 && location[1] >= 5 && location[0] >= 2.5) {
                                    locationPoints[0][counter] = location[0];
                                    locationPoints[1][counter] = location[1];
                                    counter++;
                                }
                            } else if (Arrays.asList(beaconAd).contains("69")) {
                                if (location[0] >= 5 && location[1] >= 5) {
                                    locationPoints[0][counter] = location[0];
                                    locationPoints[1][counter] = location[1];
                                    counter++;
                                }
                            }
                        }
                        if (Arrays.asList(beaconAd).contains("A5")) {
                            if (Arrays.asList(beaconAd).contains("69")) {
                                if (location[1] <= 5 && location[0] >= 5) {
                                    locationPoints[0][counter] = location[0];
                                    locationPoints[1][counter] = location[1];
                                    counter++;
                                }
                            } else if (Arrays.asList(beaconAd).contains("BA")) {
                                if (location[0] >= 2.5 && location[1] <= 5 && location[0] <= 7.5) {
                                    locationPoints[0][counter] = location[0];
                                    locationPoints[1][counter] = location[1];
                                    counter++;
                                }
                            }
                        }
                        if (Arrays.asList(beaconAd).contains("9E")) {
                            if (Arrays.asList(beaconAd).contains("BA")) {
                                if (location[1] <= 5 && location[0] <= 5) {
                                    locationPoints[0][counter] = location[0];
                                    locationPoints[1][counter] = location[1];
                                    counter++;
                                }
                            } else if (Arrays.asList(beaconAd).contains("96")) {
                                if (location[1] >= 5 && location[0] <= 5) {
                                    locationPoints[0][counter] = location[0];
                                    locationPoints[1][counter] = location[1];
                                    counter++;
                                }
                            }
                        }
                        largest = -200;
                        larger = -200;
                        large = -200;
                        indb0 = 8;
                        indb1 = 8;
                        indb2 = 8;
                        location[0] = 0;
                        location[1] = 0;
                    }
                    if (counter == 5) {
                        counter = 0;
                        for (int i = 0; i < 5; i++) {

                            finalPosition[0] = locationPoints[0][i] + finalPosition[0];
                            finalPosition[1] = locationPoints[1][i] + finalPosition[1];
                        }
                        finalPosition[0] = finalPosition[0] / 5;
                        finalPosition[1] = finalPosition[1] / 5;
                        if (finalPosition[0] < 0.001) {
                            finalPosition[0] = 0;
                        }
                        if (finalPosition[1] < 0.001) {
                            finalPosition[1] = 0;
                        }
                        outfinalPosition[0] = finalPosition[0];
                        outfinalPosition[1] = finalPosition[1];
//                        finalPosition[0] = 0;
//                        finalPosition[1] = 0;
                    }
                    // auto scroll for text view

                }
            }
            stopScanning();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
            }
        }
    }



}


