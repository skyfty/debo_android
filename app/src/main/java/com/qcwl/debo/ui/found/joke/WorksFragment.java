package com.qcwl.debo.ui.found.joke;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.qcwl.debo.R;
import com.qcwl.debo.model.Joke_like_list;
import com.qcwl.debo.model.Joke_list;
import com.qcwl.debo.model.PersonalBean;
import com.qcwl.debo.utils.DeviceUtil;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WorksFragment extends Fragment {
    private FragmentActivity instance;
    private WorksFragment worksFragment;
    private PersonalAdapter adapter;
    @Bind(R.id.recycler_works)
    RecyclerView recyclerView;
    private List<Joke_like_list> list;
    private PersonalBean personalBean;
    private String TAG = "WorksFragment";
    private String is_follow;
    private String type;//0加载作品数据   1加载喜欢数据
    private List<Joke_list> joke_list;
    private PersonalAdapter2 adapter2;

    public static WorksFragment newInstance(PersonalBean storeid, String is_follow, String type) {
        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) storeid);
        args.putString("is_follow", is_follow);
        args.putString("type", type);
        WorksFragment fragment = new WorksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work, container, false);
        ButterKnife.bind(this, view);
        instance = getActivity();
        worksFragment = this;
        initView();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        personalBean = (PersonalBean) getArguments().getSerializable("data");
        is_follow = getArguments().getString("is_follow");
        type = getArguments().getString("type");
        DisplayMetrics displayMetrics = DeviceUtil.getScreenPixel(getActivity());
        int img_height = displayMetrics.widthPixels / 3 - (EaseTitleBar.dp2px(getActivity(), 1));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        if (type.equals("0")) {
            joke_list = personalBean.getJoke_list();
            if (joke_list == null||joke_list.size() == 0) {
                return;
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                adapter2 = new PersonalAdapter2(getActivity(), R.layout.item_works, joke_list, personalBean.getUser_info().getAvatar(), personalBean.getUser_info().getUser_nickname(), is_follow, img_height);
                recyclerView.setAdapter(adapter2);
            }
        } else if (type.equals("1")) {
            list = personalBean.getJoke_like_list();
            if (list == null||list.size() == 0) {
                return;
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new PersonalAdapter(getActivity(), R.layout.item_works, list, personalBean.getUser_info().getAvatar(), personalBean.getUser_info().getUser_nickname(), is_follow, img_height);
                recyclerView.setAdapter(adapter);
            }
        }


        //recyclerView.add
    }

}
