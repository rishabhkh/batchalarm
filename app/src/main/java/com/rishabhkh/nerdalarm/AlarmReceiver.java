package com.rishabhkh.nerdalarm;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.rishabhkh.nerdalarm.data.AlarmContract.AlarmEntry;

import com.rishabhkh.nerdalarm.data.AlarmProvider;

public class AlarmReceiver extends BroadcastReceiver {
    final String TAG = "Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int _ID = intent.getIntExtra("_ID", -1);
        Log.v(TAG, "" + _ID);

        Uri uri = Uri.parse(AlarmProvider.CONTENT_URI+"/"+_ID);
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlarmEntry.COLUMN_FLAG, 0);
        context.getContentResolver().update(uri, contentValues, null, null);

        Intent myintent = new Intent(context, AlarmNotification.class);
        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myintent);
    }
}

