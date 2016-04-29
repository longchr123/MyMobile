package com.example.administrator.mymobile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClick extends RelativeLayout {

    private TextView tv_title;
    private TextView tv_desc;
    private String update_off;
    private String update_on;
    private void init(Context context){
        //最后一个参数：添加谁进来，就是R.layout.setting_item的父亲，也就是说把布局文件挂载在传进来的这个控件上
        View view=View.inflate(context,R.layout.setting_click_view,this);
        tv_desc= (TextView) findViewById(R.id.tv_desc);
        tv_title= (TextView) findViewById(R.id.tv_title);

    }
    //在代码实例化中使用
    public SettingClick(Context context) {
        super(context);
        init(context);
    }
    //在布局文件实例化时使用
    public SettingClick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        //attrs与values中自建的attrs相关联，以命名空间的方法取出title的值
        String name=attrs.getAttributeValue("http://schemas.android.com/apk/res/com.example.administrator.mymobile","name");
        update_off=attrs.getAttributeValue("http://schemas.android.com/apk/res/com.example.administrator.mymobile","update_off");
        update_on=attrs.getAttributeValue("http://schemas.android.com/apk/res/com.example.administrator.mymobile","update_on");
        tv_title.setText(name);
        setDescription(update_off);//设置默认的描述信息
    }
    //要设置样式的时候使用
    public SettingClick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    //设置组合控件的状态信息
    public void setDescription(String text){
        tv_desc.setText(text);
    }
    //设置组合控件的标题
    public void setTitle(String title){
        tv_title.setText(title);
    }

}
