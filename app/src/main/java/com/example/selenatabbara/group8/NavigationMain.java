package com.example.selenatabbara.group8;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
// test beacon number 0
///////////////////FLIGHT NUMBERS////////////////////////////
// TRAIN FLIGHT NUMBERS;
// retest wifi and ble
//take out location + destination over firebase? optional




//speech for navigation
// you are headed toward ...
//text to speech for notification // gate number is available or delay
//navigation transit
//layout
//change logo // Name .... colors // design




///////////WITH AJ://////////////
//firbase LOCATION FOR ME201
// luggage name ?
//bluetooth ...



//////////////DONE:///////////////
//STPP SPEAKING
//make it stop talking when switching activities.........
//tap to speak try again
//firbase
//use stopscan !!!!!
// +when changing activities, interrupt scanning
//change luggage thing ; name??
//add location
//add in information all next steps;
//reinitiliasing







public class NavigationMain extends AppCompatActivity {
    DatabaseReference FI;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private static final int minRSSIval = -90;

    Destination destination;
    WifiManager wifiManager;
    private TextToSpeech TTS=null;
    boolean arrived=false;
    boolean updatelocation=true;
    boolean b_init;


    private final String DEVICE_ADDRESS = "98:D3:81:FD:47:1E";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;


    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    //int mylocation[] = {-1, -1};

    AccessPoint AP0 = new AccessPoint(17, 9, -200, "98:f1:70:24:98:67", "MS", -45, -1, 2.6);
//    AccessPoint AP0 = new AccessPoint(17, 9, -200, "60:21:c0:34:9f:e6", "EdwardAP", -43, -1, 2.2);
    AccessPoint AP1 = new AccessPoint(5.85, 5.15, -200, "00:f6:63:af:be:80", "Imperial-WPA", -42, -1, 1.9);
    AccessPoint AP2 = new AccessPoint(12.3, 21.8, -200, "00:f6:63:9e:c7:90", "Imperial-WPA", -44, -1, 2.15);
    AccessPoint AP3 = new AccessPoint(9, 37.5, -200, "00:f6:63:9f:07:40", "Imperial-WPA", -44, -1, 2.5);
    //    AccessPoint AP4 = new AccessPoint(13, 0, -200, "00ae:57:75:14:60:d4", "Nokia 1", -44, -1, 2.1); //CHANGE Y
    AccessPoint AP4 = new AccessPoint(13, 0, -200, "3e:5c:f2:8b:a1:29", "iPhone (2)", -48, -1, 2.1);
//    AccessPoint AP5 = new AccessPoint(13, 30, -200, "26:18:1d:69:b6:6f", "Alan", -48, -1, 2.6);
    AccessPoint AP5 = new AccessPoint(13, 30, -200, "2e:20:0b:d3:46:c9", "WL", -48, -1, 2.6); //to be tested
    AccessPoint AP6 = new AccessPoint(17, 37, -200, "e2:a5:3e:77:16:f4", "Jumbo", -44, -1, 2.1);
    AccessPoint AP7 = new AccessPoint(13, 17, -200, "fe:2a:9c:6a:87:0f", "fish plus dragon1", -44, -1, 2.1);
    AccessPoint AP8 = new AccessPoint(17, 24, -200, "ae:57:75:14:60:d4", "Nokia 1", -44, -1, 2.1);

// AccessPoint AP5 = new AccessPoint(11.3,21.8, -65, "00:f6:63:9e:c7:90","Imperial-WPA", -41, -1);

    AccessPoint[] AP = new AccessPoint[]{AP0, AP1, AP2, AP3, AP4, AP5, AP6, AP7, AP8};

    double[] config = new double[]{-65, -70, -75, -72, -67, -65, -65};

    Beacon B0 = new Beacon(16, 0, "A5", -100, config[0]);
    //Beacon B1 = new Beacon(11, 0,"9E",-100,-1,2.1,config[1]);
//    Beacon B2 = new Beacon(11,8.2,"69",-100,-1,2.1,config[2]);
    Beacon B1 = new Beacon(8, 35, "9E", -100, config[1]);
    Beacon B2 = new Beacon(9, 23, "69", -100, config[2]);
    Beacon B3 = new Beacon(16, 9, "88", -100, config[3]);
    Beacon B4 = new Beacon(16, 42, "90", -100, config[4]);
    Beacon B5 = new Beacon(8, 0, "BA", -100, config[5]);
    Beacon B6 = new Beacon(8, 7, "96", -100, config[6]);

