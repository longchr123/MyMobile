package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.service.KillProcessService;
import com.example.administrator.mymobile.utils.ServiceStatusUtils;

public class TaskManagerSettingActivity extends Activity {

    private CheckBox cbKillProcess;
    private CheckBox cbShowSystemProcess;
    private SharedPreferences sp;
    private Intent killProcessIntent;

    private void assignViews() {
        cbShowSystemProcess = (CheckBox) findViewById(R.id.cb_show_system_process);
        cbKillProcess = (CheckBox) findViewById(R.id.cb_kill_process);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
        assignViews();
        sp=getSharedPreferences("config",MODE_PRIVATE);
        boolean showsystem=sp.getBoolean("showsystem",true);
        if(showsystem){
            cbShowSystemProcess.setText("当前状态：显示系统进程");
        }else {cbShowSystemProcess.setText("当前状态：隐藏系统进程");}
        cbShowSystemProcess.setChecked(showsystem);
        cbShowSystemProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbShowSystemProcess.setText("当前状态：显示系统进程");
                } else {
                    cbShowSystemProcess.setText("当前状态：隐藏系统进程");
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("showsystem", isChecked).commit();
            }
        });
        killProcessIntent=new Intent(this, KillProcessService.class);
        //判断服务是否运行，如果运行，设置为true
        boolean isRuningService=ServiceStatusUtils.isRunning(this,"com.example.administrator.mymobile.service.KillProcessService");
        if(isRuningService){
            cbKillProcess.setText("当前状态：锁屏杀死后台进程");
        }else {
            cbKillProcess.setText("当前状态：锁屏不杀死进程");
        }
        cbKillProcess.setChecked(isRuningService);
        cbKillProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbKillProcess.setText("当前状态：锁屏杀死后台进程");
                    startService(killProcessIntent);
                } else {
                    cbKillProcess.setText("当前状态：锁屏不杀死进程");
                    stopService(killProcessIntent);
                }
            }
        });
    }
}
