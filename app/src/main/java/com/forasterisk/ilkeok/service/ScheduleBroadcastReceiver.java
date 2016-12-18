package com.forasterisk.ilkeok.service;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.forasterisk.ilkeok.R;
import com.forasterisk.ilkeok.ui.SplashActivity;
import com.forasterisk.ilkeok.utils.Argument;

import java.util.Calendar;

/**
 * Created by yearnning on 15. 1. 23..
 */
public class ScheduleBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ScheduleManager";

    /**
     *
     */
    private final static String ARG_TIME_TYPE = "arg_time_type";

    /**
     *
     */
    private Context mContext;
    private AlarmApiTask mAlarmApiTask = null;

    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive() called");

        this.mContext = context;

        /**
         * You can do the processing here.
         */
        if (mAlarmApiTask == null) {
            int time_type = intent.getIntExtra(ARG_TIME_TYPE, -1);
            if (time_type != -1) {
                mAlarmApiTask = new AlarmApiTask();
                mAlarmApiTask.execute(time_type);
            }
        }

    }

    /**
     * @param context
     */
    public void setAlarm(Context context) {
        Log.d(TAG, "setAlarm() called!");

        /**
         *  Handle Rebooting Situation.
         */
        BootBroadcastReceiver.enable(context, true);

        /**
         *
         */
        setAlarm(context, 0);
        Log.d(TAG, "setAlarm() finished!");
    }

    @TargetApi(19)
    private void setAlarm(Context context, int time_type) {

        /**
         *
         */
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        /**
         *
         */
        //int time = PreferenceManager.get(context, Argument.PREFS_ALARM_TIME_BREAKFAST, -1);
        int time = 0001;

        /**
         *
         */
        Intent intent = new Intent(context, ScheduleBroadcastReceiver.class);
        intent.putExtra(ARG_TIME_TYPE, time_type);
        PendingIntent pi = PendingIntent.getBroadcast(context, time_type, intent, PendingIntent.FLAG_ONE_SHOT);

        /**
         *
         */
        //long triggerAtMillis = getMiillisByTime(time);
        long triggerAtMillis = getMiillisByTime(time);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
            //am.setWindow(AlarmManager.RTC_WAKEUP, triggerAtMillis - 1000 * 60 * 10, 1000 * 60 * 20, pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
        }
    }

    /**
     * @param time
     * @return
     */
    private long getMiillisByTime(int time) {

        Calendar c_current = Calendar.getInstance();
        c_current.add(Calendar.MINUTE, 1);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, time / 100);
        c.set(Calendar.MINUTE, time % 100);
        if (c_current.after(c)) {
            c.add(Calendar.DATE, 1);
        }

        return c.getTimeInMillis();
    }

    /**
     * @param context
     */
    public void cancelAlarm(Context context) {
        Log.d(TAG, "cancelAlarm() called!");

        /**
         *  Handle Rebooting Situation.
         */
        BootBroadcastReceiver.enable(context, false);

        /**
         *
         */
        cancelAlarm(context, 0);
    }

    private void cancelAlarm(Context context, int time_type) {
        Intent intent = new Intent(context, ScheduleBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, time_type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class AlarmApiTask extends AsyncTask<Integer, Void, Void> {
        private int request_code = Argument.REQUEST_CODE_UNEXPECTED;
        private PowerManager.WakeLock wl = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG, "onPreExecute() called");

            /**
             * Acquire the lock
             */
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KAIST_BAB_ScheduleManager");
            wl.acquire();
        }

        @Override
        protected Void doInBackground(Integer... time_type) {

            Log.d(TAG, "doInBackground() called");

            try {


            } catch (Exception e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {

            Log.d(TAG, "onPostExecute() called");

            Calendar c = Calendar.getInstance();
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {

                int notificationId = 0;
                Intent intent = new Intent(mContext, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent viewPendingIntent =
                        PendingIntent.getActivity(mContext, notificationId, intent, PendingIntent.FLAG_ONE_SHOT);


                /**
                 * Build
                 */
                NotificationCompat.BigTextStyle bigStyle =
                        new NotificationCompat.BigTextStyle()
                                .setBigContentTitle("강건마에 일격날리기")
                                .bigText("수요일입니다. 강건마에게 일격은 날리셨나요?");

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(mContext)
                                .setSmallIcon(R.mipmap.ic_launcher_reverse)
                                .setContentTitle("강건마에 일격날리기")
                                .setContentText("수요일입니다. 강건마에게 일격은 날리셨나요?")
                                .setContentIntent(viewPendingIntent)
                                .setStyle(bigStyle)
                                .setAutoCancel(true);


                /**
                 *  Get an instance of the NotificationManager service
                 */
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

                /**
                 * Build the notification and issues it with notification manager.
                 */
                Notification notification = builder.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(notificationId, notification);
            }

            /**
             *
             */
            mAlarmApiTask = null;
            wl.release();

            /**
             *
             */
            setAlarm(ScheduleBroadcastReceiver.this.mContext);
            Log.d(TAG, "onPostExecute() finished");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            /**
             *
             */
            mAlarmApiTask = null;
            wl.release();

        }
    }

}