package com.qcwl.debo.ui.found.fans.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.found.fans.bean.FansDynamicPraiseBean;
import com.qcwl.debo.utils.SPUtil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/8/3.
 */

public class FansDynamicPraiseAdapter extends CommonAdapter<FansDynamicPraiseBean> {
    public FansDynamicPraiseAdapter(Context context, List<FansDynamicPraiseBean> datas) {
        super(context, R.layout.item_fans_dynamic_praise, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, final FansDynamicPraiseBean bean, int position) {
        try {
            PraiseViewHolder holder = new PraiseViewHolder(viewHolder.getConvertView());
            ImgUtil.loadHead(mContext, bean.getAvatar(), holder.imageView);
            holder.textName.setText("" + bean.getUser_nickname());
//            viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mContext.startActivity(new Intent(mContext, ContactsContentActivity.class)
//                            .putExtra("my_mobile", SPUtil.getInstance(mContext).getString("phone"))
//                            .putExtra("mobile", bean.getMobile()));
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static class PraiseViewHolder {
        @Bind(R.id.image_view)
        CircleImageView imageView;
        @Bind(R.id.text_name)
        TextView textName;

        PraiseViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
