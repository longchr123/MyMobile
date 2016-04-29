package com.app.AdvancedTools;

/*
    导入程序中用到的架包
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.administrator.mymobile.R;
/*
    高级工具的主页面，主布局为activity_atools.xml文件
    功能实现：
        1.跳转至常用号码查询页面；
        2.跳转至
 */

public class AToolsActivity extends Activity {
    /*
        	Activity生命周期中的第一个创建方法
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定布局文件
        setContentView(R.layout.activity_atools);
    }
    /*
        常用号码查询
     */
    public void commonNumberQuery(View view){
        //定义一个跳转页面的意图
        Intent intent=new Intent(AToolsActivity.this,CommonNumberQueryActivity.class);
        //启动意图
        startActivity(intent);
    }
    /*
        进入程序锁页面
     */
    public void enterAppLock(View view){
        Intent intent=new Intent(AToolsActivity.this,AppLockActivity.class);
        startActivity(intent);
    }
}
