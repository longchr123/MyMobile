package com.example.administrator.mymobile.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**此方法用于校验服务，存在两种状态，一种是本软件没有关闭，还在后台运行，另一种是完全关闭,如果运行中就返回true
 * Created by Administrator on 2015/10/7.
 */
public class ServiceStatusUtils {
    public static boolean isRunning(Context context,String serviceName){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo>  serviceInfos=am.getRunningServices(100);//获取正在运行的100个服务
        for(ActivityManager.RunningServiceInfo service:serviceInfos){
            //得到全类名
            String name=service.service.getClassName();
            if(serviceName.equals(name)){
                return true;
            }
        }
        return false;

    }
}
