package com.example.administrator.mymobile.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/10/1.
 */
public class StreamTools {
    public static String readFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//读入内存中
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len=is.read(buffer))!=-1) {
            baos.write(buffer, 0, len);
        }
        is.close();
        String result=baos.toString();
        baos.close();
        return  result;
    }
}
