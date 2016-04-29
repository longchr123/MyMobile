package com.app.AdvancedTools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 程序锁的数据库的创建
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper {
    //构造方法--把数据库已经创建
    public AppLockDBOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    //数据库已经创建了--特别适合创建表
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table applock(_id integer primary key autoincrement,packname varchar(20))");
    }

    //数据库升级的时候调用
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
