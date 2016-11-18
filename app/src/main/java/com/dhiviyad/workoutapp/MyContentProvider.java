package com.dhiviyad.workoutapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.dhiviyad.workoutapp.database.DatabaseHelper;
import com.dhiviyad.workoutapp.database.UserDetailsTable;
import com.dhiviyad.workoutapp.database.WorkoutDetailsTable;

public class MyContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int fetchUserDataCode = 1;
    private static final int fetchWorkoutDataCode = 2;

    static {
        sUriMatcher.addURI("com.dhiviyad.workoutapp", "user", fetchUserDataCode);
        sUriMatcher.addURI("com.dhiviyad.workoutapp", "workout", fetchWorkoutDataCode);
    }

    DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public MyContentProvider() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (sUriMatcher.match(uri)){
            case fetchUserDataCode: return "vnd.android.cursor.item/user";
            case fetchWorkoutDataCode: return "vnd.android.cursor.item/workout";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long newRowId = 0;
        switch (sUriMatcher.match(uri)) {
            // If the incoming URI was for all of table3
            case fetchWorkoutDataCode:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                newRowId = db.insert(WorkoutDetailsTable.WorkoutEntry.TABLE_NAME, null, values);
                break;

            default:
                throw new UnsupportedOperationException("Uri not recognized");
                // If the URI is not recognized, you should do some error handling here.
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse("workout" + "/" + newRowId);
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        if( db == null ) return false;
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
//        throw new UnsupportedOperationException("Not yet implemented");
        Log.d("AppContentProvider", "AppContentProvider" + uri.toString());

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


          /*
         * Choose the table to query and a sort order based on the code returned for the incoming
         * URI. Here, too, only the statements for table 3 are shown.
         */
        switch (sUriMatcher.match(uri)) {
            // If the incoming URI was for all of table3
            case fetchUserDataCode:
                queryBuilder.setTables(UserDetailsTable.UserEntry.TABLE_NAME);
                break;

            default:
                throw new UnsupportedOperationException("Uri not recognized");
                // If the URI is not recognized, you should do some error handling here.
        }



//        SQLiteDatabase db = new DatabaseHelper(getContext()).getReadableDatabase();
//        Cursor cursor = db.rawQuery(UserDetailsTable.SQL_SELECT, null);
//        return cursor;

        Cursor c = queryBuilder.query(db, projection, selection,  selectionArgs,null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case fetchUserDataCode:
               return db.update(
                        UserDetailsTable.UserEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);


            default:
                throw new UnsupportedOperationException("Uri not recognized");
                // If the URI is not recognized, you should do some error handling here.
        }
    }
}
