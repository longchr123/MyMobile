package com.example.administrator.mymobile;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2015/10/17.
 */
public class TaskInfo {
    private Drawable icon;
    private String name;
    private long memInfoSize;//单位为byte
    private boolean isUser;
    private String packageName;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    private boolean isChecked;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getMemInfoSize() {
        return memInfoSize;
    }

    public void setMemInfoSize(long memInfoSize) {
        this.memInfoSize = memInfoSize;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setIsUser(boolean isUser) {
        this.isUser = isUser;
    }
}
