package com.qcwl.debo.ui.found.fans;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.fans.adapter.FansDynamicAdapter;
import com.qcwl.debo.ui.found.fans.bean.FansDynamicBean;
import com.qcwl.debo.ui.found.fans.bean.FansHomeBean;
import com.qcwl.debo.ui.found.fans.contract.FansContract;
import com.qcwl.debo.ui.found.fans.presenter.FansPresenter;
import com.qcwl.debo.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class FansDynamicDetailActivity extends BaseActivity implements FansContract.View {//AppCompatActivity

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.status_bar)
    View statusBar;
    @Bind(R.id.layout_top)
    LinearLayout layoutTitle;

    private List<FansDynamicBean> items;
    private FansDynamicAdapter adapter;

    private String uid = "";

    private FansPresenter presenter;

    private FansHomeBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_dynamic_detail);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
//        StatusBarUtil.setTransparentForImageView(this, null);
        int statusHeight = ScreenUtils.getStatusHeight(this);
        Log.d("===", "statusHeight=" + statusHeight);
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight));
        layoutTitle.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight + ScreenUtils.dp2px(this, 50)));
        layoutTitle.getBackground().setAlpha(255);
    }

    private void initData() {
        uid = SPUtil.getInstance(this).getString("uid");
        items = new ArrayList<>();
        adapter = new FansDynamicAdapter(this, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyItemDecoration(0, ScreenUtils.dp2px(this, 5)));
        recyclerView.setAdapter(adapter);
        presenter = new FansPresenter(this);
        if (getIntent() == null) {
            return;
        }
        bean = (FansHomeBean) getIntent().getSerializableExtra("bean");

        presenter.getFansDynamicDetail(this, uid, bean.getM_id());

//        FansDynamicBean dynamicBean = new FansDynamicBean();
//        dynamicBean.setMoments_id(bean.getM_id());
//        dynamicBean.setMoments_uid(bean.getUid());
//        dynamicBean.setIs_upvote(bean.getIs_zan());
//        dynamicBean.setImages(bean.getImg());
//        if (bean.getType() == 3) {
//            dynamicBean.setType(1);
//        } else if (bean.getType() == 4) {
//            dynamicBean.setType(2);
//        }
//        dynamicBean.setCreate_time(bean.getAdd_time());
//        dynamicBean.setAvatar(bean.getAvatar());
//        dynamicBean.setMoments_content(bean.getContent());
//        dynamicBean.setUser_nickname(bean.getUser_nickname());
//        dynamicBean.setVideo_img(bean.getVideo_img());
//        dynamicBean.setVideo_path(bean.getVideo_path());
//        dynamicBean.setUpvote_num(bean.get);
//        items.add(dynamicBean);
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void doSuccess(int type, Object object) {
        switch (type) {
            case FansPresenter.TYPE_FANS_PRAISE:
                int isPraise = Integer.parseInt((String) object);
                items.get(mPosition).setIs_upvote(isPraise);
                int praiseNum = Integer.parseInt(items.get(mPosition).getUpvote_num());
                if (isPraise == 1) {
                    praiseNum += 1;
                } else {
                    praiseNum -= 1;
                }
                items.get(mPosition).setUpvote_num(String.valueOf(praiseNum));
                adapter.notifyDataSetChanged();
                break;
            case FansPresenter.TYPE_FANS_DELETE_DYNAMIC:
                items.remove(mPosition);
                adapter.notifyDataSetChanged();
                finish();
                break;
            case FansPresenter.TYPE_FANS_DYNAMIC_DETAIL:
                if (object == null) {
                    return;
                }
                List<FansDynamicBean> list = (List<FansDynamicBean>) object;
                items.addAll(list);
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private int mPosition = 0;

    public void praiseDynamic(int position, String moments_id, int isPraise) {
        mPosition = position;
        presenter.praiseFansDynamic(this, uid, moments_id, isPraise);
    }

    @Override
    public void onBackPressed() {
        try {
            if (JCVideoPlayer.backPress()) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            JCVideoPlayer.releaseAllVideos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.image_back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }

    @Override
    public void doFailure(int code) {

    }

    public void deleteFansDynamic(final int position) {
        this.mPosition = position;
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您确定要删除吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.deleteFansDynamic(FansDynamicDetailActivity.this, uid, items.get(position).getMoments_id());
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
