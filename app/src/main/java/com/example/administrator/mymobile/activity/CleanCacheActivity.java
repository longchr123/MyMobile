package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.mymobile.R;

import java.lang.reflect.Method;
import java.util.List;

public class CleanCacheActivity extends Activity {

    private static final int SCANING =0 ;
    private static final int SCANING_FINISH =1 ;
    private static final int SHOW_CACHE = 2;
    private TextView tvStatus;
    private ProgressBar progressbar;
    private LinearLayout llContaner;//ListView的特点是，数据加载好了，一下子呈现出来，而LinearLayout一条一条添加与呈现
    private PackageManager pm;
    private ImageView ivIcon,ivDelete;
    private TextView tvName;
    private TextView tvCache;

    private void assignViews() {
        tvStatus = (TextView) findViewById(R.id.tv_status);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        llContaner = (LinearLayout) findViewById(R.id.ll_contaner);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCANING:
                    String name= (String) msg.obj;
                    tvStatus.setText("正在扫描："+name);
                    break;
                case SCANING_FINISH:
                    tvStatus.setText("扫描结束");
                    break;
                case SHOW_CACHE:
                    final CacheInfo cacheInfo= (CacheInfo) msg.obj;
                    View view=View.inflate(CleanCacheActivity.this,R.layout.cache_item,null);
                    ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                    tvName = (TextView) view.findViewById(R.id.tv_name);
                    tvCache = (TextView) view.findViewById(R.id.tv_cache);
                    ivDelete= (ImageView) view.findViewById(R.id.iv_delete);
                    ivIcon.setImageDrawable(cacheInfo.icon);
                    tvCache.setText(Formatter.formatFileSize(CleanCacheActivity.this, cacheInfo.cacheSize));
                    tvName.setText(cacheInfo.name);
                    //要想清除某个应用的缓存信息起作用，只需要把我们的应用变成系统应用
                    ivDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Method[] methods=PackageManager.class.getMethods();
                            for(Method method:methods){
                                if("deleteApplicationCacheFiles".equals(method.getName())) {
                                    try {
                                        method.invoke(pm,cacheInfo.packname, new IPackageDataObserver.Stub() {//对这个得到的方法进行执行,得到最大的空间，即会自动清除所有缓存
                                            @Override
                                            public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //根据包名跳转到对应的系统页面。在腾讯手机卫士中有这个功能，通过打印出那个功能的日志得到的思维
                                        Intent intent=new Intent();
                                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                        intent.setData(Uri.parse("package:"+cacheInfo.packname));
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    });
                    llContaner.addView(view,0);//添加到第0个位置
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        assignViews();
        new Thread(){
            @Override
            public void run() {
                super.run();
                pm=getPackageManager();
                List<PackageInfo> packageInfos=pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
                progressbar.setMax(packageInfos.size());
                int progress=0;
                for(PackageInfo packageInfo:packageInfos){
                    try {
                        SystemClock.sleep(50);
                        String packName=packageInfo.packageName;
                        String name = packageInfo.applicationInfo.loadLabel(pm).toString();
                        Message msg = Message.obtain();
                        msg.obj = name;
                        msg.what = SCANING;
                        handler.sendMessage(msg);
                        progress++;
                        progressbar.setProgress(progress);
                        //用反射得到缓存大小
                        Method method=PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);//此方法直接得到其中的某个方法
                        method.invoke(pm,packName,new MyIPackageStatsObserver());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                //结束时再发一个
                Message msg=Message.obtain();
                msg.what=SCANING_FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }
    class MyIPackageStatsObserver extends IPackageStatsObserver.Stub{

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize=pStats.cacheSize;
            if(cacheSize>13000){
                try {
                    CacheInfo cacheInfo = new CacheInfo();
                    cacheInfo.cacheSize = cacheSize;
                    cacheInfo.packname = pStats.packageName;
                    cacheInfo.name = pm.getApplicationInfo(pStats.packageName, 0).loadLabel(pm).toString();//此时的name得用包管理器得到。
                    cacheInfo.icon = pm.getApplicationInfo(pStats.packageName, 0).loadIcon(pm);
                    Message msg=Message.obtain();
                    msg.obj=cacheInfo;
                    msg.what=SHOW_CACHE;//类型
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    class CacheInfo{
        String name;
        String packname;
        Drawable icon;
        long cacheSize;
    }
    //点击事件-清除手机里面所有应用的缓存
    public void cleanAll(View view){
        Method[] methods=PackageManager.class.getMethods();
        for(Method method:methods){
            //freeStorageAndNotify方法有一个缺点：如果手机只剩下20M空间，当此方法申请50M空间时，此方法会自动清除缓存来达到50M的空间
            if("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(pm,Integer.MAX_VALUE, new IPackageDataObserver.Stub() {//对这个得到的方法进行执行,得到最大的空间，即会自动清除所有缓存
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                            System.out.println("succeeded=="+succeeded);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
