package com.qcwl.debo.ui.my.wallet.storage;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */

public class IncomeResultBean {

    private String total_income;
    private List<IncomeBean> income_list;

    public String getTotal_income() {
        return total_income;
    }

    public void setTotal_income(String total_income) {
        this.total_income = total_income;
    }

    public List<IncomeBean> getIncome_list() {
        return income_list;
    }

    public void setIncome_list(List<IncomeBean> income_list) {
        this.income_list = income_list;
    }
}
