package com.example.administrator.mymobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2015/10/9.
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {
    //构造方法--把数据库已经创建
    public BlackNumberDBOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    //数据库已经创建了--特别适合创建表
    public void onCreate(SQLiteDatabase db) {
        //mode为拦截模式：0拦截电话，1短信拦截，2全部拦截
        db.execSQL("create table blacknumber(_id integer primary key autoincrement,number varchar(20),mode varchar(2))");
    }

    //数据库升级的时候调用
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
