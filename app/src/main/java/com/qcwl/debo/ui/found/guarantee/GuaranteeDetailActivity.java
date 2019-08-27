package com.qcwl.debo.ui.found.guarantee;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.found.guarantee.contract.GuaranteeContract;
import com.qcwl.debo.ui.found.guarantee.presenter.GuaranteePresenter;
import com.qcwl.debo.utils.TitleBarBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GuaranteeDetailActivity extends BaseActivity implements GuaranteeContract.View {

    @Bind(R.id.swipe_target)
    ListView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private int page = 1;
    private String uid = "";

    private List<GuaranteeDetailBean> items = null;
    private GuaranteeDetailAdapter adapter = null;

    private GuaranteePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarantee_detail);
        ButterKnife.bind(this);
        initTitleBar();

        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("担保明细")
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
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
//                swipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.);
                page = 1;
                presenter.getGuaranteeDetailList(GuaranteeDetailActivity.this, uid, page);
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                presenter.getGuaranteeDetailList(GuaranteeDetailActivity.this, uid, page);
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });

        items = new ArrayList<>();
        adapter = new GuaranteeDetailAdapter(this, items);
        swipeTarget.setAdapter(adapter);

        presenter = new GuaranteePresenter(this);
        presenter.getGuaranteeDetailList(this, uid, page);
    }

    private List<GuaranteeDetailBean> list = null;
    private boolean isRemoveCurrentPageDate = false;

    @Override
    public void doSuccess(int type, Object object) {
        if (type == GuaranteePresenter.TYPE_DETAIL_LIST) {
            if (list != null && isRemoveCurrentPageDate) {
                items.removeAll(list);
            }
            list = (List<GuaranteeDetailBean>) object;
            if (page == 1) {
                items.clear();
            }
            if (list != null) {
                items.addAll(list);
            }
            adapter.notifyDataSetChanged();
            isRemoveCurrentPageDate = false;
        } else if (type == GuaranteePresenter.TYPE_CONFIRM_DETAIL) {
            //请求当前页面数据
            isRemoveCurrentPageDate = true;
            presenter.getGuaranteeDetailList(this, uid, page);
        }
    }

    @Override
    public void doFailure(int code) {

    }

    public void confirmGuaranteeDetail(int state, String tri_id, int request_type) {
        presenter.confirmGuaranteeDetail(this, uid, state, tri_id, request_type);
    }
}
