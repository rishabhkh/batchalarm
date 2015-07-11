package com.rishabhkh.nerdalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class AlarmHelper {
    final String TAG = "AlarmHelper";
    AlarmManager mAlarmManager;
    Calendar mCalendar;
    Context mContext;
    Intent mIntent;

    public AlarmHelper(Context context){
        mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mCalendar = Calendar.getInstance();
        mContext = context;
        mIntent = new Intent(mContext, AlarmReceiver.class);

    }

    public void createAlarm(String label, int hour, int minute, int reqCode) {

        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        mIntent.putExtra("label", label);
        Log.v(TAG, "Setting Alarm:" + reqCode + "Time:" + hour + ":" + minute);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.v(TAG, "KITKAT");
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), getPendingIntent(reqCode));
        }
        else{
            Log.v(TAG, "NOTKITKAT");
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), getPendingIntent(reqCode));
        }
    }

    public PendingIntent getPendingIntent(int reqCode) {
        return PendingIntent.getBroadcast(mContext, reqCode, mIntent, 0);
    }

}
