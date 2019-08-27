package com.qcwl.debo.ui.my.sign;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AlMn on 2018/1/16.
 */

public class SignAdapter extends CommonAdapter<SignBean> {

    public SignAdapter(Context context, List<SignBean> datas) {
        super(context, R.layout.item_sign, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, SignBean item, int position) {
        SignViewHolder holder = new SignViewHolder(viewHolder.getConvertView());
        if (!TextUtils.isEmpty(item.getDay())) {
            holder.textDay.setText("" + item.getDay());
            holder.textDay.setVisibility(View.VISIBLE);
            holder.imgDay.setVisibility(View.GONE);
        } else {
            if (item.getIs_sign_in() == 1) {
                holder.imgDay.setImageResource(R.mipmap.sign_yes);
            } else {
                holder.imgDay.setImageResource(R.mipmap.sign_no);
            }
            holder.textDay.setVisibility(View.GONE);
            holder.imgDay.setVisibility(View.VISIBLE);
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
