package com.dhiviyad.workoutapp.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by dhiviyad on 11/14/16.
 */

public class UserContentProvider extends ContentProvider{

    private static final String PROVIDER_NAME = "com.dhiviyad.workoutapp.database";
    public static final String URL = "content://" + PROVIDER_NAME + "/" + UserDetailsTable.UserEntry.TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    static final int USER = 1;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,  UserDetailsTable.UserEntry.TABLE_NAME, USER);
    }

    DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private static HashMap<String, String> UserMap;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        if( db == null ) return false;
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        return null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(UserDetailsTable.UserEntry.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case USER:
                queryBuilder.setProjectionMap(UserMap);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = queryBuilder.query(db, projection, selection,  selectionArgs,null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case USER: return "vnd.android.cursor.dir/vnd.workoutapp.usercontentprovider.user";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case USER:
                count = db.update(UserDetailsTable.UserEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;

    }
}
