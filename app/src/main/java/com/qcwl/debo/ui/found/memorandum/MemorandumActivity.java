package com.qcwl.debo.ui.found.memorandum;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.codbking.calendar.CaledarAdapter;
import com.codbking.calendar.CalendarBean;
import com.codbking.calendar.CalendarDateView;
import com.codbking.calendar.CalendarUtil;
import com.codbking.calendar.CalendarView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.found.memorandum.activity.AddScheduleActivity;
import com.qcwl.debo.ui.found.memorandum.activity.ContentDetailsActivity;
import com.qcwl.debo.ui.found.memorandum.activity.DataStatisticsActivity;
import com.qcwl.debo.ui.found.memorandum.activity.PendingTaskActivity;
import com.qcwl.debo.ui.found.memorandum.activity.QuickMemoryActivity;
import com.qcwl.debo.ui.found.memorandum.bean.ScheduleDayBean;
import com.qcwl.debo.ui.found.memorandum.bean.ScheduleMonthBean;
import com.qcwl.debo.ui.found.memorandum.builder.TimePickerBuilder;
import com.qcwl.debo.ui.found.memorandum.listener.OnTimeSelectChangeListener;
import com.qcwl.debo.ui.found.memorandum.listener.OnTimeSelectListener;
import com.qcwl.debo.ui.found.memorandum.view.TimePickerView;
import com.qcwl.debo.utils.ColorUtils;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.BounceTopEnter2;
import com.qcwl.debo.view.SharerightDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MemorandumActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.data)
    TextView mData;
    @Bind(R.id.calendarDateView)
    CalendarDateView mCalendarDateView;
    @Bind(R.id.list)
    ListView mList;
    private ImageView mBack;
    private TextView mTitle;
    private RelativeLayout mRight;
    private ImageView mRightImg;
    private BounceTopEnter2 mBasIn;
    private SharerightDialog dialog;
    private ArrayList<ScheduleDayBean> mDayBeans;
    private String mTimeStamp;
    private Dialog mDialog;
    private TimePickerView pvTime;
    private String time_stamp = "";
    private LinearLayout mLiner_no;
    private TextView mRemind_time;
    private BaseAdapter mBaseAdapter;
    private List<ScheduleMonthBean> mScheduleMonthBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorandum);
        ButterKnife.bind(this);
        initHeaderView();
        initTitleBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMonthDetails();
        initData();
        getDayDetails(mTimeStamp);
    }

    private void initTitleBar() {
        mBack = (ImageView) findViewById(R.id.left_image);
        mTitle = (TextView) findViewById(R.id.title);
        mRight = (RelativeLayout) findViewById(R.id.right_layout);
        mRightImg = (ImageView) findViewById(R.id.right_image);
        mTitle.setText(R.string.memorandum);
        mBack.setOnClickListener(this);
        mRight.setOnClickListener(this);
        mRightImg.setBackgroundResource(R.mipmap.btn_more);
    }


    private void initData() {
        mCalendarDateView.setAdapter(new CaledarAdapter() {

            int count = 0;

            @Override
            public View getView(View convertView, ViewGroup parentView, CalendarBean bean) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_debo, null);
                }
                TextView chinaText = (TextView) convertView.findViewById(R.id.chinaText);
                TextView text = (TextView) convertView.findViewById(R.id.text);
                ImageView spot_img = (ImageView) convertView.findViewById(R.id.spot_img);

                text.setText("" + bean.day);
//                Log.i("schedule_bean",mScheduleMonthBeans.toString()+"*-*");
//                count += 1;
//                Log.i("schedule_count",count+"");
                if (bean.mothFlag != 0) {
                    text.setTextColor(0xff9299a1);
                } else {
                    text.setTextColor(0xff444444);
                }
                chinaText.setText(bean.chinaDay);
                return convertView;
            }
        });

        mCalendarDateView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("onpageselected",position+"");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mCalendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, CalendarBean bean) {
                //日 bean.day
                mData.setText(bean.year + "年" + bean.moth + "月");
                initList(bean.year, bean.moth, bean.day);
                long stringToDate = getStringToDate(bean.year + "年" + bean.moth + "月" + bean.day + "日");
                mTimeStamp = getTimeStamp(stringToDate + "");
                mRemind_time.setText(bean.year + "年" + bean.moth + "月" + bean.day + "日");
                Log.i("time_stamp", mTimeStamp);
                getDayDetails(mTimeStamp);
            }
        });
        time_stamp = getTime() + "";
        int[] data = CalendarUtil.getYMD(new Date());
        //日 +data[2]；
        mData.setText(data[0] + "年" + data[1] + "月");
        mRemind_time.setText(data[0] + "年" + data[1] + "月" + data[2] + "日");
        mTimeStamp = getTimeStamp(getStringToDate(data[0] + "年" + data[1] + "月" + data[2] + "日") + "");
        Log.i("time_stamp", mTimeStamp);
        getDayDetails(mTimeStamp);
        initList(data[0], data[1], data[2]);
    }

    private String getTimeStamp(String time){
        String substring = time.substring(0, 10);
        return substring;
    }

    private void initList(final int year, final int month, final int day) {
        mDayBeans = new ArrayList<>();
        mBaseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (mDayBeans != null) {
                    return mDayBeans.size();
                } else
                    return 1;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @SuppressLint("ResourceType")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(MemorandumActivity.this).inflate(R.layout.item_schedule, null);
                }
                TextView textView = (TextView) convertView.findViewById(R.id.title);
                TextView remind_time = (TextView) convertView.findViewById(R.id.remind_time);
                TextView time_period = (TextView) convertView.findViewById(R.id.time_period);
                ImageView spot = (ImageView) convertView.findViewById(R.id.spot);
                if (mDayBeans != null) {
                    ScheduleDayBean bean = mDayBeans.get(position);
                    spot.setBackgroundResource(ColorUtils.classifyColor[Integer.parseInt(bean.getBack_color())]);

                    Log.i("color_bac",bean.getBack_color());
//                    spot.setBackgroundResource(Color.parseColor(bean.getBack_color()));
                    textView.setText(bean.getContent());
                    if (bean.getIs_remind() == 1) {
                        remind_time.setText(bean.getRem_time());
                    } else {
                        remind_time.setText("未提醒");
                    }
                    time_period.setText(stampToDateAccuracy(Long.parseLong(bean.getStart_time())));
                } else {
                    textView.setText("暂无日程");
                    remind_time.setText(year + "年" + month + "月" + day + "日");
                    time_period.setText("新增日程");
                }

                return convertView;
            }
        };
        mList.setAdapter(mBaseAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    return;
                }
                long date = getStringToDate(year + "年" + month + "月" + day + "日");
                Log.i("data_time",date+"/"+position+"/"+mDayBeans.get(position-1).getId());
                if(mDayBeans!=null){
                    startActivity(new Intent(MemorandumActivity.this,ContentDetailsActivity.class).putExtra("m_id",mDayBeans.get(position-1).getId()));
                }
            }
        });

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //长按删除事件
//                delSchedule();
                Log.i("position",position+"");
                showDelSchedule(mDayBeans.get(position-1).getId());
                return true;
            }
        });
    }

    private void initHeaderView() {
        View inflate = LayoutInflater.from(MemorandumActivity.this).inflate(R.layout.list_headerview, null);
        ImageView add_schedule = (ImageView) inflate.findViewById(R.id.add_schedule);
        mLiner_no = (LinearLayout) inflate.findViewById(R.id.liner_no);
        mRemind_time = (TextView) inflate.findViewById(R.id.remind_time);
        mLiner_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MemorandumActivity.this,AddScheduleActivity.class)
                        .putExtra("title","添加日程")
                        .putExtra("type","0"));
            }
        });
        mRemind_time.setText(stampToDate(getTime()));
        add_schedule.setOnClickListener(this);
        mList.addHeaderView(inflate);
    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_layout:
                showRightDialog();
                break;
            case R.id.add_schedule:
