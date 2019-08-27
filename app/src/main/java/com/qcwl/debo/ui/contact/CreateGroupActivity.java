package com.qcwl.debo.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.fragment.GroupMemberPicByContactFragment;
import com.qcwl.debo.fragment.GroupMemberPicByRenMaiFragment;
import com.qcwl.debo.presenter.GroupPresenter;
import com.qcwl.debo.presenterInf.GroupPresenterInf;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.widget.sortlistview.SortGroupMemberAdapter;

/**
 * Created by Administrator on 2017/8/2.
 */

public class CreateGroupActivity extends BaseActivity implements SortGroupMemberAdapter.ChangeState, GroupPresenterInf ,SortGroupMemberAdapter.ChangeStateWhere {
    private TabLayout tabLayout;
    private GroupMemberPicByContactFragment groupMemberPicByContactFragment;
    private GroupMemberPicByRenMaiFragment groupMemberPicByRenMaiFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private EditText query;
    private ImageButton clearSearch;
    private int flag;
    private String member;
    private GroupPresenter groupPresenter;
    private String type;
    String tittle = "创建群聊";
    private int renMaiCount = 0;
    private int contactCount = 0;
    private String renMaiIds = "";
    private String contactIds = "";
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_create_group);
        type = getIntent().getStringExtra("type");
        if (type!=null){
            tittle = "发布动态";
        }else{
            type = "";
        }
        initTitleBar();
        initView();
        initFragment();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle(tittle).setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).setTextRight("确定").setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type!= null&&type.equals("publishcircle")||type.equals("wherecircle")){
                    Intent intent = new Intent();
                    intent.putExtra("renMaiIds",renMaiIds);
                    intent.putExtra("contactIds",contactIds);
                    setResult(103,intent);
                    finish();
                    return;
                }
                String str = "";
                if (TextUtils.isEmpty(sp.getString("name"))) {
                    str = sp.getString("phone")+"创建的群";
                } else {
                    str = sp.getString("name")+"创建的群";
                }
                groupPresenter.createGroup(CreateGroupActivity.this, str, "", sp.getString("phone"), member, flag + 1);
            }
        });
    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        query = (EditText) findViewById(R.id.query);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        groupPresenter = new GroupPresenter(this);

        if (tabLayout != null) {
            tabLayout.removeAllTabs();
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText("好友");
            tabLayout.addTab(tab);

            TabLayout.Tab tab1 = tabLayout.newTab();
            tab1.setText("人脉");
            tabLayout.addTab(tab1);
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ft = fm.beginTransaction();
                query.getText().clear();
                if (tab.getPosition() == 0) {
                    flag = 0;
                    if (!type.equals("publishcircle")&&!type.equals("wherecircle")) {
                        groupMemberPicByRenMaiFragment.resetData();
                    }
                    ft.show(groupMemberPicByContactFragment).hide(groupMemberPicByRenMaiFragment);
                } else {
                    flag = 1;
                    if (!type.equals("publishcircle")&&!type.equals("wherecircle")) {
                        groupMemberPicByContactFragment.resetData();
                    }
                    ft.show(groupMemberPicByRenMaiFragment).hide(groupMemberPicByContactFragment);
                }
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
                if (flag == 0) {
                    if (groupMemberPicByContactFragment != null && s != null)
                        groupMemberPicByContactFragment.filterData(s.toString());
                } else if (flag == 1) {
                    if (groupMemberPicByRenMaiFragment != null && s != null)
                        groupMemberPicByRenMaiFragment.filterData(s.toString());
                }
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

    }

    private void initFragment() {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        groupMemberPicByContactFragment = new GroupMemberPicByContactFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        groupMemberPicByContactFragment.setArguments(bundle);
        groupMemberPicByContactFragment.setListener(this);
        groupMemberPicByContactFragment.setListener2(this);
        groupMemberPicByRenMaiFragment = new GroupMemberPicByRenMaiFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("type",type);
        groupMemberPicByRenMaiFragment.setArguments(bundle2);
        groupMemberPicByRenMaiFragment.setListener(this);
        groupMemberPicByRenMaiFragment.setListener2(this);
        ft.add(R.id.frame, groupMemberPicByContactFragment);
        ft.add(R.id.frame, groupMemberPicByRenMaiFragment);
        ft.show(groupMemberPicByContactFragment).hide(groupMemberPicByRenMaiFragment).commit();
    }

    @Override
    public void getCounts(int count) {
        if (count == 0) {
            new TitleBarBuilder(this).setTextRight("确定");
        } else {
            new TitleBarBuilder(this).setTextRight("确定( " + count + " )");
        }
    }

    @Override
    public void getMember(String member) {
        this.member = member;
        Log.i("CreateGroupActivity","............="+member);
    }

    @Override
    public void getResult(int code, String message, Object o) {

        if (code == 0) {
            finish();
        }
        ToastUtils.showShort(this, message);
    }

    @Override
    public void getWhereCounts(String key, int count) {
        if (key.equals("renmai")){
            renMaiCount = count;
        }else if(key.equals("contact")){
            contactCount = count;
        }
        count = renMaiCount+contactCount;
        if (count == 0) {
            new TitleBarBuilder(this).setTextRight("确定");
        } else {
            new TitleBarBuilder(this).setTextRight("确定( " + count + " )");
        }

    }

    @Override
    public void getWhereMember(String key, String member) {
        if (key.equals("renmai")){
            renMaiIds = member;
        }else if(key.equals("contact")){
            contactIds = member;
        }
    }
}
