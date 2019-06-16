package com.example.selenatabbara.group8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Locale;

public class SelectOption extends AppCompatActivity {
    private TextToSpeech TTS=null;
    TextView Result;
    private final int SPLASH_DISPLAY_DURATION_LENGTH = 5000;
    Button C1, C2, C3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option);
        C1 = findViewById(R.id.Departure);
        C2 = findViewById(R.id.Arrival);
        C3 = findViewById(R.id.Transit);
        Result = (TextView) findViewById(R.id.flighttime);
        selectoptionspeech("Do you want to go to departure, arrival or transit?");
        delay();

//        getSpeechInput(null);
//        selectchoicesfinal();

        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = Settings.edit();
        prefEditor.putInt("Option", 0);
        prefEditor.commit();

    }



    public void goSelectionOptionDialog_Departure (View view) {
        if(TTS !=null) {
            TTS.stop();
        }
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = Settings.edit();
        prefEditor.putInt("Option", 1);
        prefEditor.commit();
        Intent i2 = new Intent(SelectOption.this, SelectOptionDialog.class);
        startActivity(i2);
    }
    public void goSelectionOptionDialog_Arrival (View view) {
        if(TTS !=null) {
            TTS.stop();
        }
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = Settings.edit();
        prefEditor.putInt("Option", 2);
        prefEditor.commit();
        Intent i2 = new Intent(SelectOption.this, SelectOptionDialog.class);
        startActivity(i2);
    }
    public void goSelectionOptionDialog_Transit (View view) {
        if(TTS !=null) {
            TTS.stop();
        }
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = Settings.edit();
        prefEditor.putInt("Option", 3);
        prefEditor.commit();
        Intent i2 = new Intent(SelectOption.this, SelectOptionDialog.class);
        startActivity(i2);
    }

    public void selectoptionspeech(String selectoption) {
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale localeToUse = new Locale("en", "UK");
                    TTS.setLanguage(localeToUse);
                    TTS.speak(selectoption, TextToSpeech.QUEUE_FLUSH, null);
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
                    //Result.setText(result.get(0));
                    selectchoicesfinal(result.get(0));
                }
                else {
                    selectoptionspeech("Sorry, I didn't get that, try again.");
                    new Handler().postDelayed(new Runnable() {
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


    public void delay(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSpeechInput(null);
            }
        },SPLASH_DISPLAY_DURATION_LENGTH);
    }

    public void selectchoicesfinal(String finalchoice ){
        //String finalchoice = Result.getText().toString().trim();
        if (finalchoice.equals("departure")){
            goSelectionOptionDialog_Departure(null);
        }
//
        else if (finalchoice.equals("arrival")){
            goSelectionOptionDialog_Arrival(null);
        }

        else if (finalchoice.equals("Transit")){
            goSelectionOptionDialog_Transit(null);
        }
        else{

            selectoptionspeech("Sorry, We only have departure, arrival or transit options. Please select an option again.");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                }
            },SPLASH_DISPLAY_DURATION_LENGTH);


        }
    }

}