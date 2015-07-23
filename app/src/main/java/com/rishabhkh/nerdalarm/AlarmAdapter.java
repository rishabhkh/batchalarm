package com.rishabhkh.nerdalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.rishabhkh.nerdalarm.data.AlarmContract.AlarmEntry;
import com.rishabhkh.nerdalarm.data.AlarmProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rishabh on 7/21/2015.
 */
public class AlarmAdapter extends CursorAdapter {
    Context mContext;



    public AlarmAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Context c = context;
        TextView tv =(TextView) view.findViewById(R.id.listitem);
        Switch s = (Switch) view.findViewById(R.id.switch1);

        final int hour = cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_HOUR));
        final int minute = cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_MINUTE));
        int flag = cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_FLAG));
        final int _ID = cursor.getInt(cursor.getColumnIndex(AlarmEntry._ID));

        //Log.v("Adapter", formatTime(hour, minute));
        tv.setText(formatTime(hour, minute));

        s.setOnCheckedChangeListener(null);
        s.setChecked(flag == 1);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Inside Checked Change", "HI");
                AlarmHelper alarmHelper = new AlarmHelper(c);
                if(isChecked){
                    Uri uri = Uri.parse(AlarmProvider.CONTENT_URI+"/"+_ID);
                    ContentValues cv = new ContentValues();
                    cv.put(AlarmEntry.COLUMN_FLAG,1);
                    c.getContentResolver().update(uri, cv, null, null);
                    alarmHelper.createSingleAlarm(_ID, 1, 0);
                }
                else{
                    alarmHelper.cancelSingleAlarm(_ID);
                }

            }
        });
     }

    public static String formatTime(int hour,int minute){
        Date date = new Date(AlarmHelper.timeInMillis(hour,minute));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    public static String formatDate(){
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("c,dd/MM/yyyy");
        return sdf.format(date);
    }


}
