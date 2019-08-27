package com.qcwl.debo.ui.found.fans.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.fans.bean.FansBean;
import com.qcwl.debo.ui.found.fans.FansListActivity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/8/2.
 */

public class FansListAdapter extends CommonAdapter<FansBean> {
    public FansListAdapter(Context context, List<FansBean> datas) {
        super(context, R.layout.item_fans_list, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, final FansBean item, final int position) {
        try {
            FansViewHolder holder = new FansViewHolder(viewHolder.getConvertView());
            ImgUtil.load(mContext, item.getSmall_avatar(), holder.imageView);
            holder.textName.setText("" + item.getUser_nickname());
            holder.textSign.setText("个性签名：" + item.getSignature());
            if (item.getIs_concern() == 0) {//未关注
                holder.imgFocus.setImageResource(R.mipmap.ic_fans_normal);
                holder.textFocus.setText("关注");
                holder.textFocus.setTextColor(mContext.getResources().getColor(R.color.color_trumpet_red));
            } else if (item.getIs_concern() == 1) {//已关注
                holder.imgFocus.setImageResource(R.mipmap.ic_fans_focused);
                holder.textFocus.setText("已关注");
                holder.textFocus.setTextColor(mContext.getResources().getColor(R.color.fans_gray));
            }
            holder.layoutFocus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getIs_concern() == 0) {//未关注
                        item.setIs_concern(1);
                    } else if (item.getIs_concern() == 1) {//已关注
                        item.setIs_concern(0);
                        mDatas.remove(position);
                    }
                    notifyDataSetChanged();
                    if (mContext instanceof FansListActivity) {
                        ((FansListActivity) mContext).focusFans(item.getId());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class FansViewHolder {
        @Bind(R.id.image_view)
        CircleImageView imageView;
        @Bind(R.id.text_name)
        TextView textName;
        @Bind(R.id.text_sign)
        TextView textSign;
        @Bind(R.id.layout_focus)
        View layoutFocus;
        @Bind(R.id.text_focus)
        TextView textFocus;
        @Bind(R.id.img_focus)
        ImageView imgFocus;

        FansViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
