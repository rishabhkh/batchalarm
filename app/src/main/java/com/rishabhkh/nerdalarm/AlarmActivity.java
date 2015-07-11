package com.rishabhkh.nerdalarm;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;


public class AlarmActivity extends ActionBarActivity {

    final String TAG = "Alarm";
    int mHour;
    int mMinute;
    int numberOfAlarms=5;
    private TimePicker timePicker1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        final AlarmHelper alarmHelper = new AlarmHelper(AlarmActivity.this);

        timePicker1 = (TimePicker) findViewById(R.id.timePicker);
        Button button = (Button)findViewById(R.id.b1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mHour = timePicker1.getCurrentHour();
                mMinute = timePicker1.getCurrentMinute();
                Log.v(TAG, "TimePicker Hour:" + mHour + "Minutes:" + mMinute);
                for (int i = 0; i <= numberOfAlarms; i++) {
                    alarmHelper.createAlarm(String.valueOf(i), mHour, mMinute + i, i);
                }
            }
        });
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

}
