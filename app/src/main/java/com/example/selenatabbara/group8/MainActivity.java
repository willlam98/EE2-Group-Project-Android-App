package com.example.selenatabbara.group8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_DURATION_LENGTH = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Resetting the Shared Preference settings
        SharedPreferences Settings = getSharedPreferences("MyItinerary", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = Settings.edit();
        prefEditor.putString("Boarding_Time", " ");
        prefEditor.putString("Flight_Time",  " ");
        prefEditor.putLong("Gate_number", -1);
        prefEditor.putLong("Destination ", -1);
        prefEditor.putString("FlightNumber", " ");
        prefEditor.putBoolean("Wrong_Flight_Number",false);
        prefEditor.putString("Step1", " ");
        prefEditor.putString("Step2", " ");
        prefEditor.putString("Step3", " ");
        prefEditor.putInt("Step_num", 1);
        prefEditor.commit();


//        SharedPreferences Coordinates = getSharedPreferences("MAP", MODE_PRIVATE);
//        SharedPreferences.Editor prefEditorc = Coordinates.edit();
//        prefEditorc.putInt("Gate1 x", 0);
//        prefEditorc.putInt("Gate1 y", 0);
//        prefEditorc.putInt("Gate2 x", 4);
//        prefEditorc.putInt("Gate2 y", 0);
//        prefEditorc.putInt("Luggage1 x", 1);
//        prefEditorc.putInt("Luggage1 y", 6);
//        prefEditorc.putInt("Luggage2 x", 3);
//        prefEditorc.putInt("Luggage2 y", 8);
//        prefEditorc.putInt("Security Check x", 7);
//        prefEditorc.putInt("Security Check y", 8);
//        prefEditorc.putInt("Luggage Deposit x", 6);
//        prefEditorc.putInt("Luggage Deposit y", 5);
//        prefEditorc.putInt("UK Boarders x", 9);
//        prefEditorc.putInt("UK Boarders y", 1);
//        prefEditorc.putInt("Exit x", 10);
//        prefEditorc.putInt("Exit y", 10);
//        prefEditorc.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(MainActivity.this, SelectOption.class);
                startActivity(mainIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        },SPLASH_DISPLAY_DURATION_LENGTH);

    }
}