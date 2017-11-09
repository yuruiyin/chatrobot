package com.fzu.chatrobot.bean;

/**
 * 头像bean
 * Created by yury on 2016/8/28.
 */
public class HeadPic {

    private int picResId;
    private String picInfo;
    private String name;

    public HeadPic(int picResId, String picInfo, String name) {
        this.picResId = picResId;
        this.picInfo = picInfo;
        this.name = name;
    }

    public HeadPic(int picResId, String picInfo) {
        this.picResId = picResId;
        this.picInfo = picInfo;
    }

    public int getPicResId() {
        return picResId;
    }

    public void setPicResId(int picResId) {
        this.picResId = picResId;
    }

    public String getPicInfo() {
        return picInfo;
    }

    public void setPicInfo(String picInfo) {
        this.picInfo = picInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
