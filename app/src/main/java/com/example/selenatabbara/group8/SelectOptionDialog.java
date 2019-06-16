package com.example.selenatabbara.group8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import static android.R.id.message;
import static com.example.selenatabbara.group8.R.id.textView;

public class SelectOptionDialog extends AppCompatActivity {
    private TextToSpeech TTS=null;

    String Checkingoption;
    TextView txtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_dialog);
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        int myoption= Settings.getInt("Option",0);


        if (myoption ==0){
            Intent i2 = new Intent(SelectOptionDialog.this, SelectOption.class);
            startActivity(i2);
        }
        else if(myoption==1){
            Checkingoption=" You have selected Departure.";
            selectedoption("departure");
            SharedPreferences.Editor prefEditor = Settings.edit();
            prefEditor.putString("Step1", "Luggage Deposit");
            prefEditor.putString("Step2", "Security Check");
            prefEditor.putString("Step3", "Go to Gate");
            prefEditor.commit();
        }
        else if(myoption==2){
            Checkingoption=" You have selected Arrival.";
            selectedoption("Arrival");

            SharedPreferences.Editor prefEditor = Settings.edit();
            prefEditor.putString("Step1", "UK Boarders");
            prefEditor.putString("Step2", "Luggage Claim");
            prefEditor.putString("Step3", "Exit");
            prefEditor.commit();

        }else if(myoption==3){
            Checkingoption=" You have selected Transit.";
            selectedoption("Transit");

            SharedPreferences.Editor prefEditor = Settings.edit();
            prefEditor.putString("Step1", "Go to Gate");
            prefEditor.putString("Step2", " ");
            prefEditor.putString("Step3", " ");
            prefEditor.commit();
        }
        txtView = (TextView) findViewById(R.id.Next2);
        txtView.setText(Checkingoption);
        // Set the text view as the activity layout
       // setContentView(textView);
    }

    public void goFlightNumber (View view) {
        if(TTS !=null) TTS.stop();
        Intent i1 = new Intent(SelectOptionDialog.this, FlightNumber.class);
        startActivity(i1);
    }
    public void goBackSelectOption (View view) {
        if(TTS !=null) {
            TTS.stop();
        }
        Intent i2 = new Intent(SelectOptionDialog.this, SelectOption.class);
        startActivity(i2);
    }


    // Speech
    public void selectedoption(String option) {
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale localeToUse = new Locale("en", "UK");
                    TTS.setLanguage(localeToUse);
                    TTS.speak("You have selected " + option + ". If correct press up, if not press down.", TextToSpeech.QUEUE_FLUSH, null);
                    int result = TTS.setLanguage(Locale.UK);
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }

        });
    }

}
