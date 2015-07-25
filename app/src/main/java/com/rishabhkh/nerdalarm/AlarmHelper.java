package com.rishabhkh.nerdalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import com.rishabhkh.nerdalarm.data.AlarmContract;
import com.rishabhkh.nerdalarm.data.AlarmProvider;


import java.util.Calendar;



public class AlarmHelper {
    final static String TAG = "AlarmHelper";

    AlarmManager mAlarmManager;
    Intent mIntent;
    Context mContext;
    ContentResolver contentResolver;

    public AlarmHelper(Context context) {
        contentResolver = context.getContentResolver();
        mContext = context ;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mIntent = new Intent(mContext, AlarmReceiver.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    public void cancelMultipleAlarms() {
        Cursor cursor = contentResolver.query(AlarmProvider.CONTENT_URI, null, null, null, null);
        int numOfAlarms = cursor.getCount();
        cursor.close();
        for (int i=1;i<=numOfAlarms; i++) {
            cancelSingleAlarm(i);
        }
    }

    public void createMultipleAlarms(int toastFlag,int editFlag) {
        Cursor cursor = contentResolver.query(AlarmProvider.CONTENT_URI, null, null, null, null);
        int numOfAlarms = cursor.getCount();//Log.v(TAG, "Number of Alarms="+numOfAlarms);
        if(numOfAlarms!=0) {
            cursor.moveToFirst();
            int hour = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_HOUR));
            int minute = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_MINUTE));

            if(toastFlag==1)
                toastDifference(hour, minute,1);


            for(int i=1;i<=numOfAlarms;i++) {
                //Log.v(TAG, "Loop i="+i);
                createSingleAlarm(i, 0, editFlag);
            }
        }
        cursor.close();
    }

    public void createSingleAlarm(int _ID,int toastFlag,int editToastFlag) {
        String id = "/"+_ID;
        Uri uri = Uri.parse(AlarmProvider.CONTENT_URI + id);

        if(editToastFlag == 1) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AlarmContract.AlarmEntry.COLUMN_FLAG, 1);
            contentResolver.update(uri, contentValues, null, null);
        }
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        int hour = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_HOUR));
        int minute = cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_MINUTE));
        long time = timeInMillis(hour, minute );
        int flag =  cursor.getInt(cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_FLAG));
        cursor.close();

        if(toastFlag == 1)
            toastDifference(hour, minute,0);

        if(flag==1){
            mIntent.putExtra("_ID", _ID);
            Log.v(TAG, "Setting Alarm:" + _ID+":"+time);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, time, getPendingIntent(_ID));
            }
            else{
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, time, getPendingIntent(_ID));
            }
        }
    }

    public void cancelSingleAlarm(int _ID){
        Log.v(TAG, "Cancelling:" + _ID);
        mAlarmManager.cancel(getPendingIntent(_ID));
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlarmContract.AlarmEntry.COLUMN_FLAG, "0");
        Uri uri = Uri.parse(AlarmProvider.CONTENT_URI + "/" + _ID);
        contentResolver.update(uri, contentValues, null, null);
    }

    public PendingIntent getPendingIntent(int _ID) {
        //Intent intent = new Intent(mContext, AlarmReceiver.class);
        return PendingIntent.getBroadcast(mContext, _ID, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void toastDifference(int hour,int minute,int firstFlag){
        long alarmTime = timeInMillis(hour, minute);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long difference = alarmTime - currentTime;
        long diffHour = difference/3600000;
        long diffMinute = (difference%3600000)/60000;
        String toastMessage;
        if(!(firstFlag==1)) {
            toastMessage = "Alarm set " + diffHour + " hour(s) " + diffMinute + " minute(s) from now.";
            if (diffHour == 0) {
                toastMessage = "Alarm set " + diffMinute + " minute(s) from now.";
                if (diffMinute == 0)
                    toastMessage = "Alarm set less than 1 minute from now.";
            }
        }
        else {
            toastMessage = "First alarm set " + diffHour + " hour(s) " + diffMinute + " minute(s) from now.";
            if (diffHour == 0) {
                toastMessage = "First alarm set " + diffMinute + " minute(s) from now.";
                if (diffMinute == 0)
                    toastMessage = "First alarm set less than 1 minute from now.";
            }
        }
        Toast.makeText(mContext, toastMessage,
                Toast.LENGTH_SHORT).show();

    }

    public static long timeInMillis(int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        long timeBefore = calendar.getTimeInMillis();
        Log.v("Before",""+timeBefore);
        if(Calendar.getInstance().getTimeInMillis()>timeBefore)
            calendar.add(Calendar.DAY_OF_WEEK, 1);


        Log.v("Before",""+calendar.getTimeInMillis());
        return calendar.getTimeInMillis();
    }

}
