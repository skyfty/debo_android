package com.qcwl.debo.model;

/**
 * Created by Administrator on 2017/8/21.
 */

public class SettingBean {
    private String is_forbid;
    private String conn_type;
    private String pur_price;
    private String day_num;
    private String operator;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getPur_price() {
        return pur_price;
    }

    public void setPur_price(String pur_price) {
        this.pur_price = pur_price;
    }

    public String getDay_num() {
        return day_num;
    }

    public void setDay_num(String day_num) {
        this.day_num = day_num;
    }

    public String getIs_forbid() {
        return is_forbid;
    }

    public void setIs_forbid(String is_forbid) {
        this.is_forbid = is_forbid;
    }

    public String getConn_type() {
        return conn_type;
    }

    public void setConn_type(String conn_type) {
        this.conn_type = conn_type;
    }
}
