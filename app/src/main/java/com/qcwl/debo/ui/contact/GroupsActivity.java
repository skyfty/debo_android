package com.qcwl.debo.ui.contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.qcwl.debo.R;
import com.qcwl.debo.adapter.GroupAdapter;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.model.GroupBean;
import com.qcwl.debo.presenter.GroupPresenter;
import com.qcwl.debo.presenterInf.GroupPresenterInf;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.widget.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */

public class GroupsActivity extends BaseActivity implements GroupPresenterInf {
    private ListView groupListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GroupPresenter groupPresenter;
    private List<GroupBean> list;
    private GroupAdapter adapter;
    private MyBrocastRecevier myBrocastRecevier;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_fragment_groups);
        initTitleBar();
        initView();
        monitor();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("群组").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                finish();
            }
        });
    }

    private void initView() {
        groupPresenter = new GroupPresenter(this);
        list = new ArrayList<>();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        groupListView = (ListView) findViewById(R.id.list);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_list_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                groupPresenter.getGroup(GroupsActivity.this, sp.getString("phone"));
            }
        });

        adapter = new GroupAdapter(this, list);
        groupListView.setAdapter(adapter);

        myBrocastRecevier = new MyBrocastRecevier();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.debo.contact"); // 添加要收到的广播
        registerReceiver(myBrocastRecevier, intentFilter);
    }


    private void monitor() {
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < adapter.getList().size()) {
                    // enter group chat
                    Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
                    // it is group chat
                    intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                    intent.putExtra("userId", adapter.getList().get(position).getGroupid());
                    intent.putExtra("type", adapter.getList().get(position).getQroup_type());
                    intent.putExtra("is_show_name", adapter.getList().get(position).getIs_show_name());
                    startActivityForResult(intent, 0);
                }
            }
        });
        groupListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        groupPresenter.getGroup(this, sp.getString("phone"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBrocastRecevier);
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            List<GroupBean> groupBeanList = (List<GroupBean>) o;
            if (adapter != null)
                adapter.setList(groupBeanList);
        } else {
            if (adapter != null)
                adapter.setList(list);
            ToastUtils.showShort(this, message);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public class MyBrocastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            groupPresenter.getGroup(GroupsActivity.this, sp.getString("phone"));
        }

    }

}
