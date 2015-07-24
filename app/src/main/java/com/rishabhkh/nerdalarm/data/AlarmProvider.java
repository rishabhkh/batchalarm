package com.rishabhkh.nerdalarm.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class AlarmProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.rishabhkh.nerdalarm";
    static final String URL = "content://" + PROVIDER_NAME + "/alarms";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    static final int ALARMS = 1;
    static final int ALARM_ID = 2;

    SQLiteDatabase db;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "alarms", ALARMS);
        uriMatcher.addURI(PROVIDER_NAME, "alarms/#", ALARM_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        AlarmDBHelper dbHelper = new AlarmDBHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db!=null);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(	AlarmContract.AlarmEntry.TABLE_NAME, "", values);
        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(AlarmContract.AlarmEntry.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ALARMS:
                break;

            case ALARM_ID:
                qb.appendWhere( AlarmContract.AlarmEntry._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;

        switch (uriMatcher.match(uri)){
            case ALARMS:
                count = db.delete(AlarmContract.AlarmEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case ALARM_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(AlarmContract.AlarmEntry.TABLE_NAME, AlarmContract.AlarmEntry._ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;

        switch (uriMatcher.match(uri)){
            case ALARMS:
                count = db.update(AlarmContract.AlarmEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case ALARM_ID:
                count = db.update(AlarmContract.AlarmEntry.TABLE_NAME, values, AlarmContract.AlarmEntry._ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
       return null;
    }
}