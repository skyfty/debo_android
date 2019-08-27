package com.qcwl.debo.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.PublicChatRoomsActivity;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.presenterInf.OnOperItemClickL;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.contact.GroupsActivity;
import com.qcwl.debo.ui.contact.ModifyRemarkActivity;
import com.qcwl.debo.ui.contact.NewFriendsMsgActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.view.AutoSwipRefreshLayout;
import com.qcwl.debo.view.NormalListDialog;
import com.qcwl.debo.widget.sortlistview.SideBar;
import com.qcwl.debo.widget.sortlistview.SortGroupMemberAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.qcwl.debo.R.id.listView;


/**
 * Created by Administrator on 2017/7/14.
 * 人脉-好友
 */

public class ContactListFragment extends Fragment implements View.OnClickListener, ContactListPresenterInf {
    private View view, headerview;
    private ListView sortListView;
    private AutoSwipRefreshLayout refresh_layout;
    private ContactListPresenter contactListPresenter;
    private SortGroupMemberAdapter adapter;
    private List<ContactsBean> SourceDateList = new ArrayList<ContactsBean>();
    private LinearLayout group, friends,chatRoom;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private MyBrocastRecevier myBrocastRecevier;
    private IntentFilter intentFilter;
    private String name;
    private SPUtil sp;
    private String[] mStringItems = {"删除", "查看详情"};//"设置备注",
    private SideBar sideBar;
    private TextView dialog,tip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.contact_list, null);

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
        if (!TextUtils.isEmpty(sp.getString("uid")))
            getDatas();
    }

    private void getDatas() {
        contactListPresenter.getContactList(getActivity(), sp.getString("uid"), SourceDateList);
    }

    private void initView() {
        contactListPresenter = new ContactListPresenter(this);
        sp = SPUtil.getInstance(getActivity());

        headerview = LayoutInflater.from(getActivity()).inflate(R.layout.contact_header, sortListView, false);
        sortListView = (ListView) view.findViewById(listView);
        group = (LinearLayout) headerview.findViewById(R.id.contact_header_group);
        friends = (LinearLayout) headerview.findViewById(R.id.contact_header_friends);
        chatRoom = (LinearLayout) headerview.findViewById(R.id.contact_header_chat_room);
        tip = (TextView) headerview.findViewById(R.id.tip);

        sortListView.addHeaderView(headerview);
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
    }

//    private void setSlideLayout() {
//        SwipeMenuCreator creator = new SwipeMenuCreator() {
//
//            @Override
//            public void create(SwipeMenu menu) {
//                // create "open" item
//                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
//                // set item background
//                openItem.setBackground(new ColorDrawable(Color.rgb(255, 0,
//                        0)));
//                // set item width
//                openItem.setWidth(DpUtils.dp2px(getActivity(), 90));
//                // set item title
//                openItem.setTitle("删除");
//                // set item title fontsize
//                openItem.setTitleSize(16);
//                // set item title font color
//                openItem.setTitleColor(Color.WHITE);
//               // openItem.setBackground(Color.RED);
//                // add to menu
//                menu.addMenuItem(openItem);
//
//            }
//        };
//        sortListView.setMenuCreator(creator);
//    }

    private void monitor() {

        friends.setOnClickListener(this);
        group.setOnClickListener(this);
        chatRoom.setOnClickListener(this);

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position > 0) {
                    ContactsBean c = adapter.getList().get(position - 1);
                    Intent intent = new Intent(getActivity(), ContactsContentActivity.class);
                    intent.putExtra("mobile", c.getMobile());
                    intent.putExtra("my_mobile", sp.getString("phone"));
                    intent.putExtra("f_uid", c.getId());
                    intent.putExtra("type", "1");
                    startActivity(intent);
                }
            }
        });

        sortListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                            .widthScale(0.65f)
                            .show();

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
                                    intent.putExtra("type", "1");
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contact_header_friends:
                startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
                break;
            case R.id.contact_header_group:
                startActivity(new Intent(getActivity(), GroupsActivity.class));
                break;
            case R.id.contact_header_chat_room:
                startActivity(new Intent(getActivity(), PublicChatRoomsActivity.class));
                break;
        }
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

    public class MyBrocastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("add".equals(intent.getStringExtra("flag"))) {
                getDatas();
            } else if ("del".equals(intent.getStringExtra("flag"))) {
                getDatas();
            } else if ("notifation".equals(intent.getStringExtra("flag"))) {
                tip.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(myBrocastRecevier);
    }

    public void refresh(int notice) {
        if (notice > 0) {
            tip.setVisibility(View.VISIBLE);
        } else {
            tip.setVisibility(View.GONE);
        }
    }
}
