package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/10/3.
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector detector;//定义一个手势识别器
    protected SharedPreferences sp_base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp_base=getSharedPreferences("config",MODE_PRIVATE);
        //实例化手势识别器
        detector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            //e1表示手指刚触屏的那个点，e2表示抬起手指的点 velocity为滑动速度
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //屏蔽滑动速度慢，速度是以“像素每秒
                if(Math.abs(velocityX)<150){
                    Toast.makeText(getApplication(),"请滑快点.....",Toast.LENGTH_SHORT).show();
                    return true;//直接返回，下面不执行
                }
                //屏蔽斜滑
                if(Math.abs(e1.getY()-e2.getY())>100){
                    Toast.makeText(getApplication(),"请水平滑动.....",Toast.LENGTH_SHORT).show();
                    return true;//直接返回，下面不执行
                }

                if(e2.getX()-e1.getX()>200){
                    ShowPrevious();
                    //显示上一个页面
                }else if(e1.getX()-e2.getX()>200){
                    //显示下一个页面
                    ShowNext();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }
    public void next(View view){
        ShowNext();
    }
    public void previous(View view){
        ShowPrevious();
    }

    public abstract void ShowNext() ;
    public abstract void ShowPrevious() ;

    //使用手势识别器
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
