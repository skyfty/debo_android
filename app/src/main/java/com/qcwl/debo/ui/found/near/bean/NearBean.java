package com.qcwl.debo.ui.found.near.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/31.
 */

public class NearBean implements Serializable{


    /**
     * n_id : 29
     * lng : 50.982375923
     * lat : 40.23984739
     * create_time : 1501483777
     * uid : 36
     * juli : 0
     * user_nickname : 小龙人
     * avatar : http://123.57.148.47/debo/data/upload/user/user_36/337.jpg
     * mobile : 15210276853
     * sex : 1
     * signature : 帅也是一种个性
     */

    private String n_id;
    private double lng;
    private double lat;
    private String create_time;
    private String uid;
    private String juli;
    private String user_nickname;
    private String avatar;
    private String mobile;
    private int sex;
    private String signature;

    public String getN_id() {
        return n_id;
    }

    public void setN_id(String n_id) {
        this.n_id = n_id;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getJuli() {
        return juli;
    }

    public void setJuli(String juli) {
        this.juli = juli;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
