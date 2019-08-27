package com.qcwl.debo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.model.ContactsBean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */

public class RenMaiAdapter extends BaseAdapter {
    private List<ContactsBean> list;
    private Context context;
    private ContactsBean bean;

    public RenMaiAdapter(List<ContactsBean> list, Context context) {
        this.list = list;
        this.context = context;
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
        ViewHolder vh = null;
        bean = list.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.renmai_item, null);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.content = (TextView) convertView.findViewById(R.id.content);
            vh.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

//        vh.name.setText(bean.getName());
//        vh.content.setText(bean.getContent());
//        vh.time.setText(bean.getTime());

        return convertView;
    }

    private class ViewHolder {
        private TextView name, content, time;
    }
}
