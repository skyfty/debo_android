package com.qcwl.debo.ui.my.wallet.trading;

/**
 * Created by Administrator on 2017/9/7.
 */

public class TradingDetailBean {


    /**
     * tran_id : 16
     * order_sn : 2017081514272040651
     * order_price : ￥1.00
     * pay_status : 未完成
     * pay_type : 支付宝
     * indent : 1
     * info : 充值
     * time : 2017-08-15 14:27:20
     */

    private String tran_id;
    private String order_sn;
    private String order_price;
    private String pay_status;
    private String pay_type;
    private int indent;
    private String info;
    private String time;

    public String getTran_id() {
        return tran_id;
    }

    public void setTran_id(String tran_id) {
        this.tran_id = tran_id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getOrder_price() {
        return order_price;
    }

    public void setOrder_price(String order_price) {
        this.order_price = order_price;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
