package com.qcwl.debo.ui.found.near;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.found.near.bean.NearBean;
import com.qcwl.debo.ui.found.near.bean.TrumpetBean;
import com.qcwl.debo.ui.found.near.contact.NearContact;
import com.qcwl.debo.ui.found.near.presenter.NearPresenter;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.zhy.adapter.abslistview.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class NearActivity extends BaseActivity implements BaiduMap.OnMapLoadedCallback, SensorEventListener, NearContact.View {

    @Bind(R.id.map_view)
    MapView mapView;

    BaiduMap baiduMap;
    MapStatus ms;
    @Bind(R.id.image_trumpet)
    ImageView imageTrumpet;
    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.text_distance)
    TextView textDistance;
    @Bind(R.id.layout_send_message)
    View layoutSendMessage;

    private int distance = 0;

    private double latitude, longitude;

    List<NearBean> nearList = null;

    List<TrumpetBean> ownList = new ArrayList<>();
    private NearPresenter presenter;

    private String uid = "";

    private final int space = 0;

    private boolean isFirstLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near);
        ButterKnife.bind(this);
        uid = sp.getString("uid");
        initTitleBar();
        layoutSendMessage.setVisibility(View.GONE);

        baiduMap = mapView.getMap();

        baiduMap.setMyLocationEnabled(true);
        baiduMap.getUiSettings().setCompassEnabled(true);
        baiduMap.setOnMapLoadedCallback(this);

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker == null) {
                    return false;
                }
                Bundle bundle = marker.getExtraInfo();
                if (bundle == null) {
                    return false;
                }
                NearBean nearBean = (NearBean) bundle.getSerializable("info");
                startActivity(new Intent(NearActivity.this, ContactsContentActivity.class)
                        .putExtra("my_mobile", SPUtil.getInstance(NearActivity.this).getString("phone"))
                        .putExtra("mobile", nearBean.getMobile())
                        .putExtra("type", "1"));
                return false;
            }
        });
        //MPermissions.requestLocationPermissions(this, REQUEST_CODE, permissions);
        requestLocationPermissions();
        presenter = new NearPresenter(this);
        imageTrumpet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pop == null) {
                    initTrumpetPop();
                }
                pop.showAsDropDown(imageTrumpet);
            }
        });

        textDistance.setText("距离你" + space + "米以内邻近的人");
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
        if (presenter != null) {
            presenter.getTrumpetList(this, uid);
        }

        StatService.onResume(this);
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("邻近的人")
                .setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        })
                .setImageRightRes(R.mipmap.ic_trumpet_white).setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NearActivity.this, PurchaseTrumpetActivity.class));
            }
        });
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        StatService.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        StatService.onPageEnd(this, "结束附近的人页面");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this, "启动附近的人页面");
    }


    /**
     * 向地图添加Marker点
     */
    public void addMarkers(List<NearBean> list) {
        baiduMap.clear();
        for (final NearBean nearBean : list) {
            loadImage(nearBean, new LoadImageListener() {
                @Override
                public void loadResult(BitmapDescriptor descriptor) {
                    setMarkerData(nearBean, descriptor);
                }
            });
        }
    }

    Marker marker = null;

    private void setMarkerData(NearBean nearBean, BitmapDescriptor descriptor) {
        MarkerOptions options = new MarkerOptions();
        options.icon(descriptor)
                .position(new LatLng(nearBean.getLat(), nearBean.getLng()))
                .animateType(MarkerOptions.MarkerAnimateType.grow);
        marker = (Marker) (baiduMap.addOverlay(options));
        Bundle bundle = new Bundle();
        bundle.putSerializable("info", nearBean);
        marker.setExtraInfo(bundle);
    }


    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(this, dpVal);
    }

    @Override
    public void onMapLoaded() {
        ms = new MapStatus.Builder().zoom(13.0f).build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private int mCurrentDirection = 0;
    MyLocationData locData;
    private float mCurrentAccracy;
    private Double lastX = 0.0;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(latitude)
                    .longitude(longitude).build();
            baiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private int flag = 0;

    private void setLocationCenter() {
//        if (distance == 0) {
//            return;
//        }
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
    public void doTrumpetListSuccess(List<TrumpetBean> items) {
        if (items != null) {
            ownList.clear();
            for (TrumpetBean item : items) {
                if (item.getIs_free() == 2) {
                    ownList.add(item);
                    layoutSendMessage.setVisibility(View.VISIBLE);
                } else {
                    if (item.getIs_purchase() == 1) {
                        ownList.add(item);
                        layoutSendMessage.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (ownList.size() > 0) {
                int d = 0;
                int pos = 0;
                for (int i = 0; i < ownList.size(); i++) {
                    distance = ownList.get(i).getDistance();
                    if (d <= distance) {
                        d = distance;
                        pos = i;
                    }
                }
                distance = d;
                imageSetting(ownList);
                imageTrumpet.setImageResource(ownList.get(pos).getImgId());
            } else {
                distance = 0;
            }
            //textDistance.setText("距离你" + distance + "米以内附近的人");
            editContent.setHint("向" + distance + "米以内的人喊话");
            if (flag == 0) {
                //setLocationCenter();
            }
        }
    }

    @Override
    public void doNearListSuccess(List<NearBean> list) {
        this.nearList = list;
        if (nearList != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMarkers(nearList);
                }
            });
        }
    }

    @Override
    public void doTrumpetShout() {
        ToastUtils.showShort(this, "发送完成");
        editContent.setText("");
    }

    @Override
    public void doFailure(int code) {

    }

    @OnClick({R.id.img_location, R.id.text_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_location:
                locationClient.start();
                break;
            case R.id.text_send:
                String content = editContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShort(this, "喊话内容不能为空");
                    return;
                }
                presenter.trumpetShout(this, uid, "" + latitude, "" + longitude, "" + distance, content);
                break;
        }
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
            presenter.getNearList(NearActivity.this, uid, latitude, longitude, space);//distance
            isFirstLocation = false;
        }
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

    View layout, layout2, layout3;
    MarkerViewHolder holder, holder2, holder3;

    private MarkerViewHolder initMarkerView(View view) {
        return new MarkerViewHolder(view);
    }

    private void loadImage(final NearBean nearBean, final LoadImageListener listener) {
        layout = LayoutInflater.from(this).inflate(R.layout.layout_marker, null);
        layout2 = LayoutInflater.from(this).inflate(R.layout.layout_marker, null);
        layout3 = LayoutInflater.from(this).inflate(R.layout.layout_marker, null);
        holder = initMarkerView(layout);
        holder2 = initMarkerView(layout2);
        holder3 = initMarkerView(layout3);
        switch (nearBean.getSex()) {//性别；0：保密，1：男；2：女
            case 0:
                holder.imageLayout.setBackgroundResource(R.mipmap.ic_map_marker_gray);
                loadImage(nearBean.getAvatar(), holder.imageView, layout, listener);
                Log.e("===", "sex=0");
                break;
            case 1:
                holder2.imageLayout.setBackgroundResource(R.mipmap.ic_map_marker_blue);
                loadImage(nearBean.getAvatar(), holder2.imageView, layout2, listener);
                Log.e("===", "sex=1");
                break;
            case 2:
                holder3.imageLayout.setBackgroundResource(R.mipmap.ic_map_marker_red);
                loadImage(nearBean.getAvatar(), holder3.imageView, layout3, listener);
                Log.e("===", "sex=2");
                break;
            default:
                break;
        }
//        layout.setLayoutParams(new RelativeLayout.LayoutParams(dp2px(49), dp2px(66)));
    }

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

    private PopupWindow pop;

    private void initTrumpetPop() {
        pop = new PopupWindow(this);
        pop.setWidth(dp2px(50));
        pop.setHeight(-2);
        View view = LayoutInflater.from(this).inflate(R.layout.pop_trumpet, null);
        pop.setContentView(view);
        pop.setFocusable(true);
        pop.setTouchable(true);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        imageSetting(ownList);
        listView.setAdapter(new CommonAdapter<TrumpetBean>(this, R.layout.item_pop_trumpet, ownList) {
            @Override
            protected void convert(com.zhy.adapter.abslistview.ViewHolder viewHolder, TrumpetBean item, int position) {
                ImageView imageView = viewHolder.getView(R.id.image_view);
                //imageView.setImageResource(item.getImgId());
                ImgUtil.load(mContext, item.getUrl(), imageView);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imageTrumpet.setImageResource(ownList.get(position).getImgId());
                //ImgUtil.load(NearActivity.this, ownList.get(position).getImgId(), imageTrumpet);
                editContent.setHint("向" + ownList.get(position).getDistance() + "米以内的人喊话");
                //textDistance.setText("距离你" + ownList.get(position).getDistance() + "米以内附近的人");
                //setLocationCenter();
                pop.dismiss();
            }
        });
    }

    private void imageSetting(List<TrumpetBean> items) {
        for (int i = 0; i < items.size(); i++) {
            switch (i % 5) {
                case 0:
                    items.get(i).setImgId(R.mipmap.ic_trumpet_yellow);
                    break;
                case 1:
                    items.get(i).setImgId(R.mipmap.ic_trumpet_orange);
                    break;
                case 2:
                    items.get(i).setImgId(R.mipmap.ic_trumpet_red);
                    break;
                case 3:
                    items.get(i).setImgId(R.mipmap.ic_trumpet_blue);
                    break;
                case 4:
                    items.get(i).setImgId(R.mipmap.ic_trumpet_purple);
                    break;
                default:
                    break;
            }
        }
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
