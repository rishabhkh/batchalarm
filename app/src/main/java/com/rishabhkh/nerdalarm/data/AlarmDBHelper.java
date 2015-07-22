package com.rishabhkh.nerdalarm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rishabhkh.nerdalarm.data.AlarmContract.AlarmEntry;

/**
 * Created by Rishabh on 7/21/2015.
 */
public class AlarmDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "alarm.db";

    public AlarmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ALARMS_TABLE = "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                AlarmEntry._ID + " INTEGER PRIMARY KEY," +
                AlarmEntry.COLUMN_HOUR + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_MINUTE + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_FLAG + " INTEGER NOT NULL "+" );";
        Log.v("HelperOnCreate", SQL_CREATE_ALARMS_TABLE);
        db.execSQL(SQL_CREATE_ALARMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
        onCreate(db);
    }
}
