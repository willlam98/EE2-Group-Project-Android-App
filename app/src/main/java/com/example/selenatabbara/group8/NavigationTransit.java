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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class NavigationTransit extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_navigation_transit);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }
DatabaseReference FI;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    String destination;


    private final String DEVICE_ADDRESS="98:D3:81:FD:47:1E";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;


    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    //int mylocation[] = {-1, -1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);


//        if(BTinit())
//        {
//            if(BTconnect())
//            {
//                Toast.makeText(this, "\nConnection Opened!\n", Toast.LENGTH_LONG).show();
//            }
//
//        }


        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);

        FI = FirebaseDatabase.getInstance().getReference(Settings.getString("FlightNumber", " "));

        long mygate = Settings.getLong("Gate_number", -1);
        TextView txtView5 = (TextView) findViewById(R.id.Gate);
        if (Settings.getInt("Option",0)==1) {
            if (mygate == -1) {
                txtView5.setText("Your gate number is not available");
            } else {
                txtView5.setText("Your gate number is " + mygate);
            }
        }
        else {
            if (mygate == -1) {
                txtView5.setText("Your luggage number is not available");
            } else {
                txtView5.setText("Your luggage number is " + mygate);
            }
        }

        init();
        startScanning();
    }


    //Set colors of the steps accordingto which stpe you're in


    public void runAstar(int myx, int myy) {
        SharedPreferences Coordinates = getSharedPreferences("MAP", MODE_PRIVATE);
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        long mygatenumber = Settings.getLong("Gate_number", -1);
        if (mygatenumber != -1)
            runAstarinit(myx, myy, Coordinates.getInt("Gate" + mygatenumber + " x", 0), Coordinates.getInt("Gate" + mygatenumber + " x", 0));
    }

    public void runAstarinit(int myx, int myy, int destx, int desty) {
        Astar aStar = new Astar(10, 10, myx, myy, destx, desty);
        int angle = aStar.run();
        String string = Integer.toString(angle);
//        string.concat("\n");
//        try {
//            outputStream.write(string.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Toast.makeText(this, ("\nSent Data:"+string+"\n"), Toast.LENGTH_LONG).show();

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

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

        public void setDistance(double r) {
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

    NavigationTransit.beaconDetails[] beacons = new NavigationTransit.beaconDetails[]{new NavigationTransit.beaconDetails(5, 5, "90", -100, 0), new NavigationTransit.beaconDetails(2.5, 10, "96", -100, 0), new NavigationTransit.beaconDetails(7.5, 10, "88", -100, 0), new NavigationTransit.beaconDetails(10, 0, "69", -100, 0), new NavigationTransit.beaconDetails(7.5, 0, "A5", -100, 0), new NavigationTransit.beaconDetails(2.5, 0, "BA", -100, 0), new NavigationTransit.beaconDetails(0, 5, "9E", -100, 0)};


    public double distance(int rssiValue) {
        return Math.pow(10, (-70 - rssiValue) / (10 * 2.25));
    }


    int largest = -200;
    int larger = -200;
    int large = -200;
    int indb0 = 8;
    int indb1 = 8;
    int indb2 = 8;
    double currDistance = 0;
    double[] location = new double[]{0, 0};
    double[][] locationPoints = new double[2][5];
    double[] finalPosition = new double[]{0, 0};
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
                            TextView txtView = (TextView) findViewById(R.id.b10);
                            txtView.setText((10 * (finalPosition[0])) / 10 + " " +  (10 * (finalPosition[1])) / 10);
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


    public void NextStep(View view) {
        stopScanning();
        Intent i1 = new Intent(NavigationTransit.this, End.class);
        startActivity(i1);
    }



    public void GoInformation(View view) {
        Intent i1 = new Intent(NavigationTransit.this, Information.class);
        startActivity(i1);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //FI = FirebaseDatabase.getInstance().getReference("ME201");
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        final Flight_Info_Data Flightref= new Flight_Info_Data(Settings.getString("Boarding_Time", " "), Settings.getString("Flight_Time", " "),Settings.getString("Destination"," "), Settings.getLong("Gate_number", -1));
        long mygate = Settings.getLong("Gate_number", -1);

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

                    }
                    //else if (!(Settings.getString("Boarding_Time", " ")).equals(myFlightInfo.getmyBoarding_Time())){
                    //add code,
                    //      TextView txtView = (TextView) findViewById(R.id.b10);
                    //     txtView.setText("The boarding is at : " + myFlightInfo.getmyBoarding_Time());
                    //  }
                    if (Flightref.getMyGate_number() != (myFlightInfo.getMyGate_number())) {
                        //tobe changed
                        Flightref.setMyGate_number(myFlightInfo.getMyGate_number());
                        TextView txtView5 = (TextView) findViewById(R.id.Gate);
                        if (Settings.getInt("Option",0)==1) {
                            if (myFlightInfo.getMyGate_number() == -1) {
                                txtView5.setText("Your gate number is not available");
                            } else {
                                txtView5.setText("Your gate number is " + myFlightInfo.getMyGate_number());
                            }
                        }
                        else {
                            if (myFlightInfo.getMyGate_number() == -1) {
                                txtView5.setText("Your luggage number is not available");
                            } else {
                                txtView5.setText("Your luggage number is " + myFlightInfo.getMyGate_number());
                            }
                        }

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

                }
                else{
                    SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = Settings.edit();
                    prefEditor.putBoolean("Wrong_Flight_Number",true);
                    prefEditor.commit();
                    Intent i2 = new Intent(NavigationTransit.this, FlightNumber.class);
                    startActivity(i2);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });

    }

//    public boolean BTinit()
//    {
//        boolean found=false;
//        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
//        }
//        if(!bluetoothAdapter.isEnabled())
//        {
//            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableAdapter, 0);
//            try {
//                Thread.sleep(800);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
//        if(bondedDevices.isEmpty())
//        {
//            Toast.makeText(getApplicationContext(),"Please Pair the Device first",Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            for (BluetoothDevice iterator : bondedDevices)
//            {
//                if(iterator.getAddress().equals(DEVICE_ADDRESS))
//                {
//                    device=iterator;
//                    found=true;
//                    break;
//                }
//            }
//        }
//        return found;
//    }
//
//    public boolean BTconnect()
//    {
//        boolean connected=true;
//        try {
//            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
//            socket.connect();
//        } catch (IOException e) {
//            e.printStackTrace();
//            connected=false;
//        }
//        if(connected)
//        {
//            try {
//                outputStream=socket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                inputStream=socket.getInputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//
//        return connected;
//    }
//

}
