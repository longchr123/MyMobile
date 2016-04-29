package com.example.administrator.mymobile;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/10/1.
 */
public class FocusedTextView extends TextView{
    //带有一个参数的方法：通常是在代码实例化的时候用到
    public FocusedTextView(Context context) {
        super(context);
    }
    //带有两个参数的方法：布局文件使用某个空间，默认调用带有两个参数的构造方法
    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //设置样式时用
    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //当前这个控件不一定获得焦点，我只是欺骗阳性的系统，让其以我获得焦点的方式去处理事务
    @Override
    public boolean isFocused() {
        return true;
    }
}
