package com.example.administrator.mymobile;

import android.test.AndroidTestCase;

import com.app.AdvancedTools.AppInfo;
import com.app.AdvancedTools.AppInfoProvider;

import java.util.List;

/**
 * Created by Administrator on 2015/10/13.
 */
public class GetAppTest extends AndroidTestCase{
    public void getApp(){
        List<AppInfo> appInfos=AppInfoProvider.getAllAppInfos(getContext());
        for(AppInfo info : appInfos){
            System.out.println(info.toString());
        }
    }
}
