package com.rishabhkh.batchalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rishabhkh.nerdalarm.R;
import com.rishabhkh.batchalarm.data.AlarmContract.AlarmEntry;
import com.rishabhkh.batchalarm.data.AlarmProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmAdapter extends CursorAdapter {
    Context mContext;

    public AlarmAdapter(Context context, Cursor c,int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
   public View getView(int position, View convertView, ViewGroup parent) {
       return super.getView(position, null, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Context c = context;
        TextView tv =(TextView) view.findViewById(R.id.listitem);
        SwitchCompat s = (SwitchCompat) view.findViewById(R.id.switch1);

        final int hour = cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_HOUR));
        final int minute = cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_MINUTE));
        int flag = cursor.getInt(cursor.getColumnIndex(AlarmEntry.COLUMN_FLAG));
        final int _ID = cursor.getInt(cursor.getColumnIndex(AlarmEntry._ID));

        tv.setText(formatTime(hour, minute));

        s.setChecked(flag == 1);
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmHelper alarmHelper = new AlarmHelper(c);
                SwitchCompat s =(SwitchCompat)v;
                if(s.isChecked()){
                    Uri uri = Uri.parse(AlarmProvider.CONTENT_URI+"/"+_ID);
                    ContentValues cv = new ContentValues();
                    cv.put(AlarmEntry.COLUMN_FLAG,1);
                    c.getContentResolver().update(uri, cv, null, null);
                    alarmHelper.createSingleAlarm(_ID, 1, 0);
                    s.toggle();
                }
                else{
                    alarmHelper.cancelSingleAlarm(_ID);
                    s.toggle();
                }
            }
        });
    }

    public static String formatTime(int hour,int minute){
        Date date = new Date(AlarmHelper.timeInMillis(hour,minute));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.US);
        return sdf.format(date);
    }

    public static String formatDate(){
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("c,dd/MM/yyyy", Locale.US);
        return sdf.format(date);
    }


}
