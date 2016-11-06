package com.dhiviyad.workoutapp.dataLayer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dhiviyad on 11/5/16.
 */

public class GraphDetails implements Serializable {
    ArrayList<Long> caloriesEveryFiveMins;
    ArrayList<Long> distanceEveryFiveMins;
    private float maxSpeed;
    private float minSpeed;
    private float sumSpeed;
    private long workoutCount; // to update every 5 mins

}
