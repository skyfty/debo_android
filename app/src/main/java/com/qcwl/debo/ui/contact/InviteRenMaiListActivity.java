package com.qcwl.debo.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.AutoSwipRefreshLayout;
import com.qcwl.debo.widget.sortlistview.SideBar;
import com.qcwl.debo.widget.sortlistview.SortGroupMemberAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */

public class InviteRenMaiListActivity extends BaseActivity implements ContactListPresenterInf {
    private EditText query;
    private ImageButton clearSearch;
    private ListView listView;
    private ContactListPresenter contactListPresenter;
    private List<ContactsBean> list = new ArrayList<>();
    private AutoSwipRefreshLayout refresh_layout;
    private SortGroupMemberAdapter adapter;
    private SideBar sideBar;
    private TextView dialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.renmai_list_activity);
        initTitleBar();
        initView();
        monitor();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("邀请人脉").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                finish();
            }
        });
    }

    private void initView() {
        contactListPresenter = new ContactListPresenter(this);
        query = (EditText) findViewById(R.id.query);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        listView = (ListView) findViewById(R.id.listView);

        refresh_layout = (AutoSwipRefreshLayout) findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.holo_list_light);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contactListPresenter.getReMai(InviteRenMaiListActivity.this, sp.getString("phone"), "2", list);
            }
        });

        adapter = new SortGroupMemberAdapter(this, list,"");
        listView.setAdapter(adapter);

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        contactListPresenter.getReMai(this, sp.getString("phone"), "2", list);
    }

    private void monitor() {
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
                contactListPresenter.filterContactList(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(InviteRenMaiListActivity.this, ChatActivity.class).putExtra("userId",adapter.getList().get(position).getMobile()).putExtra("nickname",adapter.getList().get(position).getUser_nickname()).putExtra("type","2"));
            }
        });
    }


    @Override
    public void getResult(int code, String message, Object o) {
        refresh_layout.setRefreshing(false);
        if (code == 0) {
            adapter.notifyDataSetChanged();
        } else {
            list.clear();
            adapter.notifyDataSetChanged();
            ToastUtils.showShort(this, message);
        }
    }

    @Override
    public void updateDataByFilter(List<ContactsBean> filterDateList) {
        if (adapter != null)
            adapter.updateListView(filterDateList);
    }
}
