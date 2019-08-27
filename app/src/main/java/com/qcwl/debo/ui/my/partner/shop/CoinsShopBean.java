package com.qcwl.debo.ui.my.partner.shop;

import java.io.Serializable;

/**
 * Created by AlMn on 2017/11/20.
 */

public class CoinsShopBean implements Serializable{


    /**
     * id : 3
     * uid : 113
     * debo_coins : 100
     * status : 1
     * time : 2017-11-20
     * money : 100.00
     */

    private String id;
    private String uid;
    private String debo_coins;
    private int status;
    private String time;
    private String money;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDebo_coins() {
        return debo_coins;
    }

    public void setDebo_coins(String debo_coins) {
        this.debo_coins = debo_coins;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
