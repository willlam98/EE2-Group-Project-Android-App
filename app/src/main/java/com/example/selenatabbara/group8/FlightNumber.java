package com.example.selenatabbara.group8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class FlightNumber extends AppCompatActivity {
    private final int SPLASH_DISPLAY_DURATION_LENGTH = 3000;

    EditText flightnumber;
    Button send;
    private TextToSpeech TTS=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_number);
        flightnumberspeech();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSpeechInput(null);
            }
        },SPLASH_DISPLAY_DURATION_LENGTH);



        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        //Settings.getBoolean("Wrong_Flight_Number", false);
//        if(Settings.getBoolean("Wrong_Flight_Number", false)){
//            Toast.makeText(this,"The flight number is wrong", Toast.LENGTH_LONG).show();
//            SharedPreferences.Editor prefEditor = Settings.edit();
//            prefEditor.putBoolean("Wrong_Flight_Number",false);
//            prefEditor.commit();
//
//        }
        flightnumber = (EditText)findViewById(R.id.FlightNumber);

       /* FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ME201");

        myRef.setValue("Hello, World!");*/
        send=(Button)findViewById(R.id.Done);
    }
    public void goFlightNumberDialog (View view) {
        if(TTS !=null) TTS.stop();
        String myflightnumber=flightnumber.getText().toString().trim();
        myflightnumber = myflightnumber.toUpperCase();
        for (int i=0; i<myflightnumber.length();i++){
            if(myflightnumber.charAt(i) == ' '|| myflightnumber.charAt(i) == '-') myflightnumber = myflightnumber.substring(0, i) + myflightnumber.substring(i+1);
        }
        if(myflightnumber.equals("SD244")||myflightnumber.equals("ST24FOR")||myflightnumber.equals("SQ244")) myflightnumber="ST244";
        if(myflightnumber.equals("AZ651")||myflightnumber.equals("ACS651")||myflightnumber.equals("8651")) myflightnumber="AC651";
        if(myflightnumber.equals("EY231")||myflightnumber.equals("EY23DO")||myflightnumber.equals("UY2310")||myflightnumber.equals("UY2310")) myflightnumber="EY2310";
        if(myflightnumber.equals("GT888")||myflightnumber.equals("GTA888")||myflightnumber.equals("GPS888S")) myflightnumber="GP888";

        if (TextUtils.isEmpty(myflightnumber)){
            Toast.makeText(this,"Input Missing", Toast.LENGTH_LONG).show();
        }
        else {
            SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = Settings.edit();
            prefEditor.putString("FlightNumber", myflightnumber);
            prefEditor.commit();
            Intent i1 = new Intent(FlightNumber.this, FlightNumberDialog.class);
            startActivity(i1);
        }
    }





    // speech for the flightnumber input
    public void flightnumberspeech() {
        String speech;
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        if(Settings.getBoolean("Wrong_Flight_Number", false)){
            SharedPreferences.Editor prefEditor = Settings.edit();
            Toast.makeText(this,"The flight number is wrong", Toast.LENGTH_LONG).show();
            speech="The flight number is wrong, please try again";
            prefEditor.putBoolean("Wrong_Flight_Number",false);
            prefEditor.commit();

        }
        else speech = "Please say your flight number";
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale localeToUse = new Locale("en", "UK");
                    TTS.setLanguage(localeToUse);
                    TTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                    int result = TTS.setLanguage(Locale.UK);
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }

        });
    }
    // speech input

    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    flightnumber.setText(result.get(0));
                    send.performClick();
                }
                else {
                    String speech = "Sorry, I didn't get that, try again.";
                    TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                Locale localeToUse = new Locale("en", "UK");
                                TTS.setLanguage(localeToUse);
                                TTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                                int result = TTS.setLanguage(Locale.UK);
                            } else {
                                Log.e("TTS", "Initialization failed");
                            }
                        }

                    });                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);

                        }
                    },3000);

                }
                break;
        }
    }
    //flight number speech result,  // tell you what you just entered


}
