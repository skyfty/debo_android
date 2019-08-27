package com.qcwl.debo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcwl.debo.R;
import com.qcwl.debo.model.ContactsBean;

import java.util.List;


/**
 * Created by Administrator on 2017/5/11.
 */

public class ContactCardAdapter extends BaseAdapter {
    private Context context;
    private List<ContactsBean> list;

    public ContactCardAdapter(Context context, List<ContactsBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final ContactsBean mContent = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.social_item, null);

            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
            viewHolder.touxiang = (ImageView) convertView.findViewById(R.id.touxiang);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvLetter.setOnClickListener(null);

        viewHolder.tvLetter.setVisibility(View.GONE);

        if (TextUtils.isEmpty(mContent.getUser_nickname())) {
            viewHolder.tvTitle.setText(this.list.get(position).getMobile());
        } else {
            viewHolder.tvTitle.setText(this.list.get(position).getUser_nickname());
        }
        if (TextUtils.isEmpty(list.get(position).getArea())) {
            viewHolder.touxiang.setImageResource(R.mipmap.head);
        } else {
            ImageLoader.getInstance().displayImage(list.get(position).getAvatar(), viewHolder.touxiang);
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView tvLetter;
        private TextView tvTitle;
        private ImageView touxiang;
    }
}
