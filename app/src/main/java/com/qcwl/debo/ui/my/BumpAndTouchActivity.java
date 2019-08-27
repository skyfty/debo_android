package com.qcwl.debo.ui.my;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.presenter.PublishWishingPresenter;
import com.qcwl.debo.presenterInf.PublishWishingPresenterInf;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.Luban;
import com.qcwl.debo.ui.found.bump.QueryActivity;
import com.qcwl.debo.ui.pay.PayDialog;
import com.qcwl.debo.utils.AppUtils;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.BounceTopEnter;
import com.qcwl.debo.view.FilterImageView;
import com.qcwl.debo.view.ShareBottomDialog;
import com.qcwl.debo.widget.BaseAnimatorSet;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static com.qcwl.debo.utils.PicUtil.REQUEST_IMAGE;

/**
 * Created by Administrator on 2017/7/18.
 */

public class BumpAndTouchActivity extends BaseActivity implements View.OnClickListener, PublishWishingPresenterInf, ShareBottomDialog.SetData {

    private FilterImageView post_add_pic;
    private View viewRoot;
    private LinearLayout layoutRoot;
    private EditText headline, link, price, number, content;
    private TextView start_time, end_time;
    private Button commit;
    private List<File> file;
    private String[] permissions;
    private final int PESSION_CODE = 1;
    private LinearLayout picContainer;//图片容器
    private List<String> imgs = null;
    private int size;
    private int padding;//小图间距
    private HorizontalScrollView post_scrollview;
    private PublishWishingPresenter publishWishingPresenter;
    private ShareBottomDialog dialog;
    private BaseAnimatorSet mBasIn;
    private String start;
    private String end;
    private int type = 0;//1 碰一碰 ；2 撞一幢 ； 3 许愿星
    private String title = "";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.bump_and_touch);
        if (getIntent() == null) {
            return;
        }
        type = getIntent().getIntExtra("type", 0);
        if (type == 1) {
            title = "碰一碰";
        } else if (type == 2) {
            title = "撞一撞";
        } else if (type == 3) {
            title = "许愿星";
        }
        initTitleBar();
        initView();
        monitor();
    }

    private void initTitleBar() {
        TitleBarBuilder builder = new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle(title)
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        if (type == 1 || type == 2) {
            builder.setImageRightRes(R.mipmap.btn_why)
                    .setRightListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(BumpAndTouchActivity.this, QueryActivity.class);
                            intent.putExtra("type", type);
                            startActivity(intent);
                        }
                    });
        }
    }

    private void initView() {
        viewRoot = findViewById(R.id.root);
        publishWishingPresenter = new PublishWishingPresenter(this);
        mBasIn = new BounceTopEnter();
        size = (int) getResources().getDimension(R.dimen.size_80);
        padding = (int) getResources().getDimension(R.dimen.padding_10);
        headline = (EditText) findViewById(R.id.headline);
        layoutRoot = (LinearLayout) findViewById(R.id.layout_root);
        link = (EditText) findViewById(R.id.link);
        price = (EditText) findViewById(R.id.price);
        number = (EditText) findViewById(R.id.number);
        start_time = (TextView) findViewById(R.id.start_time);
        end_time = (TextView) findViewById(R.id.end_time);
        content = (EditText) findViewById(R.id.content);
        post_add_pic = (FilterImageView) findViewById(R.id.post_add_pic);
        picContainer = (LinearLayout) findViewById(R.id.post_pic_container);
        post_scrollview = (HorizontalScrollView) findViewById(R.id.post_scrollview);
        commit = (Button) findViewById(R.id.commit);

        if (type == 3) {
            viewRoot.getBackground().setAlpha(0);
            layoutRoot.setBackgroundResource(R.mipmap.wish_star_bg_select);
        } else if (type == 1 || type == 2) {
            layoutRoot.setBackgroundResource(R.color.color_gray);
        }
        price.addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (".".equals(s.toString())) {
                        price.setText("0.");
                    }
                    selectionStart = price.getSelectionStart();
                    Log.e("====", "start=" + selectionStart);
                    Log.e("====", "end=" + selectionEnd);
                    selectionEnd = price.getSelectionEnd();
                    if (!isOnlyPointNumber(price.getText().toString())) {
                        //ToastUtils.showShort(WithdrawalActivity.this, "您输入的数字保留在小数点后两位");
                        //删除多余输入的字（不会显示出来）
                        s.delete(selectionStart - 1, selectionEnd);
                        price.setText(s);
                    }

                    price.setSelection(price.length());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (type == 1) {
            StatService.onPageStart(this, "启动发布碰一碰页面");
        }else if (type == 2){
            StatService.onPageStart(this, "启动发布撞一撞页面");
        }else {
            StatService.onPageStart(this, "启动发布许愿星页面");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (type == 1) {
            StatService.onPageEnd(this,"结束碰一碰页面");
        }else if (type ==2){
            StatService.onPageEnd(this,"结束撞一撞页面");
        }else {
            StatService.onPageEnd(this,"结束许愿星页面");
        }
    }

    public static boolean isOnlyPointNumber(String number) {//保留两位小数正则
        Pattern pattern = Pattern.compile("^\\d+\\.?\\d{0,2}$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    private void monitor() {
        post_add_pic.setOnClickListener(this);
        start_time.setOnClickListener(this);
        end_time.setOnClickListener(this);
        commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_add_pic:
                file = new ArrayList<>();
                hideSoftKeyboard();
                requestPermission();
                break;
            case R.id.start_time:
                dialog = new ShareBottomDialog(this);
                dialog.setListener(this);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setType(1);
                dialog.widthScale(0.95f).heightScale(0.75f).showAnim(mBasIn)
                        .show();
                break;
            case R.id.end_time:
                if (TextUtils.isEmpty(start_time.getText().toString())) {
                    ToastUtils.showShort(this, "请先选择起始时间");
                    return;
                }
                dialog = new ShareBottomDialog(this);
                dialog.setListener(this);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setType(2);
                dialog.widthScale(0.95f).heightScale(0.75f).showAnim(mBasIn)
                        .show();
                break;
            case R.id.commit:
                submit();
                break;
        }
    }

    String adTitle = "";
    String adPrice = "";

    private void submit() {
        adTitle = headline.getText().toString();
        if (TextUtils.isEmpty(adTitle)) {
            ToastUtils.showShort(this, "广告标题不能为空");
            return;
        }
        adPrice = price.getText().toString();
        if (TextUtils.isEmpty(adPrice)) {
            ToastUtils.showShort(this, "请输入红包金额");
            return;
        }
        if (Double.parseDouble(adPrice) == 0) {
            ToastUtils.showShort(this, "红包金额不能为零");
            return;
        }
        if (Double.parseDouble(adPrice) < 0.01) {
            ToastUtils.showShort(this, "红包金额不能小于1分");
            return;
        }
        String adNum = number.getText().toString();
        if (TextUtils.isEmpty(adNum)) {
            ToastUtils.showShort(this, "请输入红包个数");
            return;
        }
        if (Integer.parseInt(adNum) < 1) {
            ToastUtils.showShort(this, "红包个数最小为1");
            return;
        }
        String adStartTime = start_time.getText().toString();
        String adEndTime = end_time.getText().toString();
        if (TextUtils.isEmpty(adStartTime) || TextUtils.isEmpty(adEndTime)) {
            ToastUtils.showShort(this, "请选择起止时间");
            return;
        }
        publishWishingPresenter.publishAdvertising(this, "" + type, sp.getString("uid"), adTitle,
                link.getText().toString(), adPrice, adNum, content.getText().toString(),
                adStartTime, adEndTime, file);
    }

    private void requestPermission() {
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        MPermissions.requestPermissions(this, PESSION_CODE, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(PESSION_CODE)
    public void requestPermissionSuccess() {
        MultiImageSelector.create()
                .showCamera(true) // show camera or not. true by default
                .count(3) // max select image size, 9 by default. used width #.multi()
                //.single() // single mode
                .multi() // multi mode, default mode;
                //.origin(imgs) // original select data set, used width #.multi()
                .start(this, REQUEST_IMAGE);
    }

    @PermissionDenied(PESSION_CODE)
    public void requestPermissionFailed() {
        Toast.makeText(this, "权限被禁止，请您去设置界面开启！", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_IMAGE:
                if (imgs != null && imgs.size() > 0) {
                    picContainer.removeViews(0, picContainer.getChildCount() - 1);
                }
                if (data == null) {
                    return;
                }
                imgs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (imgs == null || imgs.size() == 0) {
                    return;
                }

                file = new ArrayList<>();

                for (int i = 0; i < imgs.size(); i++) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                    FilterImageView imageView = new FilterImageView(this);
                    params.rightMargin = padding;
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ImgUtil.setGlideHead(this, imgs.get(i), imageView);
                    //Glide.with(this).load(imgs.get(i)).into(imageView);
                    picContainer.addView(imageView, picContainer.getChildCount() - 1);
                    file.add(Luban.get(this).launch(new File(imgs.get(i))));
                }
                //延迟滑动至最右边
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        post_scrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                }, 50L);
                break;
        }
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            //1 碰一碰 ；2 撞一幢 ； 3 许愿星
            String id = (String) o;
            if (type == 3) {
                Intent data = new Intent();
                data.putExtra("ad_id", id);
                data.putExtra("ad_title", title);
                data.putExtra("ad_price", adPrice);
                setResult(RESULT_OK, data);
                finish();
            } else if (type == 1 || type == 2) {
                PayDialog.createDialog(this, title, 3, id, adPrice,"").show();
            }
        } else {
            ToastUtils.showShort(this, message);
        }
    }

    @Override
    public void setStartTime(String startNear, String startMonth, String startDay) {
        start = startNear + "-" + startMonth + "-" + startDay;
        start_time.setText(start);
        dialog.dismiss();
    }

    @Override
    public void setEndTime(String endNear, String endMonth, String endDay) {
        end = endNear + "-" + endMonth + "-" + endDay;
        if (isDateOneBigger(end, start)) {
            end_time.setText(end);
            dialog.dismiss();
        } else {
            ToastUtils.showShort(this, "结束时间必须大于起始时间");
        }

    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-M-dd
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public static boolean isDateOneBigger(String str1, String str2) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }
}
