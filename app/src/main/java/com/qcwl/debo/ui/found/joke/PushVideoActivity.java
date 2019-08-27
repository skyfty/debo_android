package com.qcwl.debo.ui.found.joke;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.vod.upload.VODUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODUploadClient;
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo;
import com.alibaba.sdk.android.vod.upload.model.VodInfo;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.RedPacketRecorderActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.GetRedPacketRecordBean;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CustomProgressDialog;
import com.qcwl.debo.view.LoadingDialog;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.apache.http.Header;
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;

/**
 * Created by qcwl on 2017/11/8.
 */

public class PushVideoActivity extends BaseActivity{
    private TextView name,describe,tv_push;
    private int index = 0;
    private  String filepath;
    private String upload_address="";
    private String upload_auth="";
    private String video_id="";
    private VODUploadClient uploader;
    private  String city;
    public LocationClient locationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private String latitude, longitude;
    private CustomProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushvideo_layout);
        filepath = getIntent().getStringExtra("filepath");
        ButterKnife.bind(this);
        initTitleBar();
        // 打开日志。
        OSSLog.enableLog();

        locationClient = new LocationClient(getApplicationContext());
        initLocation();
        locationClient.registerLocationListener(myListener);

        MPermissions.requestPermissions(this, REQUEST_CODE, permissions);

        uploader = new VODUploadClientImpl(getApplicationContext());
        VODUploadCallback callback = new VODUploadCallback() {
            @Override
            public void onUploadSucceed(UploadFileInfo info) {
                //上传成功
                OSSLog.logDebug("onsucceed ------------------" + info.getFilePath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // video_success_notyfy(latitude,longitude,city, "","","");
                    }
                });
            }

            @Override
            public void onUploadFailed(UploadFileInfo info, String code, String message) {
                //上传失败
                OSSLog.logError("onfailed ------------------ " + info.getFilePath() + " " + code + " " + message);
                hideProgressDialog();
                ToastUtils.showShort(PushVideoActivity.this,"上传失败"+"message");
            }

            @Override
            public void onUploadProgress(UploadFileInfo info, long uploadedSize, long totalSize) {
                //上传进度
                OSSLog.logDebug("onProgress ------------------ " + info.getFilePath() + " " + uploadedSize + " " + totalSize);
            }

            @Override
            public void onUploadTokenExpired() {
               // getShotVideoToken();//tst方式
                getNew_ShotVideoAdress();
            }

            @Override
            public void onUploadRetry(String code, String message) {
                //上传过程中，状态由正常切换为异常时触发
                OSSLog.logError("onUploadRetry ------------- ");
            }

            @Override
            public void onUploadRetryResume() {
                //上传过程中，从异常中恢复时触发
                OSSLog.logError("onUploadRetryResume ------------- ");
            }

            @Override
            public void onUploadStarted(UploadFileInfo uploadFileInfo) {
                uploader.setUploadAuthAndAddress(uploadFileInfo, upload_auth, upload_address);
                OSSLog.logError("onUploadStarted ------------- ");
                OSSLog.logError("file path:" + uploadFileInfo.getFilePath() +
                        ", endpoint: " + uploadFileInfo.getEndpoint() +
                        ", bucket:" + uploadFileInfo.getBucket() +
                        ", object:" + uploadFileInfo.getObject() +
                        ", status:" + uploadFileInfo.getStatus());
            }
        };
        uploader.init(callback);
        initView();
    }
    private VodInfo getVodInfo() {
        index++;
        VodInfo vodInfo = new VodInfo();

        vodInfo.setTitle(name.getText().toString());//标题
        vodInfo.setDesc(describe.getText().toString());//描述
        vodInfo.setCateId(index);
        vodInfo.setIsProcess(true);
        vodInfo.setCoverUrl("");//http://files.jb51.net/file_images/article/201610/20161027113728286.png?2016927113755
        List<String> tags = new ArrayList<>();
        tags.add("标签" + index);
        vodInfo.setTags(tags);
        vodInfo.setIsShowWaterMark(false);
        vodInfo.setPriority(7);
        return vodInfo;
    }

    private void initView() {
        name = (TextView) findViewById(R.id.name);
        describe = (TextView) findViewById(R.id.describe);
        tv_push = (TextView) findViewById(R.id.tv_push);
        uploader.clearFiles();
        uploader.addFile(filepath,
                getVodInfo());

     //   getShotVideoAdress(getVodInfo().getTitle(),filepath,getVodInfo().getCoverUrl(),getVodInfo().getDesc());

        tv_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传
                if (name.getText().toString().isEmpty()){
                    ToastUtils.showShort(PushVideoActivity.this,"请填写视频名称");
                }else if (describe.getText().toString().isEmpty()){
                    ToastUtils.showShort(PushVideoActivity.this,"请填写视频描述");
                }else {
                    getShotVideoAdress(name.getText().toString(),filepath,getVodInfo().getCoverUrl(),describe.getText().toString());
                }
            }
        });
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("发布视频").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //请求过期 更新视频地址和凭证
    private  void getNew_ShotVideoAdress() {
        showProgressDialog("正在请求");
        Api.getNew_ShotVideoAdress(video_id,new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    JSONObject jsonObject = JSON.parseObject(apiResponse.getData());
                    upload_auth = jsonObject.getString("upload_auth");
                    Log.i(" 更新视频地址",jsonObject.toString());
                    uploader.resumeWithAuth(PushVideoActivity.this.upload_auth);
                } else {
                    ToastUtils.showShort(PushVideoActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    //视频地址和凭证
    private  void getShotVideoAdress(String title,String filenamenew,String url,String describe) {
        showProgressDialog("正在请求");
        Api.getShotVideoAdress(title,filenamenew,url,describe,""+latitude,""+longitude,city,sp.getString("uid"),new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    JSONObject jsonObject = JSON.parseObject(apiResponse.getData());
                    upload_auth = jsonObject.getString("upload_auth");
                    upload_address = jsonObject.getString("upload_address");
                    video_id = jsonObject.getString("video_id");
                    uploader.start();
                } else {
                    ToastUtils.showShort(PushVideoActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    //上传成功之后 调取
    private  void video_success_notyfy(String lat,String lng,String city,String Title,String Description,File file) {
        showProgressDialog("正在上传");
        Log.i("上传成功之后参数",video_id+"   "+lat+"    "+lng+"    "+city+"    "+sp.getString("uid"));
        Api.video_success_notyfy(video_id,lat,lng,city,sp.getString("uid"), Title, Description, file,new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    PushVideoActivity.this.finish();
                } else {
                    ToastUtils.showShort(PushVideoActivity.this, apiResponse.getMessage());
                }
                hideProgressDialog();
            }

            @Override
            public void onFailure(String errMessage) {
                super.onFailure(errMessage);
                ToastUtils.showShort(PushVideoActivity.this, errMessage.toString());
            }
        });
    }


    public static final int REQUEST_CODE = 200;

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                latitude = ""+location.getLatitude();    //获取纬度信息
                longitude = ""+location.getLongitude();    //获取经度信息
                city = location.getCity();
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
        Toast.makeText(this, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
    }

    @Override
           public boolean onKeyDown(int keyCode, KeyEvent event) {
       if ((keyCode == KeyEvent.KEYCODE_BACK)){
          System.out.println("按下了back键   onKeyDown()");
           if(mProgressDialog.isShowing()){
               hideProgressDialog();

           }
            return false;
         }else{
            return super.onKeyDown(keyCode,event);
          }
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


}
