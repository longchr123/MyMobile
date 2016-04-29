package com.app.AdvancedTools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
  * App信息提供者
 *      通过PackageManager来得到我们需要的应用图标、名字和包名
 */
public class AppInfoProvider {

    //通过这个类将得到所有应用信息
    public static List<AppInfo> getAllAppInfos(Context context){//没context无法得到包管理器
        List<AppInfo> appInfos=new ArrayList<AppInfo>();
        PackageManager pm=context.getPackageManager();
        List<PackageInfo> packageInfos=pm.getInstalledPackages(0);//得到安装应用的信息
        for(PackageInfo info : packageInfos){
            AppInfo appInfo=new AppInfo();
            String packageName=info.packageName;
            appInfo.setPackName(packageName);
            Drawable icon=info.applicationInfo.loadIcon(pm);//得到图标，从清单文件中的<application>中的icon
            appInfo.setIcon(icon);
            String name=info.applicationInfo.loadLabel(pm).toString();//得到名称
            appInfo.setName(name);
            //得到应用的标识，可任意组合
            int flage=info.applicationInfo.flags;
            //例如：flage为0010，& 0100 如果flage为0100，则为内部存储
            if((flage& ApplicationInfo.FLAG_SYSTEM)==0){
                //用户程序
                appInfo.setIsUser(true);
            }else {
                //系统程序
                appInfo.setIsUser(false);
            }
            if((flage& ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
                //内部存储
                appInfo.setIsRom(true);
            }else{
                //外部存储
                appInfo.setIsRom(false);
            }

            appInfos.add(appInfo);

        }
        return appInfos;
    }
}
