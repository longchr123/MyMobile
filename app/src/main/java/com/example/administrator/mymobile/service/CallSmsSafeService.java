package com.example.administrator.mymobile.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.example.administrator.mymobile.db.BlackNumber;

import java.lang.reflect.Method;


public class CallSmsSafeService extends Service {
    private InnerSMSReceiver receiver;
    private BlackNumber bn;
    private TelephonyManager tm;
    private MyPhoneStateListener listener;
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //广播接收者，用于拦截短信
    private class InnerSMSReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信
            Object[] pdus= (Object[]) intent.getExtras().get("pdus");
            for(Object pdu:pdus){
                SmsMessage sms=SmsMessage.createFromPdu((byte[]) pdu);
                String sender=sms.getOriginatingAddress();
                String body=sms.getMessageBody();
                if(bn.query(sender)) {//要拦截的电话号码
                    String mode = bn.queryMode(sender);
                    if ("1".equals(mode) || "2".equals(mode)) {
                        abortBroadcast();//把短信广播终止
                    }
                }else if(body.contains("打折")){//根据内容拦截短信
                    abortBroadcast();//把短信广播终止
                }
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        bn=new BlackNumber(CallSmsSafeService.this);
        //注册监听短信
        receiver=new InnerSMSReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");//对短信有用
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver, filter);
        tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener=new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }
    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://打电话进来
                    if(bn.query(incomingNumber)){
                        String mode=bn.queryMode(incomingNumber);
                        if("0".equals(mode) || "2".equals(mode)){
                            endCall();//把当前电话挂断，生成呼叫记录不是同步的
//                            deleteCallLog(incomingNumber);
                            //观察数据变化，再去删除
                            Uri url=Uri.parse("content://call_log/calls");
                            //注册内容观察者
                            getContentResolver().registerContentObserver(url, true, new MyContentObserver(new Handler(),incomingNumber));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
    //删除呼叫记录
    private void deleteCallLog(String incomingNumber) {
        ContentResolver resolver=getContentResolver();
        Uri url=Uri.parse("content://call_log/calls");
        resolver.delete(url,"number=?",new String[]{incomingNumber});
    }

    private void endCall() {
        try {
        //1.得到字节码
        Class clazz= CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            //2.得到对应的方法getService
            Method method=clazz.getMethod("getService",String.class);
            //3.执行这个方法
            IBinder b= (IBinder) method.invoke(null,TELEPHONY_SERVICE);
            //4.拷贝.aidl文件
            //5.生成java代码
            ITelephony service= ITelephony.Stub.asInterface(b);
//            //6.执行java中的endCall（）
            service.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //内容观察者
    private class MyContentObserver extends ContentObserver{
        private String incomingNumber;
        public MyContentObserver(Handler handler,String incomingNumber) {
            super(handler);
            this.incomingNumber=incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //当我们观察的路径发生变化时，再删除
            deleteCallLog(incomingNumber);
            //取消注册内容观察者
            getContentResolver().unregisterContentObserver(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册监听短信
        unregisterReceiver(receiver);
        receiver=null;
        //监听电话注册
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
    }

}
