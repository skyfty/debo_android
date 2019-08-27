package com.qcwl.debo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.joke.CommentBean;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Administrator on 2017/7/13.
 */

public class CommentDuanziAdapter extends BaseAdapter {
    private List<CommentBean> mListCommentBeen;
    private Context context;
    private CommentBean bean;

    public CommentDuanziAdapter(List<CommentBean> mListCommentBeen, Context context) {
        this.mListCommentBeen = mListCommentBeen;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mListCommentBeen.size();
    }

    @Override
    public Object getItem(int position) {
        return mListCommentBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        bean = mListCommentBeen.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item, null);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.content = (TextView) convertView.findViewById(R.id.content);
            vh.time = (TextView) convertView.findViewById(R.id.time);
            vh.touxiang = (RoundedImageView) convertView.findViewById(R.id.touxiang);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.name.setText(bean.getUser_nickname());
        vh.content.setText(bean.getContent());
        vh.time.setText(bean.getTime());
        ImgUtil.setGlideHead(context,bean.getAvatar(),vh.touxiang);
       /* Glide.with(context)
                .load(bean.getAvatar()).bitmapTransform(new CropCircleTransformation(context))
                .placeholder(R.mipmap.head)
                .error(R.mipmap.head)
                .into(vh.touxiang);*/
        return convertView;
    }

    private class ViewHolder {
        private TextView name, content, time;
        private RoundedImageView touxiang;
    }
}
