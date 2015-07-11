package com.rishabhkh.nerdalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;


public class AlarmActivity extends ActionBarActivity {
    final String TAG = "Alarm";
    AlarmManager mAlarmManager;
    int mHour = 19;
    int mMinute = 14;
    Intent mIntent;
    Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mCalendar = Calendar.getInstance();

        createAlarm("Alarm!", mHour, mMinute, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createAlarm(String label, int hour, int minute, int reqCode) {

        mCalendar.set(mCalendar.HOUR_OF_DAY, mHour);
        mCalendar.set(mCalendar.MINUTE, mMinute);
        mIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        mIntent.putExtra("label", label);
        Log.v(TAG, "Setting Alarm");
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), getPendingIntent(reqCode));

    }

    public PendingIntent getPendingIntent(int reqCode) {
        return PendingIntent.getBroadcast(AlarmActivity.this, reqCode, mIntent, 0);
    }


}
