package com.example.administrator.mymobile.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2015/10/17.
 */
public class SystemInfoUtils {
    //得到当前手机运行进程数量
    public static int getRunningProcessCount(Context context){//传入上下文件能得到ActivityManager
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses().size();
    }
    //得到当前手机的可用内存
    public static long getAvailRam(Context context){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }
    //得到当前手机的总内存
    public static long getTotalRam(Context context){
        //此totalMem中能运行在16版本以上的手机中
//        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo outInfo=new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(outInfo);
//        return outInfo.totalMem;

        //这是改善的方法
        try {
            File file=new File("/proc/meminfo");
            FileInputStream fis=new FileInputStream(file);
            BufferedReader reader=new BufferedReader(new InputStreamReader(fis));
            //读取出第一行：信息为   MemTotal:       516452KB
            //而第二行是可用空间
            String result=reader.readLine();
//            String result2=reader.readLine();此为读取第二行

            //再取出数字
            StringBuffer buffer=new StringBuffer();
            for(char c :result.toCharArray()){
               if(c>='0'&&c<='9'){
                   buffer.append(c);
               }
            }
            return Integer.parseInt(buffer.toString())*1024;//Integer.parseInt方法中的第一个参数为String类型，第二个默认为10（十进制）
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

}
