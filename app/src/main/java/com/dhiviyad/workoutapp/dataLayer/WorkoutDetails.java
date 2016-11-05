package com.dhiviyad.workoutapp.dataLayer;

import java.io.Serializable;

/**
 * Created by dhiviyad on 10/30/16.
 */

public class WorkoutDetails implements Serializable{
    private float distance;
    private long duration;
    private long stepsCount;
    private float maxSpeed;
    private float minSpeed;
    private float avgSpeed;
    private long caloriesBurnt;
    private long workoutDate;

    public WorkoutDetails(long workoutDate) {
        this.workoutDate = workoutDate;
        stepsCount = 0;
    }

    public long getStepsCount() {
        return stepsCount;
    }

    public void addSteps(){
        stepsCount++;
    }
}

//todo: to calculate distance:
// Females: Your height x .413 = stride length (in ft)
// Males: Your height x .415 = stride length (in ft)
//1 ft = 0.000189394 miles

//1 step's cal burnt = 28/1000 if 100lb weight
// weight*28/1000
//100lb => 28/1000
//calories burnt => (weight * 28/1000)/100 (in lbs)
