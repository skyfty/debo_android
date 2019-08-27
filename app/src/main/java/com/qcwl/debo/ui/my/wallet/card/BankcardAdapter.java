package com.qcwl.debo.ui.my.wallet.card;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/19.
 */

public class BankcardAdapter extends CommonAdapter<BankcardBean> {
    public BankcardAdapter(Context context, List<BankcardBean> datas) {
        super(context, R.layout.item_bankcard, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, BankcardBean item, int position) {
        try {
            CardViewHolder holder = new CardViewHolder(viewHolder.getConvertView());
            if (item == null) {
                holder.imageAdd.setVisibility(View.VISIBLE);
                holder.layout.setVisibility(View.GONE);
                holder.imageAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BankcardActivity) mContext).startActivityForResult(new Intent(mContext, AddBankcardActivity.class), 100);
                    }
                });
            } else {
                holder.imageAdd.setVisibility(View.GONE);
                holder.layout.setVisibility(View.VISIBLE);
                switch (position % 4) {
                    case 0:
                        holder.layout.setBackgroundResource(R.mipmap.bankcard_bg_green);
                        break;
                    case 1:
                        holder.layout.setBackgroundResource(R.mipmap.bankcard_bg_red);
                        break;
                    case 2:
                        holder.layout.setBackgroundResource(R.mipmap.bankcard_bg_blue);
                        break;
                    case 3:
                        holder.layout.setBackgroundResource(R.mipmap.bankcard_bg_brown);
                        break;
                    default:
                        break;
                }
                ImgUtil.load(mContext, item.getLogo_url(), holder.imageBank);
                holder.textBankName.setText(item.getBank_name());
                holder.textCardType.setText(item.getBank_card_type());
                String number = item.getBank_account();
                holder.textCardNo.setText(number.substring(number.length() - 4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class CardViewHolder {
        @Bind(R.id.image_bank)
        ImageView imageBank;
        @Bind(R.id.text_bank_name)
        TextView textBankName;
        @Bind(R.id.text_card_type)
        TextView textCardType;
        @Bind(R.id.text_card_no)
        TextView textCardNo;
        @Bind(R.id.layout)
        LinearLayout layout;
        @Bind(R.id.image_add)
        ImageView imageAdd;

        CardViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
