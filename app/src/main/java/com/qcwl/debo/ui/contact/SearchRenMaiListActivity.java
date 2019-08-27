package com.qcwl.debo.ui.contact;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.fragment.AristocracyRenMaiFragment;
import com.qcwl.debo.fragment.OpenRenMaiFragment;
import com.qcwl.debo.utils.TitleBarBuilder;

/**
 * Created by Administrator on 2017/8/23.
 */

public class SearchRenMaiListActivity extends BaseActivity {

    private TabLayout tabLayout;
    private AristocracyRenMaiFragment aristocracyRenMaiFragment;
    private OpenRenMaiFragment openRenMaiFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.search_renmai);
        initTitleBar();
        initView();
        initFragment();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("查找人脉").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.removeAllTabs();
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText("贵族人脉");
            tabLayout.addTab(tab);

            TabLayout.Tab tab1 = tabLayout.newTab();
            tab1.setText("开放人脉");
            tabLayout.addTab(tab1);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ft = fm.beginTransaction();
                if (tab.getPosition() == 0) {
                    ft.show(aristocracyRenMaiFragment).hide(openRenMaiFragment);
                } else {
                    ft.show(openRenMaiFragment).hide(aristocracyRenMaiFragment);
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

    }

    private void initFragment() {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        aristocracyRenMaiFragment = new AristocracyRenMaiFragment();
        openRenMaiFragment = new OpenRenMaiFragment();
        ft.add(R.id.frame, aristocracyRenMaiFragment);
        ft.add(R.id.frame, openRenMaiFragment);
        ft.show(aristocracyRenMaiFragment).hide(openRenMaiFragment).commit();
    }

}
