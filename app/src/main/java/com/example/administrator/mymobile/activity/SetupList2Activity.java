package com.example.administrator.mymobile.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.SettingItem;

public class SetupList2Activity extends BaseSetupActivity {
    private SettingItem si_bind_sim;
    private TelephonyManager tm;//定义电话服务，读取SIM卡信息，监听来电和挂断
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_list2);
        tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);//实例化
        si_bind_sim= (SettingItem) findViewById(R.id.si_bind_sim);
        String sim=sp_base.getString("sim","");//保存信息读取出来
        if(TextUtils.isEmpty(sim)){
            //没有绑定sim卡
            si_bind_sim.setchecked(false);
        }else {
            si_bind_sim.setchecked(true);
        }
        si_bind_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp_base.edit();
                if (si_bind_sim.isChecked()) {
                    si_bind_sim.setchecked(false);
                    editor.putString("sim", null).commit();
                } else {
                    si_bind_sim.setchecked(true);
                    //读取sim卡串号
                    String sim = tm.getSimSerialNumber();
                    Toast.makeText(SetupList2Activity.this, sim, Toast.LENGTH_SHORT).show();
                    editor.putString("sim", sim).commit();
                }
            }
        });
    }

    @Override
    public void ShowNext() {
        String sim=sp_base.getString("sim","");
        if(TextUtils.isEmpty(sim)){
            Toast.makeText(getApplication(),"请绑定SIM卡",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent =new Intent(this,SetupList3Activity.class);
        startActivity(intent);
        finish();
        //设置两个页面切换的动漫效果
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    public void ShowPrevious() {
        Intent intent =new Intent(this,SetupList1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }
}
