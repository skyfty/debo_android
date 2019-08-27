package com.qcwl.debo.ui.found.memorandum.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.found.memorandum.bean.ScheduleDayBean;
import com.qcwl.debo.utils.ColorUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2018/6/21.
 */

public class MemoTaskAdapter extends CommonAdapter<ScheduleDayBean> {

    public MemoTaskAdapter(Context context, List<ScheduleDayBean> datas) {
        super(context, R.layout.item_task, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, ScheduleDayBean item, int position) {
        MemoTaskHolder taskHolder = new MemoTaskHolder(viewHolder.getConvertView());
        taskHolder.mTextTitle.setText(item.getContent());
        String toDate = stampToDate(Long.parseLong(item.getStart_time()));
        taskHolder.mTextTime.setText(toDate);
        if(item.getBack_color()!= null){
            taskHolder.spot.setBackgroundResource(ColorUtils.circleColor[Integer.parseInt(item.getBack_color())]);
        }else{
            taskHolder.spot.setBackgroundResource(ColorUtils.circleColor[Integer.parseInt(item.getM_type())]);
        }
        if(item.getIs_remind() == 0){
            taskHolder.mTextRemind.setText("未提醒");
        }else
            taskHolder.mTextRemind.setText(item.getContent());
        if(item.getStatus().equals("1")){
            taskHolder.mTextTitle.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            taskHolder.mTextRemind.setText("已完成");
        }
//        textview.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG ); //中间横线
    }

    /*
     * 将时间戳转换为时间
     */
    public String stampToDate(long timeMillis){
        if (String.valueOf(timeMillis).length() == 10) {
            timeMillis = timeMillis * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

    static class MemoTaskHolder {

        @Bind(R.id.text_title)
        TextView mTextTitle;
        @Bind(R.id.text_time)
        TextView mTextTime;
        @Bind(R.id.text_remind)
        TextView mTextRemind;
        @Bind(R.id.spot)
        ImageView spot;

        MemoTaskHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
