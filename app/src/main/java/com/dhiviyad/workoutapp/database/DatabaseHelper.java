package com.dhiviyad.workoutapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dhiviyad on 11/1/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AlphaWorkouts.db";
    public static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WorkoutDetailsTable.SQL_CREATE_ENTRY);
        db.execSQL(UserDetailsTable.SQL_CREATE_ENTRY);
    }

    // This database is only a cache for online data, so its upgrade policy is
    // to simply to discard the data and start over
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(WorkoutDetailsTable.SQL_DELETE_ENTRIES);
        db.execSQL(UserDetailsTable.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

//
//public class DataBaseHelper extends SQLiteOpenHelper {
//
//    private static final String DATABASE_NAME = "AlphaFitness";
//
//    private static final int DATABASE_VERSION = 2;
//
//    public DataBaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    // Method is called during creation of the database
//    @Override
//    public void onCreate(SQLiteDatabase database) {
//        database.execSQL(UserTable.CREATE_ENTRIES);
//        database.execSQL(StepCounterTable.CREATE_ENTRIES);
//    }
//
//    // Method is called during an upgrade of the database,
//    @Override
//    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
//        Log.w(DataBaseHelper.class.getName(),
//                "Upgrading database from version " + oldVersion + " to "
//                        + newVersion + ", which will destroy all old data");
//        //database.execSQL("DROP TABLE IF EXISTS MyEmployees");
//        onCreate(database);
//    }
//
//    public long insertStepCounterTable() {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(StepCounterTable.C_STARTTIME, "datetime()");
//        values.put(StepCounterTable.C_COUNT, 0);
//
//        // insert row
//        long id = db.insert(StepCounterTable.TABLE_NAME, null, values);
//        return id;
//    }
//}