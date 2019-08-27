package com.qcwl.debo.ui.my.remind;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.AlbumDetailActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemindFragment extends Fragment {


    @Bind(R.id.swipe_target)
    ListView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private int page = 1;
    private int type = 0;
    private String uid = "";

    private List<RemindBean> items;
    private RemindAdapter adapter;

    public RemindFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remind, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        swipeToLoadLayout.setRefreshEnabled(false);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnLoadMoreListener(new com.aspsine.swipetoloadlayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        historyMessage();
                        if (swipeToLoadLayout == null) {
                            return;
                        }
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });

        if (getArguments() == null) {
            return;
        }
        type = getArguments().getInt("type");
        uid = SPUtil.getInstance(getActivity()).getString("uid");

        items = new ArrayList<>();
        adapter = new RemindAdapter(type, getActivity(), items);
        swipeTarget.setAdapter(adapter);
        historyMessage();
        swipeTarget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1、朋友圈；2、人脉圈；3、粉丝圈
                Intent intent = new Intent();
                switch (items.get(position).getCircle_type()) {
                    case 1:
                    case 2:
                        intent.setClass(getActivity(), AlbumDetailActivity.class);
                        intent.putExtra("type", items.get(position).getCircle_type());
                        intent.putExtra("moments_id", items.get(position).getMoments_id());
                        break;
                    case 3:
                        //intent.setClass(getActivity(), FansDynamicDetailActivity.class);//需要传javabean
                        break;
                    default:
                        break;
                }
                startActivity(intent);
            }
        });
    }

    private void historyMessage() {
        Api.historyMessage(type, uid, page,
                new ApiResponseHandler(getActivity()) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            List<RemindBean> list = JSON.parseArray(apiResponse.getData(), RemindBean.class);
                            if (list != null && list.size() > 0) {
                                for (RemindBean bean : list) {
                                    if (bean.getCircle_type() == 1 || bean.getCircle_type() == 2) {
                                        items.add(bean);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtils.showShort(getActivity(), apiResponse.getMessage());
                        }
                    }
                });
    }

    public void clearRemind() {
        if (items == null || items.size() == 0) {
            return;
        }
        Api.clearRemind(uid, type, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    items.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(getActivity(), apiResponse.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
