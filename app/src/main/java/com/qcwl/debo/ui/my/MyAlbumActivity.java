package com.qcwl.debo.ui.my;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.ecloud.pulltozoomview.PullToZoomListViewEx;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.model.MyPhotoBean;
import com.qcwl.debo.presenter.MyPhotoPresenter;
import com.qcwl.debo.presenterInf.MyPhotoPresenterInf;
import com.qcwl.debo.ui.circle.AlbumDetailActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.my.remind.RemindActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.MyPhotoAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyAlbumActivity extends BaseActivity implements MyPhotoPresenterInf {

    @Bind(R.id.swipe_target)
    PullToZoomListViewEx listView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private TitleBarBuilder builder;
    private float alpha = 0.6f;
    private MyPhotoPresenter myPhotoPresenter;
    private List<MyPhotoBean> items;
    private MyPhotoAdapter adapter;
    private int page = 1;
    private String f_uid = "";

    private View headerView, zoomView;

    private HeaderViewHolder headerHolder;
    private ZoomViewHolder zoomHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_album);
        ButterKnife.bind(this);
        initTitleBar();
        initViews();
        if (getIntent() == null) {
            return;
        }
        f_uid = getIntent().getStringExtra("f_uid");
        myPhotoPresenter = new MyPhotoPresenter(this);
        myPhotoPresenter.getMyInfo(this, f_uid);
        myPhotoPresenter.getMyPhoto(this, sp.getString("uid"), page, f_uid);
    }

    private void initTitleBar() {
        builder = new TitleBarBuilder(this);
        builder.setTitle("我的相册")
                .setTitleBarBackGround(R.color.black)
                .setAlpha(0.6f)
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setImageRightRes(R.mipmap.btn_img_msg)
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MyAlbumActivity.this, RemindActivity.class));
                    }
                });
    }

    private void initHeaderAndZoomView() {
        headerView = LayoutInflater.from(this).inflate(R.layout.profile_head_view, null);
        zoomView = LayoutInflater.from(this).inflate(R.layout.list_head_zoom_view, null);
        headerHolder = new HeaderViewHolder(headerView);
        zoomHolder = new ZoomViewHolder(zoomView);
    }

    private void headerLayoutSetting() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)) + ScreenUtils.dp2px(this, 80));
        listView.setHeaderLayoutParams(localObject);
    }

    private void initViews() {
        initHeaderAndZoomView();
        items = new ArrayList<>();
        adapter = new MyPhotoAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setParallax(false);
        listView.setZoomView(zoomView);
        listView.setHeaderView(headerView);
        headerLayoutSetting();
        swipeToLoadLayout.setRefreshEnabled(false);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToLoadLayout == null) {
                            return;
                        }
                        swipeToLoadLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToLoadLayout == null) {
                            return;
                        }
                        page++;
                        myPhotoPresenter.getMyPhoto(MyAlbumActivity.this, sp.getString("uid"), page, f_uid);
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });
        listView.getPullRootView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position > 0) {
                        MyPhotoBean bean = adapter.getList().get(position - 1);
                        if (bean == null) {
                            return;
                        }
                        int circle_type = bean.getCircle_type();
                        Intent intent = new Intent(MyAlbumActivity.this, AlbumDetailActivity.class);
                        intent.putExtra("type", circle_type);
                        intent.putExtra("moments_id", bean.getMoments_id());
                        intent.putExtra("is_both", bean.getIs_both());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listView.getPullRootView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0) {
//                    View firstVisibleItemView = view.getChildAt(0);
//                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
//                        Log.d("ListView", "##### 滚动到顶部 #####");
//                    }
//                } else
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = view.getChildAt(view.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == view.getHeight()) {
                        Log.d("ListView", "##### 滚动到底部 ######");
                        swipeToLoadLayout.setLoadMoreEnabled(true);
                    } else {
                        swipeToLoadLayout.setLoadMoreEnabled(false);
                    }
                } else {
                    swipeToLoadLayout.setLoadMoreEnabled(false);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this, "启动我的相册页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, "结束我的相册页面");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (builder != null) {
            builder.setAlpha(alpha);
        }
//        root.setFocusable(true);
//        root.setFocusableInTouchMode(true);
//        root.requestFocus();
        StatService.onResume(this);
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            if (o instanceof List) {
                // 千万别忘了告诉控件加载完毕了哦！
                List<MyPhotoBean> list = (List<MyPhotoBean>) o;
                if (adapter != null)
                    if (page == 1)
                        items.clear();
                items.addAll(list);
                adapter.updateList(items);
            } else if (o instanceof ContactsBean) {
                ContactsBean cb = (ContactsBean) o;
                headerHolder.textNickname.setText(cb.getUser_nickname());

               // ImgUtil.setGlideHead(this, cb.getAvatar(), headerHolder.imagePhoto);
                ImgUtil.setGlideErrorHead(this, cb.getMoments_background_img(),R.mipmap.head, headerHolder.imagePhoto);
                //  Glide.with(this).load(cb.getAvatar()).error(R.mipmap.head).into(headerHolder.imagePhoto);
                headerHolder.textSign.setText(cb.getSignature());
                ImgUtil.setGlideErrorHead(this, cb.getMoments_background_img(), R.mipmap.debo_logo, zoomHolder.imageView);
                // Glide.with(this).load(cb.getMoments_background_img()).error(R.mipmap.debo_logo).into(zoomHolder.imageView);
            }
        } else {
            ToastUtils.showShort(this, message);
        }
    }

    static class HeaderViewHolder {
        @Bind(R.id.image_photo)
        CircleImageView imagePhoto;
        @Bind(R.id.text_nickname)
        TextView textNickname;
        @Bind(R.id.text_sign)
        TextView textSign;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ZoomViewHolder {
        @Bind(R.id.imageView)
        ImageView imageView;

        ZoomViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
