package com.qcwl.debo.ui.my.wallet.storage;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
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
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.utils.RAS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TotalIncomeActivity extends BaseActivity {

    @Bind(R.id.root)
    View titleView;
    @Bind(R.id.text_total_income)
    TextView textTotalIncome;
    @Bind(R.id.swipe_target)
    ListView listView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.text_right)
    TextView textRight;

    private int page = 1;
    private IncomeAdapter adapter;
    private List<IncomeBean> items;

    private Map<String, String> map;

    private int height = 0;

    private int type = 0;//1-->存储罐，2-->合伙人

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_income);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            type = getIntent().getIntExtra("type", 0);
        }
        initData();
        height = ScreenUtils.getScreenHeight(this) - ScreenUtils.dp2px(this, 70);
    }

    @Override
    public void statusBarSetting() {
        //重写，不需要设置沉浸式
    }

    private void initData() {
        textTitle.setText("累计收益");
        if (type == 1) {
            textRight.setText("筛选");
            textRight.setVisibility(View.VISIBLE);
        }
        map = new HashMap<>();
        //map.put("type", "" + type);
        map.put("uid", RAS.getPublicKeyStrRAS(sp.getString("uid").getBytes()));
        map.put("mobile", RAS.getPublicKeyStrRAS(sp.getString("phone").getBytes()));
       /* map.put("is_month", "");
        map.put("is_year", "");*/
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                map.put("page", "" + page);
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
                map.put("page", "" + page);
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
        items = new ArrayList<>();
        adapter = new IncomeAdapter(this, items,type);
        listView.setAdapter(adapter);
        loadData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private IncomeResultBean resultBean = null;

    private void loadData() {
        Api.getIncomeList(map, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    resultBean = JSON.parseObject(apiResponse.getData(), IncomeResultBean.class);
                    if (resultBean == null) {
                        return;
                    }
                    textTotalIncome.setText("" + resultBean.getTotal_income());
                    if (page == 1) {
                        items.clear();
                    }
                    items.addAll(resultBean.getIncome_list());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(TotalIncomeActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private PopupWindow popup = null;
    private RadioGroup radioGroup = null;

    private void initPopup() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_popup_income, null);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_total:
                        break;
                    case R.id.radio_month:
                        map.put("is_month", "1");
                        break;
                    case R.id.radio_year:
                        map.put("is_year", "1");
                        break;
                    default:
                        break;
                }
                page = 1;
                loadData();
                popup.dismiss();
            }
        });
        view.findViewById(R.id.view_blank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        popup = new PopupWindow(view, -1, -1);
        popup.setBackgroundDrawable(new ColorDrawable(0x80000000));
        popup.setTouchable(true);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
    }

//    private void setBgAlpha(float alpha){
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = alpha;
//        lp.width=-1;
//        lp.height=height;
//        getWindow().setAttributes(lp);
//    }

    @OnClick({R.id.left_image, R.id.text_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.text_right:
                if (popup == null) {
                    initPopup();
                }
                showPopup(titleView);
                break;
        }
    }

    private void showPopup(View view) {
        if (Build.VERSION.SDK_INT < 24) {
            popup.showAsDropDown(view);
        } else {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            popup.showAtLocation(view, Gravity.NO_GRAVITY, 0, y + view.getHeight());
        }
    }
}
