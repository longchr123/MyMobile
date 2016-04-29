package com.example.administrator.mymobile;

import android.test.AndroidTestCase;

import com.example.administrator.mymobile.db.BlackNumber;
import com.example.administrator.mymobile.db.BlackNumberDBOpenHelper;

import java.util.Random;

/**
 * Created by Administrator on 2015/10/9.
 */
public class CreatBlackNumberTest extends AndroidTestCase {
    public void CreatBlackNumberTest(){
        BlackNumberDBOpenHelper helper =new BlackNumberDBOpenHelper(getContext());
        helper.getWritableDatabase();//读和写只有在磁盘不够的情况下才有区别
    }
    public void delete(){
        BlackNumber dao=new BlackNumber(getContext());
        dao.delete("119");
    }
    public void query(){
        BlackNumber dao=new BlackNumber(getContext());
        boolean result=dao.query("1192");
        assertEquals(true,result);
    }
    public void queryMode(){
        BlackNumber dao=new BlackNumber(getContext());
        String mode=dao.queryMode("1191");
        System.out.println("mode="+mode);
    }

    public void add(){
        BlackNumber dao=new BlackNumber(getContext());
        Random random=new Random();//左闭右开
        for(int i=0;i<50;i++) {
            dao.add("119"+i, ""+random.nextInt(3));
        }
    }
}
