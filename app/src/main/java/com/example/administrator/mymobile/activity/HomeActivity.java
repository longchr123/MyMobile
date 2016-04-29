package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.AdvancedTools.AToolsActivity;
import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.utils.MD5Utils;

/**
 * Created by Administrator on 2015/10/1.
 */
public class HomeActivity extends Activity {

    private GridView list_home;
    private static final String names[]={"手机防盗","通讯卫士","应用管理","进程管理","手机杀毒",
            "缓存清理","高级工具","设置中心"};
    private static final int ids[]={R.drawable.safe,R.drawable.safe1,R.drawable.safe2,R.drawable.safe3,
            R.drawable.safe4,R.drawable.safe5,R.drawable.safe6,R.drawable.safe7};
    private SharedPreferences sp_save_pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp_save_pwd=getSharedPreferences("config_pwd",MODE_PRIVATE);
        list_home= (GridView) findViewById(R.id.list_home);
        list_home.setAdapter(new HomeAdaper());
        list_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    case 0://进入手机防盗
                        showLostFindDialog();
                        break;
                    case 1:
                        intent=new Intent(HomeActivity.this,CallSmsSafeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade, R.anim.exit);
                        break;
                    case 2:
                        intent=new Intent(HomeActivity.this,AppManagerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade, R.anim.exit);
                        break;
                    case 3:
                        intent=new Intent(HomeActivity.this,TaskManagerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade, R.anim.exit);
                        break;
//                    case 4://流量统计
//                        intent=new Intent(HomeActivity.this,TrafficManager1Activity.class);
//                        startActivity(intent);
//                        break;
                    case 4:
                        intent=new Intent(HomeActivity.this,AntiVirusActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade, R.anim.exit);
                        break;
                    case 5:
                        intent=new Intent(HomeActivity.this,CleanActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade, R.anim.exit);
                        break;
                    case 6://高级设置
                        intent=new Intent(HomeActivity.this,AToolsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade, R.anim.exit);
                        break;
                    case 7://进入设置中心
                        intent=new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade, R.anim.exit);
                        break;
                }
            }
        });
    }
    //根据当前情况弹出不同的对话框
    private void showLostFindDialog() {
        //判断是否设置了密码，如果没有设置就弹出设置对话框，否则弹出输入对话框
        if(isSetupPwd()){
            showEnterDialog();
        }else{
            showSetupDialog();
        }

    }

    //判断是否设置了密码
    private boolean isSetupPwd(){
        String password=sp_save_pwd.getString("password",null);
//        if(TextUtils.isEmpty(password)){
//            return false;
//        }else {
//            return true;
//        }
        return !TextUtils.isEmpty(password);
    }
    //    登录页面
    private void showEnterDialog() {
        AlertDialog.Builder bulder=new AlertDialog.Builder(HomeActivity.this);
        //        把布局文件转换为view类型
        View view=View.inflate(HomeActivity.this,R.layout.enter_pwd,null);
        final EditText et_password= (EditText) view.findViewById(R.id.et_password);
        final EditText et_user= (EditText) view.findViewById(R.id.et_user);
        Button btn_ok= (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel= (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//bulder.show()返回的是 AlertDialog类型
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.得到两个输入框的密码
                String user=et_user.getText().toString().trim();
                String password=et_password.getText().toString().trim();
                String username=sp_save_pwd.getString("username", "");
                String confirm=sp_save_pwd.getString("password","");
                //2.输入框不能为空
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(HomeActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //3.判断两个密码是否相同
                if(user.equals(username)&&MD5Utils.ecoder(password).equals(confirm)) {//密文比较
                    dialog.dismiss();//消除对话框
                    Log.e("HomeActivity","输入密码正确，消除对话框，进入手机防盗页面");
                    Intent intent=new Intent(HomeActivity.this,LostFindActivity.class);
                    startActivity(intent);//此处不能finish（），因为点返回时还得返回到主页面
                    overridePendingTransition(R.anim.fade, R.anim.exit);
                }else{
                    Toast.makeText(HomeActivity.this,"您输入的用户名或密码不对",Toast.LENGTH_SHORT).show();
                    et_user.setText(null);
                    et_password.setText(null);
                }
            }
        });
        bulder.setView(view);
        dialog = bulder.show();
    }

    private AlertDialog dialog;
    //注册页面
    private void showSetupDialog() {
        AlertDialog.Builder bulder=new AlertDialog.Builder(HomeActivity.this);
        //        把布局文件转换为view类型
        View view=View.inflate(HomeActivity.this,R.layout.setup_pwd,null);
        final EditText et_password= (EditText) view.findViewById(R.id.et_password);
        final EditText et_user= (EditText) view.findViewById(R.id.et_user);
        final EditText et_confirm= (EditText) view.findViewById(R.id.et_confirm);
        Button btn_ok= (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel= (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//bulder.show()返回的是 AlertDialog类型
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.得到两个输入框的密码
                String username = et_user.getText().toString().trim();//修剪，取消两边的空格
                String password = et_password.getText().toString().trim();//修剪，取消两边的空格
                String confirm = et_confirm.getText().toString().trim();
                //2.输入框不能为空
                if (TextUtils.isEmpty(username)||TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
                    Toast.makeText(HomeActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //3.判断两个密码是否相同
                if (password.equals(confirm)) {
                    //4.保存密码，消除对话框，进入手机防盗页面
                    sp_save_pwd.edit().putString("username",username).commit();//保存的是加密后的密文
                    sp_save_pwd.edit().putString("password",MD5Utils.ecoder(password)).commit();//保存的是加密后的密文
                    dialog.dismiss();//消除对话框
                    Log.e("HomeActivity", "保存密码，消除对话框，进入手机防盗页面");
                    Intent intent=new Intent(HomeActivity.this,LostFindActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "您输入的密码不一致", Toast.LENGTH_SHORT).show();
                    et_password.setText(null);
                    et_confirm.setText(null);
                }
            }
        });
        bulder.setView(view);
        dialog = bulder.show();
        //在低版本中运行上面两会很难看看，但布局背景要为白色，应改为：
        //dialog=bulider.creat();
        //dialog.setView(view,0,0,0,0) 去除边框
        //dialog.show();
    }
    //适配器
    private class HomeAdaper extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(HomeActivity.this,R.layout.home_item,null);
            ImageView iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
            TextView tv_name= (TextView) view.findViewById(R.id.tv_name);
            tv_name.setText(names[position]);
            iv_icon.setImageResource(ids[position]);
            return view;
        }
    }
}
