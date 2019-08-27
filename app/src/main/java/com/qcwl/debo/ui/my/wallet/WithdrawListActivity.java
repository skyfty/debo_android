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
import com.qcwl.debo.ui.my.wallet.trading.WithDrawAdapter;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WithdrawListActivity extends BaseActivity {

    @Bind(R.id.swipe_target)
    ListView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private int page = 1;

    private String uid, mobile;
    private int indent = 0;
    private String start_time = "", end_time = "";

    private List<TradingBean> items = null;
    private WithDrawAdapter adapter = null;

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
                .setTitle("提下记录")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
               /* .setTextRight("筛选")
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initTradingDialog();
                    }
                });*/
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
        adapter = new WithDrawAdapter(this, items);
        swipeTarget.setAdapter(adapter);
        getTradingRecord();
        swipeTarget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (items.get(position).getType() == 1) {
                    startActivity(new Intent(WithdrawListActivity.this,
                            TradingRecordDetailActivity.class)
                            .putExtra("order_sn", items.get(position).getOrder_sn())
                            .putExtra("img_res_id", items.get(position).getImgResId()));
                }
            }
        });
    }

    private void getTradingRecord() {
        Api.getTradingRecord(uid, mobile,
                page, 9, start_time, end_time,
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
                            ToastUtils.showShort(WithdrawListActivity.this, apiResponse.getMessage());
                        }
                    }
                });
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
