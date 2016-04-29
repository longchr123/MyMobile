package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.mymobile.R;

public class LostFindActivity extends Activity {

    private SharedPreferences sp_list_find;
    private TextView tv_safe_number;
    private ImageView iv_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp_list_find=getSharedPreferences("config",MODE_PRIVATE);
        boolean configed=sp_list_find.getBoolean("configed", false);
        if(configed) {
            //如果设置了防盗页面就进入防盗主页
            setContentView(R.layout.activity_lost_find);
            tv_safe_number= (TextView) findViewById(R.id.tv_safe_number);
            iv_status= (ImageView) findViewById(R.id.iv_status);
            tv_safe_number.setText(sp_list_find.getString("safenumber",""));
//            boolean protect=sp_list_find.getBoolean("protect", false);
//            if(protect){
////                iv_status.setImageResource();
//                System.out.println("11111111111111111111111111111111111111111");
//            }else {
//                System.out.println("00000000000000000000000000000000000000000");
//            }
        }else {
            enterSetting();
        }
    }
    public void reEnterSetting(View view){
        enterSetting();
        overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
    }
    private void enterSetting() {
        //跳转至手机防盗设置向导的第一个页面
        Intent intent=new Intent(this,SetupList1Activity.class);
        startActivity(intent);
        finish();//关闭当前页面
    }


}
