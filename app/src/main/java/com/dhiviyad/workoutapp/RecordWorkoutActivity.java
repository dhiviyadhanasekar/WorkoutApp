package com.dhiviyad.workoutapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.telecom.RemoteConnection;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;


public class RecordWorkoutActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private float counter = 0;//todo: remove this later
    private boolean recordWorkout = false;

    LocationRequest mLocationRequest;
    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    GoogleApiClient mGoogleApiClient;
    Location startLocation = null, currentLocation = null;
//    long mLastUpdateTime;
    GoogleMap googleMap;
    PolylineOptions options = new PolylineOptions().width(10).color(Color.RED).geodesic(true);

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5 * 1; // 15 s
    private static final long FASTEST_INTERVAL = 1000 * 5 * 1; // 15 s

    IWorkoutAidlInterface remoteService;
    RemoteConnection remoteConnection = null;

    class RemoteConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IWorkoutAidlInterface.Stub.asInterface((IBinder) service);
            Toast.makeText(RecordWorkoutActivity.this, "remote service connected", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            remoteService = null;
            Toast.makeText(RecordWorkoutActivity.this, "remote service disconnected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        setContentView(R.layout.activity_record_workout);

        remoteConnection = new RemoteConnection();
        Intent intent = new Intent();
        intent.setClassName("com.dhiviyad.workoutapp", com.dhiviyad.workoutapp.WorkoutRemoteService.class.getName());
        if(!bindService(intent, remoteConnection, BIND_AUTO_CREATE)){
            Toast.makeText(RecordWorkoutActivity.this, "failed to bind remote service", Toast.LENGTH_LONG).show();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(remoteConnection);
        remoteConnection = null;
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = googleAPI.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            googleAPI.getErrorDialog(this, status, 0).show();//getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }
    @Override
    public void onStop() {
        super.onStop();
        super.onStop();
        Log.i(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.i(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PackageManager.PERMISSION_GRANTED);
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.i(TAG, "Location update started ..............: ");
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.toString());
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Firing onLocationChanged..............................................");
        currentLocation = location;
        if(recordWorkout == true) {
//        mLastUpdateTime = Calendar.getInstance().getTimeInMillis();
            if(startLocation == null) startLocation = currentLocation;
            drawLine(); //added
        } else markCurrentLocation();
    }

    private void markCurrentLocation(){
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        googleMap.clear();
        googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(8)
                .strokeColor(Color.BLUE)
                .fillColor(Color.BLUE));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                (float) 17));
        startLocation = null;
        counter = 0;
    }

    private void drawLine(){
        LatLng latLng = new LatLng(currentLocation.getLatitude()-counter, currentLocation.getLongitude());
        counter += 0.002;
        options.add(latLng);
        googleMap.clear();  //clears all Markers and Polylines
        googleMap.addPolyline(options); //add Polyline
        googleMap.addCircle(new CircleOptions()
                .center(new LatLng(startLocation.getLatitude(), startLocation.getLongitude()))
                .radius(8)
                .strokeColor(Color.BLUE)
                .fillColor(Color.BLUE));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                (float) 17));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.i(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.i(TAG, "Location update resumed .....................");
        }
    }

    public void toggleWorkout(View v){
        Log.i(TAG, "Clicked button");
        Button b = (Button) findViewById(R.id.workout_toggle_button);
        String buttonStr =  b.getText().toString().toLowerCase();
        if(buttonStr.equals("start workout")){
            buttonStr = "Stop workout";
            recordWorkout = true;
        } else {
            buttonStr = "Start workout";
            recordWorkout = false;
        }
//        try {
//            Log.v(TAG, "RService valueeeee ===== " + remoteService.square((int)(1+counter*1000)));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        b.setText(buttonStr);
    }

}
