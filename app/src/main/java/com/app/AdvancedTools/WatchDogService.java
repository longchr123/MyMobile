package com.app.AdvancedTools;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import java.util.List;
/*
    看门狗服务
    功能：监听手机软件的打开事件，当有应用打开时，立刻查询是否被加锁，如果加锁了就打开密码输入页面。
 */
public class WatchDogService extends Service {

    private ActivityManager am;
    private boolean flag;
    private AppLockDao dao;
    private Intent intent;
    private InnerReceiver receiver;
    private String stop1;
    private List<String> packnames;
    //内容观察者
    private MyContentObserver contentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        am= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        dao=new AppLockDao(this);
        packnames=dao.queryAll();//这个数据库只保存了加锁的程序
        intent=new Intent(this, EnterOtherAppPwdActivity.class);
        //在服务中启动activity必须添加下面这句
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //监听停止保护对象,也就是密码输入正确后转到应用页面
        receiver=new InnerReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.example.administrator.mymobile.activity.stopprotectting");
        registerReceiver(receiver, filter);

        new Thread(){
            @Override
            public void run() {
                super.run();
                flag=true;
                //看门狗查看最近打开的应用--包名
                while (flag){
                    //要得到应用，得打开栈，一个应用有一个栈，但要得到最近打开的栈，1，只装一个栈，而得到这个栈的索引为0，取出。需要加一个GET_TASKS权限
                    ActivityManager.RunningTaskInfo taskInfo=am.getRunningTasks(1).get(0);
                    //base和topActivity都是一样，得到最近打开的一个应用
                    String packname=taskInfo.baseActivity.getPackageName();
                    //每次访问数据库相当耗时，所以应该改为在内存中查找，所以先得到加锁的包名，再比较
                    if(packnames.contains(packname)){
                        if(packname.equals(stop1)){
                            //如果第二取得的包名和第一次不一样，则会调出密码页面
                            ActivityManager.RunningTaskInfo taskInfo2=am.getRunningTasks(1).get(0);
                            //base和topActivity都是一样，得到最近打开的一个应用
                            String packname2=taskInfo.baseActivity.getPackageName();
                            if(packname.equals(packname2)){
                                //什么也不用做
                            }else {
                                //弹出页面
                                intent.putExtra("packname",packname);//把包名传出去
                                startActivity(intent);//此处为startActivity
                            }
                        }else {
                            //弹出页面
                            intent.putExtra("packname",packname);//把包名传出去
                            startActivity(intent);//此处为startActivity
                        }
                    }
                    SystemClock.sleep(5);
                }
            }
        }.start();
        //数据发生改变了发这个信息，但要上下文
        Uri uri=Uri.parse("content://com.example.administrator.mymobile.db.change");
        contentObserver=new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(uri,true,contentObserver);
    }

    //定义内容观察者
    private class MyContentObserver extends ContentObserver{

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Toast.makeText(WatchDogService.this, "数据发生变化了", Toast.LENGTH_SHORT).show();
            packnames=dao.queryAll();
        }
    }

    //定义一个广播接收者
    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            stop1=intent.getStringExtra("packname");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag=false;
        //取消广播注册
        unregisterReceiver(receiver);
        receiver=null;
        //取消注册内容观察者
        getContentResolver().unregisterContentObserver(contentObserver);
        contentObserver=null;
    }
    //程序自带的
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
