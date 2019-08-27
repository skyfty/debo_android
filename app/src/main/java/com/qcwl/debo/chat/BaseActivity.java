package com.qcwl.debo.chat;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.gyf.barlibrary.ImmersionBar;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.utils.ActivityManager;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CustomProgressDialog;
import com.qcwl.debo.widget.DemoHelper;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;


/**
 * Created by Administrator on 2016/9/22.
 */

public class BaseActivity extends EaseBaseActivity {
    private ConversationSqlite cs;
    private SQLiteDatabase db;
    public SPUtil sp;
    private String sql = "delete from conversation";
    private String mysql = "delete from person";
    private CustomProgressDialog mProgressDialog;
    private String TAG = "BaseActivity";
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initLocationClient();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityManager.getActivityManager().addActivity(this);
        sp = SPUtil.getInstance(this);

        // setSendLogStrategy已经@deprecated，建议使用新的start接口
        // 如果没有页面和自定义事件统计埋点，此代码一定要设置，否则无法完成统计
        // 进程第一次执行此代码，会导致发送上次缓存的统计数据；若无上次缓存数据，则发送空启动日志
        // 由于多进程等可能造成Application多次执行，建议此代码不要埋点在Application中，否则可能造    成启动次数偏高
        // 建议此代码埋点在统计路径触发的第一个页面中，若可能存在多个则建议都埋点
        StatService.start(this);

        //   为了方便开发者进行调试，SDK支持打开调试开关查看logcat日志数据：
        // 开发时调用，建议上线前关闭，以免影响性能
        StatService.setDebugOn(true);
    }

    @Override
    public void statusBarSetting() {
        super.statusBarSetting();
        setTransparentStatusBar();
    }

    private ImmersionBar mImmersionBar = null;

    public void setTransparentStatusBar() {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(false)
                .keyboardEnable(true)
                .init();
    }

    public void setDefaultStatusBar() {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(true)
                .statusBarColor(R.color.default_status_bar)
                .keyboardEnable(true)
                .init();
    }

    private void logOut() {
        DemoHelper.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {

                runOnUiThread(new Runnable() {
                    public void run() {
                        cs = ConversationSqlite.getInstance(BaseActivity.this);
                        db = cs.getWritableDatabase();
                        if (db.isOpen()) {
                            db.execSQL(sql);
                            db.execSQL(mysql);
//                        db.close();
//                        cs.close();
                            sp.setIsLogin(false);
                            sp.setString("uid", "");
                        }
                        ActivityManager.getActivityManager().finishAll();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        ToastUtils.showShort(BaseActivity.this, "退出失败");
                    }
                });
            }
        });
    }

    public void clearDatas() {
        logOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }

    public void showProgressDialog(String msg) {
        mProgressDialog = new CustomProgressDialog(this);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Util.isOnMainThread() && !this.isFinishing()) {
            Glide.with(this).pauseRequests();
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
        ActivityManager.getActivityManager().removeActivity(this);
    }

    public boolean onTouchEvent(MotionEvent event) {
//        if (null != this.getCurrentFocus()) {
//            /**
//             * 点击空白位置 隐藏软键盘
//             */
//            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
//        }
        hideSoftInput();
        return super.onTouchEvent(event);
    }

    private void initLocationClient() {
        locationClient = new LocationClient(getApplicationContext());
        initLocation();
        locationClient.registerLocationListener(myListener);
    }

    public LocationClient locationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private String send_time, the_other_mobile;
    private boolean isSendMessage = false;

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            Log.i(TAG, "....接受到消息");
            for (EMMessage message : messages) {
                DemoHelper.getInstance().getNotifier().onNewMsg(message);
                String value = message.getStringAttribute("location", "");
                if ("send".equals(value)) {
                    isSendMessage = true;
                    send_time = message.getStringAttribute("send_time", "");
                    the_other_mobile = message.getStringAttribute("mobile", "");
                    requestLocationPermissions();
                } else if ("receive".equals(value)) {
                    receiveLocationResult(message);
                } else if ("timeout".equals(value)) {

                }
            }
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            // red packet code : 处理红包回执透传消息
            for (EMMessage message : messages) {
                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message
                        .getBody();
                final String action = cmdMsgBody.action();// 获取自定义action
            }

            // end of red packet code
            refreshUIWithMessage();
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
        //消息撤回
            Log.i(TAG,".....调用了撤回方法");
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };

    public void requestLocationPermissions() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        //MPermissions.requestLocationPermissions(this, CODE_LOCATION, permissions);
        new RxPermissions(this)
                .request(permissions)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean granted) {
                        if (granted) {
                            locationClient.start();
                        } else {
                            Toast.makeText(BaseActivity.this, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                if (isSendMessage) {
                    sendEMMessage(location);
                } else {
                    locationSuccessResult(location);
                }
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

    private void sendEMMessage(BDLocation location) {
        isSendMessage = false;
        double lat = location.getLatitude();    //获取纬度信息
        double lng = location.getLongitude();    //获取经度信息
        Map<String, String> map = new HashMap<>();
        map.put("lat", "" + lat);
        map.put("lng", "" + lng);
        map.put("my_mobile", "" + sp.getString("phone"));
        map.put("the_other_mobile", "" + the_other_mobile);
        map.put("send_time", "" + send_time);
        Api.receiveLocation(map, new ApiResponseHandler(BaseActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {

            }
        });
    }

    //处理收到消息后的结果
    public void receiveLocationResult(EMMessage message) {
    }

    //MainActivity中消息刷新处理
    public void refreshUIWithMessage() {

    }

    //定位结果处理
    public void locationSuccessResult(BDLocation location) {

    }

    @Override
    public void finish() {
        hideSoftInput();
        super.finish();
    }

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
