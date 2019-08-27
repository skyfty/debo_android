package com.qcwl.debo.ui.found.fans;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.fans.adapter.FansDynamicAdapter;
import com.qcwl.debo.ui.found.fans.bean.FansDynamicBean;
import com.qcwl.debo.ui.found.fans.contract.FansContract;
import com.qcwl.debo.ui.found.fans.presenter.FansPresenter;
import com.qcwl.debo.utils.SPUtil;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class FansDynamicActivity extends BaseActivity  implements FansContract.View {//AppCompatActivity

    @Bind(R.id.swipe_target)
    RecyclerView recyclerView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.status_bar)
    View statusBar;
    @Bind(R.id.layout_top)
    LinearLayout layoutTitle;

    private List<FansDynamicBean> items;
    private FansDynamicAdapter adapter;

    private String uid = "";

    private int page = 1;

    private FansPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_dynamic);
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
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight));
        layoutTitle.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight + ScreenUtils.dp2px(this, 50)));
        layoutTitle.setBackgroundResource(R.mipmap.gradient_bg);
        layoutTitle.getBackground().setAlpha(255);
    }

    private void dialog() {
        new AlertDialog.Builder(this)
                .setItems(new String[]{"视频", "图片"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                MPermissions.requestPermissions(FansDynamicActivity.this, CODE_CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.CAMERA);
                                break;
                            case 1:
                                startActivityForResult(new Intent(FansDynamicActivity.this, PublishFansDynamicActivity.class),CODE_PUBLISH);
                                break;
                            default:
                                break;
                        }
                    }
                }).create().show();
    }

    public static final int CODE_PUBLISH = 299;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_PUBLISH:
                case CODE_CAMERA:
                    page=1;
                    presenter.getFansDynamicList(FansDynamicActivity.this, uid, page);
                    break;
                default:
                    break;
            }
        }
    }

    public static final int CODE_CAMERA = 200;

    @PermissionGrant(CODE_CAMERA)
    public void requestPermissionSuccess() {
        startActivityForResult(new Intent(FansDynamicActivity.this, CameraActivity.class).putExtra("is_cycle", 0),CODE_CAMERA);
    }

    @PermissionDenied(CODE_CAMERA)
    public void requestPermissionFailed() {
        Toast.makeText(this, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initData() {
        uid = SPUtil.getInstance(this).getString("uid");
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
//                swipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.);
                page = 1;
                presenter.getFansDynamicList(FansDynamicActivity.this, uid, page);
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                presenter.getFansDynamicList(FansDynamicActivity.this, uid, page);
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });
        items = new ArrayList<>();
        adapter = new FansDynamicAdapter(this, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyItemDecoration(0, ScreenUtils.dp2px(this, 5)));
        recyclerView.setAdapter(adapter);
        presenter = new FansPresenter(this);
        presenter.getFansDynamicList(this, uid, page);
    }

    private void testData() {
        FansDynamicBean bean = null;
        List<String> list = new ArrayList<>();
        list.add("http://img4.imgtn.bdimg.com/it/u=4106927789,1354415979&fm=26&gp=0.jpg");
        list.add("http://img5.imgtn.bdimg.com/it/u=2229838092,1101533423&fm=26&gp=0.jpg");
        list.add("http://img0.imgtn.bdimg.com/it/u=1317572779,2093630777&fm=26&gp=0.jpg");
        list.add("http://img3.imgtn.bdimg.com/it/u=1474171487,3364139126&fm=26&gp=0.jpg");
        list.add("http://img5.imgtn.bdimg.com/it/u=1107430513,822064330&fm=26&gp=0.jpg");
        list.add("http://img5.imgtn.bdimg.com/it/u=1554463608,2052066869&fm=26&gp=0.jpg");
        list.add("http://img3.imgtn.bdimg.com/it/u=3155821337,3800975372&fm=26&gp=0.jpg");
        list.add("http://img4.imgtn.bdimg.com/it/u=2239670223,3611984363&fm=26&gp=0.jpg");
        list.add("http://img4.imgtn.bdimg.com/it/u=3663375017,642410595&fm=26&gp=0.jpg");
        for (int i = 0; i < 10; i++) {
            bean = new FansDynamicBean();
            bean.setType(1);
            bean.setMoments_id("" + i);
            bean.setMoments_uid(i + "" + i);
            if (i % 2 == 0) {
                bean.setUser_nickname("檀无心");
                bean.setAvatar("http://img3.imgtn.bdimg.com/it/u=1862279566,3669985389&fm=26&gp=0.jpg");
                bean.setMoments_content("此间事了，我带你云游天下，四海为家，好吗？紫萍。");
                bean.setIs_upvote(1);
            } else {
                bean.setUser_nickname("殷紫萍");
                bean.setAvatar("http://img4.imgtn.bdimg.com/it/u=4106927789,1354415979&fm=26&gp=0.jpg");
                bean.setMoments_content("我这一生，经历过许多人许多事，杀了不该杀的人，救不了想救的人，无憾的人生，是从遇见那个叫无心的人开始的。");
                bean.setIs_upvote(2);
            }
            bean.setCreate_time("6-28 11:28");
            switch (i) {
                case 0:
                    bean.setImages(list.subList(0, 1));
                    break;
                case 1:
                    bean.setImages(list.subList(0, 2));
                    break;
                case 2:
                    bean.setImages(list.subList(0, 3));
                    break;
                case 3:
                    bean.setImages(list.subList(0, 4));
                    break;
                default:
                    bean.setImages(list);
                    break;
            }
            items.add(bean);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void doSuccess(int type, Object object) {
        switch (type) {
            case FansPresenter.TYPE_FANS_DYNAMIC_LIST:
                List<FansDynamicBean> list = (List<FansDynamicBean>) object;
                if (list != null) {
                    if (page == 1) {
                        items.clear();
                    }
                    items.addAll(list);
                }
                adapter.notifyDataSetChanged();
                break;
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
                break;
            default:
                break;
        }
    }

    @Override
    public void doFailure(int code) {

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

    @OnClick({R.id.image_back, R.id.image_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.image_camera:
                dialog();
                break;
        }
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
                        presenter.deleteFansDynamic(FansDynamicActivity.this, uid, items.get(position).getMoments_id());
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

}