//                跳转添加详情界面
                startActivity(new Intent(this,AddScheduleActivity.class)
                        .putExtra("title","添加日程")
                        .putExtra("type","0"));
                break;
            case R.id.agency_task:
//                待办任务
                startActivity(new Intent(this,PendingTaskActivity.class));
                mDialog.dismiss();
                break;
            case R.id.data_statistics:
//                数据统计
                startActivity(new Intent(this,DataStatisticsActivity.class));
                mDialog.dismiss();
                break;
            case R.id.quick_memory:
//                快速记事
                startActivity(new Intent(this,QuickMemoryActivity.class).putExtra("time_stamp",time_stamp));
                mDialog.dismiss();
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void showRightDialog() {
        mDialog = getDialog();
        Window window = mDialog == null ? null : mDialog.getWindow();
        if (mDialog != null && window != null) {
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.width = 500;
                Window dialogWindow = mDialog.getWindow();
                dialogWindow.setBackgroundDrawable(null);
                if (dialogWindow != null) {
                    dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim2);//修改动画样式
                    dialogWindow.setGravity(Gravity.RIGHT);//改成right,右侧显示
                }
                mDialog.show();
            }
        }
    }

    public Dialog getDialog() {
        Dialog dialog = new Dialog(this,R.style.ios_bottom_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_more_right,null);
        LinearLayout agency_task = (LinearLayout) view.findViewById(R.id.agency_task);
        LinearLayout data_statistics = (LinearLayout) view.findViewById(R.id.data_statistics);
        LinearLayout quick_memory = (LinearLayout) view.findViewById(R.id.quick_memory);
        agency_task.setOnClickListener(this);
        data_statistics.setOnClickListener(this);
        quick_memory.setOnClickListener(this);
        dialog.setContentView(view);
        return dialog;
    }

    @OnClick(R.id.data)
    public void onViewClicked() {
//        initRemindTimePicker();
//        pvTime.show();
    }

    //删除提示框
    private void showDelSchedule(final String position){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MemorandumActivity.this);
        normalDialog.setTitle("确定是否删除");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delSchedule(position);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    //选择时间dialog
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void initRemindTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Log.i("pvTime", "onTimeSelect");
                mData.setText(getTime(date));
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
//                        Log.i("pvTime", getTime(date)+"this is time");
                    }
                })
                .setType(new boolean[]{true, true, false, false, false, false})
                .isDialog(true)
