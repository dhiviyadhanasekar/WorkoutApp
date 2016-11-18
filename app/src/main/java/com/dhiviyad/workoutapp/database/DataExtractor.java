package com.dhiviyad.workoutapp.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.dhiviyad.workoutapp.dataLayer.UserDetails;
import com.dhiviyad.workoutapp.dataLayer.WorkoutDetails;

/**
 * Created by dhiviyad on 11/17/16.
 */

public class DataExtractor {

    public static final String CONTENT_COM_DHIVIYAD_WORKOUTAPP_USER = "content://com.dhiviyad.workoutapp/user";
    public static final String CONTENT_COM_DHIVIYAD_WORKOUTAPP_WORKOUT = "content://com.dhiviyad.workoutapp/workout";

    public static UserDetails getUserData(ContentResolver contentResolver){

        String[] mProjection =
                {
                        UserDetailsTable.UserEntry.COLUMN_GENDER,
                        UserDetailsTable.UserEntry.COLUMN_HEIGHT,
                        UserDetailsTable.UserEntry.COLUMN_ID,
                        UserDetailsTable.UserEntry.COLUMN_USERNAME,
                        UserDetailsTable.UserEntry.COLUMN_WEIGHT
                };

        Cursor cursor = contentResolver.query(
                getUserTableUri(),   // The content URI of the words table
                mProjection,                        // The columns to return for each row
                null,                    // Selection criteria
                null,                     // Selection criteria
                null);                      // The sort order for the returned rows
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

    private static Uri getUserTableUri() {
        return Uri.parse(CONTENT_COM_DHIVIYAD_WORKOUTAPP_USER);
    }

    public static void updateUserData(ContentResolver contentResolver, UserDetails details, int userId){

        ContentValues values = getUserTableContentValues(details);

        // Which row to update, based on the title
        String selection = UserDetailsTable.UserEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { userId+"" };

        int count = contentResolver.update(
                getUserTableUri(),   // the user dictionary content URI
                values,                       // the columns to update
                selection,                    // the column to select on
                selectionArgs                      // the value to compare to
        );
        Log.i("User table updated", "count =" + count);
    }

    private static ContentValues getUserTableContentValues(UserDetails details) {
        ContentValues values = new ContentValues();
        values.put(UserDetailsTable.UserEntry.COLUMN_USERNAME, details.getName());
        values.put(UserDetailsTable.UserEntry.COLUMN_GENDER, details.getGender());
        values.put(UserDetailsTable.UserEntry.COLUMN_WEIGHT, details.getWeight());
        values.put(UserDetailsTable.UserEntry.COLUMN_HEIGHT, details.getHeight());
        return values;
    }

    public static Uri saveWorkout(ContentResolver contentResolver, WorkoutDetails w){
        if(w == null) return null;
//        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WorkoutDetailsTable.WorkoutEntry.COLUMN_CALORIES_BURNED, w.getCaloriesBurnt());
        values.put(WorkoutDetailsTable.WorkoutEntry.COLUMN_DISTANCE, w.getDistance());
        values.put(WorkoutDetailsTable.WorkoutEntry.COLUMN_TIME, w.getDuration());

        // Insert the new row, returning the primary key value of the new row
//        long newRowId = db.insert(WorkoutDetailsTable.WorkoutEntry.TABLE_NAME, null, values);
        Uri newRowId =  contentResolver.insert(
                Uri.parse(CONTENT_COM_DHIVIYAD_WORKOUTAPP_WORKOUT),   // the user dictionary content URI
                values                          // the values to insert
        );
        Log.i("Workout row created => ", "id is" + newRowId );
        return newRowId;

    }
}
