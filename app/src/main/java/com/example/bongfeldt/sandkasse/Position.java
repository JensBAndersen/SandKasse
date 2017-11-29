package com.example.bongfeldt.sandkasse;

/**
 * Created by Bongfeldt on 28/11/2017.
 */

public class Position {
    public Position(double _latitude, double _longitude){
        latitude = _latitude;
        longitude = _longitude;
    }

    public void setPosition(double _latitude, double _longitude){
        latitude = _latitude;
        longitude = _longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    private double latitude;
    private double longitude;
}
