package com.example.administrator.mymobile.activity;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymobile.R;

import java.lang.reflect.Field;

/*
    土司拖动效果，
 */
public class DragViewActivity extends AppCompatActivity {
    private ImageView iv_dragview;
    private SharedPreferences sp;
    private WindowManager wm;
    private TextView tv_top,tv_bottom;
    private long[] mHits=new long[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);
        tv_bottom= (TextView) findViewById(R.id.tv_bottom);
        tv_top= (TextView) findViewById(R.id.tv_top);
        wm= (WindowManager) getSystemService(WINDOW_SERVICE);
        final int mWidth=wm.getDefaultDisplay().getWidth();
        final int mHight=wm.getDefaultDisplay().getHeight();
        iv_dragview= (ImageView) findViewById(R.id.iv_dragview);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        final int LastX=sp.getInt("LastX",0);
        int LastY=sp.getInt("LastY",0);
        if(LastY>mHight/2){//当返回再次进入时执行
            //当前控件在我们的底部
            tv_top.setVisibility(View.VISIBLE);
            tv_bottom.setVisibility(View.INVISIBLE);
        }else {
            //当前控件在我们的头部
            tv_top.setVisibility(View.INVISIBLE);
            tv_bottom.setVisibility(View.VISIBLE);
        }
//        iv_dragview.layout(LastX,LastY,iv_dragview.getWidth()+LastX,iv_dragview.getHeight()+LastY);
        Log.e("DragViewActivity","高:"+iv_dragview.getHeight()+",宽："+iv_dragview.getWidth());
        //保存最新的位置之后的实现
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) iv_dragview.getLayoutParams();
        params.leftMargin=LastX;
        params.topMargin=LastY;
        iv_dragview.setLayoutParams(params);
        iv_dragview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    //双击居中
                    iv_dragview.layout(mWidth / 2 - iv_dragview.getWidth() / 2, iv_dragview.getTop(),
                            mWidth / 2 + iv_dragview.getWidth() / 2, iv_dragview.getBottom());
                    saveDate();//保存坐标
                    return;
                }
            }
        });

        //设置触摸事件在activity中，只对其设置触摸事件，触摸事件必须返回true，如果设置了点击事件，则必须返回为false
        iv_dragview.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://手指按下时
//                        1.手指按下时记录起始坐标
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://手指在屏幕上划动
                        //2.手指移动时记录坐标
                        float newX = event.getRawX();
                        float newY = event.getRawY();
                        //3.计算偏移量
                        int dX = (int) (newX - startX);
                        int dY = (int) (newY - startY);
                        //4.根据偏移量更新控件的位置
                        //屏蔽非法拖动
                        int newl = iv_dragview.getLeft() + dX;
                        int newt = iv_dragview.getTop() + dY;
                        int newr = iv_dragview.getRight() + dX;
                        int newb = iv_dragview.getBottom() + dY;
                        if (newl < 0 || newt < 0 || newr > mWidth || newb > mHight - getStatusBarHeight()) {
                            break;
                        }
                        if (newt > mHight / 2) {
                            //当前控件在我们的底部
                            tv_top.setVisibility(View.VISIBLE);
                            tv_bottom.setVisibility(View.INVISIBLE);
                        } else {
                            //当前控件在我们的头部
                            tv_top.setVisibility(View.INVISIBLE);
                            tv_bottom.setVisibility(View.VISIBLE);
                        }
                        iv_dragview.layout(newl, newt, newr, newb);
                        //5.重新记录坐标
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        saveDate();
                }
                return false;
            }
        });
    }

    private void saveDate() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("LastX", iv_dragview.getLeft());
        editor.putInt("LastY", iv_dragview.getTop());
        editor.commit();
    }

    /*用于获取状态栏的高度。
        return 返回状态栏高度的像素
        导入包java.lang.reflect.Field
     */
    private int statusBarHeight;
    private int getStatusBarHeight(){
        if(statusBarHeight == 0) {
            try {
                Class c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (int) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return statusBarHeight;
    }

}
