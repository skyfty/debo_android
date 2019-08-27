package com.qcwl.debo.ui.found.joke;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.utils.AndroidSoftEditUtils;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qcwl on 2017/11/18.
 */

public class CommentActivity extends BaseActivity {
    private String video_id;
    private Button btn_reply;
    private EditText editText;
    private List<CommentBean> list;
    private CommentDuanziAdapter2 adapter;
    private int page = 1;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.swipe_target)
    RecyclerView mListView;
    private String reply_content = "";
    private CustomProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        //AndroidSoftEditUtils.init(this);
        View rootView=findViewById(R.id.root);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenUtils.dp2px(this, 50)));
        rootView.setPadding(0,0,0,0);
        video_id = getIntent().getStringExtra("video_id");
        ButterKnife.bind(this);
        initView();
        initTitleBar();
        videoFollowList(video_id, 1);
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("评论详情").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void statusBarSetting() {
        //
    }

    private void initView() {
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                videoFollowList(video_id, page);
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToLoadLayout == null) {
                            return;
                        }
                        swipeToLoadLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        swipeToLoadLayout.setOnLoadMoreListener(new com.aspsine.swipetoloadlayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                videoFollowList(video_id, page);
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToLoadLayout == null) {
                            return;
                        }
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });
        btn_reply = (Button) findViewById(R.id.btn_reply);
        editText = (EditText) findViewById(R.id.edit_text);
        list = new ArrayList<>();
        mListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentDuanziAdapter2(this, list);
        mListView.setAdapter(adapter);

        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditEmply()) {        //判断用户是否输入内容
                    videoComment(reply_content);
                    onFocusChange(false);
                }
            }
        });
    }

    /**
     * 判断对话框中是否输入内容
     */
    private boolean isEditEmply() {
        reply_content = editText.getText().toString().trim();
        if (reply_content.equals("")) {
            Toast.makeText(getApplicationContext(), "评论不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        editText.setText("");
        return true;
    }

    /**
     * 显示或隐藏输入法
     */
    private void onFocusChange(boolean hasFocus) {
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        editText.getContext().getSystemService(INPUT_METHOD_SERVICE);
                if (isFocus) {
                    //显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    //隐藏输入法
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    private void videoComment(String content) {
        Api.videoComment(SPUtil.getInstance(this).getString("uid"), video_id, content, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                ToastUtils.showShort(CommentActivity.this, "评论成功！");
                swipeToLoadLayout.setRefreshing(true);
            }
        });
    }

    //评论列表
    private void videoFollowList(String video_id, int page) {
        showProgressDialog("正在加载");
        Api.videoCommentList(video_id, page, new ApiResponseHandler(CommentActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<CommentBean> commentBeen = JSON.parseArray(apiResponse.getData(), CommentBean.class);
                    list.clear();
                    list.addAll(commentBeen);
                } else {
                    ToastUtils.showShort(CommentActivity.this, apiResponse.getMessage().toString());
                }
                adapter.notifyDataSetChanged();
                hideProgressDialog();
            }

            @Override
            public void onFailure(String errMessage) {
                super.onFailure(errMessage);
                hideProgressDialog();
            }
        });
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
