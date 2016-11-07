package com.dhiviyad.workoutapp;

import java.util.concurrent.TimeUnit;

/**
 * Created by dhiviyad on 11/5/16.
 */

public class StringUtils {

    public static String getFormattedDistance(float dist){
        return String.format("%.3f", dist);
    }

    public static String getFormattedTime(long millis){
        return String.format("%02dh %02dm %02ds", TimeUnit.MILLISECONDS.toHours(millis),
                Math.abs(TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                Math.abs(TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
    }
}
