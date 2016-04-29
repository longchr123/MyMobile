package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.mymobile.AddressService;
import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.SettingClick;
import com.example.administrator.mymobile.SettingItem;
import com.example.administrator.mymobile.service.CallSmsSafeService;
import com.app.AdvancedTools.WatchDogService;
import com.example.administrator.mymobile.utils.ServiceStatusUtils;

public class SettingActivity extends Activity {

    private SettingItem si_show_address;//设置来电归属地显示
    private SettingItem si_update;
    private SharedPreferences sp;//保存软件参数数据（在返回后保存CheckBox数据），
    private SettingClick sc_change;//设置归属地显示框的背景
    private  SettingClick sc_change_postion;// 设置归属地显示框的位置
    private  SettingItem si_black_number;//设置黑名单拦截
    private SettingItem si_applock;//设置程序锁
    private Intent watchDogInetnt;//看门狗
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp=getSharedPreferences("config",MODE_PRIVATE);//实例化
        si_update= (SettingItem) findViewById(R.id.si_update);
        si_show_address= (SettingItem) findViewById(R.id.si_show_address);
        boolean update=sp.getBoolean("update", false);//默认为false,保存后还得取出数据，写入对应的地方
        boolean show_address=sp.getBoolean("show_address", false);
//        boolean blacknumber=sp.getBoolean("blacknumber", false);
        if(update){
            //自动升级已经开启
//            si_update.setDescription("当前状态为自动升级已经开启");
        }else {
            //自动升级已经关闭
//            si_update.setDescription("当前状态为自动升级已经关闭");
        }
        si_update.setchecked(update);
        si_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();//在保存之前先得到一个编辑器
                //得到是否被勾选
                if (si_update.isChecked()) {
                    si_update.setchecked(false);
//                    si_update.setDescription("当前状态为自动升级已经关闭");
                    editor.putBoolean("update", false);
                } else {
                    si_update.setchecked(true);
//                    si_update.setDescription("当前状态为自动升级已经开启");
                    editor.putBoolean("update", true);
                }
                editor.commit();//必须写在外面
            }
        });
        si_show_address.setchecked(show_address);
        //检验本服务是否在运行中，输入全类明
        boolean addressService= ServiceStatusUtils.isRunning(this, "com.example.administrator.mymobile.AddressService");
        si_show_address.setchecked(addressService);

        si_show_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (si_show_address.isChecked()) {
                    si_show_address.setchecked(false);
                    Intent addressIntent = new Intent(SettingActivity.this, AddressService.class);
                    stopService(addressIntent);
                } else {
                    si_show_address.setchecked(true);
                    Intent addressIntent = new Intent(SettingActivity.this, AddressService.class);
                    startService(addressIntent);
                }
            }
        });
        //设置显示框风格
        sc_change= (SettingClick) findViewById(R.id.sc_change);
        final String items[]={"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
        final int which=sp.getInt("which",0);//默认为0
        sc_change.setDescription(items[which]);
        sc_change.setTitle("归属地提示框风格");
        sc_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int whichs = sp.getInt("which", 0);//刚开始进入时which与whichs是一样的，当点击设置时which不会运行，而whichs会改变，就不一样了。
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("归属地提示框风格");
                //默认为第whichs个
                builder.setSingleChoiceItems(items, whichs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        1.保存起来
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("which", which);
                        editor.commit();
//                        2.设置描述信息
                        sc_change.setDescription(items[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        String test=null;
//                        test.toString();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        sc_change_postion= (SettingClick) findViewById(R.id.sc_change_position);
        sc_change_postion.setTitle("设置归属地显示框位置");
        sc_change_postion.setDescription("设置归属地提示框显示的位置");
        sc_change_postion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, DragViewActivity.class);
                startActivity(intent);

            }
        });
        //设置黑名单拦截
        si_black_number=(SettingItem) findViewById(R.id.si_black_number);
//        si_black_number.setchecked(blacknumber);
        boolean blackNumberService=ServiceStatusUtils.isRunning(this, "com.example.administrator.mymobile.service.CallSmsSafeService");
        si_black_number.setchecked(blackNumberService);
        si_black_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if (si_black_number.isChecked()) {
                    si_black_number.setchecked(false);
                    Intent blackNumberIntent = new Intent(SettingActivity.this, CallSmsSafeService.class);
//                    editor.putBoolean("blacknumber", false);
//                    editor.commit();
                    stopService(blackNumberIntent);
                } else {
                    si_black_number.setchecked(true);
                    Intent blackNumberIntent = new Intent(SettingActivity.this, CallSmsSafeService.class);
//                    editor.putBoolean("blacknumber", true);
//                    editor.commit();
                    startService(blackNumberIntent);
                }
            }
        });
        //设置程序锁
        si_applock= (SettingItem) findViewById(R.id.si_applock);
        watchDogInetnt=new Intent(this, WatchDogService.class);
        boolean watchService=ServiceStatusUtils.isRunning(this,"com.app.Advanced.Tools.WatchDogService");
        si_applock.setchecked(watchService);
        stopService(watchDogInetnt);
        si_applock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (si_applock.isChecked()) {
                    si_applock.setchecked(false);
                    stopService(watchDogInetnt);
                } else {
                    si_applock.setchecked(true);
                    startService(watchDogInetnt);
                }
            }
        });
    }


    //此方法存在的原因：当用户处于桌面再点回软件时，此时软件不会调用onCreate方法，而是此方法。
    protected void onResume() {
        super.onResume();
        boolean addressService=ServiceStatusUtils.isRunning(this,"com.example.administrator.mymobile.AddressService");
        si_show_address.setchecked(addressService);
        boolean blackNumberService=ServiceStatusUtils.isRunning(this,"com.example.administrator.mymobile.service.CallSmsSafeService");
        si_black_number.setchecked(blackNumberService);
    }
}
