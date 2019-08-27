package com.qcwl.debo.ui.found.star;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.RedPacketBean;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/8/11.
 */

public class WishingRecordAdapter extends CommonAdapter<RedPacketBean> {
    public WishingRecordAdapter(Context context, List<RedPacketBean> datas) {
        super(context, R.layout.item_wishing_record, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, RedPacketBean item, int position) {
        try {
            RecordViewHolder holder = new RecordViewHolder(viewHolder.getConvertView());
            ImgUtil.load(mContext, item.getAvatar(), holder.imageView);
            holder.textName.setText(item.getUser_nickname());
            holder.textSign.setText(item.getWishing_content());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static class RecordViewHolder {
        @Bind(R.id.image_view)
        CircleImageView imageView;
        @Bind(R.id.text_name)
        TextView textName;
        @Bind(R.id.text_sign)
        TextView textSign;

        RecordViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
