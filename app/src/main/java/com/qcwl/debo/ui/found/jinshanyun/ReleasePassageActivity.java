package com.qcwl.debo.ui.found.jinshanyun;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.KeyBoardUtils;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReleasePassageActivity extends BaseActivity {
    @Bind(R.id.status_bar)
    View mStatusBar;
    @Bind(R.id.image_back)
    ImageView mImageBack;
    @Bind(R.id.text_title)
    TextView mTextTitle;
    @Bind(R.id.text_keep)
    TextView mTextKeep;
    @Bind(R.id.layout_top)
    LinearLayout mLayoutTop;
    @Bind(R.id.img_vague)
    ImageView mImgVague;
    @Bind(R.id.img_frame)
    ImageView mImgFrame;
    @Bind(R.id.edit_release)
    EditText mEditRelease;
    @Bind(R.id.text_num)
    TextView mTextNum;
    @Bind(R.id.btn_send)
    Button mBtnSend;
    private int mPublishType;
    private ProgressDialog progressDialog;

    private String videoPath;
    private String thumbPath;
    private String uid = "";//"34";
    private String latitude = "" ;//维度
    private String longitude = "" ;//精度
    private String city = "";//城市
    private LocationManager locationManager;
    private String locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_passage);
        ButterKnife.bind(this);
        if(getIntent() == null){
            return;
        }
        getLocation();
        uid = SPUtil.getInstance(this).getString("uid");
        if(!SPUtil.getInstance(this).getString("city").isEmpty()){
            city = SPUtil.getInstance(this).getString("city");
//            city = "北京";
        }else{
            city = "null";
        }
        if(getIntent()!=null){
            mPublishType = getIntent().getIntExtra("publishType", 0);
            if(mPublishType == 1){
                videoPath = getIntent().getStringExtra("videoPath");
                thumbPath = getIntent().getStringExtra("thumbPath");
                ImgUtil.load(this, thumbPath, mImgVague);
                mImgVague.setAlpha(50);
                ImgUtil.load(this, thumbPath, mImgFrame);
            }
        }else{
            return;
        }
        hideSoftInput();
        int statusHeight = ScreenUtils.getStatusHeight(this);
        mStatusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight));
        mLayoutTop.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight + ScreenUtils.dp2px(this, 50)));
        mLayoutTop.getBackground().setAlpha(255);
        mEditRelease.addTextChangedListener(mTextWatcher);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("发布中……");
    }

    @OnClick({R.id.image_back, R.id.text_keep, R.id.edit_release, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.text_keep:
                keepVideo();
                break;
            case R.id.edit_release:
                break;
            case R.id.btn_send:
                sendVideo();
                break;
        }
    }

    private void keepVideo() {
//        ToastUtils.showShort(ReleasePassageActivity.this,"保存视频");
        String srcPath = videoPath;
        String desDir = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)
                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                + "/Camera/" : Environment.getExternalStorageDirectory().getAbsolutePath() + "/KSYShortVideo";
        String name = srcPath.substring(srcPath.lastIndexOf('/'));
        String desPath = desDir + name;
        File desFile = new File(desPath);
        try {
            File srcFile = new File(srcPath);
            if (srcFile.exists() && !desFile.exists()) {
                InputStream is = new FileInputStream(srcPath);
                FileOutputStream fos = new FileOutputStream(desFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
                ToastUtils.showShort(ReleasePassageActivity.this,"文件保存相册成功");
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendVideo(){
        progressDialog.show();
        //发布视频
        final String release = mEditRelease.getText().toString().trim();
        final List<File> files = new ArrayList<>();
        files.add(new File(thumbPath));//第一帧图片
        files.add(new File(videoPath));//视频路径
        Api.publishParagraphInfo(uid, files,latitude,longitude,city, release, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (apiResponse.getCode() == 0) {
                    KeyBoardUtils.closeKeybord(mEditRelease, ReleasePassageActivity.this);
                    Toast.makeText(ReleasePassageActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    Log.e("android_video",thumbPath+"/*/"+videoPath);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ReleasePassageActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mTextNum.setText(s.length()+"/20");
        }
    };

    private void getLocation(){
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }
        //获取Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            //不为空,显示地理位置经纬度
            Log.e("TAG","经度"+location.getLongitude()+"纬度"+location.getLatitude());
            latitude = location.getLatitude()+"";
            longitude = location.getLongitude() + "";
        }

    }
}
