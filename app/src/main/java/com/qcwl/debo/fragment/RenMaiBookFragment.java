package com.qcwl.debo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
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
import com.qcwl.debo.presenterInf.OnOperItemClickL;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.contact.ContractRenMaiListActivity;
import com.qcwl.debo.ui.contact.DirectRenMaiListActivity;
import com.qcwl.debo.ui.contact.InviteRenMaiListActivity;
import com.qcwl.debo.ui.contact.SearchRenMaiListActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.view.AutoSwipRefreshLayout;
import com.qcwl.debo.view.NormalListDialog;
import com.qcwl.debo.widget.sortlistview.SideBar;
import com.qcwl.debo.widget.sortlistview.SortGroupMemberAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/7/13.
 * 人脉-人脉Fragment
 */

public class RenMaiBookFragment extends Fragment implements ContactListPresenterInf {
    private View view, header;
    private ListView listView;
    private AutoSwipRefreshLayout refresh_layout;
    private SortGroupMemberAdapter adapter;
    private ContactListPresenter contactListPresenter;
    private List<ContactsBean> SourceDateList = new ArrayList<ContactsBean>();
    private SPUtil sp;
    private String[] mStringItems = {"删除", "查看详情"};//, "设置备注"
    private SideBar sideBar;
    private TextView dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.renmai, null);
        header = inflater.inflate(R.layout.renmai_book_header, null);
        initView();
        monitor();
        return view;
    }

    private void initView() {
        sp = SPUtil.getInstance(getActivity());
        contactListPresenter = new ContactListPresenter(this);
        listView = (ListView) view.findViewById(R.id.listView);
        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
        dialog = (TextView) view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        refresh_layout = (AutoSwipRefreshLayout) view.findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.holo_list_light);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });


        adapter = new SortGroupMemberAdapter(getActivity(), SourceDateList, "");
        listView.setAdapter(adapter);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("RenMaiBook", ".....uid=" + sp.getString("uid"));
        if (!TextUtils.isEmpty(sp.getString("uid"))) {
            getData();
        }
    }

    private void getData() {
        Log.i("RenMaiBook", ".....getData");
        contactListPresenter.getReMai(getActivity(), sp.getString("phone"), "0", SourceDateList);
    }

    public void filterData(String filter) {
        contactListPresenter.filterContactList(filter);
    }

    public void isAddHead(boolean b) {
        boolean enabled = listView.areHeaderDividersEnabled();
        Log.i("RenMaiBook", ".....enabled=" + enabled + "     " + listView.getHeaderViewsCount());
        if (b == true) {
            if (listView.getHeaderViewsCount() == 0) {
                listView.addHeaderView(header);
            }
        } else {
            listView.removeHeaderView(header);
        }


    }

    private void monitor() {
        header.findViewById(R.id.allpeople).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //全部人脉
                getData();
            }
        });
        header.findViewById(R.id.zhijierenmai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactListPresenter.getReMai(getActivity(), sp.getString("phone"), "1", SourceDateList);
                adapter.notifyDataSetChanged();
                //startActivity(new Intent(getActivity(), DirectRenMaiListActivity.class));
            }
        });
        header.findViewById(R.id.yaoqingrenmai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactListPresenter.getReMai(getActivity(), sp.getString("phone"), "2", SourceDateList);
                adapter.notifyDataSetChanged();
                //startActivity(new Intent(getActivity(), InviteRenMaiListActivity.class));
            }
        });
        header.findViewById(R.id.heyuerenmai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactListPresenter.getReMai(getActivity(), sp.getString("phone"), "3", SourceDateList);
                adapter.notifyDataSetChanged();
                //startActivity(new Intent(getActivity(), ContractRenMaiListActivity.class));
            }
        });
        header.findViewById(R.id.chazhaorenmai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchRenMaiListActivity.class));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("RenMaiBookFragment", "..........=" + position);
                List<ContactsBean> list = adapter.getList();
                if (list != null && list.size() > 0) {
                    ContactsBean c = list.get(position);
                    //ContactsBean c = adapter.getList().get(position - 1);
                    Intent intent = new Intent(getActivity(), ContactsContentActivity.class);
                    intent.putExtra("mobile", c.getMobile());
                    intent.putExtra("my_mobile", sp.getString("phone"));
                    intent.putExtra("f_uid", c.getId());
                    intent.putExtra("type", "2");
                    intent.putExtra("api_type", "2");
                    startActivity(intent);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position > 0) {
                    final ContactsBean c = adapter.getList().get(position - 1);
                    final NormalListDialog dialog = new NormalListDialog(getActivity(), mStringItems);
                    dialog.title("请选择")//
                            .isTitleShow(false)//
                            .itemPressColor(getResources().getColor(R.color.font_select))//
                            .itemTextColor(getResources().getColor(R.color.font_normal))//
                            .itemTextSize(16)//
                            .cornerRadius(5)//
                            .widthScale(0.65f).show();

                    dialog.setOnOperItemClickL(new OnOperItemClickL() {
                        @Override
                        public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0:
                                    contactListPresenter.delContact(getActivity(), sp.getString("uid"), c, adapter.getList());
                                    break;
                                case 1:
                                    //                                    startActivity(new Intent(getActivity(), ModifyRemarkActivity.class).putExtra("remark", c.getUser_nickname()).putExtra("mobile", c.getMobile()));
                                    //                                    break;
                                    //                                case 2:
                                    Intent intent = new Intent(getActivity(), ContactsContentActivity.class);
                                    intent.putExtra("mobile", c.getMobile());
                                    intent.putExtra("my_mobile", sp.getString("phone"));
                                    intent.putExtra("f_uid", c.getId());
                                    intent.putExtra("type", "2");
                                    startActivity(intent);
                                    break;
                            }
                            dialog.dismiss();
                        }
                    });
                }
                return true;
            }
        });
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
