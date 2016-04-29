package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.service.UpdateAppWidgetService;
import com.example.administrator.mymobile.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

    private SharedPreferences sp;
    private static final int ENRER_HOME =1;
    private static final int SHOW_UPDATE_DIALOG=2;
    private static final int URL_ERROR =3;
    private static final int NETWORK_ERROR =4;
    private static final int JSON_ERROR =5;
    private TextView tv_splash_version;
    private  TextView tv_splash_updateinfo;
    protected static final String TAG = "SplashActivity";
    //升级的描述信息和地址
    private String description;
    private String apkurl;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ENRER_HOME://进入主页面
                    enterHome();
                    break;
                case SHOW_UPDATE_DIALOG://弹出升级对话框
                    Log.e(TAG, "有新版本，请升级");
                    showUpdateDialog();
                    break;
                case URL_ERROR://URL异常
                    enterHome();
                    Toast.makeText(MainActivity.this, "URL异常", Toast.LENGTH_SHORT).show();
                    break;
                case NETWORK_ERROR://网络异常
                    enterHome();
                    Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    break;
                case JSON_ERROR://JSON解析异常
                    enterHome();
                    Toast.makeText(MainActivity.this,"JSON解析异常",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    enterHome();
                    break;
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();
        copyDB("address.db");
        copyDB("commonnum.db");
        copyDB("antivirus.db");
        createShortCut();//创建快捷键
        tv_splash_version.setText("版本名：" + getVersionName());//从清单文件中获取板本信息（自定义方法的实现）
        //软件升级
        if(sp.getBoolean("update",false)) {
            //延时两秒进入主页面
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterHome();
                }
            }, 2000);
        }else {
            checkVersion();
        }
        //在主页面显示之前的这个非引导页面透明度变化
//        transparent();
    }

    //把我们的assets目录下的address.db拷贝到/data/data/com.example.administrator.mymobile/files/address.ab
    private void copyDB(String dbname) {
        File file=new File(getFilesDir(),dbname);
        Toast.makeText(this, getFilesDir()+"", Toast.LENGTH_SHORT).show();
        if(file.exists()&&file.length()>0){
            System.out.println("数据库已经存在且不为空,不用再拷贝了");
        }else {
            try {
                InputStream is = getAssets().open(dbname);
                FileOutputStream fos = new FileOutputStream(file);
                //把输出流写入对应的文件中
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkVersion() {//检验是否有新版本
        //下面方法同样可定义为new Thread(new Runnable() {public void run() {}}).start();
        Thread t=new Thread(){
            public void run() {
                Message msg=Message.obtain();//去消息池中取消息，如果没有就new一个
                long startTime=System.currentTimeMillis();//子线程运行的起始时间
                try {
                    //请求网络，得到最新版本信息,需权限
//                    URL url;  getString(R.string.server_url  getResources().getString(R.string.server_url)
                    URL url=new URL(getString(R.string.server_url));
                    //请求连接，网络异常将会报错
                    HttpURLConnection con= (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");//设置请求方法
                    con.setConnectTimeout(5000);
                    int code=con.getResponseCode();
                    if(code==200){
                        //请求成功，把流换成String类型
                        InputStream is=con.getInputStream();
                        String result= StreamTools.readFromStream(is);
                        Log.e(TAG,"result--"+result);//在本类中打印输出
                        //解析JSON
                        JSONObject obj=new JSONObject(result);
                        //服务器最新版本
                        String version= (String) obj.get("version");
                        description= (String) obj.get("description");
                        apkurl= (String) obj.get("apkurl");
                        if(getVersionName().equals(version)){
                            //如果本地的版本和获得版本一样，就没有新版本，进入主页面
                            msg.what=ENRER_HOME;
                        }else {
                            //弹出升级对话框
                            msg.what=SHOW_UPDATE_DIALOG;
                        }
                    }
                } catch (MalformedURLException e) {
                    //URL错误
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                }catch(IOException e){
                    //网络异常
                    msg.what=NETWORK_ERROR;
                }catch (JSONException e){
                    //解析json异常
                    msg.what=JSON_ERROR;
                }finally {
                    long endTime=System.currentTimeMillis();
                    long dTime=endTime-startTime;
                    if(dTime<2000){
                        SystemClock.sleep(2000 - dTime);//如果子线程运行不到2s，则睡眠剩下的时间
                    }
                    //一定要记得发消息,返回到定义的hanndler中
                    handler.sendMessage(msg);
                }
            }
        };
        t.start();
    }

    private String getVersionName(){//从清单文件中获取板本信息
        //包管理器
        PackageManager pm=getPackageManager();
        //功能清单文件信息
        try {
            PackageInfo packageInfo=pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public void init(){//初始化所有变量
        tv_splash_version= (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_updateinfo=(TextView) findViewById(R.id.tv_splash_updateinfo);
        sp=getSharedPreferences("config",MODE_PRIVATE);
    }
    private void enterHome() {//进入主页面
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade,R.anim.exit);
        //关闭启动页面，或是当前页面，否则点击返回又会返回到当前页面
        finish();
    }
    private void showUpdateDialog() {//弹出升级对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示");
//        builder.setCancelable(false);//强制升级，与下面的setOnCancelListener不能同在
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {//在弹出升级对话框时，点击其他取消监听
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();//对话框消失
                enterHome();
            }
        });
        builder.setMessage(description);//内容与JSON中的一致
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//对话框消失
                enterHome();
            }
        });
        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //sd卡是否可用，MEDIA_MOUNTED为可用状态，是否存在安装包
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //下载apk，替换
                    FinalHttp http = new FinalHttp();
                    //参数：下载地址； 存储地址，
                    http.download(apkurl, Environment.getExternalStorageDirectory() + "/mobilesafe3.0.apk", new AjaxCallBack<File>() {
                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                            t.printStackTrace();//打出错误日志
                            Toast.makeText(getApplication(), "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            Toast.makeText(getApplication(), "下载成功", Toast.LENGTH_SHORT).show();
                            installApk(file);
                        }

                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            tv_splash_updateinfo.setVisibility(View.VISIBLE);//设置下载进度值可见
                            int progress = (int) (current * 100 / count);
                            tv_splash_updateinfo.setText("下载进度" + progress + "%");
                        }
                    });
                } else {
                    Toast.makeText(getApplication(), "SD卡不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }
    //安装apk
    private void installApk(File file) {
        Intent intent=new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void transparent() {
        AlphaAnimation aa=new AlphaAnimation(1.0f,0.1f);//从0.1变到1.0
        aa.setDuration(2000);//用时1s
        findViewById(R.id.rl_splash_root).startAnimation(aa);
    }

    //创建快捷键
    private void createShortCut() {
        boolean shortcut=sp.getBoolean("shortcut",false);
        if(shortcut) {//为了防止重复产生快捷键
            Toast.makeText(this, "shortcut="+shortcut, Toast.LENGTH_SHORT).show();
            return;
        }else {
            Intent intent = new Intent();
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");//创建的动作
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机安全");//名称
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(
                    getResources(), R.drawable.hanma));//设置图标
            //点击此快捷键的动作
            Intent intent2 = new Intent();
            intent2.setAction("com.example.administrator.mymobile.activity.atools");
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
            sendBroadcast(intent);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("shortcut", true).commit();
        }
    }
}
