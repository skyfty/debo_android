package com.qcwl.debo.ui.circle;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PositionActivity extends BaseActivity {
    @Bind(R.id.map_view)
    MapView mMapView;
    @Bind(R.id.text_send)
    TextView tv_send;
    @Bind(R.id.image_back)
    ImageView iv_back;
    @Bind(R.id.marker)
    ImageView img_pos;
    private BaiduMap mBaiduMap;
    private ProgressDialog progressDialog;
    LocationClient mLocClient;
    private BaiduSDKReceiver mBaiduReceiver;
    public static EaseBaiduMapActivity instance = null;
    static BDLocation lastLocation = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private UiSettings mUiSettings;
    private boolean isFirstLocation = true;
    private double lat;
    private double lon;
    private String address;
    private int t = 0;
    public class BaiduSDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String st1 = getResources().getString(com.hyphenate.easeui.R.string.Network_error);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {

                String st2 = getResources().getString(com.hyphenate.easeui.R.string.please_check);
                Toast.makeText(instance, st2, Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(instance, st1, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        Log.i("PositionActivity", "..........进入位置界面");
        ButterKnife.bind(this);

        MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings. setScrollGesturesEnabled(true);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        initMapView();
        mMapView = new MapView(this, new BaiduMapOptions());
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, null));
        showMapWithLocationClient();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mBaiduReceiver = new BaiduSDKReceiver();
        registerReceiver(mBaiduReceiver, iFilter);
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            /** 因某种操作导致地图状态开始改变。
             * @param mapStatus 地图状态改变开始时的地图状态
             * @param，取值有：
             * 1：用户手势触发导致的地图状态改变,比如双击、拖拽、滑动底图
             * 2：SDK导致的地图状态改变, 比如点击缩放控件、指南针图标
             * 3：开发者调用,导致的地图状态改变
             */
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
        t = 1;
            }
            /**
             * 地图状态变化中
             * @param mapStatus 当前地图状态
             */
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }
            /**
             * 地图状态改变结束
             * @param mapStatus 地图状态改变结束后的地图状态
             */
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                Point mpint = mapStatus.targetScreen;
               /* int x = mpint.x;
                int y = mpint.y;
                //Point lntlng = new Point(x,y);
                LatLng lntlng = mapStatus.bound.getCenter();*/

                int[] location =new int[2];
                img_pos.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                Point centerTop = new Point(x,y);
                //获取屏幕点 的坐标位置
                LatLng latlng = mBaiduMap.getProjection().fromScreenLocation(centerTop);
                getInfoFromLAL(latlng);
                Log.i("PositionActivity",".............="+latlng.latitude+"     "+latlng.longitude);

                GeoCoder geocoder=GeoCoder.newInstance();

                geocoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                        Log.i("PositionActivity",".............="+geoCodeResult.getAddress());
                    }
                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                        address=reverseGeoCodeResult.getAddress();
                        ReverseGeoCodeResult.AddressComponent address2 = reverseGeoCodeResult.getAddressDetail();
                        //String addr2 = address2.district+"  "+address2.street+"   "+address2.province +"    "+address2.countryName+"   "+address2.streetNumber;
                        if(!TextUtils.isEmpty(address)){
                            lat=reverseGeoCodeResult.getLocation().latitude;
                            lon=reverseGeoCodeResult.getLocation().longitude;
                             //Toast.makeText(PositionActivity.this,"获取地址失败！",Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(PositionActivity.this,"获取地址成功！",Toast.LENGTH_SHORT).show();
                        //Log.i("PositionActivity","............获取地址失败，请重新获取="+address+ "   "+addr2+"   "+reverseGeoCodeResult.getLocation());
                    }
                });
               boolean b = geocoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
                Log.i("PositionActivity","............b="+b);
            }
        });

    }

   /* public void requestCenterPos(MapStatus status){

        //方法一：

        LatLng mCenterLatLng = status.target;
        *//**获取经纬度*//*
        double lat = mCenterLatLng.latitude;
        double lng = mCenterLatLng.longitude;


        //方法二：

        //获取屏幕点
        //int[] location =new int[2];
        //img_pos.getLocationOnScreen(location);
        //int x = location[0];
        //int y = location[1];
        //Point centerTop = new Point(x,y);
        ////获取屏幕点 的坐标位置
        //LatLng latlng = mBaiduMap.getProjection().fromScreenLocation(centerTop);

    }*/
    // 根据经纬度查询位置
    private void getInfoFromLAL(LatLng point) {
      /*  GeoCoder gc = GeoCoder.newInstance();
        gc.reverseGeoCode(new ReverseGeoCodeOption().location(point));
        gc.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Log.i("PositionActivity", "未能找到结果");
                } else {
                    Log.i("PositionActivity",".............="+point.latitude+"     "+point.longitude+"   "+result.getAddress());
                }
            }
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                Log.i("PositionActivity",".............2222="+point.latitude+"     "+point.longitude+"   "+result.getAddress());
            }
        });*/
       /* GeoCoder geocoder=GeoCoder.newInstance();
        geocoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
        geocoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                Log.i("PositionActivity",".............="+point.latitude+"     "+point.longitude+"   "+geoCodeResult.getAddress());
            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                String address=reverseGeoCodeResult.getAddress();
                if(!TextUtils.isEmpty(address)){
                    lat=reverseGeoCodeResult.getLocation().latitude;
                    lon=reverseGeoCodeResult.getLocation().longitude;
                }
                Log.i("PositionActivity","............获取地址失败，请重新获取");
            }
        });*/
        //
    }
    private void initMapView() {
        mMapView.setLongClickable(true);
    }
    @OnClick({R.id.image_back, R.id.text_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.text_send:
                Intent intent = new Intent();
                if (t == 0) {
                    intent.putExtra("position", lastLocation.getAddrStr());
                }else if (t == 1){
                    intent.putExtra("position", address);
                }
                intent.putExtra("lat",lastLocation.getLatitude()+"");
                intent.putExtra("lng",lastLocation.getLongitude()+"");
                this.setResult(101,intent);
                finish();
                overridePendingTransition(com.hyphenate.easeui.R.anim.slide_in_from_left, com.hyphenate.easeui.R.anim.slide_out_to_right);
                break;
        }
    }
    private void showMapWithLocationClient() {
        String str1 = getResources().getString(com.hyphenate.easeui.R.string.Making_sure_your_location);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(str1);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface arg0) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Log.d("map", "cancel retrieve location");
                finish();
            }
        });

        progressDialog.show();

        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// open gps
        // option.setCoorType("bd09ll");
        // Johnson change to use gcj02 coordination. chinese national standard
        // so need to conver to bd09 everytime when draw on baidu map
        option.setCoorType("gcj02");
        option.setAddrType("all");
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(0);
        mLocClient.setLocOption(option);
    }

   // 设置中心点

    private void setUserMapCenter() {
        Log.v("pcw","setUserMapCenter : lat : "+ lat+" lon : " + lon);
        LatLng cenpt = new LatLng(lat,lon);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onPause();
        lastLocation = null;
    }
    @Override
    protected void onResume() {
        mMapView.onResume();
        if (mLocClient != null) {
            mLocClient.start();
        }
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.onDestroy();
        unregisterReceiver(mBaiduReceiver);
        super.onDestroy();
    }
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            if (location == null) {
                return;
            }
            Log.d("map", "On location change received:" + location);
            Log.d("map", "addr:" + location.getAddrStr() + "     " + location.getLatitude()+"   ="+location.getLongitude());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    if (lastLocation != null) {
                        if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
                            Log.d("map", "same location, skip refresh");
                            // mMapView.refresh(); //need this refresh?
                            return;
                        }

                    }
                    lastLocation = location;
                    //if(isFirstLocation){
                        //
                    // isFirstLocation = false;
                       // setMarker();
                        setUserMapCenter();
                   // }
                    mBaiduMap.clear();
                    lat = lastLocation.getLatitude();
                    lon = lastLocation.getLongitude();
                    LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//                    CoordinateConverter converter = new CoordinateConverter();
//                    converter.coord(llA);
//                    converter.from(CoordinateConverter.CoordType.COMMON);
//                    LatLng convertLatLng = converter.convert();
                    OverlayOptions ooA = new MarkerOptions().position(llA).icon(BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.near)))
                            .zIndex(9).draggable(true);
                    mBaiduMap.addOverlay(ooA);

                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
                    mBaiduMap.animateMapStatus(u);
                }
            });

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }

}
