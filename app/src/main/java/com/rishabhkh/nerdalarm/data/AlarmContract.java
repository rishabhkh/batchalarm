package com.rishabhkh.nerdalarm.data;

import android.provider.BaseColumns;

/**
 * Created by Rishabh on 7/21/2015.
 */
public class AlarmContract {

    public static final class AlarmEntry implements BaseColumns{
        public static final String TABLE_NAME = "Alarms";
        public static final String COLUMN_HOUR = "hour";
        public static final String COLUMN_MINUTE = "minute";
        public static final String COLUMN_FLAG = "flag";
        //public static final String COLUMN_TONE = "tone";
    }

}
