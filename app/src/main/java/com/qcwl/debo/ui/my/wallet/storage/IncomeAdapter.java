package com.qcwl.debo.ui.my.wallet.storage;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */

public class IncomeAdapter extends CommonAdapter<IncomeBean> {
    private int type = 0;
    public IncomeAdapter(Context context,  List<IncomeBean> datas,int type) {
        super(context, R.layout.item_income, datas);
        this.type = type;
    }

    @Override
    protected void convert(ViewHolder viewHolder, IncomeBean item, int position) {
            try {

                TextView tv_date=viewHolder.getView(R.id.tv_date);
                TextView tv_time=viewHolder.getView(R.id.tv_time);
                TextView tv_money=viewHolder.getView(R.id.tv_money);
                TextView tv_income=viewHolder.getView(R.id.tv_income);
                if (type == 1){
                    tv_date.setVisibility(View.GONE);
                    tv_time.setVisibility(View.GONE);
                }
                tv_date.setText(""+item.getRegular());
                tv_time.setText(""+item.getTime());
                tv_money.setText(""+item.getMoney()+"币");
                tv_income.setText(""+item.getContent());
            } catch (Exception e) {
               e.printStackTrace();
            }
    }
}
