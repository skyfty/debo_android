package com.qcwl.debo.ui.found.fans;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.found.fans.adapter.FansDynamicPraiseAdapter;
import com.qcwl.debo.ui.found.fans.bean.FansDynamicPraiseBean;
import com.qcwl.debo.ui.found.fans.contract.FansContract;
import com.qcwl.debo.ui.found.fans.presenter.FansPresenter;
import com.qcwl.debo.utils.TitleBarBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FansDynamicPraiseListActivity extends BaseActivity implements FansContract.View {

    @Bind(R.id.swipe_target)
    RecyclerView recyclerView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private int page = 1;

    private FansDynamicPraiseAdapter adapter;
    private List<FansDynamicPraiseBean> items;

    private FansPresenter presenter;

    private String uid = "";
    private String moments_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_dynamic_praise_list);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("全部点赞")
                .setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        if (getIntent() == null) {
            return;
        }
        moments_id = getIntent().getStringExtra("moments_id");
        uid = sp.getString("uid");
        items = new ArrayList<>();
        adapter = new FansDynamicPraiseAdapter(this, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyItemDecoration(0, 1));
        recyclerView.setAdapter(adapter);
        presenter = new FansPresenter(this);
        presenter.getDynamicPraiseList(this, uid, moments_id);
    }

    private void testData() {
        FansDynamicPraiseBean bean = null;
        for (int i = 0; i < 10; i++) {
            bean = new FansDynamicPraiseBean();
            bean.setId("" + i + "" + i);
            switch (i % 5) {
                case 0:
                    bean.setUser_nickname("巴卫师兄");
                    bean.setAvatar("http://img4.imgtn.bdimg.com/it/u=671237967,3300686551&fm=26&gp=0.jpg");
                    break;
                case 1:
                    bean.setUser_nickname("莫忘尘");
                    bean.setAvatar("http://img2.imgtn.bdimg.com/it/u=1070393410,4158620846&fm=26&gp=0.jpg");
                    break;
                case 2:
                    bean.setUser_nickname("檀无心");
                    bean.setAvatar("http://img4.imgtn.bdimg.com/it/u=380020946,3721512050&fm=26&gp=0.jpg");
                    break;
                case 3:
                    bean.setUser_nickname("萧若兰");
                    bean.setAvatar("http://img3.imgtn.bdimg.com/it/u=1959782189,2791261238&fm=26&gp=0.jpg");
                    break;
                case 4:
                    bean.setUser_nickname("冷月心");
                    bean.setAvatar("http://img2.imgtn.bdimg.com/it/u=1029824850,320079902&fm=26&gp=0.jpg");
                    break;
                default:
                    break;
            }
            items.add(bean);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void doSuccess(int type, Object object) {
        if (type == FansPresenter.TYPE_FANS_DYNAMIC_PRAISE_LIST) {
            List<FansDynamicPraiseBean> list = (List<FansDynamicPraiseBean>) object;
            if (list != null) {
                if (page == 1) {
                    items.clear();
                }
                items.addAll(list);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void doFailure(int code) {

    }
}
