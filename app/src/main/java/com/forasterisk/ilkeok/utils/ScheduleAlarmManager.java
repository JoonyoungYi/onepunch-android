package com.forasterisk.ilkeok.utils;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;

import com.forasterisk.ilkeok.service.ScheduleBroadcastReceiver;


/**
 * Created by yearnning on 15. 2. 12..
 */
public class ScheduleAlarmManager {

    private Context mContext;
    private SwitchCompat mSwitchCompat = null;
    private boolean mAlarmOn = false;
    private ScheduleBroadcastReceiver mScheduleManager;

    public ScheduleAlarmManager(Context context, SwitchCompat switchCompat) {

        /**
         *
         */
        this.mContext = context;
        this.mSwitchCompat = switchCompat;

        /**
         *
         */
        mScheduleManager = new ScheduleBroadcastReceiver();

        /**
         *
         */
        mAlarmOn = isAlarmOn();
        if (mAlarmOn) {
            if (mSwitchCompat != null)
                mSwitchCompat.setChecked(true);
        } else {
            if (mSwitchCompat != null)
                mSwitchCompat.setChecked(false);
        }
    }

    public boolean isAlarmOn() {

        int alarm_time_breakfast = -1;
        int alarm_time_launch = -1;
        int alarm_time_dinner = -1;

        try {
            alarm_time_breakfast = PreferenceManager.get(mContext, Argument.PREFS_ALARM_TIME_BREAKFAST, -1);
            alarm_time_launch = PreferenceManager.get(mContext, Argument.PREFS_ALARM_TIME_LUNCH, -1);
            alarm_time_dinner = PreferenceManager.get(mContext, Argument.PREFS_ALARM_TIME_DINNER, -1);

            if (alarm_time_breakfast == -1 || alarm_time_launch == -1 || alarm_time_dinner == -1) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public void toggleAlaram() {
        if (mAlarmOn) {
            makeAlarmOff();
        } else {
            makeAlarmOn();
        }
    }

    public void makeAlarmOn() {
        if (mSwitchCompat != null)
            mSwitchCompat.setChecked(true);
        PreferenceManager.put(mContext, Argument.PREFS_ALARM_TIME_BREAKFAST, 730);
        PreferenceManager.put(mContext, Argument.PREFS_ALARM_TIME_LUNCH, 1130);
        PreferenceManager.put(mContext, Argument.PREFS_ALARM_TIME_DINNER, 1720);

        if (mScheduleManager != null) {
            mScheduleManager.setAlarm(mContext);
        }

        mAlarmOn = true;
    }

    public void makeAlarmOff() {

        if (mSwitchCompat != null)
            mSwitchCompat.setChecked(false);

        PreferenceManager.put(mContext, Argument.PREFS_ALARM_TIME_BREAKFAST, -1);
        PreferenceManager.put(mContext, Argument.PREFS_ALARM_TIME_LUNCH, -1);
        PreferenceManager.put(mContext, Argument.PREFS_ALARM_TIME_DINNER, -1);

        if (mScheduleManager != null) {
            mScheduleManager.cancelAlarm(mContext);
        }
        mAlarmOn = false;
    }
}
