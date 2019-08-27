package com.qcwl.debo.ui.found.joke;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CustomProgressDialog;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class JokeFragment extends Fragment {
    @Bind(R.id.swipe_target)
    RecyclerView recyclerView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    private int page = 1;
    int key = 0;
    private JokeAdapter adapter;
    private List<VideoBean> mListVideo;
    public LocationClient locationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private String latitude, longitude;
    private String city;
    private CustomProgressDialog mProgressDialog;
    private String TAG = "JokeFragment";
    private int index = -1;
    public JokeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joke, container, false);
        ButterKnife.bind(this, view);

        UpdateReceiver updateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("playvidew_update");
        getActivity().registerReceiver(updateReceiver, intentFilter);

        locationClient = new LocationClient(getActivity());
        initLocation();
        locationClient.registerLocationListener(myListener);
        initView();
        return view;
    }

    private void initView() {
        key = getArguments().getInt("key", 0);
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
//              swipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.);
                page = 1;
                loadData();
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
        swipeToLoadLayout.setOnLoadMoreListener(new com.aspsine.swipetoloadlayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                loadData();
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToLoadLayout == null) {
                            return;
                        }
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mListVideo = new ArrayList<>();
        adapter = new JokeAdapter(getActivity(), mListVideo);
        recyclerView.setAdapter(adapter);
        loadData();
    }

    private void loadData() {

        if (key == 0) {
            //关注列表
            getFollowVideo_list();
        } else if (key == 1) {
            //发现列表
            getFoundVideo_list();
        } else if (key == 2) {
            //同城列表
            MPermissions.requestPermissions(this, REQUEST_CODE, permissions);
        }
    }

    private int dp2px(float dpVal) {
        return ScreenUtils.dp2px(getActivity(), dpVal);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //获取视频列表  发现
    private void getFoundVideo_list() {
        //    showProgressDialog("正在获取");
        Api.getFoundVideo_list(SPUtil.getInstance(getActivity()).getString("uid"), page, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                //        hideProgressDialog();
                Log.i("发现列表", "发现apiResponse。。。" + apiResponse);
                if (apiResponse.getCode() == 0) {
                    List<VideoBean> mList = JSON.parseArray(apiResponse.getData(), VideoBean.class);
                    Log.e("page_num", "" + apiResponse.getData());
                    if ((page + "").equals("1")) {
                        mListVideo.clear();
                    }
                    mListVideo.addAll(mList);
                } else {
                    ToastUtils.showShort(getActivity(), apiResponse.getMessage());
                    Log.e("getactivity", apiResponse.getMessage());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    //获取视频列表  同城
    private void getNearbyVideo_list(String city, String lat, String lng) {
        //  showProgressDialog("正在获取");
        Log.i("发现列表", "同城参数。。。" + city + "    " + lat + "    " + lng + "    " + SPUtil.getInstance(getActivity()).getString("uid"));
        Api.getNearbyVideo_list(SPUtil.getInstance(getActivity()).getString("uid"), page, city, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                //       hideProgressDialog();
                Log.i("发现列表", "同城apiResponse。。。" + apiResponse);
                if (apiResponse.getCode() == 0) {
                    List<VideoBean> mList = JSON.parseArray(apiResponse.getData(), VideoBean.class);
                    Log.e("page_num", "" + apiResponse.getPage());
                    if ((page + "").equals("1")) {
                        mListVideo.clear();
                    }
                    mListVideo.addAll(mList);
                } else {
                    //   ToastUtils.showShort(getActivity(), apiResponse.getMessage());
                }
                adapter.notifyDataSetChanged();

            }
        });
    }

    //获取视频列表  关注
    private void getFollowVideo_list() {
        //   showProgressDialog("正在获取");
        Api.getFollowVideo_list(SPUtil.getInstance(getActivity()).getString("uid"), page, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                //         hideProgressDialog();
                Log.i("发现列表", "关注apiResponse。。。" + apiResponse);
                if (apiResponse.getCode() == 0) {
                    List<VideoBean> mList = JSON.parseArray(apiResponse.getData(), VideoBean.class);
                    if ((page + "").equals("1")) {
                        mListVideo.clear();
                    }
                    mListVideo.addAll(mList);
                } else {
                    // ToastUtils.showShort(getActivity(), apiResponse.getMessage());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static final int REQUEST_CODE = 200;

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                latitude = "" + location.getLatitude();    //获取纬度信息
                longitude = "" + location.getLongitude();    //获取经度信息
                city = "" + location.getCity();    //获取经度信息

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNearbyVideo_list(city, latitude, longitude);
                    }
                });
                locationClient.stop();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        //option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        locationClient.setLocOption(option);
    }

    @PermissionGrant(REQUEST_CODE)
    public void requestPermissionSuccess() {
        locationClient.start();
        //注册监听函数
    }

    @PermissionDenied(REQUEST_CODE)
    public void requestPermissionFailed() {
        Toast.makeText(getActivity(), "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog(String msg) {
        mProgressDialog = new CustomProgressDialog(getActivity());
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    //自定义接受网络变化的广播接收器
    class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "............UpdateReceiver="+action);

        }

    }
}
