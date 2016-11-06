package com.dhiviyad.workoutapp.dataLayer;

import java.io.Serializable;

/**
 * Created by dhiviyad on 10/30/16.
 */

public class WorkoutDetails implements Serializable{
    private float distance;
    private long duration;
    private long stepsCount;
    private long caloriesBurnt;
    private long startTime;

    private double strideLength;
    private double caloriePerStep;

    public WorkoutDetails(long startTime, UserDetails user) {
        this.startTime = startTime;
        stepsCount = 0;
        if(user.getGender().toUpperCase().equals("F")){
            strideLength = user.getHeight() * 0.413;
        } else strideLength = user.getHeight() * 0.415;
        strideLength *= 0.00019; //in mi
        caloriePerStep = (user.getWeight() * 28/10000)/100;

    }

    public long getStepsCount() {
        return stepsCount;
    }
    public float getDistance() {
        return distance;
    }

    public void addSteps(){
        stepsCount++;
        distance = (float) (stepsCount * strideLength);
        caloriesBurnt = (long)(stepsCount * caloriePerStep);
    }
}

//todo: to calculate distance:
// Females: Your height x .413 = stride length (in ft)
// Males: Your height x .415 = stride length (in ft)
//1 ft = 0.000189394 miles
//distance = stride length * 0.000189394 * stepsCount (in mi)

//1 step's cal burnt = 28/1000 if 100lb weight
// weight*28/1000
//100lb => 28/1000
//calories burnt => (stepsCount * weight * 28/1000)/100 (in lbs)
