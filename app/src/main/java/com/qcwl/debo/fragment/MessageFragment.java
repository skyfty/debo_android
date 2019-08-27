package com.qcwl.debo.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.util.NetUtils;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.contact.CreateGroupActivity;
import com.qcwl.debo.ui.contact.add.FindFriendsActivity;
import com.qcwl.debo.ui.my.sign.SignIn2Activity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.BounceTopEnter;
import com.qcwl.debo.view.ShareTopDialog;
import com.qcwl.debo.widget.BaseAnimatorSet;
import com.qcwl.debo.zxing.android.CaptureActivity;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;


/**
 * Created by Administrator on 2017/7/6.
 * 嘚啵模块Fragment
 */

public class MessageFragment extends Fragment implements ShareTopDialog.MyOnclickListener {

    private View view;
    //private TabLayout tabLayout;
    private ContactMessageFragment messageFragment;
    //private RenMaiMessageFragment renMaiFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private EditText query;
    private ImageButton clearSearch;
    private final int PESSION_CODE = 1;
    private ShareTopDialog dialog;
    private BaseAnimatorSet mBasIn;
   // private int flag;
    //private ImageView image1, image2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.message, null);
        initTitleBar();
        initView();
        initFragment();
        return view;
    }

    private void initTitleBar() {
        new TitleBarBuilder(view)
                .setTitle("嘚啵")
                .setImageRightRes(R.mipmap.btn_add)
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBasIn = new BounceTopEnter();
                        dialog = new ShareTopDialog(getActivity());
                        dialog.setType(1);
                        dialog.setMyOnclickListener(MessageFragment.this);
                        dialog.showAnim(mBasIn).show();
                    }
                })
                .setImageLeftRes(R.mipmap.btn_sign)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetUtils.hasNetwork(getActivity())) {
                            ToastUtils.showShort(getActivity(), "网络连接失败");
                            return;
                        }
                        startActivity(new Intent(getActivity(), SignIn2Activity.class));
                    }
                });
    }

    private void initView() {
       /* tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        image1 = (ImageView) view.findViewById(R.id.image1);
        image2 = (ImageView) view.findViewById(R.id.image2);*/
        query = (EditText) view.findViewById(R.id.query);
        clearSearch = (ImageButton) view.findViewById(R.id.search_clear);
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
                if (tab.getPosition() == 0) {
                    flag = 0;
                    ft.show(messageFragment).hide(renMaiFragment);
                } else {
                    flag = 1;
                    ft.show(renMaiFragment).hide(messageFragment);
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
                //if (flag == 0) {
                    if (messageFragment != null) {
                        messageFragment.refreshMessage(s);
                    }

                //Log.i("MessageFragment",".........query");
                /*} else {
                    if (renMaiFragment != null)
                        renMaiFragment.refreshMessage(s);
                }*/
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
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
                if (messageFragment != null)
                    messageFragment.hideSoft();
              /*  if (renMaiFragment != null)
                    renMaiFragment.hideSoft();*/
            }
        });

    }

    private void initFragment() {
        fm = getChildFragmentManager();
        ft = fm.beginTransaction();
        messageFragment = new ContactMessageFragment();
       // renMaiFragment = new RenMaiMessageFragment();
        ft.add(R.id.frame, messageFragment);
       // ft.add(R.id.frame, renMaiFragment);
        ft.show(messageFragment).commit();
    }

    public void refresh() {
       //if (flag == 0) {
            if (messageFragment != null) {
                messageFragment.refresh();
            }
       /* } else {
            if (renMaiFragment != null) {
                renMaiFragment.refresh();
            }
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(PESSION_CODE)
    public void requestPermissionSuccess() {
        startActivity(new Intent(getActivity(), CaptureActivity.class).putExtra("flag", 1).putExtra("tip", "addFriend").putExtra("title", "瞅一瞅"));
    }

    @PermissionDenied(PESSION_CODE)
    public void requestPermissionFailed() {
        Toast.makeText(getActivity(), "权限被禁止，请您去设置界面开启！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(int i) {
        switch (i) {
            case 1:
//                startActivity(new Intent(getActivity(), AddFriendsActivity.class));
                startActivity(new Intent(getActivity(), FindFriendsActivity.class));
                break;
            case 2:
                startActivity(new Intent(getActivity(), CreateGroupActivity.class));
                break;
            case 3:
                String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                MPermissions.requestPermissions(MessageFragment.this, PESSION_CODE, permissions);
                break;
            case 4:
                ToastUtils.showShort(getActivity(), "进入收付款页面");
                break;
            case 5:
                ToastUtils.showShort(getActivity(), "进入帮助页面");
                break;
        }
    }

    /*public void refreshCount(int count1, int count2) {
        if (count1 > 0) {
            image1.setVisibility(View.VISIBLE);
        } else {
            image1.setVisibility(View.GONE);
        }
        if (count2 > 0) {
            image2.setVisibility(View.VISIBLE);
        } else {
            image2.setVisibility(View.GONE);
        }
    }*/
}
