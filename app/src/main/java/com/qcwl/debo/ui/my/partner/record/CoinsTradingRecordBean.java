package com.qcwl.debo.ui.my.partner.record;

/**
 * Created by AlMn on 2017/11/21.
 */

public class CoinsTradingRecordBean {


    /**
     * id : 1
     * uid : 113
     * num : 0
     * pay_type : 3
     * money : 888.00
     * create_time : 2017-11-18
     * is_pay : 1
     * order_sn : 201711181632549952
     * con_uid : 0
     * type : 1
     */

//    num	string	购买个数
//    pay_type	string	支付方式 1为支付宝 2为微信 3余额支付
//    money	string	支付金额
//    create_time	string	时间
//    is_pay	string	支付状态 0、未支付；1、支付完成
//    order_sn	string	支付单号
//    con_uid	string	出售嘚啵币的用户uid
//    type	string	1、购买平台嘚啵币；2、购买其他合伙人嘚啵币

    private String id;
    private String uid;
    private String num;
    private int pay_type;
    private String money;
    private String create_time;
    private int is_pay;
    private String order_sn;
    private String con_uid;
    private int type;

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

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getIs_pay() {
        return is_pay;
    }

    public void setIs_pay(int is_pay) {
        this.is_pay = is_pay;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getCon_uid() {
        return con_uid;
    }

    public void setCon_uid(String con_uid) {
        this.con_uid = con_uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
