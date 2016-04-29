package com.example.administrator.mymobile.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymobile.R;
import com.app.AdvancedTools.AppInfo;
import com.app.AdvancedTools.AppInfoProvider;
import com.example.administrator.mymobile.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

//在drawable中放图片，图片会原封不动的显示，在drawable-hdpi中存放一些手机高清的资源，
// 同样的图片放在此目录下，手机会根据屏幕密度（dpi）做相应的缩小处理，drawable-xxhdpi
// 一般存放的是超大图片，所以会做更大的图片缩小处理
public class AppManagerActivity extends Activity implements View.OnClickListener {

    private TextView tv_rom, tv_sdcard, tv_status;
    private ListView lv_app;
    private LinearLayout ll_loading;
    private LinearLayout ll_uninstall;//卸载
    private LinearLayout ll_start;//启动
    private LinearLayout ll_share;//分享
    private List<AppInfo> appInfos;//所有应用程序的信息
    private AppMangeAdapter adapter;
    private List<AppInfo> systemAppInfos;
    private List<AppInfo> userAppInfos;
    private PopupWindow window;
    private AppInfo appInfo;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter = new AppMangeAdapter();
            lv_app.setAdapter(adapter);
            ll_loading.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        init();
        tv_rom.setText("内存可用：" + getAvailSpace(Environment.getDataDirectory().getAbsolutePath()));
        tv_sdcard.setText("sd卡可用：" + getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath()));
        fillData();//加载数据
        lv_app.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userAppInfos == null || systemAppInfos == null) return;//防止下面的空指针异常
                dismissPopupWindow();//当滚动时也消除已有的PopupWindow
                if (firstVisibleItem > userAppInfos.size()) {
                    //显示系统程序
                    tv_status.setText("系统程序（" + systemAppInfos.size() + ")");
                } else {
                    tv_status.setText("用户程序（" + userAppInfos.size() + ")");
                }
            }
        });
        //设置点击某一条的响应
        lv_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = lv_app.getItemAtPosition(position);//根据位置得到列表中某个数据,从下面的BaseAdapter中的getItem得到
                if (obj != null) {//也就是没有点击text文本
                    dismissPopupWindow();
                    appInfo = (AppInfo) obj;
//                    TextView contentView=new TextView(AppManagerActivity.this);
//                    contentView.setTextColor(Color.RED);
//                    contentView.setText(appInfo.getPackName());
//                    window=new PopupWindow(contentView,-2,-2);//-2为包裹类型
                    View contentView = View.inflate(AppManagerActivity.this, R.layout.popup_window_item, null);
                    window = new PopupWindow(contentView, -2, ActionBar.LayoutParams.WRAP_CONTENT);

                    ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                    ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
                    ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                    ll_uninstall.setOnClickListener(AppManagerActivity.this);//在Click中再设置Click有点不好区分，所以把本次的onClick写出去
                    ll_start.setOnClickListener(AppManagerActivity.this);
                    ll_share.setOnClickListener(AppManagerActivity.this);
                    //要想要popup播放动画，则需要背景
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//透明
                    int[] location = new int[2];
                    view.getLocationInWindow(location);//得到点击对像的坐标,代码中写的长度单位是像素
                    //把60像素当成dip，根据不同屏幕，转换成不同的像素
                    int px = DensityUtil.dip2px(AppManagerActivity.this, 60);
//                    System.out.println("px======"+px);
                    window.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, px, location[1]);
                    //在drawable中放图片，图片会原封不动的显示，在drawable-hdpi中存放一些手机高清的资源，
                    // 同样的图片放在此目录下，手机会根据屏幕密度（dpi）做相应的缩小处理，drawable-xxhdpi一般存放的是超大图片，所以会做更大的图片缩小处理

                    //渐变动画
                    AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
                    aa.setDuration(500);
                    //缩放动画
                    ScaleAnimation sa = new ScaleAnimation(0.4f, 1.0f, 0.4f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(500);

                    AnimationSet set = new AnimationSet(false);
                    set.addAnimation(aa);
                    set.addAnimation(sa);
                    contentView.startAnimation(set);

                }
            }
        });
    }

    //当PopupWindow存在时而要点击下一个view时，消除上一个PopupWindow
    private void dismissPopupWindow() {
        if (window != null && window.isShowing()) {
            window.dismiss();
            window = null;
        }
    }


    private class AppMangeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return (systemAppInfos.size() + 1 + userAppInfos.size() + 1);//外加两行text文本
        }

        @Override
        public Object getItem(int position) {
            AppInfo appInfo;
            if (position == 0) {
                return null;
            } else if (position == (userAppInfos.size() + 1)) {
                return null;
            } else if (position <= userAppInfos.size()) {
                //用户程序
                int newposition = position - 1;//因为插入一个文本了，占用一行
                appInfo = userAppInfos.get(newposition);
            } else {
                int newposition = position - 2;
                //系统程序
                appInfo = systemAppInfos.get(newposition - userAppInfos.size());
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //得到应用程序的信息
            AppInfo appInfo;
            if (position == 0) {
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户程序（" + userAppInfos.size() + ")");
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (position == (userAppInfos.size() + 1)) {
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统程序（" + systemAppInfos.size() + ")");
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (position <= userAppInfos.size()) {
                //用户程序
                int newposition = position - 1;//因为插入一个文本了，占用一行
                appInfo = userAppInfos.get(newposition);
            } else {
                int newposition = position - 2;
                //系统程序
                appInfo = systemAppInfos.get(newposition - userAppInfos.size());
            }
            View view;
            ViewHolder holder;
            //上面的if没有返回Tag，所以会导致holder.getTag空指针，所以再加下一个相对布局条件
            if (convertView != null && convertView instanceof RelativeLayout) {//布局文件的结点为相对布局，实例化之后也就是相对布局
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.appmanager_item, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_location);
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                //把对应关系保存
                view.setTag(holder);
            }

            holder.tv_name.setText(appInfo.getName());
            if (appInfo.isRom()) {
                //安装在我们的手机内部
                holder.tv_location.setText("内部存储");
            } else {
                holder.tv_location.setText("外部存储...");
            }
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            return view;
        }

    }

    //定义容器
    private static class ViewHolder {
        TextView tv_name;
        TextView tv_location;
        ImageView iv_icon;
    }

    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                super.run();
                appInfos = AppInfoProvider.getAllAppInfos(AppManagerActivity.this);
                //划分数据
                systemAppInfos = new ArrayList<AppInfo>();
                userAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo : appInfos) {
                    if (appInfo.isUser()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    //根据路径得到可用内存
    private String getAvailSpace(String path) {
        StatFs fs = new StatFs(path);
        //得到多少块可用空间
        long blocks = fs.getAvailableBlocksLong();
        //得到每个空间的大小
        long size = fs.getBlockSizeLong();
        //blocks*size是以字节为单位，本方法可自动将其转换为MB或更大的GB等
        return android.text.format.Formatter.formatFileSize(this, blocks * size);
    }

    //点击事件写在此处，
    @Override
    public void onClick(View v) {
        dismissPopupWindow();
        switch (v.getId()) {//根据id进行
            case R.id.ll_uninstall://卸载
                uninstallApp();
                break;
            case R.id.ll_start://启动
//                startApp();
                startApp2();
                break;
            case R.id.ll_share://分享
                shareApp();
                break;
        }
    }

    private void startApp2() {
        Intent intent;
        //得到清单文件中的Activity信息
        PackageManager pm = getPackageManager();
        String packName = appInfo.getPackName();
        intent = pm.getLaunchIntentForPackage(packName);
        startActivity(intent);
    }

    private void startApp() {
        Intent intent = new Intent();
        //得到清单文件中的Activity信息
        PackageManager pm = getPackageManager();
        String packName = appInfo.getPackName();
        try {
            PackageInfo packInfo = pm.getPackageInfo(packName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] acrivityInfos = packInfo.activities;
            if (acrivityInfos != null && acrivityInfos.length > 0) {
                //得到第一个Activity
                ActivityInfo activityInfo = acrivityInfos[0];
                String name = activityInfo.name;//全类名
                intent.setClassName(packName, name);
                startActivity(intent);
            } else {
                Toast.makeText(this, "这个程序没有页面", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    //分享到微博、qq微信等，只要你手机上有的这个意图
    private void shareApp() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "最近使用：" + appInfo.getName() +
                "下载地址：http:www.appchina.com/app/" + appInfo.getPackName());
        startActivity(intent);

    }

    private void uninstallApp() {
        if (appInfo.isUser()) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + appInfo.getPackName()));
            startActivityForResult(intent, 0);//不直接用startActivity的原因：卸载一个软件之后，需要把列表中的这个程序的信息也刷新出去
        } else {
            Toast.makeText(this, "需要root权限才能卸载", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fillData();//重新加载数据
    }

    private void init() {
        tv_rom = (TextView) findViewById(R.id.tv_rom);
        tv_sdcard = (TextView) findViewById(R.id.tv_sdcard);
        lv_app = (ListView) findViewById(R.id.lv_app);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_status = (TextView) findViewById(R.id.tv_status);

    }


}
