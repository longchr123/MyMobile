package com.example.administrator.mymobile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/9.
 */
public class BlackNumber {
    private BlackNumberDBOpenHelper helper;
    public BlackNumber(Context context){
        helper=new BlackNumberDBOpenHelper(context);

    }
    //添加一条黑名单数据,mode为拦截模式：0拦截电话，1短信拦截，2全部拦截
    public void add(String number,String mode){
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("number",number);
        values.put("mode", mode);
        db.insert("blacknumber",null,values);
        db.close();
    }
    public void delete(String number){
        SQLiteDatabase db=helper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
    }
    //修改新的拦截模式
    public void update(String number,String mode){
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("mode", mode);
        db.update("blacknumber", values, "number=?", new String[]{number});
        db.close();
    }
    public boolean query(String number){
        boolean result=false;
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("blacknumber", null, "number=?", new String[]{number}, null, null, null);
        if(cursor.moveToNext()){
            result=true;
        }
        db.close();
        return result;
    }
    //查询黑名单的拦截模式
    public String queryMode(String number){
        String result="2";
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        if(cursor.moveToNext()){
            result=cursor.getString(0);
        }
        db.close();
        return result;
    }
    //得到所有的黑名单信息
    public List<BlackNumberInfo> queryAll(){
        List<BlackNumberInfo> result=new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("blacknumber", new String[]{"number","mode"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            String number=cursor.getString(0);
            String mode=cursor.getString(1);
            info.setNumber(number);
            info.setMode(mode);
            result.add(info);
        }
        db.close();
        return result;
    }
    //加载部分数据，index为从什么地方开始加载20条
    public List<BlackNumberInfo> queryPart(int index){
        SystemClock.sleep(600);
        List<BlackNumberInfo> result=new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db=helper.getWritableDatabase();
//        Cursor cursor=db.rawQuery("select number,mode from blacknumber limit 20 offset ?;", new String[]{index + ""});
        //以倒序查询显示
        Cursor cursor=db.rawQuery("select number,mode from blacknumber order by _id desc limit 20 offset ?;", new String[]{index + ""});
        while (cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            String number=cursor.getString(0);
            String mode=cursor.getString(1);
            info.setNumber(number);
            info.setMode(mode);
            result.add(info);
        }
        db.close();
        return result;
    }
    public int queryCount(){
        int result=0;
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select count(*) from blacknumber;", null);
        while (cursor.moveToNext()){
            result=cursor.getInt(0);
        }
        db.close();
        return result;
    }

}
