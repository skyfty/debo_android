package com.qcwl.debo.ui.found.memorandum.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.found.memorandum.adapter.MemoTaskAdapter;
import com.qcwl.debo.ui.found.memorandum.bean.ScheduleDayBean;
import com.qcwl.debo.utils.GetTimeUtils;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuickMemoryActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.add_schedule)
    TextView mAddSchedule;
    @Bind(R.id.list_classify)
    ListView mListClassify;
    @Bind(R.id.text_data)
    TextView mTextData;
    private ImageView mBack;
    private TextView mTitle;
    private ArrayList<ScheduleDayBean> mDayBeans;
    private MemoTaskAdapter mMemoTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_task);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
        Log.i("gettime....", getTime() + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDayDetails(getTime() + "");
    }

    private void initTitleBar() {
        mBack = (ImageView) findViewById(R.id.left_image);
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText("快速记事");
        mBack.setOnClickListener(this);
    }

    private void initData() {
        String date = GetTimeUtils.stampToDate(getTime());
        String week = GetTimeUtils.getWeek(date);
        mTextData.setText(date+" "+week);
        mDayBeans = new ArrayList<>();
        mMemoTaskAdapter = new MemoTaskAdapter(this, mDayBeans);
        mListClassify.setAdapter(mMemoTaskAdapter);
        mListClassify.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("gettime_",position+"");
                showDelSchedule(mDayBeans.get(position).getId());
                return true;
            }
        });
        mListClassify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDayBeans != null) {
                    startActivity(new Intent(QuickMemoryActivity.this, ContentDetailsActivity.class).putExtra("m_id", mDayBeans.get(position).getId()));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.left_image:
                finish();
                break;
        }
    }

    @OnClick(R.id.add_schedule)
    public void onViewClicked() {
        startActivity(new Intent(this, AddScheduleActivity.class)
                .putExtra("title","添加日程")
        .putExtra("type","0"));
    }

    //删除提示框
    private void showDelSchedule(final String position) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(QuickMemoryActivity.this);
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

    //获取某天的详情
    private void getDayDetails(String date) {
        Api.getDayDetailsEvent(sp.getString("uid"), date, new ApiResponseHandler(QuickMemoryActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                mDayBeans.removeAll(mDayBeans);
                if (apiResponse.getCode() == 0) {
                    //成功
                    List<ScheduleDayBean> beans = JSON.parseArray(apiResponse.getData(), ScheduleDayBean.class);
                    Log.i("Success_day_q", apiResponse.getMessage() + "/" + apiResponse.getCode()+"*-*"+beans.get(0).getContent());
                    if (beans != null) {
                        mDayBeans.addAll(beans);
                    }
                } else {
                    Log.i("fail_day", apiResponse.getMessage() + "/" + apiResponse.getCode());
                }
                mMemoTaskAdapter.notifyDataSetChanged();
            }
        });
    }

    //删除备忘录
    private void delSchedule(String m_id) {
        Api.delMemorandumDetails(sp.getString("uid"), m_id, new ApiResponseHandler(QuickMemoryActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    getDayDetails(getTime() + "");
                    ToastUtils.showShort(QuickMemoryActivity.this, "删除成功");
                } else {
                    Log.i("del_fail", apiResponse.getMessage() + "/" + apiResponse.getCode());
                }
            }
        });
    }

    public long getTime() {
        long millis = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        return millis;
    }
}
