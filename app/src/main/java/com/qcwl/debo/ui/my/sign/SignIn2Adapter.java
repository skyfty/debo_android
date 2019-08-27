package com.qcwl.debo.ui.my.sign;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AlMn on 2018/3/6.
 */

public class SignIn2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<SignBean> items;
    private SignBaseInfo baseInfo;
    private SignResult result;

    public SignIn2Adapter(Context mContext, SignResult result) {
        this.mContext = mContext;
        this.result = result;
        this.items = result.getDaily_sign_record();
        this.baseInfo = result.getBase_info();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case 0:
                view = inflate(R.layout.layout_sign_top);
                viewHolder = new TopViewHolder(view);
                break;
            case 1:
                view = inflate(R.layout.layout_sign_calendar);
                viewHolder = new CalendarViewHolder(view);
                break;
            case 2:
                view = inflate(R.layout.layout_sign_bottom);
                viewHolder = new BottomViewHolder(view);
                break;
        }
        return viewHolder;
    }

    private int year, month;
    private CalendarViewHolder vh;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        Log.e("isQ",items.get(position).getIs_sign_in()+"");
        switch (viewType) {
            case 0:

                break;
            case 1:
                vh = (CalendarViewHolder) holder;
                if (baseInfo != null) {
                    if (!TextUtils.isEmpty(baseInfo.getTotal_sign_num())) {
                        vh.textTotalNum.setText(baseInfo.getTotal_sign_num());
                    }
                    if (!TextUtils.isEmpty(baseInfo.getCurr_month_sign_num())) {
                        vh.textCurNum.setText(baseInfo.getCurr_month_sign_num());
                    }
                }
//                    Calendar calendar = Calendar.getInstance();
//                    Locale.setDefault(Locale.CHINA);
//                    year = calendar.get(Calendar.YEAR);
//                    month = calendar.get(Calendar.MONTH)+1;
                //vh.textToday.setText(new SimpleDateFormat("yyyy年MM月", Locale.CHINA).format(new Date()))
                final String year_get = result.getYear();
                final String month_get = result.getMonth();
                year = Integer.parseInt(year_get);
                month = Integer.parseInt(month_get);
                vh.textToday.setText(year_get + "年" + month_get + "月");
                vh.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL));
                if (items != null) {
                    vh.recyclerView.setAdapter(new Sign2Adapter(mContext, items));
                }
                //上月
                vh.textLastMonth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (month == 1) {
                            items.clear();
                            year -= 1;
                            month = 12;

                        } else {
                            items.clear();
                            month -= 1;
                        }
                        handleListDate();
                        ((SignIn2Activity) mContext).signIn(year+"", month+"");
                        vh.textToday.setText(year + "年" + month + "月");
                    }
                });
                //下月
                vh.textNextMonth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (month == 12) {
                            items.clear();
                            year += 1;
                            month = 1;
                        } else {
                            items.clear();
                            month += 1;
                        }
                        handleListDate();
                        ((SignIn2Activity) mContext).signIn(year+"", month+"");
                        vh.textToday.setText(year + "年" + month + "月");
                    }
                });
                break;
            case 2:

                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    private View inflate(int layoutId) {
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }

    static class TopViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_view)
        ImageView imageView;

        TopViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class BottomViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.textView)
        TextView textView;

        BottomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.recycler_view)
        RecyclerView recyclerView;
        @Bind(R.id.text_last_month)
        TextView textLastMonth;
        @Bind(R.id.text_today)
        TextView textToday;
        @Bind(R.id.text_next_month)
        TextView textNextMonth;
        @Bind(R.id.text_total_num)
        TextView textTotalNum;
        @Bind(R.id.text_cur_num)
        TextView textCurNum;

        CalendarViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void handleListDate() {
        if (items == null || items.size() == 0) {
            return;
        }
        Locale.setDefault(Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        SignBean bean = null;
        for (int i = 0; i < day_of_week - 1; i++) {
            bean = new SignBean();
            bean.setEmpty(true);
            items.add(i, bean);
        }
    }

}
