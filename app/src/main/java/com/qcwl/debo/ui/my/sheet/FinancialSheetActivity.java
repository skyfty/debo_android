package com.qcwl.debo.ui.my.sheet;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.baidu.mobstat.StatService;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FinancialSheetActivity extends BaseActivity {

    @Bind(R.id.swipe_target)
    ListView listView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private int page = 1;

    private List<SheetBean> items;

    private SheetAdapter adapter;

    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_sheet);
        ButterKnife.bind(this);
        initTitleBar();

        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle("财务报表")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void initData() {
        uid = sp.getString("uid");
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getSheetList();
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnLoadMoreListener(new com.aspsine.swipetoloadlayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                getSheetList();
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });
        View view = new View(this);
        view.setLayoutParams(new AbsListView.LayoutParams(-1, ScreenUtils.dp2px(this, 10)));
        listView.addHeaderView(view);
        items = new ArrayList<>();
        adapter = new SheetAdapter(this, items);
        listView.setAdapter(adapter);

        getSheetList();
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this,"启动财务报表页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this,"结束财务报表页面");
    }
    private void getSheetList() {
        Api.getSheetList("" + uid, page, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<SheetBean> list = JSON.parseArray(apiResponse.getData(), SheetBean.class);
                    if (list == null) {
                        return;
                    }
                    if (page == 1) {
                        items.clear();
                    }
                    items.addAll(list);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(FinancialSheetActivity.this, "" + apiResponse.getMessage());
                }
            }
        });
    }
}
