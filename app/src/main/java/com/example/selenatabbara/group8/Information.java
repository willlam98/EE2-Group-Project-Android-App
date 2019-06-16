package com.example.selenatabbara.group8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Information extends AppCompatActivity  {

    //Flight_Info_Data myFlightInfo;
    DatabaseReference FI;
    private TextToSpeech TTS=null;
//    boolean arrived=true;

    private final int SPLASH_DISPLAY_DURATION_LENGTH = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        //FirebaseApp.initializeApp(this);
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        String myflightnumber = Settings.getString("FlightNumber", " ");
        String stepnumber="Step"+Settings.getInt("Step_num", 0);
        String mynextstep = Settings.getString(stepnumber, " ");
        String myflighttime = Settings.getString("Flight_Time", " ");
        String mydestination=Settings.getString("Destination"," ");
        String myboardingtime = Settings.getString("Boarding_Time", " ");
        long mygate = Settings.getLong("Gate_number", -1);
        FI= FirebaseDatabase.getInstance().getReference(myflightnumber);
        String destinationstr;
        if (Settings.getInt("Option",0)==2) destinationstr="From : "+ mydestination;
        else destinationstr="To : "+ mydestination;
        TextView txtView1 = (TextView) findViewById(R.id.destination);
        txtView1.setText(destinationstr);
        TextView txtView2 = (TextView) findViewById(R.id.flighttime);
        txtView2.setText("Departure time : " + myflighttime);
        TextView txtView3 = (TextView) findViewById(R.id.boarding_in);
        txtView3.setText("Boarding time : " + myboardingtime);
        TextView txtView5 = (TextView) findViewById(R.id.gatenumber);
         String gatenumber;
        if ( mygate ==-1) {
            txtView5.setText("Your gate number is not available");
             gatenumber= ". The gate number is not available.";
        }
        else {
            txtView5.setText(" Gate number : " + mygate);
             gatenumber= ". Gate number : " + mygate+".";

        }
        String mynextstepinfo;
        if(stepnumber.equals("Step3")) mynextstepinfo= "Next Step: " + mynextstep;
        else if (stepnumber.equals("Step2"))mynextstepinfo= "Next Steps: "+mynextstep+ "and "+ Settings.getString("Step3", " ");
        else mynextstepinfo="Next Steps: "+mynextstep+", "+Settings.getString("Step2", " ")+  " and "+ Settings.getString("Step3", " ");


        flightdetails( "Flight "+ myflightnumber + ", " +destinationstr
                        + ". Departure time " + myflighttime
                        + ". Boarding time :  "+ myboardingtime
                        + gatenumber
                        + mynextstepinfo
                        + ". to continue press anywhere on the screen. ");
//        delaygetspeech("The boarding starts at :  "+ myboardingtime);


        String myfn_info ="Flight " + myflightnumber + " Info ";
        TextView txtView4 = (TextView) findViewById(R.id.nextstep);
        txtView4.setText(mynextstepinfo);
        TextView txtView = (TextView) findViewById(R.id.fn_info);
        txtView.setText(myfn_info);

        //System.out.println(Flight_Time);
        //txtView2.setText(Flight_Time)*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        //FI = FirebaseDatabase.getInstance().getReference("ME201");


        FI.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Flight_Info_Data myFlightInfo = dataSnapshot.getValue(Flight_Info_Data.class);
                System.out.println(myFlightInfo);
                SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);

                SharedPreferences.Editor prefEditor = Settings.edit();
                prefEditor.putString("Boarding_Time",  myFlightInfo.getmyBoarding_Time());
                prefEditor.putString("Flight_Time",  myFlightInfo.getmyFlight_Time());
                prefEditor.putLong("Gate_number", myFlightInfo.getMyGate_number());
                prefEditor.commit();
                TextView txtView2 = (TextView) findViewById(R.id.flighttime);
                txtView2.setText("The flight departs at : " + myFlightInfo.getmyFlight_Time());
                TextView txtView3 = (TextView) findViewById(R.id.boarding_in);
                txtView3.setText("The boarding starts at : " + myFlightInfo.getmyBoarding_Time());

                TextView txtView5 = (TextView) findViewById(R.id.gatenumber);
                if ( myFlightInfo.getMyGate_number() ==-1) {
                    txtView5.setText("Your gate number is not available");
                }
                else {
                    txtView5.setText("Your gate number is " +  myFlightInfo.getMyGate_number());
                }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });

    }
    public void goNavigation(View view) {
        if(TTS !=null) TTS.stop();
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        if (Settings.getInt("Option",0)==3) {
            Intent i1 = new Intent(Information.this, NavigationTransit.class);
            startActivity(i1);
        }
        else{
            Intent i1 = new Intent(Information.this, NavigationMain.class);
            startActivity(i1);
        }
    }

    public void flightdetails(String result) {
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale localeToUse = new Locale("en", "UK");
                    TTS.setLanguage(localeToUse);
                    TTS.speak(result, TextToSpeech.QUEUE_FLUSH, null);
                    int result = TTS.setLanguage(Locale.UK);
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }

        });
    }

//    public void delaygetspeech(String textinput){
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                flightdetails(textinput);
//            }
//        },SPLASH_DISPLAY_DURATION_LENGTH);
//    }
//
}
