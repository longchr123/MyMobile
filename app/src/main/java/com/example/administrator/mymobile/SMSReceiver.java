package com.example.administrator.mymobile;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.example.administrator.mymobile.activity.LockScreenActivity;

/**
 * Created by Administrator on 2015/10/4.
 */
public class SMSReceiver extends BroadcastReceiver {
    private SharedPreferences sp;
    private DevicePolicyManager dpm;
    @Override
    public void onReceive(Context context, Intent intent) {
        sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        dpm= (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
        Object[] pdus= (Object[]) intent.getExtras().get("pdus");
        for(Object pdu:pdus){
            SmsMessage sms=SmsMessage.createFromPdu((byte[]) pdu);
            String sender = sms.getOriginatingAddress();//得到发送者
            String safenumber=sp.getString("safenumber","");
            String body=sms.getMessageBody();//得到消息内容
            if (sender.contains(safenumber)){//模拟器可以这样，真机不可以
                if("#*location*#".equals(body)){
                    System.out.println("得到手机GPS位置");
                    Intent gpsIntent=new Intent(context,GPSServer.class);
                    context.startService(gpsIntent);
                    String lastlocation =sp.getString("lastlocation","");
                    if(TextUtils.isEmpty(lastlocation)){
                        //发送一条信息回去
                        SmsManager.getDefault().sendTextMessage(sender, null, "没有得到最新地址", null, null);
                    }else {
                        SmsManager.getDefault().sendTextMessage(sender,null,lastlocation,null,null);
                    }

                }else if("#*alarm*#".equals(body)){
                    MediaPlayer player=MediaPlayer.create(context,R.raw.jht);
                    player.setVolume(1.0f,1.0f);//设置音量最大
                    player.setLooping(true);//一直循环播放
                    player.start();
                    System.out.println("播放报警音乐");
                }else if("#*wipdata*#".equals(body)){
                    ComponentName who=new ComponentName(context,MyAdmin.class);
                    if(dpm.isAdminActive(who)) {//判断是否激活，如果激活
                        dpm.wipeData(0);//手机恢复出厂设置
                        dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//格式化sd卡
                        System.out.println("远程删除数据");
                    }else {
                        openAdmin(context);
                    }
                }else if("#*lockscreen*#".equals(body)){
                    System.out.println("远程锁屏");
                    ComponentName who=new ComponentName(context,MyAdmin.class);
                    if(dpm.isAdminActive(who)) {//判断是否激活，如果激活
                        dpm.lockNow();
                        dpm.resetPassword("123", 0);//设置密码
                    }else {
                        openAdmin(context);
                    }
                }abortBroadcast();//接收短信指令，手机没有提示，小偷发现不了

            }
        }

    }

    private void openAdmin(Context context) {
        Intent openAdmin =new Intent(context,LockScreenActivity.class);
        //在广播接收者中是无法直接激活Activity和Service,必须得加一个Flag
        openAdmin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(openAdmin);
    }
}
