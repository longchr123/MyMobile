package com.example.administrator.mymobile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItem extends RelativeLayout {

    private TextView tv_title;
    private CheckBox cb_status;
    private TextView tv_desc;
    private String update_off;
    private String update_on;
    private void init(Context context){
        //最后一个参数：添加谁进来，就是R.layout.setting_item的父亲，也就是说把布局文件挂载在传进来的这个控件上
        View view=View.inflate(context,R.layout.setting_item,this);
        cb_status= (CheckBox) findViewById(R.id.cb_status);//不加“view.”的原因是已经挂载
        tv_desc= (TextView) findViewById(R.id.tv_desc);
        tv_title= (TextView) findViewById(R.id.tv_title);
    }
    //在代码实例化中使用
    public SettingItem(Context context) {
        super(context);
        init(context);
    }
    //在布局文件实例化时使用
    public SettingItem(Context context, AttributeSet attrs) {
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
    public SettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    //得到组合空间是否勾选
    public boolean isChecked(){
        return cb_status.isChecked();
    }
    //设置组合控件的勾选状态
    public void setchecked(boolean isChecked){
        cb_status.setChecked(isChecked);
        if(isChecked){
            setDescription(update_off);//自动升级已经开启
        }else {
            setDescription(update_on);//自动升级已经关闭
        }
    }
    //设置组合控件的状态信息
    public void setDescription(String text){
        tv_desc.setText(text);
    }
}
