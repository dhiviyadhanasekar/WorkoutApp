package com.dhiviyad.workoutapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.dhiviyad.workoutapp.dataLayer.UserDetails;
import com.dhiviyad.workoutapp.dataLayer.WorkoutDetails;
import com.dhiviyad.workoutapp.database.DatabaseHelper;
import com.dhiviyad.workoutapp.serializable.WorkoutLocationPoints;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class WorkoutRemoteService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,SensorEventListener {

    private static final String TAG = "WorkoutRemoteService";
    private static final long INTERVAL = 1000 * 2 * 1; // 15 s
    private static final long FASTEST_INTERVAL = 1000 * 2 * 1; // 15 s

    IWorkoutAidlInterface.Stub mBinder;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private SensorManager sensorManager;

    DatabaseHelper db;

    boolean recordingWorkout;
    WorkoutLocationPoints locationPoints;

    WorkoutDetails workout;
    long workoutStartTime; //in ms
    long workoutEndTime; //in ms


    public WorkoutRemoteService() { }

    @Override
    public void onCreate(){
        super.onCreate();
        createStepSensor();
        Log.v(TAG, "Remote service onCreate called");
        Toast.makeText(this, "remote service created", Toast.LENGTH_LONG).show();
        initAIDLBinder();
        initLocationService();
        createDB();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(recordingWorkout == true) db.saveWorkout(workout);
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        //todo: save data
        Log.v(TAG, "Remote service onDestroy called");
        Toast.makeText(this, "remote service stopped", Toast.LENGTH_LONG).show();
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) { return mBinder; }

    /************************************************
        LOCATION OVERRIDES
     ************************************************/
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected - isConnected .... " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }
    @Override
    public void onConnectionSuspended(int i) { Log.e(TAG, "Connection suspended: " + i);}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { Log.e(TAG, "Connection failed: " + connectionResult.toString()); }

    private float counter = 0;//todo: remove this later
    @Override
    public void onLocationChanged(Location location) {

//        Toast.makeText(this, "Firing onLocationChanged => " + recordingWorkout, Toast.LENGTH_LONG).show();
        Log.i(TAG, "Firing onLocationChanged...");

        double latitude = location.getLatitude() - counter;
        double longitude = location.getLongitude();
        counter += 0.0001; //todo: remove later

        Intent i = new Intent();
        i.setAction(IntentFilterNames.LOCATION_RECEIVED);

        if(recordingWorkout == true) {
            if(locationPoints == null) locationPoints = new WorkoutLocationPoints();
            locationPoints.add(latitude, longitude);
            i.putExtra(IntentFilterNames.LOCATION_DATA, locationPoints);

        } else {
            WorkoutLocationPoints pointsList = new WorkoutLocationPoints();
            pointsList.add(latitude, longitude);
            i.putExtra(IntentFilterNames.LOCATION_DATA, pointsList);
        }
        sendBroadcast(i);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(recordingWorkout == true && event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            synchronized (this) {
                if (workout == null) workout = new WorkoutDetails(System.currentTimeMillis(), db.fetchUserDetails());
                workout.addSteps();
                sendWorkoutBroadcast(workout);
            }
            Toast.makeText(this, "Firing onsensorchanged => " + workout.getStepsCount() + " => " + workout.getDistance(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void createStepSensor(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = sensorManager.getDefaultSensor( Sensor.TYPE_STEP_DETECTOR );
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void sendWorkoutBroadcast(WorkoutDetails workout) {
        float dist = workout.getDistance();
        sendDistanceBroadcast(dist);
        Intent i;
        i = new Intent();
        i.setAction(IntentFilterNames.WORKOUT_RECIEVED);
        i.putExtra(IntentFilterNames.WORKOUT_DATA,  workout);
        sendBroadcast(i);
    }

    private void sendDistanceBroadcast(float dist) {
        String val = String.format("%.3f", dist) ;
        Intent i = new Intent();
        i.setAction(IntentFilterNames.DISTANCE_RECIEVED);
        i.putExtra(IntentFilterNames.DISTANCE_DATA, val);
        sendBroadcast(i);
    }

    private void createDB(){
        db = new DatabaseHelper(getApplicationContext());
//        user = db.fetchUserDetails();
    }

    private void initAIDLBinder() {
        mBinder = new IWorkoutAidlInterface.Stub() {
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {}

            @Override
            public void startWorkout() {
                workoutStartTime = System.currentTimeMillis();
                if(locationPoints==null) locationPoints = new WorkoutLocationPoints();
                workout = new WorkoutDetails(workoutStartTime, db.fetchUserDetails());
                recordingWorkout = true;
            }
            @Override
            public void stopWorkout() {
                recordingWorkout = false;
                locationPoints = null;
                sendDistanceBroadcast(0);
                db.saveWorkout(workout);
                workout = null;
            }
            @Override
            public boolean getWorkoutState(){
                return recordingWorkout;
            }
        };
    }

    private void initLocationService() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int status = googleAPI.isGooglePlayServicesAvailable(this);
        if(ConnectionResult.SUCCESS != status) {
            Log.e(TAG, "GooglePlayServicesAvailable = false. ");
            return;
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "location permission needed!!!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Location permission not given!!!!!!!!");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.i(TAG, "Location update started ..............: ");
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        Toast.makeText(this, "Location update stopped....", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Location update stopped .......................");
    }

}
