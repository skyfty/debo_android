package com.qcwl.debo.ui.contact;

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
import com.qcwl.debo.fragment.CardByContactFragment;
import com.qcwl.debo.fragment.CardByRenMaiFragment;
import com.qcwl.debo.utils.TitleBarBuilder;



/**
 * Created by Administrator on 2017/5/11.
 */

public class ContactCardActivity extends BaseActivity {
    private TabLayout tabLayout;
    private CardByContactFragment cardByContactFragment;
    private CardByRenMaiFragment cardByRenMaiFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private EditText query;
    private ImageButton clearSearch;
    private int flag;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.contact_card);
        initTitleBar();
        initView();
        initFragment();
    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        query = (EditText) findViewById(R.id.query);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
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
                    ft.show(cardByContactFragment).hide(cardByRenMaiFragment);
                } else {
                    flag = 1;
                    ft = fm.beginTransaction();
                    ft.show(cardByRenMaiFragment).hide(cardByContactFragment);
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
                    if (cardByContactFragment != null && s != null)
                        cardByContactFragment.filterData(s.toString());
                } else if (flag == 1) {
                    if (cardByRenMaiFragment != null && s != null)
                        cardByRenMaiFragment.filterData(s.toString());
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
        cardByContactFragment = new CardByContactFragment();
        cardByRenMaiFragment = new CardByRenMaiFragment();
        ft.add(R.id.frame, cardByContactFragment);
        ft.add(R.id.frame, cardByRenMaiFragment);
        ft.show(cardByContactFragment).hide(cardByRenMaiFragment).commit();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("选择名片").setAlpha(1).setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
