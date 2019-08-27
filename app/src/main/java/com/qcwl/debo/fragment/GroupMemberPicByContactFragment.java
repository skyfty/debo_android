package com.qcwl.debo.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.qcwl.debo.MainActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.utils.PinYinUtils;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.AutoSwipRefreshLayout;
import com.qcwl.debo.widget.sortlistview.PinyinComparator;
import com.qcwl.debo.widget.sortlistview.SideBar;
import com.qcwl.debo.widget.sortlistview.SortGroupMemberAdapter;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observer;


/**
 * Created by Administrator on 2017/7/14.
 */

public class GroupMemberPicByContactFragment extends Fragment implements ContactListPresenterInf {
    private View view;
    private ListView sortListView;
    private AutoSwipRefreshLayout refresh_layout;
    private ContactListPresenter contactListPresenter;
    private SortGroupMemberAdapter adapter;
    private List<ContactsBean> SourceDateList = new ArrayList<ContactsBean>();
    private List<ContactsBean> cursor_list = new ArrayList<ContactsBean>();
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private MyBrocastRecevier myBrocastRecevier;
    private IntentFilter intentFilter;
    private SPUtil sp;
    private SideBar sideBar;
    private TextView dialog;
    private ContactsBean cb;
    // 实例化汉字转拼音类
    private PinyinComparator pinyinComparator = new PinyinComparator();
    private String sortString;
    private SortGroupMemberAdapter.ChangeState c;
    private ConversationSqlite sqlite;
    private SQLiteDatabase db;
    private Cursor cursor;
    private String type;
    private SortGroupMemberAdapter.ChangeStateWhere c1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.contact_list, null);
        try {
            type = getArguments().getString("type");
        }catch (Exception e){
            e.printStackTrace();
            type = "";
        }

        initView();
        myBrocastRecevier = new MyBrocastRecevier();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.debo.contact"); // 添加要收到的广播
        getActivity().registerReceiver(myBrocastRecevier, intentFilter);
        monitor();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        new RxPermissions(getActivity())
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean granted) {
                        if (granted) {
                            sqlite = ConversationSqlite.getInstance(getActivity());
                            db = sqlite.getReadableDatabase();
                            SourceDateList.clear();
                            cursor_list.clear();
                            cursor = db.rawQuery("select * from conversation", null);
                            if (cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    cb = new ContactsBean();
                                    cb.setMobile(cursor.getString(4));
                                    cb.setUser_nickname(cursor.getString(2));
                                    cb.setAvatar(cursor.getString(3));
                                    cb.setId(cursor.getString(1));
                                    cursor_list.add(cb);
                                }
                            }
                            cursor.close();
                        } else {
                            Toast.makeText(getActivity(), "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        SourceDateList.addAll(filledData(cursor_list));
        contactListPresenter.setList(SourceDateList);
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter.notifyDataSetChanged();
    }

    private void getDatas() {
        if (!TextUtils.isEmpty(sp.getString("uid")))
            contactListPresenter.getContactList(getActivity(), sp.getString("uid"), SourceDateList);
    }

    @Override
    public void onDestroy() {
        db.close();
        sqlite.close();
        super.onDestroy();
    }

    private void initView() {
        contactListPresenter = new ContactListPresenter(this);
        sp = SPUtil.getInstance(getActivity());

        sortListView = (ListView) view.findViewById(R.id.listView);

        adapter = new SortGroupMemberAdapter(getActivity(), SourceDateList,type);
        adapter.setType(1);
        adapter.setAddr("contact");
        adapter.setListener(c);
        adapter.setListener2(c1);
        sortListView.setAdapter(adapter);

        refresh_layout = (AutoSwipRefreshLayout) view.findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.holo_list_light);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDatas();
            }
        });

        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
        dialog = (TextView) view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
    }

    private void monitor() {

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

    }

    public void filterData(String filter) {
        if (filter != null && contactListPresenter != null)
            contactListPresenter.filterContactList(filter);
    }

    public void setListener(SortGroupMemberAdapter.ChangeState c) {
        this.c = c;
    }
    public void setListener2(SortGroupMemberAdapter.ChangeStateWhere c1) {
        this.c1 = c1;
    }

    public void resetData(){
        for (ContactsBean b : SourceDateList) {
            b.setCheck(false);
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void getResult(int code, String message, Object o) {
        refresh_layout.setRefreshing(false);
        if (code == 0) {
            adapter.notifyDataSetChanged();
        } else {
            SourceDateList.clear();
            adapter.notifyDataSetChanged();
            ToastUtils.showShort(getActivity(), message);
        }
    }

    @Override
    public void updateDataByFilter(List<ContactsBean> filterDateList) {
        if (adapter != null)
            adapter.updateListView(filterDateList);
    }

    private class MyBrocastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("add".equals(intent.getStringExtra("flag"))) {
                getDatas();
            } else if ("del".equals(intent.getStringExtra("flag"))) {
                getDatas();
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(myBrocastRecevier);
    }

    /**
     * 为ListView填充数据
     *
     * @param list
     * @return
     */
    private List<ContactsBean> filledData(List<ContactsBean> list) {
        List<ContactsBean> mSortList = new ArrayList<ContactsBean>();

        for (int i = 0; i < list.size(); i++) {
            ContactsBean sortModel = new ContactsBean();
            if (TextUtils.isEmpty(list.get(i).getUser_nickname())) {
                // 汉字转换成拼音
                sortString = PinYinUtils.getPinYinFirstLetter(list.get(i).getMobile()).toUpperCase();
            } else {
                sortModel.setUser_nickname(list.get(i).getUser_nickname());
                // 汉字转换成拼音
                sortString = PinYinUtils.getPinYinFirstLetter(list.get(i).getUser_nickname()).toUpperCase();

            }
            sortModel.setMobile(list.get(i).getMobile());
            sortModel.setId(list.get(i).getId());
            sortModel.setAvatar(list.get(i).getAvatar());

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

}
