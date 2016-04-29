package com.app.AdvancedTools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * 常用号码查询中的数据库操作
 * 实现的方法：
 *  1.得到分组的总数；
 *  2.得到某组的孩子个数
 *  3.得到分组的名称
 *  4.得到某组的某个孩子的名称
 */
public class QueryCommonNumber {


    /*
        得到分组的总数
     */
    public static int getGroupCount(SQLiteDatabase db){
        //查询数据库中个数，
        Cursor cursor=db.rawQuery("select count(*) from classlist;",null);
        //当查询出结果时，将光标移动到第一行，查询分组个数
        cursor.moveToFirst();
        //得到数值个数
        int result=cursor.getInt(0);
        cursor.close();
        return result;
    }

    /*
        得到某组的孩子个数
     */
    public static int getChildCount(SQLiteDatabase db,int groupPosition){
        //id从1开始，而位置是从0开始
        int newPosition=groupPosition+1;
        //查询数据库中个数,  ";"可以不要，因为要查多个表，所以+个数，条件为null
        Cursor cursor=db.rawQuery("select count(*) from table"+newPosition,null);
        cursor.moveToFirst();
        int result=cursor.getInt(0);
        cursor.close();
        return result;
    }

    //得到分组的名称
    public static String getGroupName(SQLiteDatabase db,int groupPosition){
        int newPosition=groupPosition+1;//id从1开始，而位置是从0开始
        //查询数据库中每个分组的名称
        Cursor cursor=db.rawQuery("select name from classlist where _idx=?;",new String[]{newPosition+""});
        cursor.moveToFirst();
        String result=cursor.getString(0);
        cursor.close();
        return result;
    }

    //得到某组的某个孩子的名称
    public static String getChildName(SQLiteDatabase db,int groupPosition,int childPosition){
        int newPosition=groupPosition+1;//id从1开始，而位置是从0开始
        int newChildPosition=childPosition+1;//id从1开始，而位置是从0开始
        //查询数据库中某个表对应的某些值
        Cursor cursor=db.rawQuery("select name , number from table" + newPosition + " where _id=?",
                new String[]{newChildPosition + ""});
        cursor.moveToFirst();
        String name=cursor.getString(0);
        String number=cursor.getString(1);
        //将两条信息连接成一条字符串类型返回
        String result="   "+name+"\n      "+number;//空格是为了对齐好看
        cursor.close();
        return result;
    }
}
