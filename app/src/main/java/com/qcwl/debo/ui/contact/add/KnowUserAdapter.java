package com.qcwl.debo.ui.contact.add;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.model.KnowUserBean;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AlMn on 2018/1/3.
 */

public class KnowUserAdapter extends CommonAdapter<KnowUserBean> {

    public KnowUserAdapter(Context context, List<KnowUserBean> datas) {
        super(context, R.layout.item_know_user, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, KnowUserBean item, int position) {
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
            holder.tv_addr.setText(item.getProvince());
            //性别；0：保密，1：男；2：女
            if (item.getSex().equals("0")) {
                holder.imageSex.setVisibility(View.GONE);
            } else if (item.getSex().equals("1")) {
                holder.imageSex.setVisibility(View.VISIBLE);
                holder.imageSex.setImageResource(R.mipmap.sex_boy);
            } else if (item.getSex().equals("2")) {
                holder.imageSex.setVisibility(View.VISIBLE);
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
        @Bind(R.id.tv_addr)
        TextView tv_addr;

        NearViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
