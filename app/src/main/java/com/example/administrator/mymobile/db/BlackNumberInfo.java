package com.example.administrator.mymobile.db;

/**代表一个黑名单号码信息
 * Created by Administrator on 2015/10/9.
 */
public class BlackNumberInfo {
    private String number;
    private String mode;

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
    public void BlackNumberInfo(String number,String mode){
        this.number=number;
        this.mode=mode;
    }

    @Override
    public String toString() {
        return "BlackNumberInfo[number="+number+", mode="+mode+"]";
    }
}
