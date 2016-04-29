package com.example.administrator.mymobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {
    private SharedPreferences sp_receiver;
    private TelephonyManager tm;
    public void onReceive(Context context, Intent intent) {
        //发短信之前先确定是否开启防盗保护
        sp_receiver = context.getSharedPreferences("config", context.MODE_PRIVATE);
        boolean protect=sp_receiver.getBoolean("protect", false);
        if(protect) {
            //1.获得之前的sim卡信息

            String saved_sim = sp_receiver.getString("sim", "");
//        2.获得当前的SIM卡信息
            tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            String current = tm.getSimSerialNumber();
//        3.两个信息相比较，如果不一致就发送信息给安全码
            if (current.equals(saved_sim)) {
            } else {
                Toast.makeText(context, "SIM卡发生变更", Toast.LENGTH_SHORT).show();
                SmsManager.getDefault().sendTextMessage(sp_receiver.getString("safenumber", ""),
                        null, "my SIM changged", null, null);
            }
        }
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
