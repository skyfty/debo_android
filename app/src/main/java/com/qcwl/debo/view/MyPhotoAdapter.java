package com.qcwl.debo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.qcwl.debo.R;
import com.qcwl.debo.model.MyPhotoBean;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/7/20.
 */

public class MyPhotoAdapter extends BaseAdapter {
    private Context context;
    private List<MyPhotoBean> list;
    private MyPhotoBean mp;

    public MyPhotoAdapter(Context context, List<MyPhotoBean> list) {
        this.context = context;
        this.list = list;
    }

    public void updateList(List<MyPhotoBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<MyPhotoBean> getList() {
        return list;
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
        mp = list.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.my_photo_item2, null);
            vh.day = (TextView) convertView.findViewById(R.id.day);
            vh.month = (TextView) convertView.findViewById(R.id.month);
            vh.image = (ImageView) convertView.findViewById(R.id.image);
            vh.content_ll = (LinearLayout) convertView.findViewById(R.id.content_ll);
            vh.content = (TextView) convertView.findViewById(R.id.content);
            vh.number = (TextView) convertView.findViewById(R.id.number);
            vh.time_ll = (LinearLayout) convertView.findViewById(R.id.time_ll);
            vh.year = (TextView) convertView.findViewById(R.id.year);
            vh.textFlag = (TextView) convertView.findViewById(R.id.text_flag);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        String section = getSectionForPosition(position);
        String section1 = getSectionForPosition1(position);
        // 如果当前位置等于该分类的位置 ，则认为是第一次出现

        if (position == getPositionForSection1(section1)) {
            vh.year.setVisibility(View.VISIBLE);
            vh.year.setText(TimeUtils.formatTimeByYear(Long.parseLong(mp.getCreate_time())) + "年");
        } else {
            vh.year.setVisibility(View.GONE);
        }

        if (position == getPositionForSection(section)) {
            vh.time_ll.setVisibility(View.VISIBLE);
            vh.day.setText(TimeUtils.formatTimeByDay(Long.parseLong(mp.getCreate_time())));
            vh.month.setText(TimeUtils.formatTimeByMonth(Long.parseLong(mp.getCreate_time())) + "月");
        } else {
            vh.time_ll.setVisibility(View.INVISIBLE);
        }
        vh.day.setText(TimeUtils.formatTimeByDay(Long.parseLong(mp.getCreate_time())));
        vh.month.setText(TimeUtils.formatTimeByMonth(Long.parseLong(mp.getCreate_time())) + "月");
        if (mp.getImages().size() == 0) {
            vh.image.setVisibility(View.GONE);
            int dpVal= ScreenUtils.dp2px(context,5);
            vh.content_ll.setPadding(0,dpVal,0,dpVal);
            vh.content_ll.setBackgroundResource(R.color.divider);
            vh.number.setText("");
        } else {
            vh.image.setVisibility(View.VISIBLE);
            if (Util.isOnMainThread()){
                ImgUtil.setGlideHead(context,mp.getImages().get(0),vh.image);
                //Glide.with(context).load(mp.getImages().get(0)).into(vh.image);
            }
            vh.content_ll.setBackgroundResource(R.color.status_text);
            vh.number.setText("共" + mp.getImages().size() + "张");
        }
        vh.content.setText(mp.getMoments_content());
        if (mp.getIs_both() == 1) {
            if (mp.getCircle_type() == 1) {
                vh.textFlag.setText("朋友圈");
            } else if (mp.getCircle_type() == 2) {
                vh.textFlag.setText("人脉圈");
            }
        } else {
            vh.textFlag.setText("朋友圈 人脉圈");
        }
        return convertView;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值 年和月
     */
    public String getSectionForPosition(int position) {
        String s = TimeUtils.formatTimeByYear(Long.parseLong(list.get(position).getCreate_time())) + TimeUtils.formatTimeByMonth(Long.parseLong(list.get(position).getCreate_time()));

        return s;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值 年
     */
    public String getSectionForPosition1(int position) {
        String s = TimeUtils.formatTimeByYear(Long.parseLong(list.get(position).getCreate_time()));
        return s;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(String section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = TimeUtils.formatTimeByYear(Long.parseLong(list.get(i).getCreate_time())) + TimeUtils.formatTimeByMonth(Long.parseLong(list.get(i).getCreate_time()));
            if (sortStr.equals(section)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection1(String section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = TimeUtils.formatTimeByYear(Long.parseLong(list.get(i).getCreate_time()));
            if (sortStr.equals(section)) {
                return i;
            }
        }
        return -1;
    }

    private class ViewHolder {
        private TextView day, month, content, number, year, textFlag;
        private ImageView image;
        private LinearLayout time_ll, content_ll;
    }
}
