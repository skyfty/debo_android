package com.qcwl.debo.ui.my.remind;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RadioGroup;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.my.ad.MyPagerAdapter;
import com.qcwl.debo.utils.TitleBarBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RemindActivity extends BaseActivity {

    @Bind(R.id.radio_group)
    RadioGroup radioGroup;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }


    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1f)
                .setTitle("消息")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setTextRight("清空")
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmClearDialog();
                    }
                });
    }

    private void confirmClearDialog() {
        String msg = "";
        if (viewPager.getCurrentItem() == 0) {
            msg = "评论";
        } else if (viewPager.getCurrentItem() == 1) {
            msg = "点赞";
        }
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您确认清空" + msg + "数据吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((RemindFragment) (fragments.get(viewPager.getCurrentItem()))).clearRemind();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void initData() {
        fragments = new ArrayList<>();
        RemindFragment fragment = null;
        Bundle bundle = null;
        for (int i = 1; i <= 2; i++) {
            fragment = new RemindFragment();
            bundle = new Bundle();
            bundle.putInt("type", i);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragments));
        radioGroup.check(R.id.raido_comment);
        viewPager.setCurrentItem(0);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.raido_comment:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.raido_praise:
                        viewPager.setCurrentItem(1);
                        break;
                    default:
                        break;
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        radioGroup.check(R.id.raido_comment);
                        break;
                    case 1:
                        radioGroup.check(R.id.raido_praise);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
