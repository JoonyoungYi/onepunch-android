package com.forasterisk.ilkeok.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by yearnning on 15. 4. 22..
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ScheduleBroadcastReceiver mScheduleManager = new ScheduleBroadcastReceiver();
            if (mScheduleManager != null) {
                mScheduleManager.cancelAlarm(context);
                mScheduleManager.setAlarm(context);
            }
        }
    }

    /**
     * Handle Rebooting Situation.
     */
    public static void enable(Context context, boolean enable) {

        ComponentName receiver = new ComponentName(context, BootBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();
        if (enable) {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
