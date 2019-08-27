/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qcwl.debo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.qcwl.debo.R;
import com.qcwl.debo.model.GroupBean;
import com.qcwl.debo.ui.circle.ImgUtil;

import java.util.List;


public class GroupAdapter extends BaseAdapter {

    private Context context;
    private List<GroupBean> list;
    private GroupBean gb;

    public GroupAdapter(Context context, List<GroupBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<GroupBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<GroupBean> getList() {
        return list;
    }

    @Override
    public int getCount() {
        if (list.size() == 0) {
            return 0;
        }
        return list.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < list.size())
            return 0;
        else
            return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder vh;
        if (type == 0) {
            gb = list.get(position);
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.group_item, null);
                vh.tvTitle = (TextView) convertView.findViewById(R.id.title);
                vh.touxiang = (ImageView) convertView.findViewById(R.id.touxiang);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            if (TextUtils.isEmpty(gb.getG_avatar())) {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(context, R.mipmap.group, vh.touxiang);
                    //Glide.with(context).load(R.mipmap.group).into(vh.touxiang);
                }
            } else {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(context, gb.getG_avatar(), vh.touxiang);
                    //Glide.with(context).load(gb.getG_avatar()).into(vh.touxiang);
                }

            }

            vh.tvTitle.setText(gb.getGroupname());
        } else {
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.number_item, null);
                vh.number = (TextView) convertView.findViewById(R.id.number);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.number.setText(list.size() + "个群组");
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView tvTitle, number;
        private ImageView touxiang;
    }

}
