package com.example.administrator.mymobile.activity;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymobile.QueryNumberAddress;
import com.example.administrator.mymobile.R;

public class NumberAddressQueryActivity extends AppCompatActivity {

    private TextView tv_result;
    private EditText et_number;
    private Vibrator vibrator;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);
        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        tv_result= (TextView) findViewById(R.id.tv_result);
        et_number= (EditText) findViewById(R.id.et_number);
        //监听号码改动，也就是只要输入号码就能自动查询显示结果
        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //当文本改动时回调,s为文本中的信息
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null&&s.length()>=3){
                    String address= QueryNumberAddress.getAddress(s.toString());
                    tv_result.setText(address);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void Query(View view){
        String number=et_number.getText().toString().trim();
        if(TextUtils.isEmpty(number)){
            Toast.makeText(getApplication(),"请输入要查询的号码",Toast.LENGTH_SHORT).show();
            //抖动效果
            Animation shake= AnimationUtils.loadAnimation(this,R.anim.shake);
//            shake.setInterpolator(new Interpolator() {//设置插入器
//                @Override
//                public float getInterpolation(float x) {
//                    //方程式的解为y
//                    return y;
//                }
//            });
            et_number.startAnimation(shake);
            //震动2秒的效果
            vibrator.vibrate(2000);//需要加权限
//            long[] pattern={500,600,1000,1200,1500,2000};//停止0.5s震动0.6s，再停止1s震动1.2s，再停止1.5s震动2s
//            vibrator.vibrate(pattern,-1);//-1为不重复，0.1.2为重复
            return;
        }else{
            //得到数据源,数据拷贝很快，可以不用在子线程中运行
            String address=QueryNumberAddress.getAddress(number);
            tv_result.setText(address);
        }
    }
}
