package com.dhiviyad.workoutapp;

import android.content.ComponentName;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.PolylineOptions;


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


//    /***********
//     * Service code
//     ***********/
//    IWorkoutAidlInterface remoteService;
//    MainActivity.RemoteConnection remoteConnection = null;
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

}
