package com.qcwl.debo.ui.my.partner.sale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.qcwl.debo.ui.my.partner.shop.CoinsDetailActivity;
import com.qcwl.debo.ui.my.partner.shop.CoinsShopBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CoinsSaleActivity extends BaseActivity {

    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.swipe_target)
    ListView listView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.text_right)
    TextView textRight;
    @Bind(R.id.btn_publish)
    Button btnPublish;

    private int page = 1;

    private List<CoinsShopBean> items;
    private CoinsSaleAdapter adapter;

    private int status = 0;//	1、上架中；2、已出售

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coins_sale);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            status = getIntent().getIntExtra("status", 0);
            if (status == 1) {
                textTitle.setText("卖出");
                textRight.setText("卖出记录");
                btnPublish.setVisibility(View.VISIBLE);
            } else if (status == 2) {
                textTitle.setText("卖出记录");
                textRight.setText("清空记录");
                btnPublish.setVisibility(View.GONE);
            }
        }
        textRight.setVisibility(View.VISIBLE);
        initView();
        items = new ArrayList<>();
        adapter = new CoinsSaleAdapter(this, items);
        listView.setAdapter(adapter);
        if (status == 1) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        startActivity(new Intent(CoinsSaleActivity.this, CoinsDetailActivity.class)
                                .putExtra("bean", items.get(position)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void statusBarSetting() {
        //
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
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
        Api.getCoinsShopList(sp.getString("uid"), status, page, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<CoinsShopBean> list = JSON.parseArray(apiResponse.getData(), CoinsShopBean.class);
                    if (page == 1) {
                        items.clear();
                    }
                    if (list != null) {
                        items.addAll(list);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CoinsSaleActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick({R.id.left_image, R.id.text_right, R.id.btn_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.text_right:
                if (status == 1) {
                    startActivity(new Intent(this, CoinsSaleActivity.class).putExtra("status", 2));
                } else if (status == 2) {
                    soldOut(-1);
                }
                break;
            case R.id.btn_publish:
                startActivity(new Intent(this, PublishSaleActivity.class));
                break;
        }
    }

    public void soldOut(final int position) {
        String debo_id = "";
        if (position >= 0) {
            debo_id = items.get(position).getId();
        } else {
            debo_id = "";
        }
        Api.soldOutCoins(sp.getString("uid"), sp.getString("phone"), debo_id,
                new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            if (status == 1) {
                                items.remove(position);
                                adapter.notifyDataSetChanged();
                            } else if (status == 2) {
                                finish();
                            }
                        } else {
                            Toast.makeText(CoinsSaleActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
