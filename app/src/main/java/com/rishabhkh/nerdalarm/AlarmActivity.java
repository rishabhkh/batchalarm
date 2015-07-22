package com.rishabhkh.nerdalarm;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TimePicker;

import com.rishabhkh.nerdalarm.data.AlarmContract.AlarmEntry;
import com.rishabhkh.nerdalarm.data.AlarmProvider;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = "Alarm";
    private final int intervalArray[] = {1,5,10,15,20,25,30};

    ListView listView;
    AlarmAdapter alarmAdapter;
    ContentResolver contentResolver;

    int mNumberOfAlarms;
    int mInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        getSupportLoaderManager().initLoader(0, null, this);

        FloatingActionButton addButton = (FloatingActionButton)findViewById(R.id.add);
        FloatingActionButton deleteButton = (FloatingActionButton)findViewById(R.id.delete);
        listView =(ListView)findViewById(R.id.listview);
        listView.setEmptyView(findViewById(R.id.empty));
        alarmAdapter = new AlarmAdapter(AlarmActivity.this, null, 0);
        listView.setAdapter(alarmAdapter);

        contentResolver = getContentResolver();

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createIntervalDialog();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmHelper alarmHelper= new AlarmHelper(AlarmActivity.this);
                alarmHelper.cancelMultipleAlarms();
                contentResolver.delete(AlarmProvider.CONTENT_URI,null,null);
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

    public void createIntervalDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
        builder.setTitle(R.string.pick_interval)
                .setItems(R.array.interval_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mInterval = intervalArray[which];
                        createNumberOfAlarmsDialog();
                    }
                });
        builder.create().show();
    }

    public void createNumberOfAlarmsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
        builder.setTitle(R.string.pick_num_of_alarms)
                .setItems(R.array.interval_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mNumberOfAlarms = intervalArray[which];
                        createTimePickerDialog();
                    }
                });
        builder.create().show();
    }

    public void createTimePickerDialog(){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                       for (int i=0;i<mNumberOfAlarms;i++){
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(AlarmEntry.COLUMN_HOUR,hourOfDay);
                            contentValues.put(AlarmEntry.COLUMN_MINUTE,minute+(i*mInterval));
                            contentValues.put(AlarmEntry.COLUMN_FLAG, 1);
                            contentResolver.insert(AlarmProvider.CONTENT_URI, contentValues);
                        }
                        AlarmHelper alarmHelper = new AlarmHelper(AlarmActivity.this);
                        alarmHelper.createMultipleAlarms();
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        //TODO: Set Custom Title
        tpd.show();

    }

    public AlarmActivity() {
        super();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(AlarmActivity.this,
                AlarmProvider.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       alarmAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        alarmAdapter.swapCursor(null);
    }
}
