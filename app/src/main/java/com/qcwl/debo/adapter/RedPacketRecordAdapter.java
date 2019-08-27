package com.qcwl.debo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.model.RedPacketInfoBean;
import com.qcwl.debo.ui.circle.ImgUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qcwl on 2017/10/26.
 */

public class RedPacketRecordAdapter extends BaseAdapter {
    private Context mContex;
    private List<RedPacketInfoBean> mInfos = new ArrayList<>();
    private RedPacketInfoBean bean = new RedPacketInfoBean();

    public RedPacketRecordAdapter(Context mContex, List<RedPacketInfoBean> mInfos) {
        this.mContex = mContex;
        this.mInfos = mInfos;
    }

    @Override
    public int getCount() {
        return mInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        bean = mInfos.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContex).inflate(R.layout.redpacket_recorder_item, null);
            vh.touxiang = (ImageView) convertView.findViewById(R.id.touxiang);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.redBagLiuYan = (TextView) convertView.findViewById(R.id.tv_red_bag_liuyan);
            vh.data = (TextView) convertView.findViewById(R.id.data);
            vh.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.name.setText(bean.getUser_nickname());
        vh.tv_money.setText(bean.getMoney());
        vh.redBagLiuYan.setText(bean.getLeave_word());
        vh.data.setText(bean.getTime());
        vh.touxiang.setBackgroundResource(R.color.white);
        ImgUtil.load(mContex, bean.getAvatar(), vh.touxiang);

        return convertView;
    }

    private class ViewHolder {
        private TextView name, redBagLiuYan, data, tv_money;
        private ImageView touxiang;
    }
}