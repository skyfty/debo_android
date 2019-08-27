//package com.qcwl.debo.ui.my;
//
//import android.Manifest;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.HorizontalScrollView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.qcwl.debo.R;
//import com.qcwl.debo.chat.BaseActivity;
//import com.qcwl.debo.presenter.PublishWishingPresenter;
//import com.qcwl.debo.presenterInf.PublishWishingPresenterInf;
//import com.qcwl.debo.ui.circle.Luban;
//import com.qcwl.debo.utils.ToastUtils;
//import com.qcwl.debo.view.BounceTopEnter;
//import com.qcwl.debo.view.FilterImageView;
//import com.qcwl.debo.view.ShareBottomDialog;
//import com.qcwl.debo.widget.BaseAnimatorSet;
//import com.zhy.m.permission.MPermissions;
//import com.zhy.m.permission.PermissionDenied;
//import com.zhy.m.permission.PermissionGrant;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import me.nereo.multi_image_selector.MultiImageSelector;
//import me.nereo.multi_image_selector.MultiImageSelectorActivity;
//
//import static com.qcwl.debo.utils.PicUtil.REQUEST_IMAGE;
//
///**
// * Created by Administrator on 2017/7/24.
// */
//
//public class PublishRedEnvelopeStarActivity extends BaseActivity implements View.OnClickListener, PublishWishingPresenterInf, ShareBottomDialog.SetData {
//    private LinearLayout left_layout;
//    private FilterImageView post_add_pic;
//    private EditText title, link, price, number, content;
//    private TextView start_time, end_time;
//    private Button commit;
//    private PublishWishingPresenter publishWishingPresenter;
//    private ShareBottomDialog dialog;
//    private BaseAnimatorSet mBasIn;
//    private List<File> file;
//    private String[] permissions;
//    private final int PESSION_CODE = 1;
//    private LinearLayout picContainer;//图片容器
//    private List<String> imgs = null;
//    private int size;
//    private int padding;//小图间距
//    private HorizontalScrollView post_scrollview;
//
//    @Override
//    protected void onCreate(Bundle arg0) {
//        super.onCreate(arg0);
//        setContentView(R.layout.publish_red_envelope_star);
//        initView();
//        monitor();
//    }
//
//    private void initView() {
//        publishWishingPresenter = new PublishWishingPresenter(this);
//        mBasIn = new BounceTopEnter();
//        size = (int) getResources().getDimension(R.dimen.size_80);
//        padding = (int) getResources().getDimension(R.dimen.padding_10);
//        left_layout = (LinearLayout) findViewById(R.id.left_layout);
//        title = (EditText) findViewById(R.id.title);
//        link = (EditText) findViewById(R.id.link);
//        price = (EditText) findViewById(R.id.price);
//        number = (EditText) findViewById(R.id.number);
//        start_time = (TextView) findViewById(R.id.start_time);
//        end_time = (TextView) findViewById(R.id.end_time);
//        content = (EditText) findViewById(R.id.content);
//        post_add_pic = (FilterImageView) findViewById(R.id.post_add_pic);
//        picContainer = (LinearLayout) findViewById(R.id.post_pic_container);
//        post_scrollview = (HorizontalScrollView) findViewById(R.id.post_scrollview);
//        commit = (Button) findViewById(R.id.commit);
//    }
//
//    private void monitor() {
//        left_layout.setOnClickListener(this);
//        post_add_pic.setOnClickListener(this);
//        start_time.setOnClickListener(this);
//        end_time.setOnClickListener(this);
//        commit.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.left_layout:
//                finish();
//                break;
//            case R.id.post_add_pic:
//                file = new ArrayList<>();
//                hideSoftKeyboard();
//                requestPermission();
//                break;
//            case R.id.start_time:
//                dialog = new ShareBottomDialog(this);
//                dialog.setListener(this);
//                dialog.setCanceledOnTouchOutside(true);
//                dialog.setType(1);
//                dialog.widthScale(0.95f).heightScale(0.75f).showAnim(mBasIn)
//                        .show();
//                break;
//            case R.id.end_time:
//                dialog = new ShareBottomDialog(this);
//                dialog.setListener(this);
//                dialog.setCanceledOnTouchOutside(true);
//                dialog.setType(2);
//                dialog.widthScale(0.95f).heightScale(0.75f).showAnim(mBasIn)
//                        .show();
//                break;
//            case R.id.commit:
//                publishWishingPresenter.publishAdvertising(this, getIntent().getStringExtra("type"), sp.getString("uid"), title.getText().toString(),
//                        link.getText().toString(), price.getText().toString(), number.getText().toString(), content.getText().toString(), start_time.getText().toString(), end_time.getText().toString()
//                        , file);
//                break;
//        }
//    }
//
//    private void requestPermission() {
//        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        MPermissions.requestPermissions(this, PESSION_CODE, permissions);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @PermissionGrant(PESSION_CODE)
//    public void requestPermissionSuccess() {
//        MultiImageSelector.create()
//                .showCamera(true) // show camera or not. true by default
//                .count(3) // max select image size, 9 by default. used width #.multi()
//                //.single() // single mode
//                .multi() // multi mode, default mode;
//                //.origin(imgs) // original select data set, used width #.multi()
//                .start(this, REQUEST_IMAGE);
//    }
//
//    @PermissionDenied(PESSION_CODE)
//    public void requestPermissionFailed() {
//        Toast.makeText(this, "权限被禁止，请您去设置界面开启！", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        switch (requestCode) {
//            case REQUEST_IMAGE:
//                if (imgs != null && imgs.size() > 0) {
//                    picContainer.removeViews(0, picContainer.getChildCount() - 1);
//                }
//                if (data == null) {
//                    return;
//                }
//                imgs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//                if (imgs == null || imgs.size() == 0) {
//                    return;
//                }
//
//                file = new ArrayList<>();
//
//                for (int i = 0; i < imgs.size(); i++) {
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
//                    FilterImageView imageView = new FilterImageView(this);
//                    params.rightMargin = padding;
//                    imageView.setLayoutParams(params);
//                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    Glide.with(this).load(imgs.get(i)).into(imageView);
//                    picContainer.addView(imageView, picContainer.getChildCount() - 1);
//                    file.add(Luban.get(this).launch(new File(imgs.get(i))));
//                }
//                //延迟滑动至最右边
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        post_scrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
//                    }
//                }, 50L);
//                break;
//        }
//    }
//
//    @Override
//    public void getResult(int code, String message, Object o) {
//        if (code == 0) {
//            finish();
//        }
//        ToastUtils.showShort(this, message);
//    }
//
//    @Override
//    public void setStartTime(String startTime) {
//        start_time.setText(startTime);
//        dialog.dismiss();
//    }
//
//    @Override
//    public void setEndTime(String endTime) {
//        end_time.setText(endTime);
//        dialog.dismiss();
//    }
//}
