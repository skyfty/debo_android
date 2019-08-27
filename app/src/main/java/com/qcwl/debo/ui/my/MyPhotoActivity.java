package com.qcwl.debo.ui.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.gigamole.library.ShadowLayout;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.model.MyPhotoBean;
import com.qcwl.debo.presenter.MyPhotoPresenter;
import com.qcwl.debo.presenterInf.MyPhotoPresenterInf;
import com.qcwl.debo.ui.circle.AlbumDetailActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.my.remind.RemindActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.DampScrollView;
import com.qcwl.debo.view.MyListView;
import com.qcwl.debo.view.MyPhotoAdapter;
import com.qcwl.debo.view.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */

public class MyPhotoActivity extends BaseActivity implements MyPhotoPresenterInf {
    private DampScrollView scroll_view;
    private RoundedImageView imageView;
    private RelativeLayout root, relative;
    private MyListView listView;
    private TextView name, sign;
    private RoundedImageView image;
    private PullToRefreshLayout pullToRefreshLayout;
    private MyPhotoAdapter adapter;
    private TitleBarBuilder builder;
    private MyPhotoPresenter myPhotoPresenter;
    private List<MyPhotoBean> list;
    private int page = 1;

    private String f_uid = "";

    private float alpha = 0.6f;
    private ShadowLayout sl;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.my_photo);
        initTitleBar();
        initView();
        monitor();
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
                        startActivity(new Intent(MyPhotoActivity.this, RemindActivity.class));
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

    private void initView() {
        myPhotoPresenter = new MyPhotoPresenter(this);
        list = new ArrayList<>();
        scroll_view = (DampScrollView) findViewById(R.id.scroll_view);
        root = (RelativeLayout) findViewById(R.id.root);
        imageView = (RoundedImageView) findViewById(R.id.imageview);
        listView = (MyListView) findViewById(R.id.listView);
        relative = (RelativeLayout) findViewById(R.id.relative);
        name = (TextView) findViewById(R.id.name);
        image = (RoundedImageView) findViewById(R.id.image);
        sign = (TextView) findViewById(R.id.sign);
        pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pullToRefreshLayout);
        sl = (ShadowLayout) findViewById(R.id.sl);
        scroll_view.setZoomView(imageView, relative);

        adapter = new MyPhotoAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    int circle_type = adapter.getList().get(position).getCircle_type();
                    Intent intent = new Intent(MyPhotoActivity.this, AlbumDetailActivity.class);
                    intent.putExtra("type", circle_type);
                    intent.putExtra("moments_id", adapter.getList().get(position).getMoments_id());
                    intent.putExtra("is_both", adapter.getList().get(position).getIs_both());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        sl.setIsShadowed(true);
        sl.setShadowAngle(45);//阴影角度
        sl.setShadowRadius(2);//阴影半径
        sl.setShadowDistance(3);//阴影距离
        sl.setShadowColor(R.color.shadowcolor);//阴影颜色

        if (getIntent() == null) {
            return;
        }
        f_uid = getIntent().getStringExtra("f_uid");
        myPhotoPresenter.getMyInfo(this, f_uid);
        myPhotoPresenter.getMyPhoto(this, sp.getString("uid"), page, f_uid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (builder != null) {
            builder.setAlpha(alpha);
        }
        root.setFocusable(true);
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        StatService.onResume(this);
    }

    private void monitor() {
       /* scroll_view.setOnScrollListener(new DampScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < 500) {
                    alpha = ((float) scrollY) / 500;
                } else {
                    alpha = 1f;
                }
                builder.setAlpha(alpha);
            }

        });*/
        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

            }

            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        myPhotoPresenter.getMyPhoto(MyPhotoActivity.this, sp.getString("uid"), page, f_uid);
                    }
                }.sendEmptyMessageDelayed(0, 1000);
            }
        });

    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            if (o instanceof List) {
                // 千万别忘了告诉控件加载完毕了哦！
                List<MyPhotoBean> l = (List<MyPhotoBean>) o;
                if (adapter != null)
                    if (page == 1)
                        list.clear();
                list.addAll(l);
                page++;
                adapter.updateList(list);
            } else if (o instanceof ContactsBean) {
                ContactsBean cb = (ContactsBean) o;
                name.setText(cb.getUser_nickname());
                if (TextUtils.isEmpty(cb.getAvatar())) {
                    if (Util.isOnMainThread()) {
                        ImgUtil.setGlideHead(this,R.mipmap.head,image);
                       // Glide.with(this).load(R.mipmap.head).into(image);
                    }
                } else {
                    if (Util.isOnMainThread()) {
                        ImgUtil.setGlideHead(this,cb.getAvatar(),image);
                        //Glide.with(this).load(cb.getAvatar()).into(image);
                    }
                }
                if (TextUtils.isEmpty(cb.getMoments_background_img())) {
                    if (Util.isOnMainThread()) {
                        ImgUtil.setGlideHead(this,R.mipmap.debo_logo,imageView);
                        //Glide.with(this).load(R.mipmap.debo_logo).into(imageView);
                    }
                } else {
                    if (Util.isOnMainThread()) {
                        ImgUtil.setGlideHead(this,cb.getMoments_background_img(),imageView);
                       //Glide.with(this).load(cb.getMoments_background_img()).into(imageView);
                    }
                }
                sign.setText(cb.getSignature());
            }
        } else {
            ToastUtils.showShort(this, message);
        }
        // builder.setProgress(View.GONE);
        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }
}
