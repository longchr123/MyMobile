package com.example.administrator.mymobile.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillProcessReceiver extends BroadcastReceiver {
    public KillProcessReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningAppProcessInfo processInfo: am.getRunningAppProcesses()){
            am.killBackgroundProcesses(processInfo.processName);
        }
    }
}
