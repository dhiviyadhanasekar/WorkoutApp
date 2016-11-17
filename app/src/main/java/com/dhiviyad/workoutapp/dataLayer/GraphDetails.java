package com.dhiviyad.workoutapp.dataLayer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dhiviyad on 11/5/16.
 */

public class GraphDetails implements Serializable {

    float previousTotalCalories;
    float previousTotalDistance;
//    long previousTotalTime;

    ArrayList<Float> caloriesEveryFiveMins;
    ArrayList<Float> distanceEveryFiveMins;

    private float maxSpeed;
    private float minSpeed;
    private float sumSpeed;
    private ArrayList<Long> time; // to update every 5 mins

    public GraphDetails() {
        caloriesEveryFiveMins = new ArrayList<>();
        distanceEveryFiveMins = new ArrayList<>();
        time = new ArrayList<>();
        maxSpeed = 0.0f;
        minSpeed = Integer.MAX_VALUE;
        sumSpeed = 0.0f;
        previousTotalCalories = 0;
        previousTotalDistance = 0;
//        previousTotalTime = 0;
    }

    public ArrayList<Float> getCaloriesEveryFiveMins() {
        return caloriesEveryFiveMins;
    }

    public ArrayList<Float> getDistanceEveryFiveMins() {
        return distanceEveryFiveMins;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getMinSpeed() {
        return minSpeed;
    }

    public float getSumSpeed() {
        return sumSpeed;
    }

    public ArrayList<Long> getTime() {
        return time;
    }
    public void addCurrentWorkout(WorkoutDetails w){

        float calories = w.getCaloriesBurnt();
        long curTime = w.getDuration();
        float dist = w.getDistance();
        curTime = curTime/(1000); //* 60); //min

        float newCalories = Math.abs(calories-previousTotalCalories);
        caloriesEveryFiveMins.add(newCalories);
        previousTotalCalories = calories;

        float newDist = Math.abs(dist - previousTotalDistance);
        distanceEveryFiveMins.add(newDist);
        previousTotalDistance = dist;

        long newTime = curTime;
        if(time.size() > 0) {
            newTime =  Math.abs(curTime-time.get(time.size()-1));
        }
        time.add(curTime);

        dist = distanceEveryFiveMins.get(distanceEveryFiveMins.size()-1);
        float speed = dist>0 ? newTime/dist: 0; //min/miles
        if(speed > maxSpeed) maxSpeed = speed;
        if(speed>0 && speed < minSpeed) minSpeed = speed;
//        if(time.size() == 0) minSpeed = speed;
        sumSpeed += speed;
    }
}
