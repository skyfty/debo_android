package com.qcwl.debo.ui.my.partner.record;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CoinsTradingRecordActivity extends BaseActivity {

    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.swipe_target)
    ListView listView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.text_right)
    TextView textRight;

    private int page = 1;

    private List<CoinsTradingRecordBean> items;
    private CoinsTradingRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coins_trading_record);
        ButterKnife.bind(this);
        textTitle.setText("交易记录");
        textRight.setVisibility(View.VISIBLE);
        textRight.setText("清空记录");
        initView();
        items = new ArrayList<>();
        adapter = new CoinsTradingRecordAdapter(this, items);
        listView.setAdapter(adapter);
        loadData();
    }

    @Override
    public void statusBarSetting() {
        //
    }

    private void initView() {
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
//                swipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.);
                page = 1;
                loadData();
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
                loadData();
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
    }

    private void loadData() {
        Api.getCoinsTradingRecord(sp.getString("uid"), sp.getString("phone"), page, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<CoinsTradingRecordBean> list = JSON.parseArray(apiResponse.getData(), CoinsTradingRecordBean.class);
                    if (page == 1) {
                        items.clear();
                    }
                    if (list != null) {
                        items.addAll(list);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CoinsTradingRecordActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteDialog(final int position) {
        String message = "";
        if (position >= 0) {
            message = "您确定要删除该条记录吗？";
        } else {
            message = "您确定要清空交易记录吗？";
        }
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRecord(position);
                    }
                }).create().show();
    }

    private void deleteRecord(final int position) {
        String debo_id = "";
        if (position >= 0) {
            debo_id = items.get(position).getId();
        } else {
            debo_id = "";
        }
        Api.deleteCoinsTradingRecord(sp.getString("uid"), sp.getString("phone"), debo_id, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    if (position >= 0) {
                        items.remove(position);
                        adapter.notifyDataSetChanged();
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(CoinsTradingRecordActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick({R.id.left_image, R.id.text_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.text_right:
                deleteDialog(-1);
                break;
        }
    }
}
