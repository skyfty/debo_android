package com.qcwl.debo.ui.my.partner.sale;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.my.partner.shop.CoinsShopBean;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AlMn on 2017/11/20.
 */

public class CoinsSaleAdapter extends CommonAdapter<CoinsShopBean> {

    public CoinsSaleAdapter(Context context, List<CoinsShopBean> datas) {
        super(context, R.layout.item_coins_sale, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, CoinsShopBean item, final int position) {
        try {
            SaleViewHolder holder = new SaleViewHolder(viewHolder.getConvertView());
            holder.textNumber.setText(item.getDebo_coins());
            holder.textPrice.setText(item.getMoney() + "元");
            holder.textDate.setText("发布日期：" + item.getTime());
            //状态 1、上架中；2、已出售
            if (item.getStatus() == 1) {
                holder.textStatus.setText("出售中……");
                holder.textStatus.setTextColor(mContext.getResources().getColor(R.color.color_red2));
                holder.textSoldOut.setVisibility(View.VISIBLE);
            } else if (item.getStatus() == 2) {
                holder.textStatus.setText("已售出");
                holder.textStatus.setTextColor(mContext.getResources().getColor(R.color.circle_time));
                holder.textSoldOut.setVisibility(View.GONE);
            }
            viewHolder.getConvertView().setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            holder.textSoldOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CoinsSaleActivity) mContext).soldOut(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class SaleViewHolder {
        @Bind(R.id.text_number)
        TextView textNumber;
        @Bind(R.id.text_price)
        TextView textPrice;
        @Bind(R.id.text_status)
        TextView textStatus;
        @Bind(R.id.text_date)
        TextView textDate;
        @Bind(R.id.text_sold_out)
        TextView textSoldOut;

        SaleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
