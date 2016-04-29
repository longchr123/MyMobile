package com.example.administrator.mymobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String number=getResultData();//获取打的电话号码
        String address=QueryNumberAddress.getAddress(number);
        Toast.makeText(context,address,Toast.LENGTH_LONG).show();
    }
}
