package com.rishabhkh.nerdalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;


public class AlarmActivity extends ActionBarActivity {
    final String TAG = "Alarm";
    AlarmManager mAlarmManager;
    int mHour = 19;
    int mMinute = 32;
    Intent mIntent;
    Calendar mCalendar;
    int numberOfAlarms=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Button button = (Button)findViewById(R.id.b1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for(int i=0;i<=numberOfAlarms;i++) {
                    createAlarm(String.valueOf(i), mHour, mMinute+i, i);
                }
            }
        });


        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mCalendar = Calendar.getInstance();
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

        mCalendar.set(mCalendar.HOUR_OF_DAY, hour);
        mCalendar.set(mCalendar.MINUTE, minute);
        mIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        mIntent.putExtra("label", label);
        Log.v(TAG, "Setting Alarm:"+reqCode);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), getPendingIntent(reqCode));
    }

    public PendingIntent getPendingIntent(int reqCode) {
        return PendingIntent.getBroadcast(AlarmActivity.this, reqCode, mIntent, 0);
    }


}
