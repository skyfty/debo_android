package com.qcwl.debo.ui.my.sign;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AlMn on 2018/1/16.
 */

public class Sign2Adapter extends CommonAdapter<SignBean> {

    private String week="";

    public Sign2Adapter(Context context, List<SignBean> datas) {
        super(context, R.layout.item_sign, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, SignBean item, int position) {
        SignViewHolder holder = new SignViewHolder(viewHolder.getConvertView());
        if (item.isEmpty()) {
            return;
        }
        week = item.getWeek();

        holder.textDay.setText(item.getDay());
        holder.textDay.setVisibility(View.VISIBLE);

        if (item.getIs_sign_in() == 1) {
            holder.textDay.setBackgroundResource(R.mipmap.ic_fingerprint);
            holder.imgDay.setVisibility(View.VISIBLE);
//            holder.textDay.setVisibility(View.GONE);
        } else {
            //holder.imgDay.setImageResource(R.mipmap.sign_no);
            if (item.getIs_holiday()==1) {
                holder.imgDay.setImageResource(R.mipmap.ic_point_tag);
                holder.imgDay.setVisibility(View.VISIBLE);
                holder.textDay.setVisibility(View.GONE);
            }else{
                holder.imgDay.setVisibility(View.GONE);
            }
        }
    }

    static class SignViewHolder {
        @Bind(R.id.textDay)
        TextView textDay;
        @Bind(R.id.imgDay)
        ImageView imgDay;

        SignViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