    Beacon[] beacons = new Beacon[]{B0, B1, B2, B3, B4, B5, B6};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);


        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);

        TextView txtView1 = (TextView) findViewById(R.id.Step1);
        txtView1.setText(Settings.getString("Step1", " "));
        TextView txtView2 = (TextView) findViewById(R.id.Step2);
        txtView2.setText(Settings.getString("Step2", " "));
        TextView txtView3 = (TextView) findViewById(R.id.Step3);
        txtView3.setText(Settings.getString("Step3", " "));
        TextView txtView4 = (TextView) findViewById(R.id.Back);
        txtView4.setText("Back");
        Setcolors();


//        if(BTinit())
//        {
//            if(BTconnect())
//            {
//                b_init=true;
//                Toast.makeText(this, "\nConnection Opened!\n", Toast.LENGTH_LONG).show();
//            }
//            else {
//                b_init=false;
//                Toast.makeText(this, "\nThe belt is not connected!\n", Toast.LENGTH_LONG).show();
//
//            }
//
//        }
//        else b_init=false;
        b_init=false;

        FI = FirebaseDatabase.getInstance().getReference(Settings.getString("FlightNumber", " "));

        long mygate = Settings.getLong("Gate_number", -1);
        TextView txtView5 = (TextView) findViewById(R.id.Gate);
        String gate ;
        if (Settings.getInt("Option", 0) == 1) {
            if (mygate == -1) {
                gate="Your gate number is not available";
            } else {
                gate="Your gate number is " + mygate;
            }
        } else {
            if (mygate == -1) {
                gate="Your luggage number is not available";

            }

            else {
                gate="Your luggage number is " + mygate;
            }
        }
        String mynextstep= Settings.getString("Step"+Settings.getInt("Step_num", 0), " ");
        if (mynextstep.equals("Go to Gate")) mynextstep="Gate";
        txtView5.setText(gate);
        TextToSpeech(gate+". You are headed to the "+ mynextstep);

        init();
