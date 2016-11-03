package com.dhiviyad.workoutapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dhiviyad.workoutapp.serializable.LocationPoint;
import com.dhiviyad.workoutapp.serializable.WorkoutLocationPoints;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class RecordWorkoutVerticalFragment extends Fragment implements OnMapReadyCallback {

    private final static String TAG = "VerticalFragment";
    private final static int DARK_GREEN = Color.rgb(0, 102, 0);
    View fragmentView;
    GoogleMap googleMap;

    public RecordWorkoutVerticalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_workout_vertical, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        fragmentView = v;

        registerBroadCastReceivers();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v(TAG, "Fragment resumed....." + googleMap);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        for(MyBroadcastReceiver br : broadcastReceivers){
            getActivity().getApplicationContext().unregisterReceiver(br);
        }
        broadcastReceivers = null;
    }


//    /******************************************************
//     * Service code
//     ******************************************************/
//    IWorkoutAidlInterface remoteService;
//    RemoteConnection remoteConnection = null;
//
//    class RemoteConnection implements ServiceConnection {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            remoteService = IWorkoutAidlInterface.Stub.asInterface((IBinder) service);
//            Log.v(TAG, "remote service connected");
////            Toast.makeText(MainActivity.this, "remote service connected", Toast.LENGTH_LONG).show();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            remoteService = null;
////            Toast.makeText(MainActivity.this, "remote service disconnected", Toast.LENGTH_LONG).show();
//            Log.v(TAG, "remote service disconnected");
//        }
//    }

    /******************************************************
     * Broadcast service code
     ******************************************************/
    ArrayList<MyBroadcastReceiver> broadcastReceivers;
    class MyBroadcastReceiver extends BroadcastReceiver {
        public MyBroadcastReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Toast.makeText(context, "Intent detected => " + action, Toast.LENGTH_SHORT).show();

            switch(action){
                case IntentFilterNames.LOCATION_RECEIVED:
                    WorkoutLocationPoints pointsList = (WorkoutLocationPoints) intent.getSerializableExtra(IntentFilterNames.LOCATION_DATA);
                    drawWorkout(pointsList);
                    break;

                default: break;
            }
        }
    }

    private void drawWorkout(WorkoutLocationPoints pointsList){

        if(pointsList == null) {
            Log.e(TAG, "points is null");
            return;
        }

        ArrayList<LocationPoint> points = pointsList.getLocationPoints();
        if(points == null || points.size() <= 0){
            Log.e(TAG, "Expecting points - no points recieved");
            return;
        }

        LocationPoint startPoint = points.get(0);
        LocationPoint endPoint = points.get(points.size()-1);
        googleMap.clear();  //clears all Markers and Polylines

        if(points.size() > 1) {
            PolylineOptions options = new PolylineOptions().width(10).color(Color.RED).geodesic(true);
            for (LocationPoint p : points) {
                LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
                options.add(latLng);
            }
            googleMap.addPolyline(options); //add Polyline
            googleMap.addCircle(new CircleOptions()
                    .center(new LatLng(endPoint.getLatitude(), endPoint.getLongitude())) //end location
                    .radius(7)
                    .strokeColor(DARK_GREEN)
                    .fillColor(DARK_GREEN));
        }

        googleMap.addCircle(new CircleOptions()
                .center(new LatLng(startPoint.getLatitude(), startPoint.getLongitude())) //start location
                .radius(7)
                .strokeColor(Color.BLUE)
                .fillColor(Color.BLUE));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(endPoint.getLatitude(), endPoint.getLongitude()),
                (float) 17));
    }

    private void registerBroadCastReceivers(){
        broadcastReceivers = new ArrayList<MyBroadcastReceiver>();
        createBroadcaseReceiver(IntentFilterNames.LOCATION_RECEIVED);
//        createBroadcaseReceiver(IntentFilterNames.TEST_RECEIVED);
    }

    private void createBroadcaseReceiver(String intentName){
        MyBroadcastReceiver r = new MyBroadcastReceiver();
        getActivity().getApplicationContext().registerReceiver(r, new IntentFilter(intentName));
        broadcastReceivers.add(r);
    }

}
