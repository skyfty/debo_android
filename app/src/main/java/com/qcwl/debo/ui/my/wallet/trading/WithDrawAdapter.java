package com.qcwl.debo.ui.my.wallet.trading;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.my.wallet.TradingRecordActivity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/6.
 */

public class WithDrawAdapter extends CommonAdapter<TradingBean> {
    int moneyTextColor = R.color.circle_name;
    public WithDrawAdapter(Context context, List<TradingBean> datas) {
        super(context, R.layout.item_withdraw, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, TradingBean item, int position) {
        try {
            TradingViewHolder holder = new TradingViewHolder(viewHolder.getConvertView());
            if (item.getType() == 1) {
                holder.textDetailDate.setText(item.getCreate_time());
                holder.textDetailMoney.setText(symbol + item.getOrder_price());
                holder.textDetailMoney.setTextColor(mContext.getResources().getColor(moneyTextColor));
                if (item.getPay_status() == 1){
                    holder.tv_type.setText("提现中");
                }else if(item.getPay_status() == 2){
                    holder.tv_type.setText("已完成");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String symbol = "";
    //15、存入（储蓄罐）、16、转出（储蓄罐）、17、转出（合伙人）、18、购买（嘚啵币）

    static class TradingViewHolder {
        @Bind(R.id.text_detail_money)
        TextView textDetailMoney;
        @Bind(R.id.text_detail_date)
        TextView textDetailDate;
        @Bind(R.id.tv_type)
        TextView tv_type;

        TradingViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
