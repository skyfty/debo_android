package com.qcwl.debo.ui.my.ad;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/11.
 */

public class AdvertisementAdapter extends CommonAdapter<AdvertisementBean> {


    public AdvertisementAdapter(Context context, List<AdvertisementBean> datas) {
        super(context, R.layout.item_advertisement, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, final AdvertisementBean item, final int position) {
        try {
            AdViewHolder holder = new AdViewHolder(viewHolder.getConvertView());
            holder.textName.setText(item.getTitle());
            holder.textDate.setText(item.getP_time());
            if (item.isVisible()) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(item.isChecked());
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick(item, position);
                }
            });
            viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick(item, position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> strList = new ArrayList<>();

    private void itemClick(AdvertisementBean item, int position) {
        if (item.isVisible()) {//编辑状态
            if (strList.contains(item.getT_id())) {
                strList.remove(item.getT_id());
            } else {
                strList.add(item.getT_id());
            }
            item.setChecked(!item.isChecked());
            notifyDataSetChanged();
        } else {//非编辑状态
            mContext.startActivity(new Intent(mContext, AdvertisementDetailActivity.class)
                    .putExtra("t_id", "" + item.getT_id()));
        }
    }

    static class AdViewHolder {
        @Bind(R.id.checkbox)
        CheckBox checkBox;
        @Bind(R.id.text_name)
        TextView textName;
        @Bind(R.id.text_date)
        TextView textDate;

        AdViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
