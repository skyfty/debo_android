package com.qcwl.debo.ui.found;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/8/7.
 */

public class RedPacketBean implements Serializable {

    /**
     * t_uid : 21
     * user_nickname : DDD
     * avatar :
     * t_id : 5
     * title : 标题
     * ad_link : http://www.test.com
     * ad_content : 碰一碰碰一碰碰一碰碰一碰
     * ad_images : {"0":"/data/upload/advertisement/advertisement_18/59706270415a69.10846960.png","1":"/data/upload/advertisement/advertisement_18/597062704292e3.04334917.jpg","2":"/data/upload/advertisement/advertisement_18/5970627043cb68.91854063.jpg","3":"/data/upload/advertisement/advertisement_18/597062704503e7.58216711.jpg"}
     * money : 3.93
     */

    private String t_uid;
    private String user_nickname;
    private String avatar;
    private String t_id;
    private String title;
    private String ad_link;
    private String ad_content;
    private String money;

    private String mobile;

    private List<String> ad_images;

    private int star_type;//1 文字型许愿星；2 红包型许愿星

    private int j_type;//1、好友；2、人脉

    public int getJ_type() {
        return j_type;
    }

    public void setJ_type(int j_type) {
        this.j_type = j_type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getT_uid() {
        return t_uid;
    }

    public void setT_uid(String t_uid) {
        this.t_uid = t_uid;
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

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAd_link() {
        return ad_link;
    }

    public void setAd_link(String ad_link) {
        this.ad_link = ad_link;
    }

    public String getAd_content() {
        return ad_content;
    }

    public void setAd_content(String ad_content) {
        this.ad_content = ad_content;
    }

    public List<String> getAd_images() {
        return ad_images;
    }

    public void setAd_images(List<String> ad_images) {
        this.ad_images = ad_images;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    //------许愿星非红包字段-------
    private String star_id;
    private String uid;
    private String wishing_content;
    private String signature;

    public int getStar_type() {
        return star_type;
    }

    public void setStar_type(int star_type) {
        this.star_type = star_type;
    }

    public String getStar_id() {
        return star_id;
    }

    public void setStar_id(String star_id) {
        this.star_id = star_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWishing_content() {
        return wishing_content;
    }

    public void setWishing_content(String wishing_content) {
        this.wishing_content = wishing_content;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
