package com.qcwl.debo.ui.my.wallet.storage;

/**
 * Created by Administrator on 2017/11/9.
 */

public class IncomeBean {

    /**
     * id : 1
     * uid : 113
     * income : 10.00
     * time : 2017-11-05
     */

    private String id;
    private String uid;
    private String income;
    private String time;
    private String regular;
    private String money;
    private String content;

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
