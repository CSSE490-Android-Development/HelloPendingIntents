package com.fernferret.helloalarms;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class HelloAlarms extends Activity {

    private static final int SINGLE_ALARM_RC = 0;
    private static final int NO_FLAGS = 0;
    private static final int TIME_IN_SECONDS_FOR_SINGLE_ALARM = 3;

    private static final String SINGLE_STATE = "SINGLE";

    // Shared Toast
    private Toast mToast;
    private SharedPreferences mSettings;

    // Single Alarm Variables
    private long mSingleStartTime;
    private Button mSingleButton;

    PowerManager.WakeLock mWakeLock;
    KeyguardManager mKeyguardManager;
    KeyguardLock mKeyguardLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mSingleButton = (Button) findViewById(R.id.single_alarm);
        mSingleButton.setOnClickListener(mSingleAlarmListener);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
    }

    @Override
    protected void onPause() {
        Editor editor = mSettings.edit();
        editor.putLong(SINGLE_STATE, mSingleStartTime);
        editor.commit();
        super.onPause();
    }

    private OnClickListener mSingleAlarmListener = new OnClickListener() {

        public void onClick(View view) {

            Intent singleAlarmIntent = new Intent(HelloAlarms.this, SingleAlarm.class);

            PendingIntent singleAlarmPendingIntent = PendingIntent.getBroadcast(HelloAlarms.this, SINGLE_ALARM_RC, singleAlarmIntent, NO_FLAGS);
            long currentTime = System.currentTimeMillis();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentTime);

            calendar.add(Calendar.SECOND, TIME_IN_SECONDS_FOR_SINGLE_ALARM);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), singleAlarmPendingIntent);

            /**
             * By using a shared toast, we can cancel a toast!
             */
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(HelloAlarms.this, R.string.one_shot_scheduled, Toast.LENGTH_SHORT);
            mToast.show();
        }
    };
}