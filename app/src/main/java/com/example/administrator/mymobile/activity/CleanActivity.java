package com.example.administrator.mymobile.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.example.administrator.mymobile.R;

public class CleanActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        TabHost host=getTabHost();
        TabHost.TabSpec spec1=host.newTabSpec("缓存清理");
        TabHost.TabSpec spec2=host.newTabSpec("sd卡清理");
        //设置指示器
        spec1.setIndicator("缓存清理");
        spec2.setIndicator("sd卡清理");
        //设置内容
        spec1.setContent(new Intent(this,CleanCacheActivity.class));
        spec2.setContent(new Intent(this,CleanSDcardActivity.class));
        host.addTab(spec1);
        host.addTab(spec2);

    }
}
