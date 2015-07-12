package com.rishabhkh.nerdalarm;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AlarmActivity extends ActionBarActivity {


    final String TAG = "Alarm";

    SharedPreferences sharedPreferences;
    Editor editor;

    ListView listView;
    Integer[] choices;
    ArrayAdapter<String> arrayAdapter;

    int numberOfAlarms;
    int interval;

    LinearLayout linearLayout;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        linearLayout = (LinearLayout)findViewById(R.id.layout);
        Button addButton = (Button)findViewById(R.id.Add);
        Button cancelButton = (Button)findViewById(R.id.Cancel);
        Spinner numSpinner = (Spinner) findViewById(R.id.num);
        Spinner intervalSpinner = (Spinner) findViewById(R.id.interval);

        sharedPreferences = getApplicationContext().getSharedPreferences("Alarm",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        listView = new ListView(this);

        choices = new Integer[] {1,2,5,10,15,20,25,30};
        List<String> alarmList = new ArrayList<String>(Arrays.asList(detailArray()));
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alarmList);


        if(sharedPreferences.getBoolean("flag", false)) {
            initialiseListView();
            listView.setAdapter(arrayAdapter);
        }
        else
        {
            textView = new TextView(this);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(lp);
            textView.setText("No Alarms Set");
            linearLayout.addView(textView);
        }
        ArrayAdapter<Integer> choiceAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, choices);
        numSpinner.setAdapter(choiceAdapter);
        intervalSpinner.setAdapter(choiceAdapter);

        numSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                numberOfAlarms = (int)item;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                interval= (int) item;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayTimeDialogue();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                AlarmHelper alarmHelper = new AlarmHelper(AlarmActivity.this);
                alarmHelper.cancelMultipleAlarms();
                arrayAdapter.clear();
                arrayAdapter.notifyDataSetChanged();
                linearLayout.removeView(listView);
                textView = new TextView(AlarmActivity.this);
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(lp);
                textView.setText("No Alarms Set");
                linearLayout.addView(textView);
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

    public void displayTimeDialogue(){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if(!sharedPreferences.getBoolean("flag", false)){
                            linearLayout.removeView(textView);
                            initialiseListView();
                            Log.v(TAG, "Inside 1st Check");
                            listView.setAdapter(arrayAdapter);
                        }

                        editor.putInt("hour", hourOfDay);
                        editor.putInt("minute", minute);
                        editor.putInt("numofalarms", numberOfAlarms);
                        editor.putInt("interval", interval);
                        editor.putBoolean("flag", true);
                        editor.commit();
                        AlarmHelper alarmHelper = new AlarmHelper(AlarmActivity.this);
                        alarmHelper.createMultipleAlarms();
                        arrayAdapter.clear();
                        String[] alarmArray = detailArray();
                        for(int i=0;i<numberOfAlarms;i++) {
                            arrayAdapter.add(alarmArray[i]);
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        tpd.show();
    }

    public String[] detailArray(){
        int hour = sharedPreferences.getInt("hour", 0);
        int minute = sharedPreferences.getInt("minute", 0);
        int noAlarms = sharedPreferences.getInt("numofalarms", 0);
        int interval = sharedPreferences.getInt("interval", 0);
        int total = 0;
        String[] array = new String[noAlarms];
        for(int i=0;i<noAlarms;i++){
            total = minute+(interval*i);
            array[i] = hour+":"+total;
        }
        return array;
    }

    public void initialiseListView(){
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        listView.setLayoutParams(lp);
        linearLayout.addView(listView);
    }
}
