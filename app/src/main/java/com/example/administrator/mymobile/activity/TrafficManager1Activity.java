package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SlidingDrawer;

import com.example.administrator.mymobile.R;

public class TrafficManager1Activity extends Activity {

    private SlidingDrawer sd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager1);
        sd= (SlidingDrawer) findViewById(R.id.sd);
        sd.open();


        //2.2版本时就引入了流量接口
//        TrafficStats.getMobileRxBytes();//手机流量下载总和
//        TrafficStats.getMobileTxBytes();//手机流量上传总和
//        TrafficStats.getTotalRxBytes();//手机+wifi流量下载总和
//        TrafficStats.getTotalTxBytes();//手机+wifi流量上传总和
//        TrafficStats.getUidRxBytes();//根据用户ID获取它下载了多少流量
//        TrafficStats.getUidTxBytes();//
    }
}
