package com.dhiviyad.workoutapp.database;

import android.provider.BaseColumns;

import com.dhiviyad.workoutapp.dataLayer.WorkoutDetails;

/**
 * Created by dhiviyad on 11/1/16.
 */

public final class WorkoutDetailsTable {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private WorkoutDetailsTable(){}

    public static class WorkoutEntry implements BaseColumns{ /* Inner class that defines the table contents */
        public static final String TABLE_NAME = "workout";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_TIME = "time"; //duration in milliseconds
        public static final String COLUMN_CALORIES_BURNED = "calories_burned";
        public static final String COLUMN_DATE = "date";
    }
    public static final String SQL_CREATE_ENTRY = "CREATE TABLE IF NOT EXISTS " + WorkoutEntry.TABLE_NAME + " (" +
            WorkoutEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            WorkoutEntry.COLUMN_DISTANCE + DatabaseFieldTypes.REAL + DatabaseFieldTypes.COMMA_SEP +
            WorkoutEntry.COLUMN_TIME + DatabaseFieldTypes.INTEGER_TYPE + DatabaseFieldTypes.COMMA_SEP+
            WorkoutEntry.COLUMN_CALORIES_BURNED + DatabaseFieldTypes.REAL + DatabaseFieldTypes.COMMA_SEP+
            WorkoutEntry.COLUMN_DATE + DatabaseFieldTypes.DATE_TIME + "DEFAULT CURRENT_DATE )";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + WorkoutEntry.TABLE_NAME;
    public static final String SQL_SELECT_ALL = "SELECT SUM(" + WorkoutDetailsTable.WorkoutEntry.COLUMN_DISTANCE + ") AS " + WorkoutDetailsTable.WorkoutEntry.COLUMN_DISTANCE
            + DatabaseFieldTypes.COMMA_SEP
            + " SUM(" + WorkoutDetailsTable.WorkoutEntry.COLUMN_CALORIES_BURNED + ") AS " + WorkoutDetailsTable.WorkoutEntry.COLUMN_CALORIES_BURNED
            + DatabaseFieldTypes.COMMA_SEP
            + " SUM(" + WorkoutDetailsTable.WorkoutEntry.COLUMN_TIME + ") AS " + WorkoutDetailsTable.WorkoutEntry.COLUMN_TIME
            + DatabaseFieldTypes.COMMA_SEP
            + " COUNT(*) AS ROWS_COUNT FROM " + WorkoutDetailsTable.WorkoutEntry.TABLE_NAME;

    public static final String SQL_SELECT_WEEKLY = SQL_SELECT_ALL + " WHERE  strftime('%W', " +WorkoutEntry.COLUMN_DATE + ") = strftime('%W', 'now')";

}