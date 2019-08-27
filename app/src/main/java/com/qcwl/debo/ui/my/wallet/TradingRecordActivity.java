package com.qcwl.debo.ui.my.wallet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bigkoo.pickerview.TimePickerView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.my.wallet.trading.RadioAdapter;
import com.qcwl.debo.ui.my.wallet.trading.RadioBean;
import com.qcwl.debo.ui.my.wallet.trading.TradingAdapter;
import com.qcwl.debo.ui.my.wallet.trading.TradingBean;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TradingRecordActivity extends BaseActivity {

    @Bind(R.id.swipe_target)
    ListView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private int page = 1;

    private String uid, mobile;
    private int indent = 0;
    private String start_time = "", end_time = "";

    private List<TradingBean> items = null;
    private TradingAdapter adapter = null;

    private List<RadioBean> raidoList;
    private RadioAdapter radioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_record);
        ButterKnife.bind(this);
        uid = sp.getString("uid");
        mobile = sp.getString("phone");
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("财务报表")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setTextRight("筛选")
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initTradingDialog();
                    }
                });
    }

    private void initData() {
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                start_time = "";
                end_time = "";
                getTradingRecord();
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToLoadLayout == null) {
                            return;
                        }
                        swipeToLoadLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                end_time = "";
                start_time = "";
                getTradingRecord();
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToLoadLayout == null) {
                            return;
                        }
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });
        items = new ArrayList<>();
        adapter = new TradingAdapter(this, items);
        swipeTarget.setAdapter(adapter);
        getTradingRecord();
        swipeTarget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (items.get(position).getType() == 1) {
                    startActivity(new Intent(TradingRecordActivity.this,
                            TradingRecordDetailActivity.class)
                            .putExtra("order_sn", items.get(position).getOrder_sn())
                            .putExtra("img_res_id", items.get(position).getImgResId()));
                    Log.i("TradingRecordActivity","..........imgres="+items.get(position).getImgResId());
                }
            }
        });
    }

    private void getTradingRecord() {
        Api.getTradingRecord(uid, mobile,
                page, indent, start_time, end_time,
                new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            List<TradingBean> list = JSON.parseArray(apiResponse.getData(), TradingBean.class);
                            if (list != null && list.size() > 0) {
                                if (page == 1) {
                                    items.clear();
                                }
                                items.addAll(list);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtils.showShort(TradingRecordActivity.this, apiResponse.getMessage());
                        }
                    }
                });
    }


    private TimePickerView pickerView = null;

    public void showTimePicker() {
        if (pickerView == null) {
            initTimePicker();
        }
        pickerView.show();
    }

    private void initTimePicker() {
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH);
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2017, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(curYear, curMonth, curDay);
        //时间选择器
        pickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                //long time = date.getTime();
                String timeStr = new SimpleDateFormat("yy.MM").format(date);
                String[] timeArr = timeStr.split("\\.");
                int year = Integer.parseInt(timeArr[0]);
                int month = Integer.parseInt(timeArr[1]);
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, 1);
                int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                start_time = year + "-" + month  + "-" + 1;
                end_time = year + "-" + month  + "-" + maxDay;
                page = 1;
                getTradingRecord();
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, false, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(true)
                .setDividerColor(Color.parseColor("#e3e3e3"))
                .setContentSize(18)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setBackgroundId(Color.parseColor("#80000000")) //设置外部遮罩颜色//0x80ffFFFF
                //.isDialog(true)
                .setTitleBgColor(Color.parseColor("#F7F8F8"))
                .setBgColor(Color.WHITE)
                .setTextColorCenter(Color.parseColor("#464646"))
//                .gravity(Gravity.BOTTOM)
                .setDecorView(null)
                .setSubmitText("确定")
                .setSubmitColor(Color.parseColor("#71CBA1"))
                .setCancelText("取消")
                .setCancelColor(Color.parseColor("#989898"))
                .build();
    }

    public void initDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog pickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                start_time = year + "-" + (month + 1) + "-" + 1;
                end_time = year + "-" + (month + 1) + "-" + calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                page = 1;
                getTradingRecord();
            }
        }, year, month, day);
        new Date().getTime();
        pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        pickerDialog.show();
    }

    Dialog bottomDialog = null;
    View contentView = null;

    private void initTradingDialog() {
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_trading_type, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels; //- ScreenUtils.dp2px(this, 16f);
        params.bottomMargin = 0;//ScreenUtils.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
        listener(contentView);
    }

    private ViewHolder holder = null;
    private int prePosition = 0;

    private void listener(View contentView) {
        holder = new ViewHolder(contentView);
        initGridViewData();
        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                prePosition = position;
                indent = raidoList.get(position).getIndent();
                for (int i = 0; i < raidoList.size(); i++) {
                    if (i == position) {
                        raidoList.get(i).setChecked(true);
                    } else {
                        raidoList.get(i).setChecked(false);
                    }
                }
                radioAdapter.notifyDataSetChanged();
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("===", "indent=" + indent);
                start_time = "";
                end_time = "";
                getTradingRecord();
                bottomDialog.dismiss();
            }
        });
    }

    //1、充值 ；2、购买合约人；3、发红包；4、领取红包；5、退款；6、购买小喇叭；7、三方客转入；8、三方客转出；9、提现；10、积分兑换;  13 转账
    private void initGridViewData() {
        raidoList = new ArrayList<>();
        raidoList.add(new RadioBean(0, "全部", false));
        raidoList.add(new RadioBean(1, "充值", false));
        raidoList.add(new RadioBean(9, "提现", false));
        raidoList.add(new RadioBean(5, "退款", false));
        raidoList.add(new RadioBean(3, "红包", false));
        raidoList.add(new RadioBean(7, "三方客", false));
        raidoList.add(new RadioBean(2, "购买合约人脉", false));
        raidoList.add(new RadioBean(6, "购买小喇叭", false));
        raidoList.add(new RadioBean(10, "积分兑换", false));
        raidoList.add(new RadioBean(13, "转账", false));
        raidoList.get(prePosition).setChecked(true);
        radioAdapter = new RadioAdapter(this, raidoList);
        holder.gridView.setAdapter(radioAdapter);
    }

    static class ViewHolder {
        @Bind(R.id.grid_view)
        GridView gridView;
        @Bind(R.id.button)
        Button button;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
