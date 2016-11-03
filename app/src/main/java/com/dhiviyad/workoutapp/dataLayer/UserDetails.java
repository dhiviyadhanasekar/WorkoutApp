package com.dhiviyad.workoutapp.dataLayer;

import java.io.Serializable;

/**
 * Created by dhiviyad on 10/30/16.
 */

public class UserDetails implements Serializable{


    private int id;
    private String name;
    private String gender;
    private double weight;
    private double height;
//    private WorkoutHistory weeklyWorkouts;
//    private WorkoutHistory allWorkouts;


    public UserDetails(){

    }
    public UserDetails(String name, String gender, double weight, double height) {
        this.name = name;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
