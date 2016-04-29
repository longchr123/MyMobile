package com.example.administrator.mymobile.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.administrator.mymobile.R;

public class SetupList4Activity extends BaseSetupActivity {
    private CheckBox cb_protect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_list4);
        sp_base=getSharedPreferences("config",MODE_PRIVATE);
        cb_protect= (CheckBox) findViewById(R.id.cb_protect);
        boolean protect=sp_base.getBoolean("protect", false);
        if(protect){
            cb_protect.setText("手机防盗已经开启");
        }else {
            cb_protect.setText("手机防盗还未开启");
        }cb_protect.setChecked(protect);
        //只能设置监听事件，而不是点击事件
        cb_protect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sp_base.edit();
                editor.putBoolean("protect",isChecked).commit();
                if(isChecked){
                    cb_protect.setText("手机防盗已经开启");
                }else {cb_protect.setText("手机防盗还未开启");}
            }
        });
    }

    public void ShowNext() {
        SharedPreferences.Editor editor=sp_base.edit();
        editor.putBoolean("configed",true).commit();
        if(cb_protect.isChecked()) {
            Intent intent = new Intent(this, LostFindActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
        }else {
            Toast.makeText(this, "为了您的手机安全，请开启防盗保护", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void ShowPrevious() {
        Intent intent =new Intent(this,SetupList3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

}
