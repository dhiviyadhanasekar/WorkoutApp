package com.dhiviyad.workoutapp.dataLayer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dhiviyad on 11/5/16.
 */

public class GraphDetails implements Serializable {
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
        maxSpeed = 0;
        minSpeed = 0;
        sumSpeed = 0;
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

        long curTime = w.getDuration();
        float dist = w.getDistance();

        caloriesEveryFiveMins.add(w.getCaloriesBurnt());
        distanceEveryFiveMins.add(dist);
        time.add(curTime);

        float speed = curTime/dist; //milliseconds/miles
        if(speed > maxSpeed) maxSpeed = speed;
        if(speed < minSpeed) minSpeed = speed;
        sumSpeed += speed;
    }
}
