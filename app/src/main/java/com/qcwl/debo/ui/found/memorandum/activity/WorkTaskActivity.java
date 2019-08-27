package com.qcwl.debo.ui.found.memorandum.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.found.memorandum.adapter.MemoTaskAdapter;
import com.qcwl.debo.ui.found.memorandum.bean.ScheduleDayBean;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkTaskActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.text_data)
    TextView mTextData;
    @Bind(R.id.add_schedule)
    TextView mAddSchedule;
    @Bind(R.id.list_classify)
    ListView mListClassify;
    private ImageView mBack;
    private TextView mTitle;
    private RelativeLayout mRight, mRoot;
    private TextView mRightImg;
    private String mClass_id;
    private List<ScheduleDayBean> mList;
    private CheckBox mAll;
    private CheckBox mCompleted;
    private CheckBox mNot_finished;
    private MemoTaskAdapter mMemoTaskAdapter;
    private PopupWindow mPop;
    private int flag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_task);
        ButterKnife.bind(this);
        mClass_id = getIntent().getStringExtra("class_id");
        initTitleBar();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllData();
    }

    private void initTitleBar() {
        mBack = (ImageView) findViewById(R.id.left_image);
        mTitle = (TextView) findViewById(R.id.title);
        mRight = (RelativeLayout) findViewById(R.id.right_layout);
        mRoot = (RelativeLayout) findViewById(R.id.root);
        mRightImg = (TextView) findViewById(R.id.right_tv);
        mTitle.setText(R.string.work_task);
        mBack.setOnClickListener(this);
        mRight.setOnClickListener(this);
        mRightImg.setText(R.string.screen);
        mRightImg.setVisibility(View.VISIBLE);
    }

    private void initData(){
        mList = new ArrayList<>();
        mMemoTaskAdapter = new MemoTaskAdapter(this,mList);
        mListClassify.setAdapter(mMemoTaskAdapter);
        mListClassify.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("gettime_leth",position+"");
                showDelSchedule(mList.get(position).getId());
                return true;
            }
        });
        mListClassify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mList != null) {
                    startActivity(new Intent(WorkTaskActivity.this, ContentDetailsActivity.class).putExtra("m_id", mList.get(position).getId()));
                }
            }
        });
    }

    //删除提示框
    private void showDelSchedule(final String position) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(WorkTaskActivity.this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_layout:
                screenPopupWindow();
                break;
            case R.id.all:
                flag = 1;
                getAllData();
                mPop.dismiss();
                break;
            case R.id.completed:
                flag = 2;
                getCompleted();
                mPop.dismiss();
                break;
            case R.id.not_finished:
                flag = 3;
                getNotFinished();
                mPop.dismiss();
                break;
        }
    }

    private void screenPopupWindow() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = mLayoutInflater.inflate(R.layout.popup_screen, null);// R.layout.pop为 PopupWindow 的布局文件
        mAll = (CheckBox) contentView.findViewById(R.id.all);
        mCompleted = (CheckBox) contentView.findViewById(R.id.completed);
        mNot_finished = (CheckBox) contentView.findViewById(R.id.not_finished);
        mAll.setOnClickListener(this);
        mCompleted.setOnClickListener(this);
        mNot_finished.setOnClickListener(this);
        switch (flag){
            case 1:
                mAll.setChecked(true);
                mCompleted.setChecked(false);
                mNot_finished.setChecked(false);
                break;
            case 2:
                mAll.setChecked(false);
                mCompleted.setChecked(true);
                mNot_finished.setChecked(false);
                break;
            case 3:
                mAll.setChecked(false);
                mCompleted.setChecked(false);
                mNot_finished.setChecked(true);
                break;
        }
        mPop = new PopupWindow(contentView, WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        pop.setBackgroundDrawable(new BitmapDrawable());                            // 指定 PopupWindow 的背景
        mPop.setFocusable(true);                   // 设定 PopupWindow 取的焦点，创建出来的 PopupWindow 默认无焦点
        mPop.showAsDropDown(mRoot);
    }

    @OnClick({R.id.text_data, R.id.add_schedule})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_data:
                break;
            case R.id.add_schedule:
                startActivity(new Intent(this, AddScheduleActivity.class)
                        .putExtra("title","添加日程")
                        .putExtra("type","0"));
                break;
        }
    }
    /**
     * 网络请求
     *  1.全部
     *  2.未完成
     *  3.已完成
     */
    private void getAllData(){
        Api.getClassifyInfo(sp.getString("uid"),mClass_id, new ApiResponseHandler(WorkTaskActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                mList.removeAll(mList);
                if(apiResponse.getCode() == 0){
                    List<ScheduleDayBean> list = JSON.parseArray(apiResponse.getData(), ScheduleDayBean.class);
                    if(list!=null){
                        mList.addAll(list);
                    }
                }else{
                    Log.i("fail_day",apiResponse.getMessage()+"/"+apiResponse.getCode());
                    ToastUtils.showShort(WorkTaskActivity.this,apiResponse.getMessage());
                }
                mMemoTaskAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getCompleted(){
        Api.getClassifyInfoDone(sp.getString("uid"),mClass_id, new ApiResponseHandler(WorkTaskActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                mList.removeAll(mList);
                if(apiResponse.getCode() == 0){
                    List<ScheduleDayBean> list = JSON.parseArray(apiResponse.getData(), ScheduleDayBean.class);
                    if(list!=null){
                        mList.addAll(list);
                    }
                }else{
                    Log.i("fail_day",apiResponse.getMessage()+"/"+apiResponse.getCode());
                    ToastUtils.showShort(WorkTaskActivity.this,apiResponse.getMessage());
                }
                mMemoTaskAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getNotFinished(){
        Api.getClassifyInfoUndo(sp.getString("uid"),mClass_id, new ApiResponseHandler(WorkTaskActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                mList.removeAll(mList);
                if(apiResponse.getCode() == 0){
                    List<ScheduleDayBean> list = JSON.parseArray(apiResponse.getData(), ScheduleDayBean.class);
                    if(list!=null){
                        mList.addAll(list);
                    }
                }else{
                    Log.i("fail_day",apiResponse.getMessage()+"/"+apiResponse.getCode());
                    ToastUtils.showShort(WorkTaskActivity.this,apiResponse.getMessage());
                }
                mMemoTaskAdapter.notifyDataSetChanged();
            }
        });
    }

    //删除备忘录
    private void delSchedule(String m_id) {
        Api.delMemorandumDetails(sp.getString("uid"), m_id, new ApiResponseHandler(WorkTaskActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    ToastUtils.showShort(WorkTaskActivity.this, "删除成功");
                    if(flag ==1){
                        getAllData();
                    }else if(flag == 2){
                        getCompleted();
                    }else if(flag == 3){
                        getNotFinished();
                    }
                    mMemoTaskAdapter.notifyDataSetChanged();
                } else {
                    Log.i("del_fail", apiResponse.getMessage() + "/" + apiResponse.getCode());
                }
            }
        });
    }
}
