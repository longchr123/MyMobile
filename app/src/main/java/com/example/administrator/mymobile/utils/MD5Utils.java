package com.example.administrator.mymobile.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**md5加密
 * Created by Administrator on 2015/10/1.
 */
public class MD5Utils {
    public static String ecoder(String password){
        //MD5加密，
        try {
        // 1.信息摘要器
        MessageDigest digest = MessageDigest.getInstance("md5");

        //2.变成byte数组
        byte[] bytes = digest.digest(password.getBytes());
        StringBuffer buffer=new StringBuffer();
        //3.每一个byte与8个二进制位做与运算
        for(byte b:bytes){
            int number=b & 0xff;
            //4.把int类型转化为16进制
            String numberStr= Integer.toHexString(number);
            //5.不足位补全
            if(numberStr.length()==1){
                buffer.append("0");//在前面加0（执行了这句再执行下一句就是在前面加0）
            }
            buffer.append(numberStr);
        }
        return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "MD5加密失败";
        }
    }
}
