package com.rishabhkh.nerdalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        inst = this;int reqcode = intent.getIntExtra("reqcode",-1);
        Log.v(TAG, "" +reqcode );
        SharedPreferences sharedPreferences = context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("startPopulating", reqcode+1);
        editor.commit();
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

