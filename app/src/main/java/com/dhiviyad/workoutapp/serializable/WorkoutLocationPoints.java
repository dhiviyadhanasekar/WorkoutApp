package com.dhiviyad.workoutapp.serializable;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dhiviyad on 10/31/16.
 */

public class WorkoutLocationPoints implements Serializable {

    private ArrayList<LocationPoint> locationPoints;

    public WorkoutLocationPoints(){
        locationPoints = new ArrayList<>();
    }

    public void add(double lat, double lon){
        locationPoints.add(new LocationPoint(lat, lon));
    }
    public ArrayList<LocationPoint> getLocationPoints(){
        return this.locationPoints;
    }

}
