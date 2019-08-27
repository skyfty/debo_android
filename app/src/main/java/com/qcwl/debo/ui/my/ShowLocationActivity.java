package com.qcwl.debo.ui.my;

import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.utils.TitleBarBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ShowLocationActivity extends BaseActivity implements BaiduMap.OnMapLoadedCallback, SensorEventListener {

    @Bind(R.id.map_view)
    MapView mapView;

    BaiduMap baiduMap;
    MapStatus ms;

    private double myLat, myLng;
    private double hisLat, hisLng;
    //39.9252409842,116.6061833657

    private String mobile, nickname, avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        ButterKnife.bind(this);
        initTitleBar();

        initData();
    }

    private void initData() {
        baiduMap = mapView.getMap();

        baiduMap.setMyLocationEnabled(true);
        baiduMap.getUiSettings().setCompassEnabled(true);
        baiduMap.setOnMapLoadedCallback(this);
        //requestLocationPermissions();

        if (getIntent() != null) {
            //lat,lng,mobile、user_nickname、avatar
            hisLat = Double.parseDouble(getIntent().getStringExtra("lat"));
            hisLng = Double.parseDouble(getIntent().getStringExtra("lng"));
            mobile = getIntent().getStringExtra("mobile");//发起者的手机号
            nickname = getIntent().getStringExtra("user_nickname");//被查询的人的昵称
            avatar = getIntent().getStringExtra("avatar");//被查询的人的头像
        }
        LatLng latLng = new LatLng(hisLat, hisLng);
//        drawCircle(latLng, 500);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(13.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        layout = LayoutInflater.from(this).inflate(R.layout.layout_marker, null);
        holder = new MarkerViewHolder(layout);
        holder.imageLayout.setBackgroundResource(R.mipmap.ic_map_marker_gray);
        loadImage(avatar, holder.imageView, layout, new LoadImageListener() {
            @Override
            public void loadResult(BitmapDescriptor descriptor) {
                addMarker(descriptor);
            }
        });
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("查看位置")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

//    @OnClick(R.id.img_location)
//    public void onViewClicked() {
//        locationClient.start();
//    }


    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    Marker marker = null;

    private void addMarker(BitmapDescriptor descriptor) {
        MarkerOptions options = new MarkerOptions();
        options.icon(descriptor)
                .position(new LatLng(hisLat, hisLng))
                .animateType(MarkerOptions.MarkerAnimateType.grow);
        marker = (Marker) (baiduMap.addOverlay(options));
    }

    private View layout = null;
    private MarkerViewHolder holder = null;

    private void loadImage(String imgUrl, final ImageView imageView, final View view, final LoadImageListener listener) {
        if (null != this && !this.isFinishing())
            Glide.with(this).load(imgUrl).asBitmap()
                    .placeholder(R.mipmap.head).error(R.mipmap.head)
                    .transform(new CropCircleTransformation(this))
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            imageView.setImageResource(R.mipmap.head);
                            listener.loadResult(BitmapDescriptorFactory.fromView(view));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap bitmap, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            imageView.setImageBitmap(bitmap);
                            listener.loadResult(BitmapDescriptorFactory.fromView(view));
                            return false;
                        }
                    })
                    .into(imageView);
    }

    private interface LoadImageListener {
        void loadResult(BitmapDescriptor descriptor);
    }

    private void drawCircle(LatLng latLng, int radius) {
        //定义一个圆 ： 圆心+半径
        //1.创建自己
        CircleOptions circleOptions = new CircleOptions();
        //2.给自己设置数据
        circleOptions.center(latLng) //圆心
                .radius(radius)//半径 单位米
                .fillColor(0x100000ff)//填充色
                .stroke(new Stroke(2, 0x3000ff00));//边框宽度和颜色

        //3.把覆盖物添加到地图中
        baiduMap.addOverlay(circleOptions);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(myLat)
                    .longitude(myLng).build();
            baiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onMapLoaded() {
        ms = new MapStatus.Builder().zoom(13.0f).build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    @Override
    public void locationSuccessResult(BDLocation location) {
        super.locationSuccessResult(location);
        myLat = location.getLatitude();    //获取纬度信息
        myLng = location.getLongitude();    //获取经度信息
        setLocationCenter();
        locationClient.stop();
    }


    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private int mCurrentDirection = 0;
    MyLocationData locData;
    private float mCurrentAccracy = 500;
    private Double lastX = 0.0;

    private void setLocationCenter() {
        LatLng latLng = new LatLng(myLat, myLng);

        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        locData = new MyLocationData.Builder()
                .accuracy(mCurrentAccracy)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection)
                .latitude(myLat)
                .longitude(myLng).build();
        baiduMap.setMyLocationData(locData);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(13.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    class MarkerViewHolder {
        @Bind(R.id.layout)
        RelativeLayout imageLayout;
        @Bind(R.id.image_view)
        ImageView imageView;

        MarkerViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
