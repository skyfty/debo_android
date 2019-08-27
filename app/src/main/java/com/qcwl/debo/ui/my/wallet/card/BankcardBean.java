package com.qcwl.debo.ui.my.wallet.card;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/19.
 */

public class BankcardBean implements Serializable{

    private String b_id;
    private String logo_url;
    private String bank_name = "";
    private String bank_card_type = "";
    private String bank_account = "";
    private String real_name = "";
    private String mobile = "";

    public String getId() {
        return b_id;
    }

    public void setId(String id) {
        this.b_id = id;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_card_type() {
        return bank_card_type;
    }

    public void setBank_card_type(String bank_card_type) {
        this.bank_card_type = bank_card_type;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
