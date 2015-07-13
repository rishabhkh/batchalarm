package com.rishabhkh.nerdalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    final String TAG = "Receiver";
    MediaPlayer mediaPlayer;
    static AlarmReceiver inst;

    @Override
    public void onReceive(Context context, Intent intent) {
        inst = this;
        Log.v(TAG, "" + intent.getIntExtra("reqcode",-1));
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, alarmUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (Exception e) {

        }
        Intent myintent = new Intent(context, AlarmNotification.class);
        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myintent);
    }

        public static AlarmReceiver getInstance(){
            return inst;
    }
}

