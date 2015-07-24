package com.rishabhkh.nerdalarm;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;


public class AlarmNotification extends Activity {
    private PowerManager.WakeLock wl;
    MediaPlayer mediaPlayer;
    Vibrator v;
    long pattern[] = {0,1000,1000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification);

        Button button = (Button)findViewById(R.id.stopthisalarm);
        Button cancelAllAlarms = (Button)findViewById(R.id.stopallalarms);
        TextView timeView = (TextView)findViewById(R.id.currenttime);
        TextView dateView = (TextView)findViewById(R.id.currentdate);
        timeView.setText(AlarmAdapter.formatTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE)));
        dateView.setText(AlarmAdapter.formatDate());

        String uri = PreferenceManager.getDefaultSharedPreferences(this).getString("alarmUri", String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)));
        //Log.v("URI=",uri);
        Uri alarmUri = Uri.parse(uri);

        playAlarm(alarmUri);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null)
                    mediaPlayer.release();
                if (v != null)
                    v.cancel();
                AlarmNotification.this.finish();
            }
        });

        cancelAllAlarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null)
                    mediaPlayer.release();
                if (v != null)
                    v.cancel();
                AlarmNotification.this.finish();
                AlarmHelper alarmHelper = new AlarmHelper(AlarmNotification.this);
                alarmHelper.cancelMultipleAlarms();
            }
        });

        if(PreferenceManager.getDefaultSharedPreferences(this).getInt("vibrateFlag",0)==1)
        {
            vibrate();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(mediaPlayer != null)
                    mediaPlayer.release();
                if(v != null)
                    v.cancel();
                    AlarmNotification.this.finish();
                }
        }, 55000);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (wl.isHeld())
            wl.release();
    }

    public void playAlarm(Uri alarmUri){
        if (alarmUri == null) {
            //Log.v("Uri","null");
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, alarmUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (Exception e) {
            e.getCause();
        }
    }

    public void vibrate(){
        Log.v("Vib","rate");
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(pattern,0);
    }

}
