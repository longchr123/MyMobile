package com.example.administrator.mymobile.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.mymobile.R;

public class SetupList3Activity extends BaseSetupActivity {
    private EditText et_safe_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_list3);
        et_safe_number= (EditText) findViewById(R.id.et_safe_number);
        et_safe_number.setText(sp_base.getString("safenumber",""));
    }
    @Override
    public void ShowNext() {
        //检验是否设置过安全号码
        String number=et_safe_number.getText().toString().trim();
        if(TextUtils.isEmpty(number)){
            Toast.makeText(this,"请设置安全号码",Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor=sp_base.edit();
        editor.putString("safenumber",number).commit();
        Intent intent =new Intent(this,SetupList4Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    public void ShowPrevious() {
        Intent intent =new Intent(this,SetupList2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }
    public void selectContact(View view){
        Intent intent=new Intent(this,SelectContactActivity.class);
        startActivityForResult(intent, 0);//0是请求码
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){return;}
        String number=data.getStringExtra("number").replace("-","");
        et_safe_number.setText(number);
    }
}
