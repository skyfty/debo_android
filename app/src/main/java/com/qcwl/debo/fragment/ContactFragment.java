package com.qcwl.debo.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.contact.add.FindFriendsActivity;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ShareUtil;
import com.qcwl.debo.view.BounceTopEnter;
import com.qcwl.debo.view.ShareTopDialog;
import com.qcwl.debo.widget.BaseAnimatorSet;
import com.qcwl.debo.zxing.android.CaptureActivity;

/**
 * Created by Administrator on 2017/7/6.
 */

public class ContactFragment extends Fragment implements ShareTopDialog.MyOnclickListener {
    private View view;
    //private TabLayout tabLayout;
    private ContactListFragment contactListFragment;
    private RenMaiBookFragment renMaiBookFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private EditText query;
    private ImageButton clearSearch;
    private int flag;
    private ShareTopDialog dialog;
    private BaseAnimatorSet mBasIn;
    private RadioButton rb_friend;
    private RadioButton rb_people;
    private RelativeLayout left_layout;
    private RelativeLayout right_layout;
    private int chechNum = 0;
    private Drawable drawable;
    private String TAG = "ContactFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.contact, null);
        //initTitleBar();
        initView();
        Log.i(TAG,"..............onCreateView");
        initFragment();
        return view;
    }

   /* private void initTitleBar() {
        new TitleBarBuilder(view).setTitle("人脉").setImageRightRes(R.mipmap.btn_add).setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBasIn = new BounceTopEnter();
                dialog = new ShareTopDialog(getActivity());
                dialog.setType(2);
                dialog.setMyOnclickListener(ContactFragment.this);
                dialog.showAnim(mBasIn).show();
            }
        });
    }*/

    private void initView() {
        //tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        rb_friend = (RadioButton) view.findViewById(R.id.rb_friend);
        rb_people = (RadioButton) view.findViewById(R.id.rb_people);
        left_layout = (RelativeLayout) view.findViewById(R.id.left_layout);
        right_layout = (RelativeLayout) view.findViewById(R.id.right_layout);
        query = (EditText) view.findViewById(R.id.query);
        clearSearch = (ImageButton) view.findViewById(R.id.search_clear);
        right_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBasIn = new BounceTopEnter();
                dialog = new ShareTopDialog(getActivity());
                dialog.setType(3);
                dialog.setMyOnclickListener(ContactFragment.this);
                dialog.showAnim(mBasIn).show();
            }
        });
        left_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 1;
                String title = "扫一扫";
                String tip = "addFriend";
                startActivity(new Intent(getActivity(), CaptureActivity.class)
                        .putExtra("flag", flag)
                        .putExtra("tip", tip)
                        .putExtra("title", title));
            }
        });
        rb_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"..............rb_friend");
                chechNum = 0;
                ft = fm.beginTransaction();
                query.getText().clear();
                flag = 0;
                ft.show(contactListFragment).hide(renMaiBookFragment);
                ft.commit();

                rb_people.setChecked(false);
                drawable = getActivity().getResources().getDrawable(R.mipmap.white_top);
                drawable.setBounds( 0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                rb_people.setCompoundDrawables(null,null,drawable,null);
            }
        });
        rb_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chechNum += 1;
                Log.i(TAG,"........rb_people="+chechNum+"    "+chechNum%2);
                if (chechNum%2 == 0){
                    renMaiBookFragment.isAddHead(true);
                    drawable = getActivity().getResources().getDrawable(R.mipmap.gree_bottom);
                }else{
                    renMaiBookFragment.isAddHead(false);
                    drawable = getActivity().getResources().getDrawable(R.mipmap.gree_top);
                }
                drawable.setBounds( 0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                rb_people.setCompoundDrawables(null,null,drawable,null);
                ft = fm.beginTransaction();
                query.getText().clear();
                flag = 1;
                ft = fm.beginTransaction();
                ft.show(renMaiBookFragment).hide(contactListFragment);
                ft.commit();
                rb_friend.setChecked(false);
            }
        });
        /*tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ft = fm.beginTransaction();
                query.getText().clear();
                if (tab.getPosition() == 0) {
                    flag = 0;
                    ft.show(contactListFragment).hide(renMaiBookFragment);
                }
                else {
                    flag = 1;
                    ft = fm.beginTransaction();
                    ft.show(renMaiBookFragment).hide(contactListFragment);
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
                Log.i(TAG,"..............===="+s.toString()+"     "+contactListFragment+"    "+s.length());
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                }
                else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
                if (flag == 0) {
                    Log.i(TAG,"..............flag 0===="+s.toString()+"     "+contactListFragment+"    "+s.length());
                    if (contactListFragment != null && s != null&&s.length()>0&&!s.equals(""))
                        Log.i(TAG,"..............flag00000");
                        contactListFragment.filterData(s.toString());

                }
                else if (flag == 1) {
                    Log.i(TAG,".............flag 1.===="+s.toString()+"     "+contactListFragment+"    "+s.length());
                    if (renMaiBookFragment != null && s != null&&s.length()>0&&!s.equals(""))
                        Log.i(TAG,"..............flag1111");
                        renMaiBookFragment.filterData(s.toString());
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
        fm = getChildFragmentManager();
        ft = fm.beginTransaction();
        contactListFragment = new ContactListFragment();
        renMaiBookFragment = new RenMaiBookFragment();
        ft.add(R.id.frame, contactListFragment);
        ft.add(R.id.frame, renMaiBookFragment);
        ft.show(contactListFragment).hide(renMaiBookFragment).commit();
    }

    protected void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(int i) {
        switch (i) {
            case 1:
                String id = RAS.getPublicKeyStrRAS(SPUtil.getInstance(getActivity()).getString("uid").getBytes());
                //ShareUtil.openShareLogo(getActivity(), R.mipmap.logo, "嘚啵一下，创造你的价值", "嘚啵是什么？他们的嘚啵完以后，居然领到了红包了耶！", "http://debo.shangtongyuntian.com/index1.php/appapi/User/web_prize?invitation_code="+ RAS.getPublicKeyStrRAS(SPUtil.getInstance(getActivity()).getString("uid").getBytes()));
                ShareUtil.shareApplets(getActivity(), "http://debo.shangtongyuntian.com/data/upload/chatroom/debo_clock.png", "嘚啵一下，创造你的价值", "嘚啵是什么？他们的嘚啵完以后，居然领到了红包了耶！", "http://debo.shangtongyuntian.com/index1.php/appapi/User/web_prize?invitation_code="+ id,id);
                break;
            case 2:
                startActivity(new Intent(getActivity(), FindFriendsActivity.class));
                break;
        }
    }

    public void refresh(int notice) {
        if (contactListFragment != null)
            contactListFragment.refresh(notice);
    }
}
