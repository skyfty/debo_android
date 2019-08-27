package com.qcwl.debo.ui.my.partner.record;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AlMn on 2017/11/20.
 */

public class CoinsTradingRecordAdapter extends CommonAdapter<CoinsTradingRecordBean> {

    public CoinsTradingRecordAdapter(Context context, List<CoinsTradingRecordBean> datas) {
        super(context, R.layout.item_coins_sale, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, CoinsTradingRecordBean item, final int position) {
        try {
            TradingViewHolder holder = new TradingViewHolder(viewHolder.getConvertView());
            if (item.getType() == 1) {
                holder.textType.setText("买入嘚啵币(个)");//购买平台
            } else if (item.getType() == 2) {
                if ("0".equals(item.getCon_uid())) {
                    holder.textType.setText("买入嘚啵币(个)");//购买合伙人
                } else {
                    holder.textType.setText("售出嘚啵币(个)");
                }
            }
            String num = item.getMoney().replace(".00","");
            holder.textNumber.setText(num);
            holder.textPrice.setText(item.getMoney() + "元");
            holder.textDate.setText("" + item.getCreate_time());
            //状态 1、上架中；2、已出售
            if (item.getIs_pay() == 0) {
                holder.textStatus.setText("未支付");
                holder.textStatus.setTextColor(mContext.getResources().getColor(R.color.color_red2));
            } else if (item.getIs_pay() == 1) {
                holder.textStatus.setText("支付完成");
                holder.textStatus.setTextColor(mContext.getResources().getColor(R.color.color_red2));
            }
            viewHolder.getConvertView().setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CoinsTradingRecordActivity) mContext).deleteDialog(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class TradingViewHolder {
        @Bind(R.id.text_number)
        TextView textNumber;
        @Bind(R.id.text_price)
        TextView textPrice;
        @Bind(R.id.text_status)
        TextView textStatus;
        @Bind(R.id.text_date)
        TextView textDate;
        @Bind(R.id.text_type)
        TextView textType;

        TradingViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