//        Scan();
        startScanning();
    }

    public void initdestination(String id) {
        destination = new Destination(id);
        double cb1 = -1;
        double cb2 = -1;
        if (destination.getBeaconindex1() != -1)
            cb1 = beacons[destination.getBeaconindex1()].configparameter;
        if (destination.getBeaconindex2() != -1)
            cb2 = beacons[destination.getBeaconindex2()].configparameter;
        destination.initparameters(cb1, cb2);
    }

    //Set colors of the steps accordingto which stpe you're in

    public void Setcolors() {
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        TextView txtView1 = (TextView) findViewById(R.id.Step1);
        TextView txtView2 = (TextView) findViewById(R.id.Step2);
        TextView txtView3 = (TextView) findViewById(R.id.Step3);
        Button btn1 = findViewById(R.id.Step1_circle);
        Button btn2 = findViewById(R.id.Step2_circle);
        Button btn3 = findViewById(R.id.Step3_circle);
        initdestination(Settings.getString("Step" + Settings.getInt("Step_num", 0), " "));

        if (Settings.getInt("Step_num", 0) == 1) {
            txtView1.setTextColor(getResources().getColor(R.color.colorAccent));
            txtView2.setTextColor(getResources().getColor(R.color.colortextlight));
            txtView3.setTextColor(getResources().getColor(R.color.colortextlight));
            btn1.setBackgroundResource(R.drawable.btn_background_accent);
            btn2.setBackgroundResource(R.drawable.btn_background);
            btn3.setBackgroundResource(R.drawable.btn_background);
        } else if (Settings.getInt("Step_num", 0) == 2) {
            txtView2.setTextColor(getResources().getColor(R.color.colorAccent));
            txtView1.setTextColor(getResources().getColor(R.color.colortextlight));
            txtView3.setTextColor(getResources().getColor(R.color.colortextlight));
            btn2.setBackgroundResource(R.drawable.btn_background_accent);
            btn1.setBackgroundResource(R.drawable.btn_background);
            btn3.setBackgroundResource(R.drawable.btn_background);
        } else if (Settings.getInt("Step_num", 0) == 3) {
            txtView3.setTextColor(getResources().getColor(R.color.colorAccent));
            txtView2.setTextColor(getResources().getColor(R.color.colortextlight));
            txtView1.setTextColor(getResources().getColor(R.color.colortextlight));
            btn3.setBackgroundResource(R.drawable.btn_background_accent);
            btn2.setBackgroundResource(R.drawable.btn_background);
            btn1.setBackgroundResource(R.drawable.btn_background);
        }
    }

    public void runAstar(int myx, int myy) {
        System.out.println("dest: " + destination);
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        if (Settings.getString("Step" + Settings.getInt("Step_num", 0), " ") == "Go to Gate") {
            long mygatenumber = Settings.getLong("Gate_number", -1);
            if (mygatenumber != -1) {
                initdestination("Gate" + mygatenumber);
                runAstarinit(myx, myy, destination.getCoordinates()[0], destination.getCoordinates()[1]);
            }
        } else if (Settings.getString("Step" + Settings.getInt("Step_num", 0), " ") == "Luggage Claim") {
            long mygatenumber = Settings.getLong("Gate_number", -1);
            if (mygatenumber != -1) {
                initdestination("Luggage" + mygatenumber);
                runAstarinit(myx, myy, destination.getCoordinates()[0], destination.getCoordinates()[1]);
            }
        } else
            runAstarinit(myx, myy, destination.getCoordinates()[0], destination.getCoordinates()[1]);
    }

    public void runAstarinit(int myx, int myy, int destx, int desty) {
        TextView txtView5 = (TextView) findViewById(R.id.Gate);
        txtView5.setText("x=" + destx + ", y=" + desty);
        Astar aStar = new Astar(43, 18, myy, myx, desty, destx);
        int angle = aStar.run();
        String string = Integer.toString(angle); // WILLIAM
        string.concat("\n");
        if(b_init==true) {

            try {
                outputStream.write(string.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, ("\nSent Data:" + string + "\n"), Toast.LENGTH_LONG).show();

//        TextView txtView5 = (TextView) findViewById(R.id.print2);
//        txtView5.setText("x=" + location1[0] + ", y=" + location1[1]);
    }

    public void init() {
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
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }

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

//    public void Scan() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                 if (beacon==true) {
////                     btScanner.startScan(leScanCallback);
////                     beacon=false;
////                 }
////
//////                wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
////                if (updatelocation==true&&beacon==false) {
////                    wifiManager.startScan();
////                    beacon = true;
////                }
//                if(arrived==false) btScanner.startScan(leScanCallback);
//                Scan();
//            }
//        }, 10000);
//    }

    private void scanSuccess() {
        System.out.println("here");
        List<android.net.wifi.ScanResult> results = wifiManager.getScanResults();
        resetAP();
//        int countap = 0;
        double currDistance;
//        Toast.makeText(this, "Scanning...." + results.size(), Toast.LENGTH_SHORT).show();
        for (android.net.wifi.ScanResult result : results) {
//                int levelrange = WifiManager.calculateSignalLevel(result.level, 100);
//                Toast.makeText(this, result.SSID+"rssi: "+ levelrange +" bssid: "+result.BSSID, Toast.LENGTH_SHORT).show();
            for (int i = 0; i < AP.length; i++) {
                if (result.SSID.equals(AP[i].getSSID())) {

                    if (result.BSSID.equals(AP[i].getBSSID())) {

                        if (result.level >= minRSSIval) {
//                            countap++;
                            AP[i].setRSSI(result.level);
                            currDistance = distance(AP[i]);
                            AP[i].setDistance(currDistance);
                            System.out.println(result.SSID + "rssi: " + result.level + "Distance " + currDistance + " bssid: " + result.BSSID);
//                            if (result.BSSID.equals(AP0.getBSSID()))
//                                Toast.makeText(this, result.SSID + "rssi: " + result.level + "Distance " + currDistance + " bssid: " + result.BSSID, Toast.LENGTH_SHORT).show();


                        }
                    }
                }
            }

        }
//        Toast.makeText(this, "count= " + count, Toast.LENGTH_SHORT).show();

        LocateUser(AP);
    }

    private void scanFailure() {
//        Toast.makeText(this, "fAIL" , Toast.LENGTH_SHORT).show();

        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
//        List<ScanResult> results = wifiManager.getScanResults();
        Toast.makeText(this, "Could not update your location", Toast.LENGTH_SHORT).show();
    }

    public void resetAP() {
        for (AccessPoint APi : AP) {
            APi.setDistance(-1);
            APi.setRSSI(-200);
        }

    }

    public double distance(AccessPoint accessPoint) {
        return Math.pow(10, (accessPoint.getConfigParameter() - accessPoint.getRSSI()) / (10 * accessPoint.evmfactor));
    }


    public void LocateUser(AccessPoint[] inAP) {
//        Log.e("debug","here");
        int largeap = -200;
        int largerap = -200;
        int largestap = -200;
        int indap0 = -1;
        int indap1 = -1;
        int indap2 = -1;
        double[] locationap;


        for (int i = 0; i < inAP.length; i++) {
            double currDistance = distance(inAP[i]);
            inAP[i].setDistance(currDistance);
            System.out.println(i + " " + inAP[i].getDistance());
            System.out.println(inAP[i].getSSID() + "rssi: " + inAP[i].getRSSI() + " bssid: " + inAP[i].getBSSID());


            if (inAP[i].getRSSI() > largestap) {
                largestap = inAP[i].getRSSI();
                indap0 = i;
            }
        }

        for (int j = 0; j < inAP.length; j++) {
            if ((inAP[j].getRSSI() > largerap) && (j != indap0)) {
                largerap = inAP[j].getRSSI();
                indap1 = j;
            }
        }
        for (int k = 0; k < inAP.length; k++) {
            if ((inAP[k].getRSSI() > largeap) && (k != indap1) && (k != indap0)) {
                largeap = inAP[k].getRSSI();
                indap2 = k;
            }
        }

//        System.out.println(" large="+inAP[indb0].getDistance()+ " larger="+inAP[indb1].getDistance()+"largest= "+inAP[indb2].getDistance());

        if (largestap >= minRSSIval && largeap >= minRSSIval && largerap >= minRSSIval) {
            double[][] positions = new double[][]{{inAP[indap0].getX(), inAP[indap0].getY()}, {inAP[indap1].getX(), inAP[indap1].getY()}, {inAP[indap2].getX(), inAP[indap2].getY()}};
            double[] distances = new double[]{inAP[indap0].getDistance(), inAP[indap1].getDistance(), inAP[indap2].getDistance()};

            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
            LeastSquaresOptimizer.Optimum optimum = solver.solve();
            locationap = optimum.getPoint().toArray();
            if (locationap[0] > 19) locationap[0] = 16;
            else if (locationap[0] >= 18) locationap[0] = 18;

            TextView txtView = (TextView) findViewById(R.id.b10);
            txtView.setText(" x=" + locationap[0] + " y=" + locationap[1]);
            System.out.println(" x=" + locationap[0] + " y=" + locationap[1]);
            runAstar((int) locationap[0], (int) locationap[1]);
//            Toast.makeText(this, " large="+inAP[indap0].getDistance()+ " larger="+inAP[indap1].getDistance()+"largest= "+inAP[indap2].getDistance(), Toast.LENGTH_SHORT).show();
            System.out.println(" large=" + inAP[indap0].getRSSI() + " larger=" + locationap[1] + "largest= ");


        }
    }

//    int[] hope = new int[]{0, 0, 0, 0, 0};
//    int j = 0;
    int count=0;

    private ScanCallback leScanCallback;

    {
        leScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
                if (arrived == false) {
                    String[] beaconAd = new String[]{"90", "96", "88", "69", "A5", "BA", "9E"};
                    String currentId = result.getDevice().getAddress().substring(15, 17);
                    System.out.println(result.getDevice().getAddress().substring(0, 14));
                    if ((result.getDevice().getAddress().substring(0, 14)).equals("0C:F3:EE:B8:0D")) {
                        if (Arrays.asList(beaconAd).contains(result.getDevice().getAddress().substring(15, 17))) {
                            for (int i = 0; i < beacons.length; i++) {
                                if (currentId.equals(beacons[i].id)) {
                                    beacons[i].Testing(result.getRssi());
                                    count++;
                                    Log.e("count", count + " ");
                                }
                            }

                            //   peripheralTextView.append("Device Address: " + result.getDevice().getAddress() + " rssi: " + result.getRssi() + "\n");
                        }
                        if (count == 10) {
                            updatelocation = true;
                            count = 0;
                            for (int i = 0; i < beacons.length; i++) {
                                if (!(((destination.getId().equals("Luggage1")) || destination.getId().equals("Luggage2") && ((i == 5) || (i == 6))))) {
                                    if (beacons[i].getAverage() >= (beacons[i].configparameter)) {
                                        if (arrived == false && ((i == destination.getBeaconindex1()) || (i == destination.getBeaconindex2()))) {
                                            arrived = true;
                                            TextToSpeech("You have arrived, press in the middle of the screen to get to your next step"); ///WILLIAM
                                        } else {
                                            updatelocation = false;
                                            TextView txtView = (TextView) findViewById(R.id.b10);
                                            txtView.setText(" x=" + beacons[i].getX() + " y=" + beacons[i].getY());
                                            runAstar(beacons[i].getX(), beacons[i].getY());
                                        }

                                    }
                                }
                            }
                            if (arrived == false) {
                                if ((destination.getId().equals("Luggage1")) || destination.getId().equals("Luggage2")) {
                                    if (beacons[5].getRssi() >= -68 && beacons[5].getRssi() >= -68) {
                                        int diif = beacons[5].rssi - beacons[6].rssi;
                                        updatelocation = false;

                                        if (diif < 3 && diif > -3) ;

                                        if (diif < 15 && diif >= 5) {
                                            if (destination.getId().equals("Luggage1")) {
                                                TextToSpeech("You have arrived, press in the middle of the screen to get to your next step");
                                                arrived = true;
                                            } else runAstar(beacons[6].getX(), beacons[6].getY());

                                        }
                                        if (diif >= 15) {
                                            if (destination.getId().equals("Luggage1")) {
                                                arrived = true;
                                                TextToSpeech("You have arrived, press in the middle of the screen to get to your next step");
                                            } else runAstar(beacons[6].getX(), beacons[6].getY());

                                        }
                                        if (diif > -15 && diif <= -5) {
                                            if (destination.getId().equals("Luggage2")) {
                                                arrived = true;
                                                TextToSpeech("You have arrived, press in the middle of the screen to get to your next step");
                                            } else runAstar(beacons[5].getX(), beacons[5].getY());
                                        }
                                        if (diif <= -15) {
                                            arrived = true;
                                            TextToSpeech("You have arrived, press in the middle of the screen to get to your next step");
                                        } else runAstar(beacons[5].getX(), beacons[5].getY());
                                    }
                                }
                                if (updatelocation == true) {
                                    updatelocation = false;
                                    wifiManager.startScan();
                                }

                            }
                        }
                        //  peripheralTextView.append("Device Address: " + result.getDevice().getAddress() + " rssi: " + result.getRssi() + "\n")
                    }
                }
            }

        };
    }


    public void startScanning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.e("start","here"+arrived);
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        arrived=true;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }


    public void NextStep(View view) {
        if(TTS !=null) TTS.stop();
        arrived=false;
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        int currentStep = Settings.getInt("Step_num", 0);
        if (currentStep == 3) {
            arrived =true;
            stopScanning();
            Intent i1 = new Intent(NavigationMain.this, End.class);
            startActivity(i1);
        } else {
            SharedPreferences.Editor prefEditor = Settings.edit();
            prefEditor.putInt("Step_num", currentStep + 1);
            prefEditor.commit();
            TextToSpeech("You're next Step is "+Settings.getString("Step" + Settings.getInt("Step_num", 0), " ")+
                        ". To go back, press the bottom left hand corner");
            Setcolors();
        }
    }

    public void BackStep(View view) {
        if(TTS !=null) TTS.stop();
        arrived=false;
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        int currentStep = Settings.getInt("Step_num", 0);
        if (currentStep == 1) {
            Toast.makeText(this, "You are in the first step", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences.Editor prefEditor = Settings.edit();
            prefEditor.putInt("Step_num", currentStep - 1);
            prefEditor.commit();
            Setcolors();
        }
    }


    public void GoInformation(View view) {
        if(TTS !=null) TTS.stop();
        stopScanning();
        Intent i1 = new Intent(NavigationMain.this, Information.class);
        startActivity(i1);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //FI = FirebaseDatabase.getInstance().getReference("ME201");
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        final Flight_Info_Data Flightref = new Flight_Info_Data(Settings.getString("Boarding_Time", " "), Settings.getString("Flight_Time", " "), Settings.getString("Destination", " "),Settings.getLong("Gate_number", -1));
//        long mygate = Settings.getLong("Gate_number", -1);

        FI.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Flight_Info_Data myFlightInfo = dataSnapshot.getValue(Flight_Info_Data.class);
                System.out.println(myFlightInfo);
                if (dataSnapshot.hasChild("boarding_Time")) {
                    SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);

                    if (!Flightref.getmyFlight_Time().equals(myFlightInfo.getmyFlight_Time())) {
                        //to be changed
                        Flightref.setmyFlight_Time(myFlightInfo.getmyFlight_Time());
                        Flightref.setmyBoarding_Time(myFlightInfo.getmyBoarding_Time());
                        TextToSpeech("There is a delay in your flight, please press the bottom right hand corner to get more information");

                    }
                    //else if (!(Settings.getString("Boarding_Time", " ")).equals(myFlightInfo.getmyBoarding_Time())){
                    //add code,
                    //      TextView txtView = (TextView) findViewById(R.id.b10);
                    //     txtView.setText("The boarding is at : " + myFlightInfo.getmyBoarding_Time());
                    //  }
                    if (Flightref.getMyGate_number() != (myFlightInfo.getMyGate_number())) {
                        //tobe changed
                        String gate;

                        Flightref.setMyGate_number(myFlightInfo.getMyGate_number());
                        TextView txtView5 = (TextView) findViewById(R.id.Gate);
                        if (Settings.getInt("Option", 0) == 1) {

                            if (myFlightInfo.getMyGate_number() == -1) {
                                gate=("Your gate number is not available");
                            } else {
                                gate= ("Your gate number is " + myFlightInfo.getMyGate_number());
                            }
                        } else {
                            if (myFlightInfo.getMyGate_number() == -1) {
                                gate= ("Your luggage number is not available");
                            } else {
                                gate= ("Your luggage number is " + myFlightInfo.getMyGate_number());
                            }

                        }
                        txtView5.setText(gate);
                        TextToSpeech(gate);


//                        if (myFlightInfo.getMyGate_number() == -1) {
//                            txtView5.setText("Your gate number is not available");
//                        } else {
//                            txtView5.setText("Your gate number is " + myFlightInfo.getMyGate_number());
//                        }
//                        TextView txtView = (TextView) findViewById(R.id.b10);
//                        txtView.setText(" ");
                    }

                    SharedPreferences.Editor prefEditor = Settings.edit();
                    prefEditor.putString("Boarding_Time", myFlightInfo.getmyBoarding_Time());
                    prefEditor.putString("Flight_Time", myFlightInfo.getmyFlight_Time());
                    prefEditor.putLong("Gate_number", myFlightInfo.getMyGate_number());
                    prefEditor.commit();
                    //TextView txtView = (TextView) findViewById(R.id.b10);
                    //txtView.setText("The flight is at : " + myFlightInfo.getmyFlight_Time());

                } else {
                    SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = Settings.edit();
                    prefEditor.putBoolean("Wrong_Flight_Number", true);
                    prefEditor.commit();
                    Intent i2 = new Intent(NavigationMain.this, FlightNumber.class);
                    startActivity(i2);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });

    }

    public void TextToSpeech(String Speech) {
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale localeToUse = new Locale("en", "UK");
                    TTS.setLanguage(localeToUse);
                    TTS.speak(Speech, TextToSpeech.QUEUE_FLUSH, null);
                    int result = TTS.setLanguage(Locale.UK);
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }

        });
    }


    public boolean BTinit()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please Pair the Device first",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean BTconnect()
    {
        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return connected;
    }


}

