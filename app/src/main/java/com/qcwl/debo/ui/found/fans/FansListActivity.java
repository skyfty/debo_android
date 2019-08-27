package com.qcwl.debo.ui.found.fans;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.found.fans.adapter.FansListAdapter;
import com.qcwl.debo.ui.found.fans.bean.FansBean;
import com.qcwl.debo.ui.found.fans.bean.FansListBean;
import com.qcwl.debo.ui.found.fans.contract.FansContract;
import com.qcwl.debo.ui.found.fans.presenter.FansPresenter;
import com.qcwl.debo.utils.TitleBarBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 粉丝列表包括我的粉丝列表和我关注的粉丝列表
 */
public class FansListActivity extends BaseActivity implements FansContract.View {

    @Bind(R.id.swipe_target)
    ListView listView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.text_fans_num)
    TextView textFansNum;

    private int page = 1;

    private int fans_type = 0;//fans_type=1表示关注的粉丝列表 fans_type=2表示我的粉丝列表
    private String title = "";

    private List<FansBean> items;
    private FansListAdapter adapter;

    private FansPresenter presenter;

    private String uid = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_list);
        ButterKnife.bind(this);
        if (getIntent() == null) {
            return;
        }
        fans_type = getIntent().getIntExtra("fans_type", 1);
        if (fans_type == 1) {
            title = "关注的粉丝";
            textFansNum.setText("关注的粉丝(" + 0 + ")");
        } else if (fans_type == 2) {
            title = "我的粉丝";
            textFansNum.setText("我的粉丝(" + 0 + ")");
        }

        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle(title).setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        swipeToLoadLayout.setRefreshEnabled(false);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnLoadMoreListener(new com.aspsine.swipetoloadlayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                getFansList();
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });
        items = new ArrayList<>();
        adapter = new FansListAdapter(this, items);
        listView.setAdapter(adapter);
        uid = sp.getString("uid");
        presenter = new FansPresenter(this);
        presenter.requestFansList(this, fans_type, page, "" + uid);
    }

    private void testData() {
        FansBean bean = null;
        for (int i = 0; i < 10; i++) {
            bean = new FansBean();
            bean.setId("" + i);
            bean.setSmall_avatar("http://img2.imgtn.bdimg.com/it/u=3439142207,445655643&fm=26&gp=0.jpg");
            bean.setUser_nickname("Ha Ha");
            if (i % 2 == 0) {
                bean.setSignature("山有木兮木有枝");
            } else {
                bean.setSignature("心悦君兮君不知");
            }
            if (i % 3 == 0) {
                bean.setIs_concern(1);
            } else {
                bean.setIs_concern(0);
            }
            items.add(bean);
        }
        adapter.notifyDataSetChanged();

    }

    public void getFansList() {

    }

    public void focusFans(String follow_uid) {
        presenter.focusFans(this, uid, follow_uid);
    }

    @Override
    public void doSuccess(int type, Object object) {
        if (object instanceof FansListBean) {
            FansListBean fansListBean = (FansListBean) object;
            if (fansListBean != null && fansListBean.getFans_list() != null) {
                if (fans_type == 1) {
                    textFansNum.setText("关注的粉丝(" + fansListBean.getFans_num() + ")");
                } else if (fans_type == 2) {
                    textFansNum.setText("我的粉丝(" + fansListBean.getFans_num() + ")");
                }
                items.addAll(fansListBean.getFans_list());
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void doFailure(int code) {

    }
}
