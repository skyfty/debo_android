package com.qcwl.debo.ui.found.guarantee;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/8/19.
 */

public class GuaranteeDetailAdapter extends CommonAdapter<GuaranteeDetailBean> {
    public GuaranteeDetailAdapter(Context context, List<GuaranteeDetailBean> datas) {
        super(context, R.layout.item_guarantee_detail, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, final GuaranteeDetailBean item, int position) {
        try {
            DetailViewHolder holder = new DetailViewHolder(viewHolder.getConvertView());
            holder.textStartDate.setText("发起日期：" + item.getInitiate_time());
            holder.textNameLeft.setText(item.getName());
            if(TextUtils.isEmpty(item.getAvatar())){
                ImgUtil.setGlideHead(mContext,R.mipmap.head,holder.imageLeft);
                //Glide.with(mContext).load(R.mipmap.head).into(holder.imageLeft);
            }else {
                ImgUtil.loadHead(mContext, item.getAvatar(), holder.imageLeft);
            }
            holder.textNameRight.setText(item.getCon_name());
            if(TextUtils.isEmpty(item.getCon_avatar())){
                ImgUtil.setGlideHead(mContext, R.mipmap.head, holder.imageRight);
                //Glide.with(mContext).load(R.mipmap.head).into(holder.imageRight);
            }else {
                ImgUtil.setGlideHead(mContext, item.getCon_avatar(), holder.imageRight);
                //ImgUtil.loadHead(mContext, item.getCon_avatar(), holder.imageRight);
            }
            holder.textNameCenter.setText(item.getState_content());
            holder.textTotal.setText(item.getPrice());
            switch (item.getTri_state()) {
                case 1:
                    holder.textCancel.setText("取消");
                    holder.textCancel.setVisibility(View.VISIBLE);
                    holder.textFinish.setVisibility(View.GONE);
                    holder.textCancel.setBackgroundResource(R.color.white);
                    holder.textCancel.setClickable(true);
                    holder.textCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mContext instanceof GuaranteeDetailActivity) {
                                ((GuaranteeDetailActivity) mContext).confirmGuaranteeDetail(item.getTri_state(),item.getTri_id(),2);
                            }
                        }
                    });
                    break;
                case 2:
                    holder.textCancel.setText("取消");
                    holder.textFinish.setText("完成");
                    holder.textCancel.setVisibility(View.VISIBLE);
                    holder.textFinish.setVisibility(View.VISIBLE);
                    holder.textFinish.setClickable(true);
                    holder.textCancel.setClickable(true);
                    holder.textFinish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mContext instanceof GuaranteeDetailActivity) {
                                ((GuaranteeDetailActivity) mContext).confirmGuaranteeDetail(item.getTri_state(),item.getTri_id(),1);
                            }
                        }
                    });
                    holder.textCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mContext instanceof GuaranteeDetailActivity) {
                                ((GuaranteeDetailActivity) mContext).confirmGuaranteeDetail(item.getTri_state(),item.getTri_id(),2);
                            }
                        }
                    });
                    break;
                case 3:
                    holder.textCancel.setVisibility(View.GONE);
                    holder.textFinish.setVisibility(View.VISIBLE);
                    holder.textFinish.setText("已完成");
                    holder.textFinish.setBackgroundResource(R.color.white);
                    holder.textFinish.setTextColor(mContext.getResources().getColor(R.color.ad_red));
                    holder.textFinish.setClickable(false);
                    break;
                case 4:
                    holder.textCancel.setVisibility(View.VISIBLE);
                    holder.textFinish.setVisibility(View.GONE);
                    holder.textCancel.setBackgroundResource(R.color.white);
                    holder.textCancel.setText("已取消");
                    holder.textCancel.setClickable(false);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class DetailViewHolder {
        @Bind(R.id.text_start_date)
        TextView textStartDate;
        @Bind(R.id.text_cancel)
        TextView textCancel;
        @Bind(R.id.text_finish)
        TextView textFinish;
        @Bind(R.id.image_left)
        CircleImageView imageLeft;
        @Bind(R.id.text_name_left)
        TextView textNameLeft;
        @Bind(R.id.image_arrow)
        ImageView imageArrow;
        @Bind(R.id.text_name_center)
        TextView textNameCenter;
        @Bind(R.id.image_right)
        CircleImageView imageRight;
        @Bind(R.id.text_name_right)
        TextView textNameRight;
        @Bind(R.id.layout_select)
        LinearLayout layoutSelect;
        @Bind(R.id.text_total)
        TextView textTotal;

        DetailViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
