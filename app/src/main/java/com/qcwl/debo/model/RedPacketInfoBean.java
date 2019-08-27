package com.qcwl.debo.model;

public class RedPacketInfoBean {
    private String uid;
    private String offer_uid;
    private String avatar;
    private String user_nickname;
    private String money;
    private String time;
    private String offer_time;
    private String is_acc;
    private String acc_time;
    private String offer_name;
    private String leave_word;
    public RedPacketInfoBean(String uid, String offer_uid, String avatar, String user_nickname, String money, String time,String offer_time,String is_acc,String acc_time,String offer_name,String leave_word) {
        this.uid = uid;
        this.offer_uid = offer_uid;
        this.avatar = avatar;
        this.user_nickname = user_nickname;
        this.money = money;
        this.time = time;
        this.offer_time = offer_time;
        this.is_acc = is_acc;
        this.acc_time = acc_time;
        this.offer_name = offer_name;
        this.leave_word = leave_word;
    }

    public RedPacketInfoBean() {
    }

    public String getLeave_word() {
        return leave_word;
    }

    public void setLeave_word(String leave_word) {
        this.leave_word = leave_word;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOffer_uid() {
        return offer_uid;
    }

    public void setOffer_uid(String offer_uid) {
        this.offer_uid = offer_uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOffer_time() {
        return offer_time;
    }

    public void setOffer_time(String offer_time) {
        this.offer_time = offer_time;
    }

    public String getIs_acc() {
        return is_acc;
    }

    public void setIs_acc(String is_acc) {
        this.is_acc = is_acc;
    }

    public String getAcc_time() {
        return acc_time;
    }

    public void setAcc_time(String acc_time) {
        this.acc_time = acc_time;
    }

    public String getOffer_name() {
        return offer_name;
    }

    public void setOffer_name(String offer_name) {
        this.offer_name = offer_name;
    }

    @Override
    public String toString() {
        return "RedPacketInfoBean{" +
                "uid='" + uid + '\'' +
                ", offer_uid='" + offer_uid + '\'' +
                ", avatar='" + avatar + '\'' +
                ", user_nickname='" + user_nickname + '\'' +
                ", money='" + money + '\'' +
                ", time='" + time + '\'' +
                ", offer_time='" + offer_time + '\'' +
                ", is_acc='" + is_acc + '\'' +
                ", acc_time='" + acc_time + '\'' +
                ", offer_name='" + offer_name + '\'' +
                '}';
    }
}
