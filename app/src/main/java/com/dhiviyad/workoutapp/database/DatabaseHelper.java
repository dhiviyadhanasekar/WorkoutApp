package com.dhiviyad.workoutapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dhiviyad.workoutapp.dataLayer.UserDetails;
import com.dhiviyad.workoutapp.dataLayer.WorkoutDetails;

/**
 * Created by dhiviyad on 11/1/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AlphaWorkouts";
    public static final int DATABASE_VERSION = 2;


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(DATABASE_NAME, "in create");
        db.execSQL(WorkoutDetailsTable.SQL_CREATE_ENTRY);
        db.execSQL(UserDetailsTable.SQL_CREATE_ENTRY);
        if(doesUserRecordExist(db) == false){
            UserDetails u = getDefaultUserDetails();
            createUserTableRow(u, db);
        }
    }

    @NonNull
    private UserDetails getDefaultUserDetails() {
        return new UserDetails("Enter name", "M", (double)120, (double)5.7);
    }

    // This database is only a cache for online data, so its upgrade policy is
    // to simply to discard the data and start over
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(WorkoutDetailsTable.SQL_DELETE);
        db.execSQL(UserDetailsTable.SQL_DELETE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void updateUserDetails(UserDetails details, int userId){
        SQLiteDatabase db = this.getReadableDatabase();

        // New value for each column
        ContentValues values = getUserTableContentValues(details);

        // Which row to update, based on the title
        String selection = UserDetailsTable.UserEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { userId+"" };

        int count = db.update(
                UserDetailsTable.UserEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        Log.i("User table updated", "count =" + count);
    }

    @NonNull
    private ContentValues getUserTableContentValues(UserDetails details) {
        ContentValues values = new ContentValues();
        values.put(UserDetailsTable.UserEntry.COLUMN_USERNAME, details.getName());
        values.put(UserDetailsTable.UserEntry.COLUMN_GENDER, details.getGender());
        values.put(UserDetailsTable.UserEntry.COLUMN_WEIGHT, details.getWeight());
        values.put(UserDetailsTable.UserEntry.COLUMN_HEIGHT, details.getHeight());
        return values;
    }

    private long createUserTableRow(UserDetails details, SQLiteDatabase db) {
        // Gets the data repository in write mode
//        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = getUserTableContentValues(details);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(UserDetailsTable.UserEntry.TABLE_NAME, null, values);
        Log.i("User table created => ", "userID is" + newRowId );
        return newRowId;
    }


    public boolean doesUserRecordExist(SQLiteDatabase db) {
        if(db == null) db = this.getReadableDatabase();
        Cursor c = db.rawQuery(UserDetailsTable.SQL_SELECT, null);
        if(c != null && c.getCount() > 0){
            Log.i("updating user row", "true");
            c.close();
            return true;
        }
        else{
            Log.i("creating user row", "true");
            c.close();
            return false;
        }

    }

    public UserDetails fetchUserDetails(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(UserDetailsTable.SQL_SELECT, null);
        UserDetails u = new UserDetails();
        if (cursor.moveToFirst()) {
            u.setId( cursor.getInt( cursor.getColumnIndex(UserDetailsTable.UserEntry.COLUMN_ID) ) ); // id is column name in db
            u.setName( cursor.getString( cursor.getColumnIndex(UserDetailsTable.UserEntry.COLUMN_USERNAME ) ) );
            u.setGender( cursor.getString( cursor.getColumnIndex(UserDetailsTable.UserEntry.COLUMN_GENDER ) ) );
            u.setHeight( cursor.getDouble( cursor.getColumnIndex(UserDetailsTable.UserEntry.COLUMN_HEIGHT ) ) );
            u.setWeight( cursor.getDouble( cursor.getColumnIndex(UserDetailsTable.UserEntry.COLUMN_WEIGHT ) ) );
        }
        cursor.close();
        return u;
    }

    public long saveWorkout(WorkoutDetails w){
        if(w == null) return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WorkoutDetailsTable.WorkoutEntry.COLUMN_CALORIES_BURNED, w.getCaloriesBurnt());
        values.put(WorkoutDetailsTable.WorkoutEntry.COLUMN_DISTANCE, w.getDistance());
        values.put(WorkoutDetailsTable.WorkoutEntry.COLUMN_TIME, w.getDuration());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(WorkoutDetailsTable.WorkoutEntry.TABLE_NAME, null, values);
        Log.i("Workout row created => ", "id is" + newRowId );
        return newRowId;
    }

    public WorkoutDetails getTotalWorkout(){
        WorkoutDetails w = new WorkoutDetails();
        String query = "SELECT SUM(" + WorkoutDetailsTable.WorkoutEntry.COLUMN_DISTANCE + ") AS " + WorkoutDetailsTable.WorkoutEntry.COLUMN_DISTANCE
                + DatabaseFieldTypes.COMMA_SEP
                + " SUM(" + WorkoutDetailsTable.WorkoutEntry.COLUMN_CALORIES_BURNED + ") AS " + WorkoutDetailsTable.WorkoutEntry.COLUMN_CALORIES_BURNED
                + DatabaseFieldTypes.COMMA_SEP
                + " SUM(" + WorkoutDetailsTable.WorkoutEntry.COLUMN_TIME + ") AS " + WorkoutDetailsTable.WorkoutEntry.COLUMN_TIME
                + DatabaseFieldTypes.COMMA_SEP
                + " COUNT(*) AS ROWS_COUNT FROM " + WorkoutDetailsTable.WorkoutEntry.TABLE_NAME ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            long rowCount = cursor.getInt( cursor.getColumnIndex("ROWS_COUNT") );
            w.setWorkoutCount(rowCount);
            w.setDistance( cursor.getFloat( cursor.getColumnIndex(WorkoutDetailsTable.WorkoutEntry.COLUMN_DISTANCE )));
            w.setCaloriesBurnt( cursor.getInt(cursor.getColumnIndex(WorkoutDetailsTable.WorkoutEntry.COLUMN_CALORIES_BURNED)) );
            w.setDuration(cursor.getLong(cursor.getColumnIndex(WorkoutDetailsTable.WorkoutEntry.COLUMN_TIME)));
        }
        return w;
    }
}
