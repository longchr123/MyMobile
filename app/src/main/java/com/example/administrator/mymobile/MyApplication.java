package com.example.administrator.mymobile;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2015/10/26.
 */
public class MyApplication extends Application {

    //程序入口，任何组件被调用前调用
    public void onCreate() {
        super.onCreate();
        //监听异常
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
    }

    private class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
        //所有没有捕获的异常都会弄到这里
            System.out.println("捕获一个异常...............");
            try {
                File file=new File(Environment.getExternalStorageDirectory(),"carch.log");
                PrintWriter err=new PrintWriter(file);
                ex.printStackTrace(err);
                err.flush();
                err.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
           //重新启动自己
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    //当系统资源耗尽时程序被系统关闭时执行
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
