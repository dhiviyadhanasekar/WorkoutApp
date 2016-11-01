package com.dhiviyad.workoutapp.serializable;

import java.io.Serializable;

/**
 * Created by dhiviyad on 10/31/16.
 */

public class LocationPoint implements Serializable {
    private double latitude;
    private double longitude;

    public LocationPoint(double lat, double lon){
        this.latitude = lat;
        this.longitude = lon;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }
}
