package com.qcwl.debo.ui.my.ad;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.utils.TitleBarBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AdvertisementListActivity extends BaseActivity {

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_list);
        ButterKnife.bind(this);
        initTitleBar();

        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("发出的广告")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setTextRight("编辑")
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = tabLayout.getSelectedTabPosition();
                        AdvertisementListFragment fragment = ((AdvertisementListFragment) (fragments.get(position)));
                        if (!fragment.getCheckBoxVisible()) {
                            fragment.setCheckBoxVisible(true);
                        } else {
                            fragment.setCheckBoxVisible(false);
                        }
                    }
                });
    }

    private void initData() {
        fragments = new ArrayList<>();
        AdvertisementListFragment fragment = null;
        Bundle bundle = null;
        for (int i = 1; i <= 3; i++) {
            fragment = new AdvertisementListFragment();
            bundle = new Bundle();
            bundle.putInt("ad_type", i);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("碰一碰");
        tabLayout.getTabAt(1).setText("撞一撞");
        tabLayout.getTabAt(2).setText("许愿星");
    }
}
