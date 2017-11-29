package com.example.bongfeldt.sandkasse;
import android.util.Log;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

import static java.lang.Math.abs;

public class Trajectory {
    public Trajectory(List<Position> _positions){
        positions = _positions;
    }

    public Trajectory(Position _position){
        positions.add(_position);
    }

    public void add(Position _position){
        positions.add(_position);
    }

    public void clear(){
        positions = new ArrayList<Position>();
    }

    public void medianFilter(int size){
        List<Position> tempPositions = new ArrayList<Position>();

        for (int i = 0; i < positions.size(); i++){
            double tempLat = 0.0;
            double tempLong = 0.0;
            double count = 0;

            for (int j = i - size; j <= i + size; j++) {
                if (j >= 0 && j < positions.size()) {
                    tempLat = tempLat + positions.get(j).getLatitude();
                    tempLong += positions.get(j).getLongitude();
                    count++;
                }
            }
            tempPositions.add(new Position(tempLat/count, tempLong/count));
        }
        positions = tempPositions;
    }

    public double getLength(){
        double distance = 0;

        for (int i = 0; i < positions.size() - 1; i++){
            double longditude = Math.abs(positions.get(i).getLongitude() - positions.get(i + 1).getLongitude());
            double latitude = Math.abs(positions.get(i).getLatitude() - positions.get(i + 1).getLatitude());

            Log.e("Trajector1 - longditude", String.valueOf(longditude));
            Log.e("Trajector1 - latitude", String.valueOf(latitude));

            longditude *= 62.445;
            latitude *= 111.096;

            Log.e("Trajectory - longditude", String.valueOf(longditude));
            Log.e("Trajectory - latitude", String.valueOf(latitude));

            distance += Math.sqrt (Math.pow(longditude,2) + Math.pow(latitude,2));
        }
        return distance;
    }

    private List<Position> positions = new ArrayList<Position>();
}