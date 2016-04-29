package com.example.administrator.mymobile.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.administrator.mymobile.MyAdmin;

public class LockScreenActivity extends AppCompatActivity {

    private DevicePolicyManager dpm;//设备策略管理员，是一个服务
    ComponentName who=new ComponentName(this,MyAdmin.class);
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        dpm= (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//        dpm.lockNow();//锁屏
        openAdmin(null);//直接开启管理员权限
        finish();
    }
    public void lockscreen(View view){

        if(dpm.isAdminActive(who)) {//如果管理员权限开启就直接锁屏
            dpm.lockNow();//锁屏
            dpm.resetPassword("123",0);//设置密码
//            dpm.wipeData(0);//让手机恢复成出厂设置，远程删除数据
//            dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//清除手机sd卡的数据
        }else {//否则开启管理员权限
            openAdmin(null);
        }
    }
    public void openAdmin(View view){
        //定义意图：动作：添加设备管理员
        Intent intent=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //激活的组件
//        ComponentName who=new ComponentName(this,MyAdmin.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,who);
        //激活的说明
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"激活管理员权限，可以一键锁屏，更安全");
        startActivity(intent);
    }
    //卸载软件
    public void uninstall(View view){
        //1.把权限干掉
//        ComponentName who=new ComponentName(this,MyAdmin.class);
        dpm.removeActiveAdmin(who);
        //2.当成普通应用卸载
        Intent intent=new Intent();
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+getPackageName()));
        startActivity(intent);
    }
}
