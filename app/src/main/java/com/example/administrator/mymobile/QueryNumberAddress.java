package com.example.administrator.mymobile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/10/6.
 */
public class QueryNumberAddress {
    private static String path="/data/data/com.example.administrator.mymobile/files/address.db";
    public static String getAddress(String number){
        String address=number;
        //以只读的形式打开
        SQLiteDatabase db=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        //手机号一般为11位,用正则表达式来表示
        //substring为取出第0-7的8个字节长度的数字
        //^1[345678]\d{9}$   ^为开始符，1为第一个数字只能为1，[345678]表示第二位只能是这几个数字，\d表示第三位是从0-9的数字，｛9｝表示9位数,$为结束符
//        Cursor cursor = null;//可能导致指针异常
        if(number.matches("^1[345678]\\d{9}$")) {
            Cursor cursor = db.rawQuery("select city from city where _id=(select city_id from number_130 where RecNo=?)", new String[]{number.substring(0, 3)});
            while (cursor.moveToNext()) {
                String location = cursor.getColumnName(1);//对应数据表，city为第2列
                address = location;//如果没有查到号码地址就会返回原本的手机号
            }cursor.close();
            db.close();
        }else {
            //110,长途
            switch (number.length()){
                case 3:
                    address="匪警号码";
                    break;
                case 4:
                    address="模拟器号码";
                    break;
                case 5:
                    address="客服号码";
                    break;
                case 6:
                case 7:
                case 8:
                    address="本地号码";
                    break;
                default:
                    if(number!=null&&number.startsWith("0")&&number.length()>=10){
                        //以010-998887777为例，取出了10两位
                        Cursor cursor=db.rawQuery("select location from number_134 where area=?",new String[]{number.substring(1,3)});
                        while (cursor.moveToNext()){
                            String location=cursor.getString(0);
                            address=location.substring(0, location.length() - 2);
                        }
                        //以0855-888272727为例，取出0855
                        cursor=db.rawQuery("select location from number_134 where area=?",new String[]{number.substring(1,4)});
                        while (cursor.moveToNext()){
                            String location=cursor.getString(0);
                            address=location.substring(0, location.length() - 2);
                        }
                        cursor.close();
                        db.close();
                    }
            }
        }

        return address;
    }
}
