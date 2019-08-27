package com.qcwl.debo.ui.my.sign;

/**
 * Created by AlMn on 2018/1/16.
 */

public class SignBean {

    /**
     * day : 1
     * is_sign_in : 0
     * is_sign_logo : http://app.qitong.shop/data/img/weiqiandao.png
     * is_today : 0
     */

    private String day;
    private int is_sign_in; //是否签到，0、未签到；1、已签到
    private String is_sign_logo;
    private int is_today;
    private int is_holiday;//1.节假日 0.工作日
    private String week; //返回的一日是几号

    private boolean empty;//是否在该位置显示日期

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public int getIs_holiday() {
        return is_holiday;
    }

    public void setIs_holiday(int is_holiday) {
        this.is_holiday = is_holiday;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getIs_sign_in() {
        return is_sign_in;
    }

    public void setIs_sign_in(int is_sign_in) {
        this.is_sign_in = is_sign_in;
    }

    public String getIs_sign_logo() {
        return is_sign_logo;
    }

    public void setIs_sign_logo(String is_sign_logo) {
        this.is_sign_logo = is_sign_logo;
    }

    public int getIs_today() {
        return is_today;
    }

    public void setIs_today(int is_today) {
        this.is_today = is_today;
    }
}
