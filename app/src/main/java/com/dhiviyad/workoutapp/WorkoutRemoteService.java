package com.dhiviyad.workoutapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.dhiviyad.workoutapp.dataLayer.GraphDetails;
import com.dhiviyad.workoutapp.dataLayer.WorkoutDetails;
import com.dhiviyad.workoutapp.database.DatabaseHelper;
import com.dhiviyad.workoutapp.serializable.WorkoutLocationPoints;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class WorkoutRemoteService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,SensorEventListener {

    private static final String TAG = "WorkoutRemoteService";
    private static final long INTERVAL = 1000 * 2 * 1; // 15 s
    private static final long FASTEST_INTERVAL = 1000 * 1 * 1; // 15 s

    IWorkoutAidlInterface.Stub mBinder;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private SensorManager sensorManager;
    DatabaseHelper db;

    boolean recordingWorkout;
    WorkoutLocationPoints locationPoints;
    WorkoutDetails workout;
    GraphDetails graphDetails;

    public WorkoutRemoteService() { }

    @Override
    public void onCreate(){
        super.onCreate();
        recordingWorkout = false;
        createStepSensor();
        Log.v(TAG, "Remote service onCreate called");
//        Toast.makeText(this, "remote service created", Toast.LENGTH_LONG).show();
        initAIDLBinder();
        initLocationService();
        createDB();
        registerBroadCastReceivers();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(recordingWorkout == true) db.saveWorkout(workout);
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        Log.v(TAG, "Remote service onDestroy called");
//        Toast.makeText(this, "remote service stopped", Toast.LENGTH_LONG).show();
        sensorManager.unregisterListener(this);
        unregisterBroadcastReceivers();
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
//        counter += 0.0001; //todo: remove later

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
//            synchronized (this) {
                if (workout == null) workout = new WorkoutDetails(System.currentTimeMillis(), db.fetchUserDetails());
                workout.addSteps();
                sendWorkoutBroadcast(workout);
//            }
//            Toast.makeText(this, "Firing onsensorchanged => " + workout.getStepsCount() + " => cal " + workout.getCaloriesBurnt() + " => dist "
//                    + workout.getDistance() , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    /******************************************************
     * Broadcast service code
     ******************************************************/
    ArrayList<MyBroadcastReceiver> broadcastReceivers;
    class MyBroadcastReceiver extends BroadcastReceiver {
        public MyBroadcastReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){

                case IntentFilterNames.SECOND_TIMER_RECEIVED:
                    handleSecondsTimer();
                    break;

                case IntentFilterNames.MIN_TIMER_RECIEVED:
                    handleMinutesTimer();
                    break;

                default: break;
            }
        }
    }

    private void registerBroadCastReceivers(){
        broadcastReceivers = new ArrayList<MyBroadcastReceiver>();
        createBroadcaseReceiver(IntentFilterNames.SECOND_TIMER_RECEIVED);
        createBroadcaseReceiver(IntentFilterNames.MIN_TIMER_RECIEVED);

        createNextAlarm();
        createMinutesAlarm();
    }

    private void createBroadcaseReceiver(String intentName){
        MyBroadcastReceiver r = new MyBroadcastReceiver();
        getApplicationContext().registerReceiver(r, new IntentFilter(intentName));
        broadcastReceivers.add(r);
    }

    private void unregisterBroadcastReceivers() {
        for(MyBroadcastReceiver br : broadcastReceivers){
            getApplicationContext().unregisterReceiver(br);
        }
        broadcastReceivers = null;
    }

    private void createNextAlarm() {
        Intent intent = new Intent(IntentFilterNames.SECOND_TIMER_RECEIVED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000), pendingIntent);
    }

    private void createMinutesAlarm(){
        Intent intent = new Intent(IntentFilterNames.MIN_TIMER_RECIEVED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 1000), pendingIntent);
    }

    private void handleSecondsTimer(){
        if(recordingWorkout == true){
            long curTime = System.currentTimeMillis();
            long millis = workout.getStartTime() - curTime;
            String timetext =  String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                            Math.abs(TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                            Math.abs(TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
            );
            sendSecondsBroadcast(timetext);
            workout.updateDuration(curTime);
        }
        createNextAlarm();
    }

    private void handleMinutesTimer(){
        if(recordingWorkout == true){
          sendCurrentGraphData();
        }
        createMinutesAlarm();
    }

    private void sendSecondsBroadcast(String timetext) {
        Intent i = new Intent();
        i.setAction(IntentFilterNames.TIME_RECEIVED);
        i.putExtra(IntentFilterNames.TIME_DATA,  timetext);
        sendBroadcast(i);
    }

    /********************************************
     * END OF BROADCAST RECEIVERS
     ********************************************/

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
        String val = StringUtils.getFormattedDistance(dist) ;
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
            public void startWorkout() { onStartWorkout(); }
            @Override
            public void stopWorkout() { onStopWorkout(); }
            @Override
            public boolean getWorkoutState(){
                return recordingWorkout;
            }
            @Override
            public void sendCurrentWorkoutData(){ if(recordingWorkout) sendWorkoutBroadcast(workout); }
            @Override
            public void sendDistanceData(){
                if(recordingWorkout) {
                    sendDistanceBroadcast(workout.getDistance());
                    sendInstantLocationBroadcast();
                }
            }
            @Override
            public void sendGraphData() { if(recordingWorkout) { sendCurrentGraphData(); } }
        };
    }
    private void sendCurrentGraphData(){
        graphDetails.addCurrentWorkout(workout);
        sendGraphDataBroadcast(graphDetails);
    }

    private void sendGraphDataBroadcast(GraphDetails graphData){
        if(graphData == null) return;
        Intent i = new Intent();
        i.setAction(IntentFilterNames.GRAPH_DATA_RECEIVED);
        i.putExtra(IntentFilterNames.GRAPH_DATA,graphData);
        sendBroadcast(i);
    }

    private void sendInstantLocationBroadcast(){
        if(locationPoints == null) return;
        Intent i = new Intent();
        i.setAction(IntentFilterNames.LOCATION_RECEIVED);
        i.putExtra(IntentFilterNames.LOCATION_DATA, locationPoints);
        sendBroadcast(i);
    }

    private void onStartWorkout() {
        long workoutStartTime = System.currentTimeMillis();
        if(locationPoints==null) locationPoints = new WorkoutLocationPoints();
        workout = new WorkoutDetails(workoutStartTime, db.fetchUserDetails());
        graphDetails = new GraphDetails();
        recordingWorkout = true;
    }

    private void onStopWorkout(){
        recordingWorkout = false;
        db.saveWorkout(workout);
        locationPoints = null;
        sendDistanceBroadcast(0);
        sendSecondsBroadcast("00:00:00");
        GraphDetails g = new GraphDetails();
        g.addCurrentWorkout(new WorkoutDetails());
        sendGraphDataBroadcast(g);
        graphDetails = null;
        workout = null;
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
