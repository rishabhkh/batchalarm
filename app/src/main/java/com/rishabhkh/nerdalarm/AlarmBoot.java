package com.rishabhkh.nerdalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class AlarmBoot extends BroadcastReceiver {
    public final String TAG = "AlarmBoot";


    public AlarmBoot() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Hi");
        AlarmHelper alarmHelper = new AlarmHelper(context);
        SharedPreferences sharedPreferences =  context.getApplicationContext().getSharedPreferences("Alarm", context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt("hour", 0);
        int minute = sharedPreferences.getInt("minute", 0);
        for(int i=0;i<=5;i++){
            alarmHelper.createAlarm(String.valueOf(i),hour,minute+i,i);
        }

    }
}
