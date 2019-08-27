package com.qcwl.debo.ui.found.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mobstat.StatService;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.near.bean.NearBean;
import com.qcwl.debo.ui.found.near.bean.TrumpetBean;
import com.qcwl.debo.ui.found.near.contact.NearContact;
import com.qcwl.debo.ui.found.near.presenter.NearPresenter;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShakeNearbyActivity extends BaseActivity implements BaiduMap.OnMapLoadedCallback, NearContact.View {

    @Bind(R.id.map_near)
    MapView mMapNear;
    @Bind(R.id.img_head_portrait)
    RoundedImageView mImgHeadPortrait;
    BaiduMap baiduMap;
    MapStatus ms;
    @Bind(R.id.linear_nobody)
    LinearLayout mLinearNobody;
    @Bind(R.id.img_man)
    CheckBox mImgMan;
    @Bind(R.id.img_woman)
    CheckBox mImgWoman;
    @Bind(R.id.text_chat)
    Button mTextChat;
    @Bind(R.id.video_chat)
    Button mVideoChat;
    @Bind(R.id.linear_lot_person)
    LinearLayout mLinearLotPerson;


    private int flag = 0;
    private double latitude, longitude;
    private float mCurrentAccracy;
    private final int space = 0;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    MyLocationData locData;
    private int mCurrentDirection = 0;
    private boolean isFirstLocation = true;
    private NearPresenter presenter;
    private String uid = "";
    List<NearBean> nearList = null;
    private int sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_nearby);
        ButterKnife.bind(this);
        uid = sp.getString("uid");
        initTitleBar();
        initMap();
        if (TextUtils.isEmpty(sp.getString("headsmall"))) {
            ImgUtil.setGlideHead(ShakeNearbyActivity.this, R.mipmap.head, mImgHeadPortrait);
            //Glide.with(ShakeNearbyActivity.this).load(R.mipmap.head).into(mImgHeadPortrait);
        } else {
            ImgUtil.setGlideHead(ShakeNearbyActivity.this, sp.getString("headsmall"), mImgHeadPortrait);
            //Glide.with(ShakeNearbyActivity.this).load(sp.getString("headsmall")).into(mImgHeadPortrait);
        }
    }

    private void initMap() {
        baiduMap = mMapNear.getMap();
        baiduMap.setMyLocationEnabled(true);
        baiduMap.getUiSettings().setCompassEnabled(true);
        baiduMap.setOnMapLoadedCallback(this);
        requestLocationPermissions();
        presenter = new NearPresenter(this);
    }

    @Override
    protected void onResume() {
        mMapNear.onResume();
        super.onResume();
//        initMap();
        if (presenter != null) {
            presenter.getTrumpetList(this, uid);
        }
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle("附近")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void setLocationCenter() {
        flag = 1;
        LatLng latLng = new LatLng(latitude, longitude);
        mCurrentAccracy = space; //distance;//location.getRadius();
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        locData = new MyLocationData.Builder()
                .accuracy(mCurrentAccracy)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection)
                .latitude(latitude)
                .longitude(longitude).build();
        baiduMap.setMyLocationData(locData);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(13.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    protected void onPause() {
        mMapNear.onPause();
        StatService.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapNear.onDestroy();
        StatService.onPageEnd(this, "结束附近的人页面");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initMap();
        StatService.onPageStart(this, "启动附近的人页面");
    }

    @Override
    public void onMapLoaded() {
        ms = new MapStatus.Builder().zoom(13.0f).build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    @Override
    public void locationSuccessResult(BDLocation location) {
        super.locationSuccessResult(location);
        latitude = location.getLatitude();    //获取纬度信息
        longitude = location.getLongitude();    //获取经度信息
        Log.i("BaiduLocationApiDem", "lat=" + latitude + ",lng=" + longitude);
        setLocationCenter();
        locationClient.stop();
        if (isFirstLocation) {
            presenter.getNearList(ShakeNearbyActivity.this, uid, latitude, longitude, space);//distance
            isFirstLocation = false;
        }
    }

    @Override
    public void doTrumpetListSuccess(List<TrumpetBean> items) {

    }

    @Override
    public void doNearListSuccess(List<NearBean> list) {
        this.nearList = list;
        if (nearList != null && nearList.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLinearNobody.setVisibility(View.GONE);
                    mLinearLotPerson.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLinearNobody.setVisibility(View.VISIBLE);
                    mLinearLotPerson.setVisibility(View.GONE);

                }
            });
        }
    }

    @Override
    public void doTrumpetShout() {

    }

    @Override
    public void doFailure(int code) {

    }

    @OnClick({R.id.img_man, R.id.img_woman, R.id.text_chat, R.id.video_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_man:
                sex = 1;
                mImgWoman.setChecked(false);
                break;
            case R.id.img_woman:
                sex = 2;
                mImgMan.setChecked(false);
                break;
            case R.id.text_chat:
                choicesex(sex, 1);
                break;
            case R.id.video_chat:
                choicesex(sex, 2);
                break;
        }
    }

    private void choicesex(int sex, int type) {
        Intent intent = new Intent(this, ChatDetailsActivity.class);
        if (mImgMan.isChecked() || mImgWoman.isChecked()) {
            if (sex == 1) {
                intent.putExtra("sex", 1);
            } else if (sex == 2) {
                intent.putExtra("sex", 2);
            }
        } else {
            if (SPUtil.getInstance(this).getString("sex").equals("1")) {
                intent.putExtra("sex", 2);
            } else if (SPUtil.getInstance(this).getString("sex").equals("2")) {
                intent.putExtra("sex", 1);
            } else
                ToastUtils.showShort(this, "您的性别保密,找不到匹配的人,请选择性别");
        }
        intent.putExtra("latitude", latitude + "");
        intent.putExtra("longitude", longitude + "");
        intent.putExtra("type", type);
        intent.putExtra("key", 1);
        startActivity(intent);
    }
}
