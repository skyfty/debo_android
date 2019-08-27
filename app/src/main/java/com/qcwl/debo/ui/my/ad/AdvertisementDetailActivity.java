package com.qcwl.debo.ui.my.ad;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.LargeImageActivity;
import com.qcwl.debo.ui.login.ErWeiMaWebActivity;
import com.qcwl.debo.ui.my.BumpAndTouchActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AdvertisementDetailActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.text_link)
    TextView textLink;
    @Bind(R.id.text_price)
    TextView textPrice;
    @Bind(R.id.text_number)
    TextView textNumber;
    @Bind(R.id.text_start_time)
    TextView textStartTime;
    @Bind(R.id.text_end_time)
    TextView textEndTime;
    @Bind(R.id.text_content)
    TextView textContent;
    @Bind(R.id.image_view)
    ImageView imageView;
    @Bind(R.id.image_view2)
    ImageView imageView2;
    @Bind(R.id.image_view3)
    ImageView imageView3;
    @Bind(R.id.btn_next)
    Button btnNext;

    private String t_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_detail);
        ButterKnife.bind(this);

        initTitleBar();

        if (getIntent() == null) {
            return;
        }
        t_id = getIntent().getStringExtra("t_id");
        loadData();
        btnNext.setClickable(false);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdvertisementDetailActivity.this, BumpAndTouchActivity.class)
                        .putExtra("type", ad_type));
            }
        });

        imageView.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        textLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean != null && !TextUtils.isEmpty(bean.getAd_link())) {
//                    Uri uri = Uri.parse("http://www.baidu.com");
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
//                    startActivity(intent);

                    if (!TextUtils.isEmpty(bean.getAd_link())) {
                        startActivity(new Intent(AdvertisementDetailActivity.this, ErWeiMaWebActivity.class)
                                .putExtra("title", "广告链接")
                                .putExtra("url", bean.getAd_link()));
                    } else {
                        ToastUtils.showShort(AdvertisementDetailActivity.this, "链接为空，不能打开");
                    }
                }
            }
        });
    }

    private int ad_type = 0;


    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1f)
                .setTitle("广告详情")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new TitleBarBuilder(this)
                .setAlpha(1f);
    }

    private AdvertisementBean bean = null;

    private void loadData() {
        Api.getAdvertisementDetail(sp.getString("uid"), t_id,
                new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        bean = JSON.parseObject(apiResponse.getData(), AdvertisementBean.class);
                        if (bean == null) {
                            return;
                        }
                        btnNext.setClickable(true);
                        ad_type = bean.getAd_type();
                        textTitle.setText("" + bean.getTitle());
                        textLink.setText("" + bean.getAd_link());
                        textPrice.setText("" + bean.getPrice());
                        textNumber.setText("" + bean.getP_num());
                        textStartTime.setText("" + bean.getStart_time());
                        textEndTime.setText("" + bean.getTime_range());
                        textContent.setText("" + bean.getAd_content());
                        if (bean.getAd_images() == null || bean.getAd_images().size() == 0) {
                            imageView.setVisibility(View.INVISIBLE);
                            imageView2.setVisibility(View.INVISIBLE);
                            imageView3.setVisibility(View.INVISIBLE);
                            imageView.setClickable(false);
                            imageView2.setClickable(false);
                            imageView3.setClickable(false);
                        } else {
                            switch (bean.getAd_images().size()) {
                                case 1:
                                    imageView.setVisibility(View.VISIBLE);
                                    imageView2.setVisibility(View.INVISIBLE);
                                    imageView3.setVisibility(View.INVISIBLE);
                                    imageView.setClickable(true);
                                    imageView2.setClickable(false);
                                    imageView3.setClickable(false);
                                    ImgUtil.load(AdvertisementDetailActivity.this, bean.getAd_images().get(0), imageView);
                                    break;
                                case 2:
                                    imageView.setVisibility(View.VISIBLE);
                                    imageView2.setVisibility(View.VISIBLE);
                                    imageView3.setVisibility(View.INVISIBLE);
                                    imageView.setClickable(true);
                                    imageView2.setClickable(true);
                                    imageView3.setClickable(false);
                                    ImgUtil.load(AdvertisementDetailActivity.this, bean.getAd_images().get(0), imageView);
                                    ImgUtil.load(AdvertisementDetailActivity.this, bean.getAd_images().get(1), imageView2);
                                    break;
                                case 3:
                                    imageView.setVisibility(View.VISIBLE);
                                    imageView2.setVisibility(View.VISIBLE);
                                    imageView3.setVisibility(View.VISIBLE);
                                    imageView.setClickable(true);
                                    imageView2.setClickable(true);
                                    imageView3.setClickable(true);
                                    ImgUtil.load(AdvertisementDetailActivity.this, bean.getAd_images().get(0), imageView);
                                    ImgUtil.load(AdvertisementDetailActivity.this, bean.getAd_images().get(1), imageView2);
                                    ImgUtil.load(AdvertisementDetailActivity.this, bean.getAd_images().get(2), imageView3);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (bean == null) {
            return;
        }
        List<String> list = new ArrayList<>();
        list.addAll(bean.getAd_images());
        Intent intent = new Intent(this, LargeImageActivity.class);
        intent.putExtra("images", (Serializable) list);
        int position = 0;
        switch (v.getId()) {
            case R.id.image_view:
                position = 0;
                break;
            case R.id.image_view2:
                position = 1;
                break;
            case R.id.image_view3:
                position = 2;
                break;
            default:
                break;
        }
        intent.putExtra("position", position);
        startActivity(intent);
    }
}
