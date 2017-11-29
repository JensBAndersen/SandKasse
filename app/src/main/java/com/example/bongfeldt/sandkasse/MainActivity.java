package com.example.bongfeldt.sandkasse;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static GoogleApiClient mApiClient;
    PendingIntent pendingIntent;
    private static final String TAG = "SandkasseMain";
    private List<Position> positionList;
    TextView textViewLocation;
    TextView textViewTrack;
    ToggleButton toggle;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        positionList = new ArrayList<>();

        textViewLocation = findViewById(R.id.textViewLocation);
        textViewTrack = findViewById(R.id.textViewTrack);

        toggle = findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startRepeatingTask();
                } else {
                    stopRepeatingTask();
                }
            }
        });

        button = findViewById(R.id.button2);


        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("Jens","onConnected");

        Intent intent = new Intent(this, ARS.class);
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 5000, pendingIntent);

        Intent locationIntent = new Intent(this, LocationService.class);
        //pendingIntent2 = PendingIntent.getService(this, 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        startService(locationIntent);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart - Connect");
        mApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        if (mApiClient.isConnected()) {
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mApiClient,pendingIntent);
            mApiClient.disconnect();
            Log.i(TAG, "was connected");
        }
        else{
            mApiClient.connect();
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mApiClient,pendingIntent);
            mApiClient.disconnect();
            Log.i(TAG, "was not connected");
        }
    }

    //private final static int INTERVAL = 1000 * 60 * 2; //2 minutes
    private final static int INTERVAL = 3000;
    Handler mHandler = new Handler();

    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            TrackRout();
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    public void TrackRout(){
        Log.e("JENS", "TrackRout");

        SharedPreferences sharedPref = getSharedPreferences(
                "preference_file_key", Context.MODE_PRIVATE);
        SharedPreferences lastlocationSharedPref = getSharedPreferences(
                "lastlocation_key", Context.MODE_PRIVATE);


        String mapTypeString = sharedPref.getString("preference_file_key", "DEFAULT2");
        Log.e("MapTypeString", mapTypeString);

        if(mapTypeString.equals("Cycling")){
            double Longitude = Double.longBitsToDouble(lastlocationSharedPref.getLong("Longitude_key", Double.doubleToLongBits(0)));
            double Latitude = Double.longBitsToDouble(lastlocationSharedPref.getLong("Latitude_key", Double.doubleToLongBits(0)));

            Log.e("Longitude", String.valueOf(Longitude));
            Log.e("Latitude", String.valueOf(Latitude));

            positionList.add(new Position(Latitude, Longitude));
            textViewLocation.setText("Long: " + Longitude + " Lat: " + Latitude);
        }
    }

    public void readTrajectory(View view){
        Trajectory trajectory = new Trajectory(positionList);
        trajectory.medianFilter(0);
        double trajectoryLenght = trajectory.getLength();

        textViewTrack.setText("Lenght: " + new DecimalFormat("##.##").format(trajectoryLenght) + "km" );
    }

    void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }
}
