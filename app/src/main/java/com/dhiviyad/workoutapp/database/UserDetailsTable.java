package com.dhiviyad.workoutapp.database;

import android.provider.BaseColumns;

/**
 * Created by dhiviyad on 11/1/16.
 */

public final class UserDetailsTable {

    private UserDetailsTable(){}

    public static class UserEntry implements BaseColumns { /* Inner class that defines the table contents */

        public static final String TABLE_NAME = "user";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_WEIGHT  = "weight";
        public static final String COLUMN_HEIGHT = "height";
    }
    public static final String SQL_CREATE_ENTRY = "CREATE TABLE IF NOT EXISTS " + UserEntry.TABLE_NAME + " (" +
            UserEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            UserEntry.COLUMN_USERNAME + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            UserEntry.COLUMN_GENDER + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP+
            UserEntry.COLUMN_WEIGHT + DatabaseFieldTypes.REAL + DatabaseFieldTypes.COMMA_SEP +
            UserEntry.COLUMN_HEIGHT + DatabaseFieldTypes.REAL + " )";

    public static final java.lang.String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

}
