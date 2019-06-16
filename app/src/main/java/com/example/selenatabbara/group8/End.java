package com.example.selenatabbara.group8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class End extends AppCompatActivity {
    TextToSpeech TTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);

        if (Settings.getInt("Option",0)==2) {
            TextView txtView = (TextView) findViewById(R.id.end);
            txtView.setText("Have a nice stay");
            TextToSpeech("Have a nice stay");
        }
        else{
            TextView txtView = (TextView) findViewById(R.id.end);
            txtView.setText("Have a safe flight");
            TextToSpeech("Have a safe flight");
        }
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
    public void reinit (View view) {
        if(TTS !=null) TTS.stop();
        Intent i1 = new Intent(End.this, MainActivity.class);
        startActivity(i1);
        finish();
        moveTaskToBack(true);
    }

}
