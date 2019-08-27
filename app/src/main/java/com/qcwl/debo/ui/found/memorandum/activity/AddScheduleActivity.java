package com.qcwl.debo.ui.found.memorandum.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.PathUtil;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.found.memorandum.adapter.ASClassifyAdapter;
import com.qcwl.debo.ui.found.memorandum.adapter.PictureAdapter;
import com.qcwl.debo.ui.found.memorandum.adapter.TextColorAdapter;
import com.qcwl.debo.ui.found.memorandum.adapter.URLPictureAdapter;
import com.qcwl.debo.ui.found.memorandum.bean.ScheduleClassifyBean;
import com.qcwl.debo.ui.found.memorandum.builder.TimePickerBuilder;
import com.qcwl.debo.ui.found.memorandum.listener.OnTimeSelectChangeListener;
import com.qcwl.debo.ui.found.memorandum.listener.OnTimeSelectListener;
import com.qcwl.debo.ui.found.memorandum.view.TimePickerView;
import com.qcwl.debo.utils.ColorUtils;
import com.qcwl.debo.utils.PicUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class AddScheduleActivity extends BaseActivity {


    @Bind(R.id.img_select)
    RelativeLayout mImgSelect;
    @Bind(R.id.text_select)
    RelativeLayout mTextSelect;
    @Bind(R.id.time_select)
    RelativeLayout mTimeSelect;
    @Bind(R.id.classify_select)
    RelativeLayout mClassifySelect;
    @Bind(R.id.image_expansion)
    LinearLayout mImageExpansion;
    @Bind(R.id.text_expansion)
    LinearLayout mTextExpansion;
    @Bind(R.id.data_expansion)
    LinearLayout mDataExpansion;
    @Bind(R.id.class_expansion)
    LinearLayout mClassExpansion;
    @Bind(R.id.edit_details)
    EditText mEditDetails;
    @Bind(R.id.picture_list)
    RecyclerView mPictureList;
    @Bind(R.id.color_text)
    RecyclerView mTextColorList;
    @Bind(R.id.text_photograph)
    TextView mTextPhotograph;
    @Bind(R.id.text_album)
    TextView mTextAlbum;
    @Bind(R.id.text_min)
    TextView mTextMin;
    @Bind(R.id.text_size)
    TextView mTextSize;
    @Bind(R.id.data_time)
    TextView mDataTime;
    @Bind(R.id.now_time)
    TextView mNowTime;
    @Bind(R.id.remind_img)
    ImageView mRemind_img;
    @Bind(R.id.img_image)
    ImageView mImageView;
    @Bind(R.id.child_classify)
    ListView mChildClassify;
    @Bind(R.id.fragment_data)
    FrameLayout mFragment_data;
    @Bind(R.id.text_size_seekbar)
    SeekBar mTextSizeSeekbar;
    @Bind(R.id.add_classify)
    ImageView mAddClassify;
    @Bind(R.id.classify_name)
    TextView mClassifyName;
    @Bind(R.id.img_size)
    TextView mImgSize;

    protected File cameraFile;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    private final int PESSION_CODE = 1;
    private String[] permissions;
    private int flag;
    private ArrayList<ScheduleClassifyBean> mList;
    private ASClassifyAdapter mClassifyAdapter;
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<File> mFiles = new ArrayList<>();
    private PictureAdapter mPictureAdapter;
    private URLPictureAdapter mURLPictureAdapter;
    private TimePickerView pvTime;
    private TextColorAdapter mTextColorAdapter;
    private String start_time = "", rem_time = "", font_color = "", m_type = "";
    private String mTitle;
    private String mContentDetails;
    private String mType;
    private ArrayList<String> mImgs;
    private String mClassify;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        ButterKnife.bind(this);
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        initIntent();
        initData();
        initTitleBar();
        initView();
        initTimePicker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getClassify();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle(mTitle)
                .setImageLeftRes(R.mipmap.back)
                .setTextRight("确认")
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (start_time.equals("") && start_time.length() == 0) {
                            ToastUtils.showShort(AddScheduleActivity.this, "请设置开始时间");
                            return;
                        }
                        if (mEditDetails.getText().toString().length() == 0 && mEditDetails.getText().toString().equals("")) {
                            ToastUtils.showShort(AddScheduleActivity.this, "请输入内容");
                            return;
                        }
                        Log.i("url_font_color", font_color + "*-*-*-");
                        /*if (font_color.equals("") && font_color.length() == 0) {
                            ToastUtils.showShort(AddScheduleActivity.this, "请设置字体颜色");
                            return;
                        }*/
                        Log.i("url_pvTime", "" + start_time + "*" + rem_time + "*" + mEditDetails.getText().toString() + "*" + font_color + "*" + m_type + "*" + mFiles.size());
                        if (mType.equals("1")&&mType!=null) {
                            updSchedule(start_time, mId, rem_time, mEditDetails.getText().toString(), font_color, mClassify, mFiles);
                        } else {
                            /*if (m_type.equals("") && m_type.length() == 0) {
                                ToastUtils.showShort(AddScheduleActivity.this, "请设置分类颜色");
                                return;
                            }*/
                            addSchedule(start_time, rem_time, mEditDetails.getText().toString(), font_color, m_type, mFiles);
                        }
                    }
                })
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void initIntent() {

        if (getIntent() != null) {
            mId = getIntent().getStringExtra("m_id");
            mTitle = getIntent().getStringExtra("title");
            mContentDetails = getIntent().getStringExtra("content");
            mType = getIntent().getStringExtra("type");
            mImgs = getIntent().getStringArrayListExtra("imgs");
            if (mImgs != null) {
//            mImages.addAll(mImgs);
                for (String url : mImgs) {
                    Uri uri = PicUtil.getImageContentUri(this, url);
                    Log.i("url_img", url);
                    mImages.add(url);
//                    if (uri != null) {
                    Log.i("url_img_url", url);
                    File file = new File(url);
                    mFiles.add(file);
                    Log.i("url_img_path", file.getAbsolutePath());
//                    }
                }
            }
            mClassify = getIntent().getStringExtra("classify");
            Log.i("url_type", mType + "*-*" + mClassify);
            mEditDetails.setText(mContentDetails);
        } else {
            return;
        }

    }

    private void initData() {
        mList = new ArrayList<>();
        Log.i("Classify", mList.toString() + "/");
        Log.i("img_arraylist", mImages.toString() + "----");

        mPictureList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        if (mType != null) {
            mURLPictureAdapter = new URLPictureAdapter(this, mImages);
            mPictureList.setAdapter(mURLPictureAdapter);
        } else {
            mPictureAdapter = new PictureAdapter(this, mImages);
            mPictureList.setAdapter(mPictureAdapter);
        }
        mTextColorList.setLayoutManager(new GridLayoutManager(this, 7));
        mTextColorAdapter = new TextColorAdapter(this);
        mTextColorList.setAdapter(mTextColorAdapter);
        mTextColorAdapter.setOnItemClickListener(new TextColorAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                mEditDetails.setTextColor(getResources().getColor(ColorUtils.getTextColor[position]));
                font_color = position + "";
                Log.i("url_font_color", font_color + "------");
            }
        });


        mClassifyAdapter = new ASClassifyAdapter(this, mList);
        mChildClassify.setAdapter(mClassifyAdapter);
        mChildClassify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //选择分类
                mClassifyName.setText(mList.get(position).getName());
                m_type = mList.get(position).getM_type();

            }
        });
    }

    private void initView() {
        mTextSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextSize.setText(10 + progress + "");
                mEditDetails.setTextSize(10 + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick({R.id.img_select, R.id.text_select, R.id.time_select, R.id.classify_select, R.id.edit_details, R.id.text_photograph, R.id.text_album, R.id.now_time, R.id.remind_img, R.id.add_classify})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_select:
                mImageExpansion.setVisibility(View.VISIBLE);
                mTextExpansion.setVisibility(View.GONE);
                mDataExpansion.setVisibility(View.GONE);
                mClassExpansion.setVisibility(View.GONE);
                hideSoftInput();
                break;
            case R.id.text_select:
                mImageExpansion.setVisibility(View.GONE);
                mTextExpansion.setVisibility(View.VISIBLE);
                mDataExpansion.setVisibility(View.GONE);
                mClassExpansion.setVisibility(View.GONE);
                hideSoftInput();
                break;
            case R.id.time_select:
                mImageExpansion.setVisibility(View.GONE);
                mTextExpansion.setVisibility(View.GONE);
                mDataExpansion.setVisibility(View.VISIBLE);
                mClassExpansion.setVisibility(View.GONE);
                mDataTime.setText(setTime());
                hideSoftInput();
//                initTimePicker();
                pvTime.show(view);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
                break;
            case R.id.classify_select:
                mImageExpansion.setVisibility(View.GONE);
                mTextExpansion.setVisibility(View.GONE);
                mDataExpansion.setVisibility(View.GONE);
                mClassExpansion.setVisibility(View.VISIBLE);
                hideSoftInput();
                break;
            case R.id.edit_details:
                mImageExpansion.setVisibility(View.GONE);
                mTextExpansion.setVisibility(View.GONE);
                mDataExpansion.setVisibility(View.GONE);
                mClassExpansion.setVisibility(View.GONE);
                break;
            case R.id.text_photograph:
                if (mImages.size() >= 9) {
                    ToastUtils.showShort(this, "只能上传九张图片");
                    return;
                }
                flag = 1;
                MPermissions.requestPermissions(AddScheduleActivity.this, PESSION_CODE, permissions);
                break;
            case R.id.text_album:
                if (mImages.size() >= 9) {
                    ToastUtils.showShort(this, "只能上传九张图片");
                    return;
                }
                flag = 2;
                MPermissions.requestPermissions(AddScheduleActivity.this, PESSION_CODE, permissions);
                break;

            case R.id.now_time:
                pvTime.setTime();
                mDataTime.setText(setTime());
                break;

            case R.id.remind_img:
                initRemindTimePicker();
                pvTime.show(view);
                break;

            case R.id.add_classify:
                startActivity(new Intent(this, AddClassifyActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(AddScheduleActivity.this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(PESSION_CODE)
    public void requestPermissionSuccess() {
        switch (flag) {
            case 1:
                selectPicFromCamera();
                break;
            case 2:
                Log.e("pic_....", "调用系统相册");
                MultiImageSelector.create()
                        .showCamera(true) // show camera or not. true by default
                        .multi().count(9 - mImages.size())
                        .start(this, REQUEST_CODE_LOCAL);
                //selectPicFromLocal();
                break;
        }
    }

    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(AddScheduleActivity.this, com.hyphenate.easeui.R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            startActivityForResult(
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                    REQUEST_CODE_CAMERA);
        } else {
            startActivityForResult(
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(AddScheduleActivity.this, "com.qcwl.debo.provider", cameraFile)).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
                    REQUEST_CODE_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    //相机拍摄图片
                    mImages.add(cameraFile.getAbsolutePath());
                    mFiles.add(cameraFile);
                    if (mImages != null && mImages.size() > 0) {
                        mImgSize.setVisibility(View.VISIBLE);
                        mImgSize.setText(mImages.size()+"");
                    }
                    break;
                case REQUEST_CODE_LOCAL:
                    //系统相册获取图片
                    if (data == null) {
                        Log.i("fail_camera", "获取失败了");
                        return;
                    }
                    List<String> imgs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (imgs == null || imgs.size() == 0) {
                        return;
                    }
                    for (String url : imgs) {
                        Uri uri = PicUtil.getImageContentUri(this, url);
                        if (uri != null) {
                            File file = new File(url);
                            mFiles.add(file);
                        }
                    }
                    mImages.addAll(imgs);
                    if (mImages != null && mImages.size() > 0) {
                        mImgSize.setVisibility(View.VISIBLE);
                        mImgSize.setText(mImages.size()+"");
                    }
                    break;
                default:
                    break;
            }
            mPictureAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //选择时间dialog
    private void initTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Toast.makeText(AddScheduleActivity.this, getTime(date) + "this is time", Toast.LENGTH_SHORT).show();
                Log.i("pvTime", "onTimeSelect");
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        mDataTime.setText(getTime(date));
                        Log.i("pvTime2", "onTimeSelect" + getTime(date));
                        Log.i("pvTime", "onTimeSelectChange" + getStringToDate(getTime(date)));
                        start_time = getStringToDate(getTime(date)) + "";
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
//                .isDialog(true)
                .setDecorView(mFragment_data)
                .build();
        RelativeLayout rv_topbar = (RelativeLayout) pvTime.findViewById(R.id.rv_topbar);
        TextView text_remind = (TextView) pvTime.findViewById(R.id.text_remind);
        text_remind.setVisibility(View.GONE);
        rv_topbar.setVisibility(View.GONE);
        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
    }

    //选择时间dialog
    private void initRemindTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Log.i("pvTime2", "onTimeSelect" + getTime(date));
                Log.i("pvTime", "onTimeSelect" + getStringToDate(getTime(date)));
                rem_time = getStringToDate(getTime(date)) + "";
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                    }
                })
                .setType(new boolean[]{false, true, true, true, true, false})
                .isDialog(true)
//                .setDecorView(mFragment_data)
                .build();
        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    private String setTime() {
        int year, month, day, hours, minute, seconds;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        seconds = calendar.get(Calendar.SECOND);
        return year + "-" + (month + 1) + "-" + day + " " + hours + ":" + minute + ":" + seconds;
    }

    /*
        网络请求
     */
    //上传备忘录
    private void addSchedule(String start_time, String rem_time,
                             String content, String font_color, String m_type, List<File> img) {
        showProgressDialog("正在上传，请稍后...");
        Api.addMemorandumDetails(start_time, "", sp.getString("uid"), rem_time,
                "", content, font_color, m_type, img, new ApiResponseHandler(AddScheduleActivity.this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            ToastUtils.showShort(AddScheduleActivity.this, "上传成功");
                            hideProgressDialog();
                            finish();
                        } else {
                            Log.i("add_fail", apiResponse.getMessage() + "/" + apiResponse.getCode());
                            hideProgressDialog();
                            ToastUtils.showShort(AddScheduleActivity.this, apiResponse.getMessage());
                        }
                    }
                });
    }

    //修改备忘录
    private void updSchedule(String start_time, String m_id, String rem_time,
                             String content, String font_color, String m_type, List<File> img) {
        showProgressDialog("正在修改，请稍后...");
        Api.updMemorandumDetails(start_time, m_id, sp.getString("uid"), rem_time,
                "", content, font_color, m_type, img, new ApiResponseHandler(AddScheduleActivity.this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            ToastUtils.showShort(AddScheduleActivity.this, "修改成功");
                            hideProgressDialog();
                            finish();
                        } else {
                            Log.i("add_fail", apiResponse.getMessage() + "/" + apiResponse.getCode());
                            hideProgressDialog();
                            ToastUtils.showShort(AddScheduleActivity.this, apiResponse.getMessage());
                        }
                    }
                });
    }

    //获取分类
    private void getClassify() {
        Api.getClassify(sp.getString("uid"), new ApiResponseHandler(AddScheduleActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i("Classify", apiResponse.getMessage() + "/" + apiResponse.getCode());
                if (apiResponse.getCode() == 0) {
                    mList.removeAll(mList);
                    List<ScheduleClassifyBean> beans = JSON.parseArray(apiResponse.getData(), ScheduleClassifyBean.class);
                    Log.i("Classify", apiResponse.getMessage() + "/" + apiResponse.getCode() + "/" + beans.get(0).toString());
                    if (beans != null) {
                        mList.addAll(beans);
                    }
                    mClassifyAdapter.notifyDataSetChanged();
                } else {
                    Log.i("Classify", apiResponse.getMessage() + "/" + apiResponse.getCode());
                }
            }
        });
    }

    private SimpleDateFormat format;

    /*将字符串转为时间戳*/
    public long getStringToDate(String time) {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime() / 1000;
    }
}
