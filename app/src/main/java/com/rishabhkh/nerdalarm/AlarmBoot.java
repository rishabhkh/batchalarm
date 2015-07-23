package com.rishabhkh.nerdalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBoot extends BroadcastReceiver {
    public final String TAG = "AlarmBoot";


    public AlarmBoot() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmHelper alarmHelper = new AlarmHelper(context);
        alarmHelper.createMultipleAlarms(0,0);
    }
}
