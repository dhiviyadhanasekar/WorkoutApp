package com.dhiviyad.workoutapp.dataLayer;

/**
 * Created by dhiviyad on 10/30/16.
 */

public class WorkoutDetails {
    private float distance;
    private long duration;
    private long stepsCount;
    private float maxSpeed;
    private float minSpeed;
    private float avgSpeed;
    private double caloriesBurnt;
    private long workoutDate;
}

//todo: to calculate distance:
// Females: Your height x .413 = stride length
// Males: Your height x .415 = stride length

//1 step's cal burnt = 28/1000 if 100lb weight
// weight*28/1000
//100lb => 28/1000
//calories burnt => (weight * 28/1000)/100 (in lbs)
