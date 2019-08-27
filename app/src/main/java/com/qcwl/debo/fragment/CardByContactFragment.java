package com.qcwl.debo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.view.AutoSwipRefreshLayout;
import com.qcwl.debo.widget.sortlistview.SideBar;
import com.qcwl.debo.widget.sortlistview.SortGroupMemberAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.qcwl.debo.R.id.listView;

/**
 * Created by Administrator on 2017/8/23.
 */

public class CardByContactFragment extends Fragment implements ContactListPresenterInf {
    private View view;
    private ListView sortListView;
    private AutoSwipRefreshLayout refresh_layout;
    private ContactListPresenter contactListPresenter;
    private SortGroupMemberAdapter adapter;
    private List<ContactsBean> SourceDateList = new ArrayList<ContactsBean>();
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private SPUtil sp;
    private SideBar sideBar;
    private TextView dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.contact_list, null);
        initView();
        monitor();
        return view;
    }

    private void getDatas() {
        contactListPresenter.getContactList(getActivity(), sp.getString("uid"), SourceDateList);
    }

    private void initView() {
        contactListPresenter = new ContactListPresenter(this);
        sp = SPUtil.getInstance(getActivity());

        sortListView = (ListView) view.findViewById(listView);

        adapter = new SortGroupMemberAdapter(getActivity(), SourceDateList,"");
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

        if (!TextUtils.isEmpty(sp.getString("uid")))
            getDatas();
    }


    private void monitor() {

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ContactsBean c = adapter.getList().get(position);
                Intent intent = new Intent();
                intent.putExtra("headStr", c.getAvatar());
                intent.putExtra("nameStr", c.getUser_nickname());
                intent.putExtra("phone", c.getMobile());
                intent.putExtra("sex", c.getSex());
                intent.putExtra("uidStr", c.getId());
                intent.putExtra("user_area", c.getArea());
                getActivity().setResult(RESULT_OK, intent);
                getActivity().finish();
            }
        });

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


    @Override
    public void getResult(int code, String message, Object o) {
        refresh_layout.setRefreshing(false);
        if (code == 0) {
            adapter.notifyDataSetChanged();
        } else {
            SourceDateList.clear();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateDataByFilter(List<ContactsBean> filterDateList) {
        if (adapter != null)
            adapter.updateListView(filterDateList);
    }


}
