package com.example.administrator.mymobile.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class KillProcessService extends Service {
    private ScreenReceiver receiver;
    private Timer timer;
    private TimerTask task;
    public KillProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
            System.out.println("定时器开始工作，勾选之后4s打印一次");
            }
        };
        timer.schedule(task,2000,4000);//启动之后2秒执行run方法，4秒执行一次
        //监听锁屏事件
        receiver=new ScreenReceiver();
        IntentFilter filter=new IntentFilter();
        //设置监听锁屏
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver,filter);

    }
    private class ScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for(ActivityManager.RunningAppProcessInfo processInfo: am.getRunningAppProcesses()){
                am.killBackgroundProcesses(processInfo.processName);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//取消注册广播接收者
        receiver=null;
        timer.cancel();
        task.cancel();
        timer=null;
        task=null;
    }
}
