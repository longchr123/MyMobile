package com.app.AdvancedTools;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.mymobile.R;

public class EnterOtherAppPwdActivity extends Activity {
    //定义应用的名字
    private TextView tvName;
    //定义应用的图标
    private ImageView ivIcon;
    //密码输入框
    private EditText etPassword;
    private Button btnOk;
    private Button btnCancel;
    private Intent intent;
    private String packname;

    private void assignViews() {
        etPassword = (EditText) findViewById(R.id.et_password);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        tvName= (TextView) findViewById(R.id.tv_name);
        ivIcon= (ImageView) findViewById(R.id.iv_icon);
        intent=getIntent();//获得传地来的意图
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_other_app_pwd);
        assignViews();
        packname=intent.getStringExtra("packname");
        //初始化包管理器
        PackageManager pm=getPackageManager();
        try {
            //根据包名获得图标
            Drawable icon=pm.getApplicationInfo(packname, 0).loadIcon(pm);
            ivIcon.setImageDrawable(icon);
            //根据包名获得应用名称
            String name= (String) pm.getApplicationInfo(packname, 0).loadLabel(pm);
            tvName.setText(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.得到密码,判断密码是否为空
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(EnterOtherAppPwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //2.判断密码是否正确，关闭当前页面
                if ("1234".equals(password)) {
                    //发一个消息给看门狗
                    Intent intent2=new Intent();
                    //设置动作，让广播接收者接收
                    intent2.setAction("com.example.administrator.mymobile.activity.stopprotectting");
                    intent2.putExtra("packname",packname);
                    //此处只能发广播，而不是startService，因为Activity运行后发Service没用
                    sendBroadcast(intent2);
                    //关闭当前页面
                    finish();
                } else {
                    Toast.makeText(EnterOtherAppPwdActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    /*当这个页面看不见时，把这个页面关闭，这样在打开其他程序时就可以再次执行onCreate方法，重新给Icon和Packname赋值，
    如果没有这个方法，onCreate只执行一次，当打开计算器再打开浏览器时图标还是计算器的。
     */
    protected void onStop() {
        super.onStop();
        finish();
    }
    //当点击返回时，回到桌面
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
    }
}
