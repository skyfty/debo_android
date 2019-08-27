package com.qcwl.debo.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.adapter.RedPacketRecordAdapter1;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.GetRedPacketRecordBean;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.BottomAnimDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 红包记录
 * Created by qcwl on 2017/10/26.
 */

public class RedPacketRecorderActivity extends BaseActivity {
    private int page = 1;
    private String type = "";
    private RoundedImageView icon_head;
    private TextView tv_name;
    private TextView tv_liuyan;
    private GetRedPacketRecordBean getRedPacketRecordBean = new GetRedPacketRecordBean();
    private List<GetRedPacketRecordBean.ListsBean> mDatas;
    private RedPacketRecordAdapter1 adapter;
    private BottomAnimDialog dialog;

    ListView listView;
    SwipeToLoadLayout swipeToLoadLayout;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.redpacketrecord_layout);
        initTitleBar();
        initView();
        getRedPacketRecord("2", 1);
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("我的红包记录")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setImageRightRes(R.mipmap.tag1)
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog = new BottomAnimDialog(RedPacketRecorderActivity.this, "我收到的红包", "我发出的红包", "取消");
                        dialog.setClickListener(new BottomAnimDialog.BottonAnimDialogListener() {
                            @Override
                            public void onItem1Listener() {
                                type = "2";
                                getRedPacketRecord(type, page);
                                dialog.dismiss();
                            }

                            @Override
                            public void onItem2Listener() {
                                type = "1";
                                getRedPacketRecord(type, page);
                                dialog.dismiss();
                            }

                            @Override
                            public void onItem3Listener() {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
    }

    private void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        listView = (ListView) findViewById(R.id.swipe_target);
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getRedPacketRecord("1", page);
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
                getRedPacketRecord("1", page);
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

        initHeaderView();

        mDatas = new ArrayList<>();
        adapter = new RedPacketRecordAdapter1(this, mDatas);
        listView.setAdapter(adapter);

    }

    private void initHeaderView() {
        View view = LayoutInflater.from(this).inflate(R.layout.redpacketrecord_head, null, false);
        icon_head = (RoundedImageView) view.findViewById(R.id.icon_head);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_liuyan = (TextView) view.findViewById(R.id.tv_liuyan);
        listView.addHeaderView(view);
    }

    //请求红包数据
    private void getRedPacketRecord(String type, final int page) {
        Api.getRedPacketRecord(sp.getString("uid"), type, page, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<GetRedPacketRecordBean> list = JSON.parseArray(apiResponse.getData(), GetRedPacketRecordBean.class);
                    getRedPacketRecordBean = list.get(0);
                    ImgUtil.load(RedPacketRecorderActivity.this, getRedPacketRecordBean.getInfo().getAvatar(), icon_head);
                    tv_name.setText(getRedPacketRecordBean.getInfo().getUser_nickname());
                    tv_liuyan.setText(getRedPacketRecordBean.getInfo().getTotal_get());

                    List<GetRedPacketRecordBean.ListsBean> listsBeen = getRedPacketRecordBean.getLists();
                    if (page == 1) {
                        mDatas.clear();
                    }
                    mDatas.addAll(listsBeen);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(RedPacketRecorderActivity.this, apiResponse.getMessage());
                }
            }
        });
    }
}
