package com.qcwl.debo.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.calendarView.CalendarView;
import com.qcwl.debo.calendarView.DateBean;
import com.qcwl.debo.calendarView.listener.OnMonthItemClickListener;
import com.qcwl.debo.calendarView.listener.OnPagerChangeListener;

import butterknife.ButterKnife;


public class ShareBottomDialog extends BottomBaseDialog<ShareBottomDialog> {
    private CalendarView calendarView;
    private Context context;
    private ImageView last_month, next_month, last_year, next_year;
    private SetData data;
    private int type;

    public ShareBottomDialog(Context context, View animateView) {
        super(context, animateView);
        this.context = context;
    }

    public ShareBottomDialog(Context context) {
        super(context);
        this.context = context;
    }

    public void setListener(SetData data) {
        this.data = data;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public View onCreateView() {
        showAnim(new FlipVerticalSwingEnter());
        dismissAnim(null);
        View inflate = View.inflate(mContext, R.layout.calendar_dialog, null);
        ButterKnife.bind(this, inflate);
        final TextView title = (TextView) inflate.findViewById(R.id.title);
        calendarView = (CalendarView) inflate.findViewById(R.id.calendar);
        last_month = (ImageView) inflate.findViewById(R.id.last_month);
        next_month = (ImageView) inflate.findViewById(R.id.next_month);
        last_year = (ImageView) inflate.findViewById(R.id.last_year);
        next_year = (ImageView) inflate.findViewById(R.id.next_year);
        calendarView.init();

        DateBean d = calendarView.getDateInit();

        title.setText(d.getSolar()[0] + "年" + d.getSolar()[1] + "月" + d.getSolar()[2] + "日");

        calendarView.setOnPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                title.setText(date[0] + "年" + date[1] + "月" + date[2] + "日");
            }
        });

        calendarView.setOnItemClickListener(new OnMonthItemClickListener() {
            @Override
            public void onMonthItemClick(View view, DateBean date) {
                title.setText(date.getSolar()[0] + "年" + date.getSolar()[1] + "月" + date.getSolar()[2] + "日");
                if (type == 1)
                    data.setStartTime(date.getSolar()[0]+"" , date.getSolar()[1]+"" ,date.getSolar()[2]+"");
                else if (type == 2)
                    data.setEndTime(date.getSolar()[0] + "" , date.getSolar()[1] + "" , date.getSolar()[2]+"");
            }
        });

        last_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.lastMonth();
            }
        });

        next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.nextMonth();
            }
        });
        last_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.lastYear();
            }
        });
        next_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.nextYear();
            }
        });

        return inflate;
    }

    @Override
    public void setUiBeforShow() {


    }

    public interface SetData {

        void setStartTime(String startNear,String startMonth,String startDay);

        void setEndTime(String endNear,String endMonth,String endDay);
    }

}