//                .setDecorView(mFragment_data)
                .build();
        TextView text_remind = (TextView) pvTime.findViewById(R.id.text_remind);
        Button btnCancel = (Button) pvTime.findViewById(R.id.btnCancel);
        Button btnSubmit = (Button) pvTime.findViewById(R.id.btnSubmit);
        RelativeLayout rv_topbar = (RelativeLayout) pvTime.findViewById(R.id.rv_topbar);
        text_remind.setVisibility(View.GONE);
        rv_topbar.setBackgroundResource(R.color.color_green);
        btnCancel.setTextColor(getResources().getColor(R.color.white));
        btnSubmit.setTextColor(getResources().getColor(R.color.white));
        btnCancel.setText("回到今天");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.setTime();
            }
        });
        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
        return format.format(date);
    }

    public long getTime(){
        long millis = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        return millis;
    }

    /*
     * 将时间戳转换为时间
     */
    public String stampToDate(long timeMillis){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

    /*
     * 将时间戳转换为时间
     */
    public String stampToDateAccuracy(long timeMillis){
        if (String.valueOf(timeMillis).length() == 10) {
            timeMillis = timeMillis * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(timeMillis);
        Log.i("todate",timeMillis+"/*/");
        return simpleDateFormat.format(date);
    }

    /*
        网络请求
     */
    //获取本月的详情
    private void getMonthDetails(){
        Api.getMonthEvent("612", new ApiResponseHandler(MemorandumActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.getCode() == 0){
                    //成功
                    mScheduleMonthBeans = JSON.parseArray(apiResponse.getData(), ScheduleMonthBean.class);
//                    ScheduleMonthBean beans = JSON.parseObject(apiResponse.getData(), ScheduleMonthBean.class);
                    Log.i("schedule_month", mScheduleMonthBeans.toString());
                    Log.i("schedule_day", mScheduleMonthBeans.get(5).getDay());
                    Log.i("schedule_count", mScheduleMonthBeans.get(5).getCount());
                    Log.i("schedule_color", mScheduleMonthBeans.get(5).getColor().toString());
                    Log.i("schedule_type", mScheduleMonthBeans.get(5).getType().toString());
                    initData();
                }else{
                    Log.i("fail_month",apiResponse.getMessage()+"/"+apiResponse.getCode());
                }
            }
        });
    }

    //获取某天的详情
    private void getDayDetails(String date){
        Api.getDayDetailsEvent(sp.getString("uid"), date, new ApiResponseHandler(MemorandumActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.getCode() == 0){
                    mLiner_no.setVisibility(View.GONE);
                    mDayBeans.removeAll(mDayBeans);
                    Log.i("Success_day",apiResponse.getMessage()+"/"+apiResponse.getCode());
                    //成功
                    List<ScheduleDayBean> beans = JSON.parseArray(apiResponse.getData(), ScheduleDayBean.class);
                    if (beans != null) {
                        mDayBeans.addAll(beans);
                    }
                    mBaseAdapter.notifyDataSetChanged();
                }else{
                    mDayBeans.removeAll(mDayBeans);
                    mLiner_no.setVisibility(View.VISIBLE);
                    Log.i("fail_day",apiResponse.getMessage()+"/"+apiResponse.getCode());
                }
            }
        });
    }

    //删除备忘录
    private void delSchedule(String m_id){
        Api.delMemorandumDetails(sp.getString("uid"), m_id, new ApiResponseHandler(MemorandumActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.getCode() == 0){
                    ToastUtils.showShort(MemorandumActivity.this,"删除成功");
                    getDayDetails(mTimeStamp);
                }else{
                    Log.i("del_fail",apiResponse.getMessage()+"/"+apiResponse.getCode());
                }
            }
        });
    }

    private SimpleDateFormat format;
    /*将字符串转为时间戳*/
    public long getStringToDate(String time) {
        format = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date();
        try{
            date = format.parse(time);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

}
