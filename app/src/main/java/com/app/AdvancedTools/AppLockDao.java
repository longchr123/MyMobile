package com.app.AdvancedTools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AppLockDao {
    private Context context;
    //创建数据库
    private AppLockDBOpenHelper helper;
    //构造函数
    public AppLockDao(Context context){
        helper=new AppLockDBOpenHelper(context);
        this.context=context;
        
    }
    //添加一条已加锁数据
    public void add(String packname){
        SQLiteDatabase db=helper.getWritableDatabase();
        //存储的名值对。
        ContentValues values=new ContentValues();
        values.put("packname", packname);
        //第二个参数为null，表示values为空时将不会被插入
        db.insert("applock", null, values);
        db.close();
        //数据发生改变了发这个信息，但要上下文
        Uri uri=Uri.parse("content://com.example.administrator.mymobile.db.change");
        context.getContentResolver().notifyChange(uri,null);//第二个参数表示：任意广播接收者都可以接收

    }
    public void delete(String packname){
        SQLiteDatabase db=helper.getWritableDatabase();
        db.delete("applock", "packname=?", new String[]{packname});
        db.close();
        //数据发生改变了发这个信息，但要上下文
        Uri uri=Uri.parse("content://com.example.administrator.mymobile.db.change");
        context.getContentResolver().notifyChange(uri, null);//第二个参数表示：任意广播接收者都可以接收
    }

    /*
        得到所有的已加锁应用包名
     */
    public List<String> queryAll(){
        List<String> result=new ArrayList<String>();
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("applock", new String[]{"packname"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            String packname=cursor.getString(0);
            result.add(packname);
        }
        db.close();
        return result;
    }

    /*
        根据包名查询信息
     */
    public boolean query(String packname){
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
        if(cursor.moveToNext()){
            return true;
        }
        db.close();
        return false;
    }
    /*
        查询数量
     */
    public int queryCount(){
        int result=0;
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select count(*) from blacknumber;", null);
        while (cursor.moveToNext()){
            //得到第一列的数据
            result=cursor.getInt(0);
        }
        db.close();
        return result;
    }

}
