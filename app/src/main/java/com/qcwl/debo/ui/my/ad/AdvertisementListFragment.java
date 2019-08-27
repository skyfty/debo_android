package com.qcwl.debo.ui.my.ad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AdvertisementListFragment extends Fragment {


    @Bind(R.id.swipe_target)
    ListView listView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.btn_backout)
    Button btnBackout;

    private int page = 1;

    private String uid = "";
    private int ad_type = 0;

    private boolean isReLoad = false;
    private AdvertisementAdapter adapter;
    private List<AdvertisementBean> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_advertisement_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) {
            return;
        }
        uid = SPUtil.getInstance(getActivity()).getString("uid");
        ad_type = getArguments().getInt("ad_type");
        initData();
    }

    private void initData() {
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getAdList();
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
                getAdList();
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
        adapter = new AdvertisementAdapter(getActivity(), items);
        listView.setAdapter(adapter);
        getAdList();
        btnBackout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < adapter.strList.size(); i++) {
                    sb.append(adapter.strList.get(i)).append(",");
                }
                if (TextUtils.isEmpty(sb)) {
                    Toast.makeText(getActivity(), "请选择要撤回的广告", Toast.LENGTH_SHORT).show();
                    return;
                }
                sb.deleteCharAt(sb.length() - 1);
                backoutAdvertisement(sb.toString());
            }
        });
    }

    private void testData() {
        List<AdvertisementBean> list = new ArrayList<>();
        AdvertisementBean bean = null;
        for (int i = 0; i < 10; i++) {
            bean = new AdvertisementBean();
            bean.setTitle("啦啦啦啦啦绿绿绿绿");
            bean.setP_time("12.12");
            bean.setT_id("" + 1);
            list.add(bean);
        }
        items.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void getAdList() {
        Api.getAdvertisementList(uid, ad_type, page, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    ResultBean result = JSON.parseObject(apiResponse.getData(), ResultBean.class);
                    if (result == null || result.getTouch() == null) {
                        return;
                    }
                    if (page == 1 || isReLoad) {
                        items.clear();
                    }
                    items.addAll(result.getTouch());
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(getActivity(), "" + apiResponse.getMessage());
                }
//                if (items.size() > 0) {
//                    btnBackout.setVisibility(View.VISIBLE);
//                } else {
//                    btnBackout.setVisibility(View.GONE);
//                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        swipeToLoadLayout.setRefreshing(false);
//        swipeToLoadLayout.setLoadingMore(false);
        ButterKnife.unbind(this);
    }

    public boolean getCheckBoxVisible() {
        return checkBoxVisible;
    }

    private boolean checkBoxVisible = false;

    public void setCheckBoxVisible(boolean isVisible) {
        swipeToLoadLayout.setRefreshEnabled(!isVisible);
        swipeToLoadLayout.setLoadMoreEnabled(!isVisible);
        this.checkBoxVisible = isVisible;
        for (AdvertisementBean item : items) {
            item.setVisible(isVisible);
            if (isVisible) {
                item.setChecked(false);
            }
        }
        adapter.notifyDataSetChanged();
        if (isVisible && items.size() > 0) {
            btnBackout.setVisibility(View.VISIBLE);
        } else {
            btnBackout.setVisibility(View.GONE);
        }
    }

    private void backoutAdvertisement(String t_id) {
        Api.backoutAdvertisement(uid, t_id, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    isReLoad = true;
                    getAdList();
                } else {
                    ToastUtils.showShort(getActivity(), apiResponse.getMessage());
                }
            }
        });
    }
}
