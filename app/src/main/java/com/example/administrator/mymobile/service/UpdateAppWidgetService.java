package com.example.administrator.mymobile.service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.receiver.MyAppWidget;
import com.example.administrator.mymobile.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateAppWidgetService extends Service {
    private AppWidgetManager awm;
    private Timer timer;
    private TimerTask task;
    private ScreenReceiver receiver;

    public UpdateAppWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //监听锁屏事件
        receiver=new ScreenReceiver();
        IntentFilter filter=new IntentFilter();
        //设置监听锁屏
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);

        startUpdate();
    }

    private void startUpdate() {
        awm= AppWidgetManager.getInstance(this);
        timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                System.out.println("开始更新");

                //激活的组件-通讯
                ComponentName provider=new ComponentName(UpdateAppWidgetService.this, MyAppWidget.class);
                //远程view，把不念旧恶listView转化为远程屏幕上的显示内容并更新
                RemoteViews views=new RemoteViews(getPackageName(), R.layout.my_app_widget);
                views.setTextViewText(R.id.process_count,"正在运行的进程："+
                        SystemInfoUtils.getRunningProcessCount(UpdateAppWidgetService.this));
                views.setTextViewText(R.id.process_memory, "可用内存：" + Formatter.formatFileSize(
                        UpdateAppWidgetService.this, SystemInfoUtils.getAvailRam(UpdateAppWidgetService.this)));
                //动作，以广播的形式发出，在KillProcessReceiver中操作。
                Intent intent=new Intent();
                intent.setAction("com.example.administrator.mymobile.service.killprocess");//自己随便定义一个动作，当点击时返回这个广播。
                //延期意图
                PendingIntent pendingInent=PendingIntent.getBroadcast(UpdateAppWidgetService.this,
                        0,intent,PendingIntent.FLAG_UPDATE_CURRENT);//第四个参数：当用户重复点击时，最后一次有效
                views.setOnClickPendingIntent(R.id.btn_clear,pendingInent);
                awm.updateAppWidget(provider,views);
            }
        };
        timer.schedule(task,0,1000);
    }

    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()==Intent.ACTION_SCREEN_OFF){
                //锁屏
                if(timer != null&&task!=null){
                    timer.cancel();
                    task.cancel();
                    task=null;
                    timer=null;
                }
            }else  if(intent.getAction()==Intent.ACTION_SCREEN_ON){
                if(timer == null&&task==null){
                    startUpdate();
                }
            }
            ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for(ActivityManager.RunningAppProcessInfo processInfo: am.getRunningAppProcesses()){
                am.killBackgroundProcesses(processInfo.processName);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null&&task!=null) {
            timer.cancel();
            task.cancel();
            task = null;
            timer = null;
        }
        unregisterReceiver(receiver);//取消注册广播接收者
        receiver=null;
    }
}
