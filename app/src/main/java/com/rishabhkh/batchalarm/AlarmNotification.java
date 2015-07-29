package com.rishabhkh.batchalarm;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification);

        TextView timeView = (TextView) findViewById(R.id.currenttime);
        TextView dateView = (TextView)findViewById(R.id.currentdate);
        timeView.setText(AlarmAdapter.formatTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE)));
        dateView.setText(AlarmAdapter.formatDate());

        String uri = PreferenceManager.getDefaultSharedPreferences(this).getString("alarmUri", String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)));

        Uri alarmUri = Uri.parse(uri);

        playAlarm(alarmUri);
        if(PreferenceManager.getDefaultSharedPreferences(this).getInt("vibrateFlag",0)==1)
        {
            vibrate();
        }

        findViewById(R.id.linlayout).setOnTouchListener(new OnSwipeTouchListener(this) {

            public void onSwipeRight() {
                if(mediaPlayer != null)
                    mediaPlayer.release();
                if(v != null)
                    v.cancel();
                AlarmNotification.this.finish();
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

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

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(pattern,0);
    }
}
