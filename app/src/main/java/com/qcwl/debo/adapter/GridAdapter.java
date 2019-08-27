package com.qcwl.debo.adapter;

/**
 * Created by Administrator on 2017/8/4.
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.GroupDetailsActivity;
import com.qcwl.debo.model.GroupInfoBean;
import com.qcwl.debo.ui.circle.ImgUtil;

import java.util.List;

/**
 * 群组成员gridadapter
 *
 * @author admin_new
 */
public class GridAdapter extends BaseAdapter {

    private Context context;
    private List<GroupInfoBean.Infos> infos;
    private GroupInfoBean.Infos info;
    private EMGroup group;

    public GridAdapter(Context context, List<GroupInfoBean.Infos> infos) {
        this.context = context;
        this.infos = infos;
    }

    public void setEMGroup(EMGroup group) {
        this.group = group;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.em_grid_owner, null);
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.iv_del = (ImageView) convertView.findViewById(R.id.iv_del);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.button = (LinearLayout) convertView.findViewById(R.id.button_avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // add button
        if (position == getCount() - 2) {
            holder.tv_name.setText("");
            holder.iv_del.setVisibility(View.GONE);
            holder.iv_avatar.setImageResource(R.mipmap.add_group_member);
            if (group != null) {
                if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) || group.getAdminList().contains(EMClient.getInstance().getCurrentUser())) {
                    convertView.setVisibility(View.VISIBLE);
                    holder.button.setVisibility(View.VISIBLE);
                } else {
                    convertView.setVisibility(View.GONE);
                    holder.button.setVisibility(View.GONE);
                }
            }
            return convertView;
        } else if (position == getCount() - 1) {
            holder.tv_name.setText("");
            holder.iv_del.setVisibility(View.GONE);
            holder.iv_avatar.setImageResource(R.mipmap.del_group_member);
            if (group != null) {
                if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) || group.getAdminList().contains(EMClient.getInstance().getCurrentUser())) {
                    convertView.setVisibility(View.VISIBLE);
                    holder.button.setVisibility(View.VISIBLE);
                } else {
                    convertView.setVisibility(View.GONE);
                    holder.button.setVisibility(View.GONE);
                }
            }
            return convertView;
        } else {
            info = infos.get(position);
            if (TextUtils.isEmpty(info.getUser_nickname())) {
                if ("1".equals(info.getMember_type())) {
                    holder.tv_name.setText(info.getOwner());
                } else {
                    holder.tv_name.setText(info.getMember());
                }
            } else {
                holder.tv_name.setText(info.getUser_nickname());
            }
            if (TextUtils.isEmpty(info.getAvatar())) {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(context, R.mipmap.head, holder.iv_avatar);
                    // Glide.with(context).load(R.mipmap.head).into(holder.iv_avatar);
                }
            } else {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(context, info.getAvatar(), holder.iv_avatar);
                    //  Glide.with(context).load(info.getAvatar()).into(holder.iv_avatar);
                }
            }
            if (!"1".equals(info.getMember_type())) {
                if (EMClient.getInstance().getCurrentUser().equals(info.getMember())) {
                    if ("2".equals(info.getMember_type())) {
                        if (!"2".equals(info.getMember_type())) {
                            if (info.isDel()) {
                                holder.iv_del.setVisibility(View.VISIBLE);
                            } else {
                                holder.iv_del.setVisibility(View.GONE);
                            }
                        } else {
                            holder.iv_del.setVisibility(View.GONE);
                        }
                    }
                }
                if (info.isDel()) {
                    holder.iv_del.setVisibility(View.VISIBLE);
                } else {
                    holder.iv_del.setVisibility(View.GONE);
                }
            } else {
                holder.iv_del.setVisibility(View.GONE);
            }

        }
        holder.iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((GroupDetailsActivity) context).removeUserFromGroup(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return infos.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        private ImageView iv_avatar, iv_del;
        private TextView tv_name;
        private LinearLayout button;
    }
}
