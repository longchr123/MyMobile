package com.example.administrator.mymobile.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/17.
 */
public class TaskInfoProvider {
    //得到手机所有运行的进程信息
    public static List<TaskInfo> getAllTaskInfos(Context context){
        List<TaskInfo> taskInfos=new ArrayList<TaskInfo>();
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos=am.getRunningAppProcesses();
        PackageManager pm=context.getPackageManager();
        for(ActivityManager.RunningAppProcessInfo processInfo:processInfos){
            TaskInfo taskInfo=new TaskInfo();
            String packName=processInfo.processName;
            taskInfo.setPackageName(packName);
            Debug.MemoryInfo memoryInfo=am.getProcessMemoryInfo(new int[]{processInfo.pid})[0];
            long meminfoSize=memoryInfo.getTotalPrivateClean()*1024;//这个应用在内存中的大小
            taskInfo.setMemInfoSize(meminfoSize);
            try {
                Drawable icon=pm.getPackageInfo(packName,0).applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);
                String name=pm.getPackageInfo(packName,0).applicationInfo.loadLabel(pm).toString();
                taskInfo.setName(name);
                int flag=pm.getPackageInfo(packName,0).applicationInfo.flags;
                if((flag& ApplicationInfo.FLAG_SYSTEM)==0){
                    taskInfo.setIsUser(true);
                }else{
                    taskInfo.setIsUser(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                //系统中有些进程是用C语言写的，则没有icon，name；
                taskInfo.setName(packName);
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.delete));
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
