package com.example.bongfeldt.sandkasse;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;


public class ARS extends IntentService {

    public ARS() {
        super("ActivityRecognizedService");
    }

    public ARS(String name) {
        super(name);
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        Log.i("JensARS","handleDetectedActivities");
        for( DetectedActivity activity : probableActivities ) {
            if (activity.getType() == DetectedActivity.STILL && activity.getConfidence() >=75){
                Log.i("JensARS","Du laver ikke en skid!");

                SharedPreferences sharedPref = getSharedPreferences(
                        "preference_file_key", Context.MODE_PRIVATE);


                String mapTypeString = sharedPref.getString("preference_file_key", "DEFAULT2");
                Log.i("JensARS",mapTypeString);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("preference_file_key", "Cycling");
                editor.apply();

                String mapTypeString2 = sharedPref.getString("preference_file_key", "DEFAULT2");

                Log.i("JensARS",mapTypeString2);

                SharedPreferences locationSharedPref = getSharedPreferences(
                        "lastlocation_key", Context.MODE_PRIVATE);

                String locationTest = locationSharedPref.getString("lastlocation_key", "DEFAULT");
                Log.i("JensARSLocation",locationTest);

             }
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i("JensARS", "onCreate");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("JensARS", "onDestroy");
    }
}
