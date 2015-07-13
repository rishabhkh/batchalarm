package com.rishabhkh.nerdalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class AlarmHelper {
    final String TAG = "AlarmHelper";

    AlarmManager mAlarmManager;
    //Intent mIntent;
    Context mContext;
    SharedPreferences sharedPreferences;

    int mHour;
    int mMinute;
    int mNoAlarms;
    int mInterval;

    public AlarmHelper(Context context) {
        mContext = context ;
        sharedPreferences = context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //mIntent = new Intent(mContext, AlarmReceiver.class);
        mHour = sharedPreferences.getInt("hour", 0);
        mMinute = sharedPreferences.getInt("minute", 0);
        mNoAlarms = sharedPreferences.getInt("numofalarms", 0);
        mInterval = sharedPreferences.getInt("interval", 0);
    }

    public void cancelMultipleAlarms() {

        for (int i=0;i<mNoAlarms; i++) {
            cancelSingleAlarm(i);
        }
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("flag", false);
        editor.commit();
    }

    public void createMultipleAlarms() {
        int totMinutes;
        String label;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, mHour);
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long time = calendar.getTimeInMillis();

        for(int i=0;i<mNoAlarms;i++){
            totMinutes = mMinute + (mInterval * i);
            label = mHour+":"+totMinutes;
            createSingleAlarm(label, time, i);
            time = time + (mInterval*60000);
        }

    }

    public void createSingleAlarm(String label, long time, int reqCode) {
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra("reqcode", reqCode);
        Log.v(TAG, label+"Setting Alarm");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Log.v(TAG, "KITKAT");
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, time, getPendingIntent(reqCode));
        }
        else{
            //Log.v(TAG, "NOTKITKAT");
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, time, getPendingIntent(reqCode));
        }
    }

    public void cancelSingleAlarm(int reqCode){
        Log.v(TAG, "Cancelling:"+reqCode );
        mAlarmManager.cancel(getPendingIntent(reqCode));
    }

    public PendingIntent getPendingIntent(int reqCode) {
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        return PendingIntent.getBroadcast(mContext, reqCode, intent, 0);
    }

}
