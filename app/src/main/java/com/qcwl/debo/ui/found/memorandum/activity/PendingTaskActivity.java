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
import com.qcwl.debo.ui.found.memorandum.adapter.ClassifyAdapter;
import com.qcwl.debo.ui.found.memorandum.bean.ScheduleClassifyBean;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PendingTaskActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.add_classify)
    ImageView mAddClassify;
    @Bind(R.id.list_classify)
    ListView mListClassify;
    private ImageView mBack;
    private TextView mTitle;
    private ArrayList<ScheduleClassifyBean> mList;
    private ClassifyAdapter mClassifyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_task);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getClassify();
    }

    private void initTitleBar() {
        mBack = (ImageView) findViewById(R.id.left_image);
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText("分类");
        mBack.setOnClickListener(this);
    }

    private void initData(){
        mList = new ArrayList<>();
        mClassifyAdapter = new ClassifyAdapter(this,mList);
        mListClassify.setAdapter(mClassifyAdapter);
        mListClassify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(PendingTaskActivity.this,WorkTaskActivity.class).putExtra("class_id",mList.get(position).getM_type()));
            }
        });
        mListClassify.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDelClassify(position);
                return true;
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


    @OnClick(R.id.add_classify)
    public void onViewClicked() {
        startActivity(new Intent(PendingTaskActivity.this,AddClassifyActivity.class));
    }

    private void showDelClassify(final int position){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(PendingTaskActivity.this);
        normalDialog.setTitle("确定是否删除");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        ToastUtils.showShort(PendingTaskActivity.this,"确定");
                        if(position>=0 &&position<=4){
                            delClassifyData(mList.get(position).getM_type());
                        }else{
                            delClassify(mList.get(position).getM_type());
                        }

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

    //获取分类
    private void getClassify(){
        Api.getClassify(sp.getString("uid"), new ApiResponseHandler(PendingTaskActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.getCode() == 0){
                    mList.removeAll(mList);
                    List<ScheduleClassifyBean> beans = JSON.parseArray(apiResponse.getData(), ScheduleClassifyBean.class);
                    Log.i("Classify",apiResponse.getMessage()+"/"+apiResponse.getCode()+"/"+beans.get(0).toString());
                    if(beans!=null){
                        mList.addAll(beans);
                    }
                    mClassifyAdapter.notifyDataSetChanged();
                }else{
                    Log.i("Classify",apiResponse.getMessage()+"/"+apiResponse.getCode());
                }
            }
        });
    }

    //删除分类数据
    private void delClassifyData(String m_type){
        Api.delMemorandumType(sp.getString("uid"), m_type, new ApiResponseHandler(PendingTaskActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.getCode() == 0){
                    ToastUtils.showShort(PendingTaskActivity.this,apiResponse.getMessage());
                    getClassify();
                    mClassifyAdapter.notifyDataSetChanged();
                }else {
                    ToastUtils.showShort(PendingTaskActivity.this,apiResponse.getMessage());
                }
            }
        });
    }

    //删除分类
    private void delClassify(String class_id){
        Api.delMemorandumClassify(sp.getString("uid"), class_id, new ApiResponseHandler(PendingTaskActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.getCode() == 0){
                    ToastUtils.showShort(PendingTaskActivity.this,apiResponse.getMessage());
                    getClassify();
                    mClassifyAdapter.notifyDataSetChanged();
                }else {
                    ToastUtils.showShort(PendingTaskActivity.this,apiResponse.getMessage());
                }
            }
        });
    }
}
