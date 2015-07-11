package com.rishabhkh.nerdalarm;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
    Editor editor;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        final AlarmHelper alarmHelper = new AlarmHelper(AlarmActivity.this);
        sharedPreferences = getSharedPreferences("Alarm");

        timePicker1 = (TimePicker) findViewById(R.id.timePicker);
        Button button1 = (Button)findViewById(R.id.b1);
        Button button2 = (Button)findViewById(R.id.b2);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mHour = timePicker1.getCurrentHour();
                mMinute = timePicker1.getCurrentMinute();
                Log.v(TAG, "TimePicker Hour:" + mHour + "Minutes:" + mMinute);
                for (int i = 0; i <= numberOfAlarms; i++) {
                    alarmHelper.createAlarm(String.valueOf(i), mHour, mMinute + i, i);
                }
                editor = sharedPreferences.edit();
                editor.putInt("hour", mHour);
                editor.putInt("minute", mMinute);
                editor.commit();
            }

        });

        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alarmHelper.cancelAlarm(2);
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

    public SharedPreferences getSharedPreferences(String fileName){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(fileName, MODE_PRIVATE);
        return pref;
    }

    public void getFromPreference(String key){
        Log.v(TAG, String.valueOf(sharedPreferences.getInt(key,0)));
    }

}
