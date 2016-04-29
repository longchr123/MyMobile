package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.db.QueryAntiVirus;
import com.example.administrator.mymobile.utils.MD5Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntiVirusActivity extends Activity {

    private static final int SCANING=0;
    private static final int SCANING_FINFISH =1;
    private ImageView ivScanning;
    private TextView tvStatus;
    private ProgressBar pb;
    private LinearLayout llContaner;
    private List<Scaninfo> antiVirus;

    private void assignViews() {
        ivScanning = (ImageView) findViewById(R.id.iv_scanning);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        pb = (ProgressBar) findViewById(R.id.pb);
        llContaner= (LinearLayout) findViewById(R.id.ll_contaner);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCANING:
                    Scaninfo scaninfo= (Scaninfo) msg.obj;
                    tvStatus.setText("正在扫描："+scaninfo.name);
                    TextView tv=new TextView(AntiVirusActivity.this);
                    if(scaninfo.isAntiVirus){
                        tv.setTextColor(Color.RED);
                        tv.setText("发现病毒：" + scaninfo.name);
                    }else {
                        tv.setTextColor(Color.GREEN);
                        tv.setText("扫描安全："+scaninfo.name);
                    }
                    llContaner.addView(tv,0);
                    break;
                case SCANING_FINFISH:
                    ivScanning.clearAnimation();//动画停止
                    if(antiVirus.size()>0&&antiVirus!=null) {
//                        Toast.makeText(AntiVirusActivity.this, "病毒，请及时处理", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder=new AlertDialog.Builder(AntiVirusActivity.this);
                        builder.setTitle("警告！！！");
                        builder.setMessage("您的手机处于十分危险状态，发现发现：" + antiVirus.size() + "个病毒，请及时处理。");
                        builder.setPositiveButton("立刻杀毒", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (Scaninfo scaninfo : antiVirus) {
                                    //卸载软件
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.DELETE");
                                    intent.addCategory("android.intent.category.DEFAULT");
                                    intent.setData(Uri.parse("package:" + scaninfo.packName));
                                    startActivity(intent);
                                }
                            }
                        });
                        builder.setNegativeButton("下次再说", null);//消除对话框
                        builder.show();

                    }else {
                        Toast.makeText(AntiVirusActivity.this, "您的手机相当安全", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        assignViews();
        antiVirus=new ArrayList<Scaninfo>();//用来装病毒
        RotateAnimation ra=new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(1000);
        ra.setRepeatCount(RotateAnimation.INFINITE);//播放无限次
        ivScanning.startAnimation(ra);

        new Thread(){
            @Override
            public void run() {
                super.run();
//                for (int i=0;i<100;i++){
////                    SystemClock.sleep(50);
////                    pb.setProgress(i);//运行时的进度条
                SystemClock.sleep(2000);
                PackageManager pm=getPackageManager();
                List<PackageInfo> packInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                        + PackageManager.GET_SIGNATURES);//得到未卸载完全的垃圾,以及签名
                Random random=new Random();
                int progress=0;
                pb.setMax(packInfos.size());
                    for(PackageInfo packInfo:packInfos){
                        //这句代码如果放在onCreate方法 assignViews();的后面，卸载的是最后一个扫描的应用
                        Scaninfo scaninfo=new Scaninfo();
                        scaninfo.name=packInfo.applicationInfo.loadLabel(pm).toString();
                        scaninfo.packName=packInfo.packageName;
                        String signatures=packInfo.signatures[0].toCharsString();//得到签名的信息
                        String md5Sign= MD5Utils.ecoder(signatures);//进行md5加密
//                        System.out.println("++++++"+scaninfo.name+"-----"+md5Sign);
                        String result= QueryAntiVirus.getDesc(md5Sign);
                        if(result !=null){
                            scaninfo.isAntiVirus=true;
                            System.out.println("是病毒");
                            antiVirus.add(scaninfo);
                        }else {
                            scaninfo.isAntiVirus=false;
                        }
                        //发出扫描信息
                        Message msg=Message.obtain();
                        msg.what=SCANING;
                        msg.obj=scaninfo;
                        handler.sendMessage(msg);
                        progress++;
                        pb.setProgress(progress);
                        SystemClock.sleep(50+random.nextInt(50));//休眠50-100之间
                    }
                    Message msg=Message.obtain();
                    msg.what=SCANING_FINFISH;
                handler.sendMessage(msg);
            }
        }.start();
    }
    //病毒扫描
    class Scaninfo{
        String name;
        String packName;
        boolean isAntiVirus;
    }
}
