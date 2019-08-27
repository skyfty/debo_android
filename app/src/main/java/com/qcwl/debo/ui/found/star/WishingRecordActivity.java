package com.qcwl.debo.ui.found.star;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.found.RedPacketBean;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WishingRecordActivity extends BaseActivity {

    @Bind(R.id.swipe_target)
    ListView listView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.root)
    View root;

    private int page = 1;
    private List<RedPacketBean> items;
    private WishingRecordAdapter adapter;

    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishing_record);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        root.getBackground().setAlpha(255);
        new TitleBarBuilder(this).setTitle("记录").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        uid = sp.getString("uid");
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getRecordList();
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        swipeToLoadLayout.setOnLoadMoreListener(new com.aspsine.swipetoloadlayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                getRecordList();
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });
        items = new ArrayList<>();
        adapter = new WishingRecordAdapter(this, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WishingRecordActivity.this, ContactsContentActivity.class);
                intent.putExtra("my_mobile", SPUtil.getInstance(WishingRecordActivity.this).getString("phone"));
                intent.putExtra("mobile", items.get(position).getMobile());
                intent.putExtra("type", "" + items.get(position).getJ_type());
                startActivity(intent);
            }
        });

        getRecordList();
    }

    public void getRecordList() {
        Api.getWishingRecord(uid, page, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<RedPacketBean> list = JSON.parseArray(apiResponse.getData(), RedPacketBean.class);
                    if (list == null) {
                        return;
                    }
                    if (page == 1) {
                        items.clear();
                    }
                    items.addAll(list);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(WishingRecordActivity.this, "" + apiResponse.getMessage());
                }
            }
        });
    }
}
