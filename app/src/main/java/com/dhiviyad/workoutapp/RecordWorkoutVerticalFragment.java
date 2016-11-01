package com.dhiviyad.workoutapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class RecordWorkoutVerticalFragment extends Fragment implements OnMapReadyCallback {

    private final static String TAG = "VerticalFragment";
    View fragmentView;
    GoogleMap googleMap;
    PolylineOptions options = new PolylineOptions().width(10).color(Color.RED).geodesic(true);

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
                    Toast.makeText(context, "DATA FROM INTENT ==" + intent.getSerializableExtra(IntentFilterNames.LOCATION_DATA), Toast.LENGTH_SHORT).show();
                Log.v(TAG, "DATA FROM INTENT ==" + intent.getSerializableExtra(IntentFilterNames.LOCATION_DATA));
                break;

                default: break;
            }
        }
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
