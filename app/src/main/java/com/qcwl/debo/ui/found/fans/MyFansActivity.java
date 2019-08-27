package com.qcwl.debo.ui.found.fans;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.baidu.mobstat.StatService;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.fans.adapter.FansHomeAdapter;
import com.qcwl.debo.ui.found.fans.bean.FansHomeBean;
import com.qcwl.debo.ui.found.fans.bean.FansHomeDataBean;
import com.qcwl.debo.ui.found.fans.contract.FansContract;
import com.qcwl.debo.ui.found.fans.presenter.FansPresenter;
import com.qcwl.debo.utils.TitleBarBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyFansActivity extends BaseActivity implements View.OnClickListener, FansContract.View {

    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.swipe_target)
    RecyclerView recyclerView;
    @Bind(R.id.img_input)
    ImageView imgInput;
    @Bind(R.id.layout_fans)
    View layoutFans;
    @Bind(R.id.text_fans_dynamic)
    TextView textFansDynamic;
    @Bind(R.id.layout_fans_manage)
    View layoutFansManage;
    @Bind(R.id.text_fans_num)
    TextView textFansNum;
    //    @Bind(R.id.edit_input)
//    EditText editInput;
    @Bind(R.id.layout_info)
    LinearLayout layoutInfo;
    @Bind(R.id.layout_yesterday_income)
    LinearLayout layoutBottom;

    private FansPresenter presenter;

    private String uid = "";
    private String nickname = "";
    private String mobile = "";
    private int page = 1;

    private List<FansHomeBean> items;
    private FansHomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fans);
        ButterKnife.bind(this);
//        AndroidSoftEditUtils.init(this);
        initTitleBar();
        initView();
        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("我的粉丝").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        imgInput.setOnClickListener(this);
        layoutFans.setOnClickListener(this);
        textFansDynamic.setOnClickListener(this);
        layoutFansManage.setOnClickListener(this);
        textFansNum.setOnClickListener(this);
        layoutInfo.setVisibility(View.VISIBLE);
//        editInput.setVisibility(View.GONE);
        swipeToLoadLayout.setRefreshEnabled(false);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnLoadMoreListener(new com.aspsine.swipetoloadlayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                presenter.requestFansHomeList(MyFansActivity.this, "" + uid, page);
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });
    }

    private void initData() {
        items = new ArrayList<>();
        try {
            adapter = new FansHomeAdapter(this, items);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new MyItemDecoration(0, ScreenUtils.dp2px(this, 10)));
            recyclerView.setAdapter(adapter);
            if (getIntent() == null) {
                return;
            }
            uid = getIntent().getStringExtra("uid");
            if (uid.equals(sp.getString("uid"))) {
                layoutBottom.setVisibility(View.VISIBLE);
                textFansNum.setVisibility(View.VISIBLE);
            } else {
                layoutBottom.setVisibility(View.GONE);
                textFansNum.setVisibility(View.GONE);
            }
            presenter = new FansPresenter(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this,"启动我的粉丝页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this,"结束我的粉丝页面");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.requestFansHomeList(this, "" + uid, page);
        }
        StatService.onResume(this);
    }

    private PopupWindow myFansPop, fansManagePop;

    private void initMyFansPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_fans, null);
        myFansPop = new PopupWindow(dp2px(100), dp2px(50));
        myFansPop.setContentView(view);
        myFansPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        myFansPop.setFocusable(true);
        myFansPop.setTouchable(true);
        myFansPop.setOutsideTouchable(true);
        view.findViewById(R.id.fans_focus).setOnClickListener(this);
        view.findViewById(R.id.fans_message).setOnClickListener(this);
    }

    private void initFansManagePop() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_fans_manage, null);
        fansManagePop = new PopupWindow(dp2px(100), -2);
        fansManagePop.setContentView(view);
        fansManagePop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        fansManagePop.setFocusable(true);
        fansManagePop.setTouchable(true);
        fansManagePop.setOutsideTouchable(true);
        view.findViewById(R.id.group_send_message).setOnClickListener(this);
        view.findViewById(R.id.group_send_dynamic).setOnClickListener(this);
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(this, dpVal);
    }


//                if (layoutInfo.getVisibility() == View.VISIBLE) {
//                    layoutInfo.setVisibility(View.GONE);
//                    editInput.setVisibility(View.VISIBLE);
//                } else {
//                    layoutInfo.setVisibility(View.VISIBLE);
//                    editInput.setVisibility(View.GONE);
//                }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_input://底部-输入框切换按钮
                //调到聊天页面
                startActivity(new Intent(this, ChatActivity.class).putExtra("userId", mobile).putExtra("nickname", nickname));
                break;
            case R.id.layout_fans://底部-我的粉丝
                if (myFansPop == null) {
                    initMyFansPop();
                }
                myFansPop.showAsDropDown(layoutFans, dp2px(10), dp2px(-110));
                break;
            case R.id.text_fans_dynamic://底部-粉丝动态
                startActivity(new Intent(this, FansDynamicActivity.class));
                break;
            case R.id.layout_fans_manage://底部-粉丝管理
                if (fansManagePop == null) {
                    initFansManagePop();
                }
                fansManagePop.showAsDropDown(layoutFansManage, dp2px(10), dp2px(-160));
                break;
            case R.id.fans_focus://关注的粉丝
                if (myFansPop != null && myFansPop.isShowing()) {
                    myFansPop.dismiss();
                }
                startActivity(new Intent(this, FansListActivity.class).putExtra("fans_type", 2));//type=1表示关注的粉丝列表
                break;
            case R.id.fans_message://粉丝消息
                if (myFansPop != null && myFansPop.isShowing()) {
                    myFansPop.dismiss();
                }
                startActivity(new Intent(this, FansMessageActivity.class));
                break;
            case R.id.group_send_message://群发消息
                if (fansManagePop != null && fansManagePop.isShowing()) {
                    fansManagePop.dismiss();
                }
                startActivity(new Intent(this, GroupSendMessageActivity.class));
                break;
            case R.id.group_send_dynamic://群发动态
                if (fansManagePop != null && fansManagePop.isShowing()) {
                    fansManagePop.dismiss();
                }
                startActivity(new Intent(this, GroupSendDynamicActivity.class));
                break;
            case R.id.text_fans_num:
                startActivity(new Intent(this, FansListActivity.class).putExtra("fans_type", 1));//type=1表示自己的粉丝列表
                break;
            default:
                break;
        }
    }

    @Override
    public void doSuccess(int type, Object object) {
        if (object instanceof FansHomeDataBean) {
            FansHomeDataBean bean = (FansHomeDataBean) object;
            if (items != null && bean != null && bean.getTrends_list() != null) {
                nickname = bean.getUser_nickname();
                mobile = bean.getMobile();
                textFansNum.setText("粉丝" + bean.getFans_num() + "人");
                if (page == 1) {
                    items.clear();
                }
                items.addAll(bean.getTrends_list());
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void doFailure(int code) {

    }
}
