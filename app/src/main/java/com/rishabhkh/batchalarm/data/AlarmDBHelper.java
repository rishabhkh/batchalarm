package com.rishabhkh.batchalarm.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.rishabhkh.batchalarm.data.AlarmContract.AlarmEntry;

public class AlarmDBHelper extends SQLiteOpenHelper {
    Context mContext;

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "alarm.db";

    public AlarmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ALARMS_TABLE = "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                AlarmEntry._ID + " INTEGER PRIMARY KEY," +
                AlarmEntry.COLUMN_HOUR + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_MINUTE + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_FLAG + " INTEGER NOT NULL "+" );";
        db.execSQL(SQL_CREATE_ALARMS_TABLE);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        Uri r = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(r==null)
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        editor.putString("alarmUri", String.valueOf(r));
        editor.apply();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
        onCreate(db);
    }
}
