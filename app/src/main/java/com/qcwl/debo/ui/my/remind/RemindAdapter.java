package com.qcwl.debo.ui.my.remind;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Administrator on 2017/9/5.
 */

public class RemindAdapter extends CommonAdapter<RemindBean> {
    private int type;//1--评论，2--点赞
    RemindViewHolder holder = null;

    public RemindAdapter(int type, Context context, List<RemindBean> datas) {
        super(context, R.layout.item_remind, datas);
        this.type = type;
    }

    @Override
    protected void convert(ViewHolder viewHolder, RemindBean item, int position) {
        try {
            holder = new RemindViewHolder(viewHolder.getConvertView());
            holder.textName.setText(item.getUser_nickname());

            Glide.with(mContext)
                    .load(item.getAvatar())
                    .placeholder(R.mipmap.head)
                    .error(R.mipmap.head)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .into(holder.imageSelf);

            holder.textDate.setVisibility(View.VISIBLE);
            if (type == 1) {
                holder.textDate.setText(item.getComment_time());
                holder.imageFlagPraise.setVisibility(View.GONE);
                holder.textComment.setText(item.getMc_content());
                holder.textComment.setVisibility(View.VISIBLE);
                showTypeInfo(item.getType(), item);
            } else if (type == 2) {
                holder.textDate.setText("" + item.getUpvote_time());
                holder.imageFlagPraise.setVisibility(View.VISIBLE);
                holder.textComment.setVisibility(View.GONE);
                showTypeInfo(item.getType(), item);
            }

//            item.getCircle_type()---1、朋友圈；2、人脉圈；3、粉丝圈
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTypeInfo(int type, RemindBean item) {
        //1、图文形式 2、视频
        if (type == 1) {
            holder.imageFlagPlay.setVisibility(View.GONE);
            if (TextUtils.isEmpty(item.getMoments_images())) {
                holder.imageOther.setVisibility(View.GONE);
                holder.textContent.setText(item.getMoments_content());
                holder.textContent.setVisibility(View.VISIBLE);
            } else {
                ImgUtil.load(mContext, item.getMoments_images(), holder.imageOther);
                holder.imageOther.setVisibility(View.VISIBLE);
                holder.textContent.setVisibility(View.GONE);
            }
        } else if (type == 2) {
            ImgUtil.load(mContext, item.getMoments_images(), holder.imageOther);
            holder.imageOther.setVisibility(View.VISIBLE);
            holder.imageFlagPlay.setVisibility(View.VISIBLE);
            holder.textContent.setVisibility(View.GONE);
        }
    }

    static class RemindViewHolder {
        @Bind(R.id.image_self)
        ImageView imageSelf;
        @Bind(R.id.text_name)
        TextView textName;
        @Bind(R.id.text_comment)
        TextView textComment;
        @Bind(R.id.image_flag_praise)
        ImageView imageFlagPraise;
        @Bind(R.id.text_date)
        TextView textDate;
        @Bind(R.id.image_other)
        ImageView imageOther;
        @Bind(R.id.image_flag_play)
        ImageView imageFlagPlay;
        @Bind(R.id.text_content)
        TextView textContent;

        RemindViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
