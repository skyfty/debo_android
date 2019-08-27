package com.qcwl.debo.ui.found.near.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.near.bean.TrumpetBean;
import com.qcwl.debo.ui.pay.PayDialog;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/4.
 */

public class TrumpetAdapter extends CommonAdapter<TrumpetBean> {

    public TrumpetAdapter(Context context, List<TrumpetBean> datas) {
        super(context, R.layout.item_trumpet, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, final TrumpetBean item, int position) {
        MyViewHolder holder = new MyViewHolder(viewHolder.getConvertView());
        //ImgUtil.load(mContext, item.getImgId(), holder.imageView);
        ImgUtil.load(mContext, item.getUrl(), holder.imageView);
        holder.textType.setText(item.getDistance() + "米以内喊话");
        if (item.getIs_free() == 2) {
            holder.textFlag.setText("免费");
            holder.textFlag.setVisibility(View.VISIBLE);
            holder.textPurchase.setVisibility(View.GONE);
        } else {
            if (item.getIs_purchase() == 1) {
                holder.textFlag.setText("已购买");
                holder.textFlag.setVisibility(View.VISIBLE);
                holder.textPurchase.setVisibility(View.GONE);
            } else {
                holder.textFlag.setVisibility(View.GONE);
                holder.textPurchase.setText("￥" + item.getHorn_price());
                holder.textPurchase.setVisibility(View.VISIBLE);
                holder.textPurchase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PayDialog.createDialog(mContext, item.getDistance() + "米以内喊话"
                                , 6, item.getHorn_id(), item.getHorn_price(),"").show();
                    }
                });
            }
        }
    }

    static class MyViewHolder {
        @Bind(R.id.image_view)
        ImageView imageView;
        @Bind(R.id.text_type)
        TextView textType;
        @Bind(R.id.text_flag)
        TextView textFlag;
        @Bind(R.id.text_purchase)
        TextView textPurchase;

        MyViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}