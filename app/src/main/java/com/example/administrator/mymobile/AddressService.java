package com.example.administrator.mymobile;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressService extends Service {
    private TelephonyManager tm;
    private MyPhoneStateListener listener;//写成类的成员变量有利于取消注册
    private OutCallReceiver receiver;
    private WindowManager wm;//一个窗口服务
    private View view;
    private SharedPreferences sp;
    private WindowManager.LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp=getSharedPreferences("config",MODE_PRIVATE);
        wm= (WindowManager) getSystemService(WINDOW_SERVICE);
        tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener=new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        //注册监听广播去电，广播接收者的代码注册
        receiver=new OutCallReceiver();
        IntentFilter filter=new IntentFilter();//意图过滤器
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");//监听去电动作
        registerReceiver(receiver,filter);//当过滤成功就会进入OutCalleceiver方法中
    }
    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://来电，铃声响起
                    String address=QueryNumberAddress.getAddress(incomingNumber);
                    MyToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://电话挂断
                    if(view!=null) {
                        wm.removeView(view);
                        view = null;
                    }
                    break;
            }
        }
    }
    //用于监听去电
    public class OutCallReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String number=getResultData();//获取打的电话号码
            String address=QueryNumberAddress.getAddress(number);
            MyToast(address);
        }
    }

    private void MyToast(String address) {
//        view=new TextView(this);
//        view.setTextSize(20);
//        view.setTextColor(Color.RED);
//        view.setText(address);
        view=view.inflate(this,R.layout.show_address,null);
        TextView tv= (TextView) view.findViewById(R.id.tv_address);
        tv.setText(address);
        int which=sp.getInt("which",0);
        int ids[]={R.drawable.btn_zoom_up_disabled,R.drawable.btn_zoom_up_disabled_focused,
                R.drawable.btn_zoom_up_normal,R.drawable.btn_zoom_up_pressed,
                R.drawable.btn_zoom_up_selected};
        int lastX=sp.getInt("LastX",0);
        int lastY=sp.getInt("LastY",0);
        view.setBackgroundResource(ids[which]);//是view而不是tv

        view.setOnTouchListener(new View.OnTouchListener() {
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
                        params.x+=dX;
                        params.y+=dY;
                        //屏蔽非法移动超出边界
                        if(params.x<0){params.x=0;}
                        if(params.y<0){params.y=0;}
                        if(params.x>wm.getDefaultDisplay().getWidth()-view.getWidth()){
                            params.x=wm.getDefaultDisplay().getWidth()-view.getWidth();}
                        if(params.y>wm.getDefaultDisplay().getHeight()-view.getHeight()){
                            params.y=wm.getDefaultDisplay().getHeight()-view.getHeight();}
                        wm.updateViewLayout(view,params);
                        //5.重新记录坐标
                        startX = event.getRawX();
                        startY = event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        saveDate();
                }
                return true;
            }
        });
        params = new WindowManager.LayoutParams();
        params.gravity= Gravity.TOP+Gravity.LEFT;//设置屏幕的重心在左上角
        params.x=lastX;
        params.y=lastY;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;//半透明效果
//        params.windowAnimations = com.android.internal.R.style.Animation_Toast;//动画
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//高于电话权限的类型
//        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//不可获得焦点
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        wm.addView(view,params);
    }

    private void saveDate() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("LastX", params.x);
        editor.putInt("LastY", params.y);
        editor.commit();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消监听来电
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        //释放资源，这一步不要忘记
        listener=null;
        //取消注册监听事件
        unregisterReceiver(receiver);
        receiver=null;
    }
}
