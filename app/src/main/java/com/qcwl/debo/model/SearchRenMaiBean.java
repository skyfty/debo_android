package com.qcwl.debo.model;

/**
 * Created by Administrator on 2017/7/24.
 */

public class SearchRenMaiBean{

    private String avatar;
    private String id;
    private String mobile;
    private String pur_price;
    private String signature;
    private String user_nickname;
    private String is_friend;

    public String getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(String is_friend) {
        this.is_friend = is_friend;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPur_price() {
        return pur_price;
    }

    public void setPur_price(String pur_price) {
        this.pur_price = pur_price;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }
}
