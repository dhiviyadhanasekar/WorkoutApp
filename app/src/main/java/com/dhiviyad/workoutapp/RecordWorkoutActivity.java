package com.dhiviyad.workoutapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
//import android.support.v4.app.FragmentActivity;


public class RecordWorkoutActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_workout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //AIzaSyCbL6GHfzFFMDj_DP0H_GHIFwo4MeGEWyQ
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("In onMapReady");
//        myMap = googleMap;
    }
}
