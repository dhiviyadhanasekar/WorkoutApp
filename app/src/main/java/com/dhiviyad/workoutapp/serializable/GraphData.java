package com.dhiviyad.workoutapp.serializable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dhiviyad on 11/4/16.
 */

public class GraphData implements Serializable {
    private ArrayList<Integer> caloriesData;
    private ArrayList<Float> distanceData;

    public void addCalorie(int calorie){
        caloriesData.add(calorie);
    }

    public void addDistance(float dist){
        distanceData.add(dist);
    }
}
