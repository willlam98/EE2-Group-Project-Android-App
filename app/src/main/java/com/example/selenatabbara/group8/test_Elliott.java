package com.example.selenatabbara.group8;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.TextView;
import com.example.selenatabbara.group8.Cell;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicStampedReference;

import static java.lang.Thread.sleep;

public class test_Elliott extends AppCompatActivity {

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test__elliott);

        init();
        startScanning();
//        Handler handler = new Handler();
//        boolean loop = true;

//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Scan();
////                        int myx = mylocation[0];
////                        int myy = mylocation[1];
//                        EditText x = (EditText) findViewById(R.id.x);
//                        x.setText("-1");
////
//                        EditText y = (EditText) findViewById(R.id.y);
//                        y.setText("-1");
//                        x = (EditText) findViewById(R.id.x);
//                        int myx = Integer.parseInt(x.getText().toString());
//                        y = (EditText) findViewById(R.id.y);
//                        int myy = Integer.parseInt(y.getText().toString());
//                        TextView txtView4 = (TextView) findViewById(R.id.print);
//                        txtView4.setText("x=" + myx + ", y=" + myy);
////                        myx+1;
//                        if ((mylocation[0] != oldloc[0]) || (mylocation[1] != oldloc[1])) {
//                            Astar aStar = new Astar(10, 10, 0, 2, 7, 8,
//                                    new int[][]{
//                                            {0, 4}, {2, 2}, {3, 1}, {3, 3}, {2, 1}, {2, 3}
//                                    });
//                            int[] location1 = aStar.run();
//                            TextView txtView5 = (TextView) findViewById(R.id.print2);
//                            txtView5.setText("x=" + myx + ", y=" + myy);
//                            oldloc = mylocation;
//                        }
//                    }
//                });
//            }
//        };
//        thread.start();
//

    }



//





    public void runAstar(int myx, int myy){
        Astar aStar = new Astar(10, 10, myx, myy, 7, 8);
                            int location1 = aStar.run();

    }

    public void init(){
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();


        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
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
        return Math.pow(10, (-60-rssiValue)/(10*2.75));
    }


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
    int counter = 0;
    private ScanCallback leScanCallback;


    {
        leScanCallback = new ScanCallback() {
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
                            TextView txtView1 = (TextView) findViewById(R.id.Print);
                            txtView1.setText((Math.round(10 * (finalPosition[0])) / 10) + " " + (int) (Math.round(10 * (finalPosition[1])) / 10));
                            runAstar((int) (Math.round(10 * (finalPosition[0])) / 10), (int) (Math.round(10 * (finalPosition[1])) / 10));
                            finalPosition[0] = 0;
                            finalPosition[1] = 0;
                        }
                    }
                }
            }
        };
    }

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


    public void startScanning() {
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

}