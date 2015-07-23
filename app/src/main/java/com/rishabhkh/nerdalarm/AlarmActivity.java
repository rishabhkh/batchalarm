package com.rishabhkh.nerdalarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    boolean vibcheck = false;

    int mNumberOfAlarms;
    int mInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        getSupportLoaderManager().initLoader(0, null, this);

        setVolumeControlStream(AudioManager.STREAM_ALARM);

        FloatingActionButton addButton = (FloatingActionButton)findViewById(R.id.add);
        FloatingActionButton deleteButton = (FloatingActionButton)findViewById(R.id.delete);
        listView =(ListView)findViewById(R.id.listview);
        listView.setEmptyView(findViewById(R.id.empty));
        alarmAdapter = new AlarmAdapter(AlarmActivity.this, null, 0);
        listView.setAdapter(alarmAdapter);
        int[] colors = {0, 0xff2d2d2d, 0};
        listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        listView.setDividerHeight(1);

        contentResolver = getContentResolver();

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createIntervalDialog();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmHelper alarmHelper = new AlarmHelper(AlarmActivity.this);
                alarmHelper.cancelMultipleAlarms();
                contentResolver.delete(AlarmProvider.CONTENT_URI, null, null);
            }
        });

        //RingtoneManager rm = new RingtoneManager(this);
        //Log.v(TAG, String.valueOf(rm.getRingtoneUri(1)));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        MenuItem menuItem = (MenuItem)menu.findItem(R.id.vibration);
        if(PreferenceManager.getDefaultSharedPreferences(this).getInt("vibrateFlag",0)==1) {
            menuItem.setIcon(R.drawable.vib_act);
            vibcheck = true;
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.vibration) {
            if(vibcheck){
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putInt("vibrateFlag", 0);
                editor.commit();
                item.setIcon(R.drawable.vib_deact);
                vibcheck = false;

            }
            else {
                item.setIcon(R.drawable.vib_act);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putInt("vibrateFlag", 1);
                editor.commit();
                vibcheck = true;
            }

        }
        else if(id == R.id.ringtone) {
            displayRingtoneSelector();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);


            if (uri != null)
            {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putString("alarmUri", String.valueOf(uri));
                editor.commit();
                //Log.v(TAG, String.valueOf(uri));
            }
            else
            {

            }
        }

    }

    public void displayRingtoneSelector(){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(PreferenceManager.getDefaultSharedPreferences(this).getString("alarmUri",null)));
        this.startActivityForResult(intent, 5);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        alarmAdapter.swapCursor(null);
    }
}
