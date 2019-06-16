package com.example.selenatabbara.group8;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


public class Flight_Info_Data {
    public String boarding_Time;
    public String flight_Time;
    public Long gate_number;
    public String destination;

   public Flight_Info_Data() {
    }

    public Flight_Info_Data(String boarding_Time, String flight_Time, String destination, long gate_number){
        this.boarding_Time=boarding_Time;
        this.flight_Time=flight_Time;
        this.destination=destination;
        this.gate_number=gate_number;
    }

    public String getmyBoarding_Time() { return boarding_Time; }
    public String getmyFlight_Time() {
        return flight_Time;
    }
    public long getMyGate_number() {
        return gate_number;
    }
    public String getmyDestination() { return destination; }

    public void setMyGate_number(long GN) {
        gate_number=GN;
    }
    public void setmyFlight_Time(String FT) {
         flight_Time=FT;
    }
    public void setmyBoarding_Time(String BT) {  boarding_Time= BT; }
    public void setmyDestination(String destination) { this.destination = destination; }
}