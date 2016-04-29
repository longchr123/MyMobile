package com.example.administrator.mymobile.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/10/6.
 */
public class QueryAntiVirus {
    private static String path="/data/data/com.example.administrator.mymobile/files/antivirus.db";
    //如果返回有值就是病毒，null为正常
    public static String getDesc(String md5){
        String result=null;
        //以只读的形式打开
        SQLiteDatabase db=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        Cursor cursor=db.rawQuery("select desc from datable where md5=?", new String[]{md5});
        if(cursor.moveToNext()){
            String desc=cursor.getString(0);
            result=desc;
        }
        return result;
    }

}
