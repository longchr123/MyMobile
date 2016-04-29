package com.app.AdvancedTools;

import android.graphics.drawable.Drawable;

/**
 * 一个APP应用的部分信息
 */
public class AppInfo {
    private Drawable icon;//图标
    private String name,packName;//应用程序的名字和包名
    /*
    * ture安装在内部
    * false为安装在外部
     */
    private boolean isRom;
    //true为用户程序，false为系统程序
    private boolean isUser;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setIsUser(boolean isUser) {
        this.isUser = isUser;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", packName='" + packName + '\'' +
                ", isRom=" + isRom +
                ", isUser=" + isUser +
                '}';
    }
}
