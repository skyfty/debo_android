package com.qcwl.debo.ui.found.memorandum.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.found.memorandum.bean.ScheduleClassifyBean;
import com.qcwl.debo.utils.ColorUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2018/6/14.
 */

public class ClassifyAdapter extends CommonAdapter<ScheduleClassifyBean> {

    public ClassifyAdapter(Context context, List<ScheduleClassifyBean> datas) {
        super(context , R.layout.item_classify, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, ScheduleClassifyBean item, int position) {
        Log.i("Classify","adapter"+item.toString());
        ClassifyHolder holder = new ClassifyHolder(viewHolder.getConvertView());
        holder.textName.setText(item.getName()+"("+item.getCount()+")");
        holder.spot.setBackgroundResource(ColorUtils.classifyColor[Integer.parseInt(item.getBack_color())]);

    }

    static class ClassifyHolder{
        @Bind(R.id.name_num)
        TextView textName;
        @Bind(R.id.spot)
        ImageView spot;

        ClassifyHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
