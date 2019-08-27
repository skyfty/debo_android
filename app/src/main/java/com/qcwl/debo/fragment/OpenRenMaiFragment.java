package com.qcwl.debo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.adapter.SearchRenMaiAdapter;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.model.SearchRenMaiBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class OpenRenMaiFragment extends Fragment implements ContactListPresenterInf {

    private View view;
    private ListView sortListView;
//    private AutoSwipRefreshLayout refresh_layout;
    private SwipeToLoadLayout swipeToLoadLayout;
    private ContactListPresenter contactListPresenter;
    private SearchRenMaiAdapter adapter;
    private List<SearchRenMaiBean> list = new ArrayList<SearchRenMaiBean>();
    private SPUtil sp;

    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.aristocracy_renmai, null);

        initView();
        monitor();
        return view;
    }

    private void getDatas(int page) {
        contactListPresenter.getAristocracyReMai(getActivity(), sp.getString("uid"), "1",page, "");
        Log.e("uid...0",sp.getString("uid"));
    }

    private void initView() {
        contactListPresenter = new ContactListPresenter(this);
        sp = SPUtil.getInstance(getActivity());

        sortListView = (ListView) view.findViewById(R.id.swipe_target);

        adapter = new SearchRenMaiAdapter(getActivity(), list);
        adapter.setType(1);
        sortListView.setAdapter(adapter);

//        refresh_layout = (AutoSwipRefreshLayout) view.findViewById(R.id.refresh_layout);
//        refresh_layout.setColorSchemeResources(R.color.holo_list_light);
//        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                getDatas();
//            }
//
//
//        });
        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
//                swipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.);
                page = 1;
                getDatas(page);
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });
        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                getDatas(page);
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 500);
            }
        });
        if (!TextUtils.isEmpty(sp.getString("uid")))
            getDatas(page);
    }

    private void monitor() {

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                    SearchRenMaiBean c = adapter.getList().get(position);
                    Intent intent = new Intent(getActivity(), ContactsContentActivity.class);
                    intent.putExtra("mobile", c.getMobile());
                    intent.putExtra("my_mobile", sp.getString("phone"));
                    intent.putExtra("f_uid", c.getId());
                    intent.putExtra("type", "1");
                    startActivity(intent);
            }
        });

    }

    @Override
    public void getResult(int code, String message, Object o) {
        Log.e("code",""+code);
//        swipeToLoadLayout.setLoadingMore(false);
        if (code == 0) {
//            list.clear();
            if (o != null) {
                List<SearchRenMaiBean> searchRenMaiBeanList = (List<SearchRenMaiBean>) o;
                Log.e("searchRenMaiBeanList",""+searchRenMaiBeanList.size());
                if (page == 1) {
                    list.clear();
                }
                list.addAll(searchRenMaiBeanList);
            }
            adapter.notifyDataSetChanged();
        } else {
//            list.clear();
            adapter.notifyDataSetChanged();
            ToastUtils.showShort(getActivity(),"没有更多了");
        }
    }

    @Override
    public void updateDataByFilter(List<ContactsBean> filterDateList) {

    }


}
