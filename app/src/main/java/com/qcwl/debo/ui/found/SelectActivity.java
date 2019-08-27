package com.qcwl.debo.ui.found;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
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

public class SelectActivity extends BaseActivity implements SortGroupMemberAdapter.ChangeState, GroupPresenterInf {
    //private TabLayout tabLayout;
    private GroupMemberPicByContactFragment groupMemberPicByContactFragment;
    private GroupMemberPicByRenMaiFragment groupMemberPicByRenMaiFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private EditText query;
    private ImageButton clearSearch;
    private int flag;
    private String member;
    private GroupPresenter groupPresenter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.message);
        initTitleBar();
        initView();
        initFragment();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("选择好友")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void initView() {
       // tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        query = (EditText) findViewById(R.id.query);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        groupPresenter = new GroupPresenter(this);

        /*if (tabLayout != null) {
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
                    groupMemberPicByRenMaiFragment.resetData();
                    ft.show(groupMemberPicByContactFragment).hide(groupMemberPicByRenMaiFragment);
                } else {
                    flag = 1;
                    groupMemberPicByContactFragment.resetData();
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
        });*/

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
        groupMemberPicByContactFragment.setListener(this);
        /*groupMemberPicByRenMaiFragment = new GroupMemberPicByRenMaiFragment();
        groupMemberPicByRenMaiFragment.setListener(this);*/
        ft.add(R.id.frame, groupMemberPicByContactFragment);
        //ft.add(R.id.frame, groupMemberPicByRenMaiFragment);
        ft.show(groupMemberPicByContactFragment)/*.hide(groupMemberPicByRenMaiFragment)*/.commit();
    }

    @Override
    public void getCounts(int count) {
//        if (count == 0) {
//            new TitleBarBuilder(this).setTextRight("OK");
//        } else {
//            new TitleBarBuilder(this).setTextRight("OK( " + count + " )");
//        }
    }

    @Override
    public void getMember(String member) {
        this.member = member;
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            finish();
        }
        ToastUtils.showShort(this, message);
    }

}
