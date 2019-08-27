package com.qcwl.debo.ui.found.joke;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.util.ImageUtils;
import com.qcwl.debo.R;
import com.qcwl.debo.http.ApiHttpClient;
import com.qcwl.debo.ui.circle.ImgUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by qcwl on 2017/11/23.
 */

public class MyVideoAdapter extends BaseAdapter {

    private Context mContext;
    private List<VideoBean> list;
    private VideoBean bean;

    public MyVideoAdapter(Context mContext, List<VideoBean> mListVideo) {
        this.mContext = mContext;
        this.list = mListVideo;
    }

    @Override
    public int getCount() {
        return list.size();
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
        ViewHolder vh = null;
        bean = list.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.myvideo_item, null);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.content = (TextView) convertView.findViewById(R.id.content);
            vh.time = (TextView) convertView.findViewById(R.id.time);
            vh.touxiang = (ImageView) convertView.findViewById(R.id.touxiang);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.name.setText(bean.getUser_nickname());
        vh.content.setText("");
        vh.time.setText(getDateToString(bean.getTime()));
        ImgUtil.setGlideHead(mContext, ApiHttpClient.VIDEO_URL + bean.getImg_url(), vh.touxiang);
       /* Glide.with(mContext)
                .load(ApiHttpClient.VIDEO_URL+bean.getImg_url())
                .placeholder(R.mipmap.head)
                .error(R.mipmap.head)
                .into(vh.touxiang);*/
        return convertView;
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time * 1000);
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return mSimpleDateFormat.format(d);
    }

    private class ViewHolder {
        private TextView name, content, time;
        private ImageView touxiang;
    }
}
