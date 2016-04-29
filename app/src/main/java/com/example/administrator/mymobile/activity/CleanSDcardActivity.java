package com.example.administrator.mymobile.activity;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.administrator.mymobile.R;

import java.io.File;

public class CleanSDcardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_sdcard);
        //写伪代码
        File file= Environment.getExternalStorageDirectory();
        File[] files=file.listFiles();
        for(File f:files){
            //如果是目录
            if(f.isDirectory()){
                //查询数据库，如果存在，就显示一条数据
                System.out.println("***--------------------------");
            }
        }
    }
}
