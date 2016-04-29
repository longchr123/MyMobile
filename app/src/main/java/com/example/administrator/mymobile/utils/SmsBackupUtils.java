package com.example.administrator.mymobile.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2015/10/12.
 *
 * 短信存储格式
 * <xml version="1.0" encoding="utf-8" ?>
 * <smss>
 *     <sms>
 *         <address>5556</address>
 *         <data> .... </data>
 *         <type>1</type>
 *         <body> .... </body>
 *     </sms>
 * </smss>
 *
 * ProgressBar和ProgressDialog中的方法在此工具中都是一样的。
 */
public class SmsBackupUtils {

    //把短信备份的ProgressBar和ProgressDialog抽取为接口或者说回调
    public interface SmsBackupCallBack{
        //短信备份前调用，把总条数传给对应的实现类
        public void smsBackupBefore(int total);
        //短信备份过程中调用，把总进度传给对应的实现类
        public void smsBackupProgress(int progress);
    }

    //为何要有context，因为要得到内容提供者，读取短信。path为要保存短信的路径
    public static void smsBackup(Context context,String path,SmsBackupCallBack back) throws Exception {
        ContentResolver resolver=context.getContentResolver();//得到内容解析者，必须得有上下文
        XmlSerializer serializer=Xml.newSerializer();//生成xml文件序列化器
        File file=new File(path);
        FileOutputStream os=new FileOutputStream(file);
        serializer.setOutput(os,"UTF-8");//生成xml文件
        serializer.startDocument("UTF-8", true);//文档开头
        serializer.startTag(null,"smss");//a.命名空间
        //把所有短信备份，包括发件箱，收件箱，草稿箱等
        Uri uri=Uri.parse("content//sms");
        //第二个参数（b）：数据库中要保存的数据，第三个参数（c）：选择条件，null为所有都备份。e：排序,cursor相当于读取信息的光标
        Cursor cursor=resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
        //设置短信的总条数
//        dialog.setMax(cursor.getCount());
        back.smsBackupBefore(cursor.getCount());
        //设置备份进度
        int progress=0;
        while(cursor.moveToNext()){//此1.2.3存放到第几列
            serializer.startTag(null,"sms");

            serializer.startTag(null, "address");
            String address=cursor.getString(0);
            serializer.text(address);
            serializer.endTag(null, "address");

            serializer.startTag(null, "data");
            String data=cursor.getString(1);
            serializer.text(data);
            serializer.endTag(null, "data");

            serializer.startTag(null, "type");
            String type=cursor.getString(2);
            serializer.text(type);
            serializer.endTag(null, "type");

            serializer.startTag(null, "body");
            String body=cursor.getString(3);
            serializer.text(body);
            serializer.endTag(null, "body");

            serializer.endTag(null, "sms");
            progress++;//每备份一条信息就自加一次
//            dialog.setProgress(progress);
            back.smsBackupProgress(progress);
        }
        cursor.close();
        serializer.endTag(null, "smss");
        serializer.endDocument();//文档结束
    }
}
