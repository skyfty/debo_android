package com.qcwl.debo.ui.found.memorandum.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiHttpClient;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.LargeImageActivity;
import com.qcwl.debo.ui.circle.MyGridView;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.memorandum.bean.ScheduleDetailsBean;
import com.qcwl.debo.utils.ColorUtils;
import com.qcwl.debo.utils.GetTimeUtils;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContentDetailsActivity extends BaseActivity {

    @Bind(R.id.btn_edit)
    Button mBtnEdit;
    @Bind(R.id.data)
    TextView mData;
    @Bind(R.id.hour_min)
    TextView mHourMin;
    @Bind(R.id.week)
    TextView mWeek;
    @Bind(R.id.content)
    TextView mContent;
    @Bind(R.id.grid_view)
    MyGridView mGridView;
    @Bind(R.id.image_schedule)
    ImageView mImageSchedule;
    private String mId;
    private ArrayList<String> mPath = new ArrayList<>();

    private int w = 110;
    private int h = 110;
    private int width = 0;
    private int height = 0;
    private int spacing = 5;
    private String mType;
    private String mStart_time;
    private String m_Id;
    private ArrayList<String> mArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_details);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            mId = getIntent().getStringExtra("m_id");
        }
        initTitleBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDetails();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("详情")
                .setImageLeftRes(R.mipmap.back)
                .setImageRightRes(R.drawable.add_icon_delete)
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ToastUtils.showShort(ContentDetailsActivity.this, "刪除");
                        showDelSchedule(mId);
                    }
                })
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void initData() {
        if (mPath != null && mPath.size() > 0) {
            final List<String> list = new ArrayList<>();
            if (mPath.size() <= 9) {
                list.addAll(mPath);
            } else {
                list.addAll(mPath.subList(0, 9));
            }
            Log.i("mpath_list", list.get(0).toString());
            final int size = list.size();
            if (size == 1) {

                mGridView.setNumColumns(1);
                String url = list.get(0);
//                width = 230;
//                height = 180;
//                mGridView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(width), dp2px(height)));
                layoutSetting(mGridView, url);
            } else if (size == 2 || size == 4) {
                mGridView.setNumColumns(2);
                height = (size == 2) ? h : h * 2;
                width = h * 2 + spacing;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(width), dp2px(height));
                params.leftMargin = dp2px(10);
                mGridView.setLayoutParams(params);
            } else {
                mGridView.setNumColumns(3);
                //height = (size == 2) ? h : h * 2;
                if (size == 3) {
                    height = h;
                } else if (size <= 6) {
                    height = h * 2 + spacing;
                } else {
                    height = h * 3 + spacing * 2;
                }
                width = h * 3 + spacing * 2;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(width), dp2px(height));
                params.leftMargin = dp2px(10);
                mGridView.setLayoutParams(params);
            }
            mGridView.setAdapter(new CommonAdapter<String>(ContentDetailsActivity.this, R.layout.item_circle_image, list) {
                @Override
                protected void convert(ViewHolder viewHolder, String item, int position) {
                    final ImageView imageView = viewHolder.getView(R.id.image_view);

                    if (size == 1) {
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(s_width), dp2px(s_height)));
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                        imageView.setBackgroundResource(R.color.transparent);
                        imageView.setBackgroundResource(R.color.default_image_bg);
                    } else if (size == 2 || size == 4) {
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(w), dp2px(h)));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setBackgroundResource(R.color.default_image_bg);
                    } else {
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(w), dp2px(h)));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setBackgroundResource(R.color.default_image_bg);
                    }
                    Log.i("mpath_remind", "*-*-*" + ApiHttpClient.VIDEO_URL + item);
                    ImgUtil.load(ContentDetailsActivity.this, ApiHttpClient.VIDEO_URL + item, imageView);
                    //viewHolder.getConvertView().setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                }
            });
            mGridView.setVisibility(View.VISIBLE);
            mArrayList = new ArrayList<>();
            for (String str : list) {
                mArrayList.add(ApiHttpClient.VIDEO_URL + str);
            }
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ContentDetailsActivity.this, LargeImageActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("images", mArrayList);
                    startActivity(intent);
                }
            });
        }
    }

    @OnClick(R.id.btn_edit)
    public void onViewClicked() {
        /*需要传*/
        startActivity(new Intent(ContentDetailsActivity.this, AddScheduleActivity.class)
                .putExtra("m_id", m_Id)
                .putExtra("title", "修改日程")
                .putExtra("content", mContent.getText().toString())
                .putExtra("type", "1")
                .putExtra("imgs", mPath)
                .putExtra("classify", mType));
    }

    //删除提示框
    private void showDelSchedule(final String position) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ContentDetailsActivity.this);
        normalDialog.setTitle("确定是否删除");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delSchedule(position);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void getDetails() {
        Api.getMemorandumDetails(sp.getString("uid"), mId, new ApiResponseHandler(ContentDetailsActivity.this) {

            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    ScheduleDetailsBean bean = JSON.parseObject(apiResponse.getData(), ScheduleDetailsBean.class);
                    String date = GetTimeUtils.stampToDate(Long.parseLong(bean.getStart_time()));
                    String hours = GetTimeUtils.stampToHoursDate(Long.parseLong(bean.getStart_time()));
                    String week = GetTimeUtils.getWeek(date);
                    mStart_time = bean.getStart_time();
                    m_Id = bean.getId();
                    mData.setText(date);
                    mHourMin.setText(hours);
                    mWeek.setText(week);
                    mContent.setText(bean.getContent());
                    if (!bean.getFont_color().equals("")) {
                        mContent.setTextColor(ColorUtils.getTextColor[Integer.parseInt(bean.getFont_color() + "")]);
                    }
                    mType = bean.getM_type();
                    if (bean.getPath() != null) {
                        mPath = bean.getPath();
                        initData();
                    }
                } else {
                    Log.i("idid_getday", apiResponse.getMessage() + "/" + apiResponse.getCode());
                }
            }
        });

    }

    //删除备忘录
    private void delSchedule(String m_id) {
        Api.delMemorandumDetails(sp.getString("uid"), m_id, new ApiResponseHandler(ContentDetailsActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    ToastUtils.showShort(ContentDetailsActivity.this, "删除成功");
                    finish();
                } else {
                    Log.i("del_fail", apiResponse.getMessage() + "/" + apiResponse.getCode());
                }
            }
        });
    }

    private int s_width = 0, s_height = 0;

    private void layoutSetting(final MyGridView gridView, String url) {
        Glide.with(this)
                .load(ApiHttpClient.VIDEO_URL + url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        int w = resource.getWidth();
                        int h = resource.getHeight();
                        if (w >= h) {
                            width = 200;
                            height = (int) (1.0f * width * h / w);
                        } else {
                            height = 150;
                            width = (int) (1.0f * height * w / h);
                        }
                        s_width = width;
                        s_height = height;
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(s_width), dp2px(s_height));
                        params.leftMargin = dp2px(10);
                        gridView.setLayoutParams(params);
                    }
                });
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(this, dpVal);
    }
}
