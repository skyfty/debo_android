package com.qcwl.debo.ui.found.memorandum.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.qcwl.debo.R;
import com.qcwl.debo.http.ApiHttpClient;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2018/6/19.
 */

public class URLPictureAdapter extends CommonAdapter<String> {

    public URLPictureAdapter(Context context, List<String> datas) {
        super(context, R.layout.item_picture, datas);
    }

    @Override
    protected void convert(ViewHolder holder, String s, final int position) {
        PictureHolder pictureHolder = new PictureHolder(holder.getConvertView());
        ImgUtil.load(mContext, ApiHttpClient.VIDEO_URL+s,pictureHolder.img_add);
        pictureHolder.del_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatas.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    static class PictureHolder{
        @Bind(R.id.img_add)
        ImageView img_add;
        @Bind(R.id.del_image)
        ImageView del_image;

        public PictureHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}
