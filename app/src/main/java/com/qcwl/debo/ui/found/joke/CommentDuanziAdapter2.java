package com.qcwl.debo.ui.found.joke;

import android.content.Context;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by qcwl on 2017/11/22.
 */

public class CommentDuanziAdapter2 extends CommonAdapter<CommentBean> {

    public CommentDuanziAdapter2(Context context, List<CommentBean> datas) {
        super(context, R.layout.comment_item, datas);
    }

    @Override
    protected void convert(ViewHolder holder, CommentBean commentBean, int position) {
        TextView name = holder.getView(R.id.name);
        TextView content = holder.getView(R.id.content);
        TextView time = holder.getView(R.id.time);
        RoundedImageView touxiang = holder.getView(R.id.touxiang);

        name.setText(commentBean.getUser_nickname());
        content.setText(commentBean.getContent());
        time.setText(commentBean.getTime());
        ImgUtil.setGlideHead(mContext, commentBean.getAvatar(), touxiang);
      /*  Glide.with(mContext)
                .load(commentBean.getAvatar()).bitmapTransform(new CropCircleTransformation(mContext))
                .placeholder(R.mipmap.head)
                .error(R.mipmap.head)
                .into(touxiang);*/
    }
}
