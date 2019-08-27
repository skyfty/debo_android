package com.qcwl.debo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.model.GetRedPacketRecordBean;
import com.qcwl.debo.model.RedPacketInfoBean;
import com.qcwl.debo.ui.circle.ImgUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qcwl on 2017/10/26.
 */

public class RedPacketRecordAdapter1 extends BaseAdapter {
    private Context mContex;
    private List<GetRedPacketRecordBean.ListsBean> mDatas = new ArrayList<>();
    private  GetRedPacketRecordBean.ListsBean mBean;
    public RedPacketRecordAdapter1(Context mContex, List<GetRedPacketRecordBean.ListsBean> mDatas) {
        this.mContex = mContex;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        mBean = mDatas.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContex).inflate(R.layout.redpacket_recorder_item_more, null);
            vh.touxiang = (ImageView) convertView.findViewById(R.id.touxiang);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.data = (TextView) convertView.findViewById(R.id.data);
            vh.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.name.setText(mBean.getUser_nickname());
        vh.tv_money.setText(mBean.getMoney());
        vh.data.setText(mBean.getTime());

        return convertView;
    }

    private class ViewHolder {
        private TextView name, data, tv_money;
        private ImageView touxiang;
    }
}