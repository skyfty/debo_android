package com.qcwl.debo.ui.contact.add;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.view.RoundedImageView;
import com.hyphenate.util.ImageUtils;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.near.bean.NearBean;
import com.qcwl.debo.utils.AppUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by AlMn on 2018/1/3.
 */

public class NearAdapter extends CommonAdapter<NearBean> {

    public NearAdapter(Context context, List<NearBean> datas) {
        super(context, R.layout.item_near_add, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, NearBean item, int position) {
        try {
            NearViewHolder holder = new NearViewHolder(viewHolder.getConvertView());
            //  ImgUtil.load(mContext, item.getAvatar(), holder.imageView);
           /* Glide.with(mContext)
                    .load(item.getAvatar()).bitmapTransform(new CropCircleTransformation(mContext))
                    .placeholder(R.mipmap.head)
                    .error(R.mipmap.head)
                    .into(holder.imageView);*/
            ImgUtil.setGlideHead(mContext, item.getAvatar(), holder.imageView);


            holder.textName.setText(item.getUser_nickname());
            //性别；0：保密，1：男；2：女
            if (item.getSex() == 0) {
                holder.imageSex.setVisibility(View.GONE);
                holder.textSex.setVisibility(View.VISIBLE);
            } else if (item.getSex() == 1) {
                holder.imageSex.setVisibility(View.VISIBLE);
                holder.textSex.setVisibility(View.GONE);
                holder.imageSex.setImageResource(R.mipmap.sex_boy);
            } else if (item.getSex() == 2) {
                holder.imageSex.setVisibility(View.VISIBLE);
                holder.textSex.setVisibility(View.GONE);
                holder.imageSex.setImageResource(R.mipmap.sex_girl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class NearViewHolder {
        @Bind(R.id.imageView)
        RoundedImageView imageView;
        @Bind(R.id.textName)
        TextView textName;
        @Bind(R.id.imageSex)
        ImageView imageSex;
        @Bind(R.id.textSex)
        TextView textSex;

        NearViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
