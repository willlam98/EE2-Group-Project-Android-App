package com.example.selenatabbara.group8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class FlightNumberDialog extends AppCompatActivity {
    String CheckingFlightNb;
    TextView txtView;
    DatabaseReference FI;
    TextToSpeech TTS= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_number_dialog);
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        String myflightnumber= Settings.getString("FlightNumber", " ");
        CheckingFlightNb ="You have entered: " + myflightnumber;
        txtView = (TextView) findViewById(R.id.Next2);
        txtView.setText(CheckingFlightNb);
        flightnumberresult(myflightnumber);
        FI= FirebaseDatabase.getInstance().getReference(myflightnumber);
        //HasAChild();

    }

    public void goBackFlightNumber (View view) {
        if(TTS !=null) TTS.stop();
        Intent i1 = new Intent(FlightNumberDialog.this, FlightNumber.class);
        startActivity(i1);
    }

    public void goInformation (View view) {
        if(TTS !=null) TTS.stop();
        super.onStart();
        // ADD TO CHECK IF FLIGHT NUMBER IS CORRECT
        FI.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("boarding_Time")) {
                    Flight_Info_Data myFlightInfo = dataSnapshot.getValue(Flight_Info_Data.class);
                    System.out.println(myFlightInfo);
                    SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = Settings.edit();
                    prefEditor.putString("Boarding_Time", myFlightInfo.getmyBoarding_Time());
                    prefEditor.putString("Flight_Time", myFlightInfo.getmyFlight_Time());
                    prefEditor.putLong("Gate_number", myFlightInfo.getMyGate_number());
                    prefEditor.putString("Destination",myFlightInfo.getmyDestination());
                    prefEditor.putBoolean("Wrong_Flight_Number",false);
                    prefEditor.commit();
                    Intent i2 = new Intent(FlightNumberDialog.this, Information.class);
                    startActivity(i2);

                }
                else{
                    SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = Settings.edit();
                    prefEditor.putBoolean("Wrong_Flight_Number",true);
                    prefEditor.commit();
                    Intent i1 = new Intent(FlightNumberDialog.this, FlightNumber.class);
                    startActivity(i1);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }

        });


    }
    public void flightnumberresult(final String flightnum) {
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale localeToUse = new Locale("en", "UK");
                    TTS.setLanguage(localeToUse);
                    TTS.speak("You have entered flight number " + flightnum  + ". If correct, press up, if not, press down.", TextToSpeech.QUEUE_FLUSH, null);
                    int result = TTS.setLanguage(Locale.UK);
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }

        });
    }

}
