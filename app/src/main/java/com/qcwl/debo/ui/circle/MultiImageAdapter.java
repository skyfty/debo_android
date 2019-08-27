package com.qcwl.debo.ui.circle;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.found.fans.PublishFansDynamicActivity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */

public class MultiImageAdapter extends CommonAdapter<String> {
    public MultiImageAdapter(Context context, List<String> datas) {
        super(context, R.layout.layout_add_image, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, String item, final int position) {
        ImageView addImg = viewHolder.getView(R.id.add_image);
        ImageView delImg = viewHolder.getView(R.id.del_image);
        if (TextUtils.isEmpty(item)) {
            delImg.setVisibility(View.GONE);
            ImgUtil.load(mContext, R.mipmap.btn_add_img, addImg);
            addImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加图片
                    if (mContext instanceof PublishCircleActivity) {
                        ((PublishCircleActivity) mContext).selectPictrue();
                    }else if (mContext instanceof PublishFansDynamicActivity) {
                        ((PublishFansDynamicActivity) mContext).selectPictrue();
                    }
                }
            });
        } else {
            delImg.setVisibility(View.VISIBLE);
            ImgUtil.load(mContext, "file://" + item, addImg);
            addImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看大图
                }
            });
            delImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatas.remove(position);
                    if (!"".equals(mDatas.get(mDatas.size() - 1))) {
                        mDatas.add("");
                    }
                    notifyDataSetChanged();
                    if (mContext instanceof PublishCircleActivity) {
                        ((PublishCircleActivity) mContext).setGridViewHeight(mDatas.size());
                    }else if (mContext instanceof PublishFansDynamicActivity) {
                        ((PublishFansDynamicActivity) mContext).setGridViewHeight(mDatas.size());
                    }
                }
            });
        }
    }
}
