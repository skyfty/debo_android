package com.qcwl.debo.ui.found.joke;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiHttpClient;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.joke.video_new.PlayVideoActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qcwl on 2017/11/23.
 */

public class MyJokeVideo extends BaseActivity {
    private SwipeMenuListView swipe_target;
    private MyVideoAdapter adapter;
    private List<VideoBean> mListVideo;
    private SwipeToLoadLayout swipeToLoadLayout;
    private int page = 1;
    private String uid;
    private ArrayList<Map<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myjokevideo);
        uid = SPUtil.getInstance(this).getString("uid");
        initTitleBar();
        initView();
        myVideoList(page);
    }

    private void initView() {
        swipe_target = (SwipeMenuListView) findViewById(R.id.swipe_target);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);

        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
//              swipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.);
                page = 1;
                myVideoList(page);
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
                myVideoList(page);
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


        mListVideo = new ArrayList<>();
        adapter = new MyVideoAdapter(this, mListVideo);
        swipe_target.setAdapter(adapter);
        initSwipeMenuListView();
        swipe_target.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fengmian = "";
                String avatar = "";
                String nickname = "";
                String video_url = "";
                String upvote = "";
                list = new ArrayList<Map<String,String>>();
                Map<String,String> map = new HashMap<String,String>();
                Intent intent = new Intent();
                intent.setClass(MyJokeVideo.this, PlayVideoActivity.class);
                intent.putExtra("vid", mListVideo.get(position).getId());
                Log.e("this_videoid", mListVideo.get(position).getId());
                intent.putExtra("follow_uid", mListVideo.get(position).getUid());
                if (!TextUtils.isEmpty(mListVideo.get(position).getImg_url())) {
                    intent.putExtra("fengmian", ApiHttpClient.VIDEO_URL + mListVideo.get(position).getImg_url());
                    map.put("fengmian",mListVideo.get(position).getImg_url());
                }
                if (!TextUtils.isEmpty(mListVideo.get(position).getAvatar())) {
                    map.put("avatar",mListVideo.get(position).getAvatar());
                    intent.putExtra("avatar", mListVideo.get(position).getAvatar());
                }
                if (!TextUtils.isEmpty(mListVideo.get(position).getUser_nickname())) {
                    map.put("nickname",mListVideo.get(position).getUser_nickname());
                    intent.putExtra("nickname", mListVideo.get(position).getUser_nickname());
                }
                if (!TextUtils.isEmpty(mListVideo.get(position).getVideo_url())) {
                    map.put("video_url",mListVideo.get(position).getVideo_url());
                    intent.putExtra("video_url", mListVideo.get(position).getVideo_url());
                }
                if (!TextUtils.isEmpty(mListVideo.get(position).getUpvote_num())) {
                    map.put("upvote",mListVideo.get(position).getUpvote_num());
                    intent.putExtra("upvote", mListVideo.get(position).getUpvote_num());
                }
                map.put("uid",mListVideo.get(position).getUid());
                map.put("video_id",mListVideo.get(position).getId());
                list.add(map);
                intent.putExtra("data", (Serializable) list);
                startActivity(intent);
            }
        });
    }

    private void initSwipeMenuListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(MyJokeVideo.this);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(ScreenUtils.dp2px(MyJokeVideo.this, 90));
                // set item title
                deleteItem.setTitle("删除");
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // set a icon
                //deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        swipe_target.setMenuCreator(creator);

        swipe_target.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        myVideoDelete(mListVideo.get(position).getId());
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }

        });
    }

    private void delete(int position) {
        myVideoDelete(mListVideo.get(position).getId());
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("我的段子").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void myVideoList(final int page) {
        showProgressDialog("正在加载");
        Api.myVideoList(sp.getString("uid"), page, new ApiResponseHandler(MyJokeVideo.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<VideoBean> mList = JSON.parseArray(apiResponse.getData(), VideoBean.class);
                    Log.e("this_videoid", mList.get(0).getId());
                    if ((page + "").equals("1")) {
                        mListVideo.clear();
                    }
                    mListVideo.addAll(mList);
                } else if (apiResponse.getCode() == -3) {
                    mListVideo.clear();
                    ToastUtils.showShort(MyJokeVideo.this, apiResponse.getMessage().toString());
                } else {
                    ToastUtils.showShort(MyJokeVideo.this, apiResponse.getMessage().toString());
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

    private void myVideoDelete(String video_id) {
        Api.myVideoDelete(sp.getString("uid"), video_id, new ApiResponseHandler(MyJokeVideo.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    myVideoList(page);
                    ToastUtils.showShort(MyJokeVideo.this, "删除成功");
                    Log.e("this_del", apiResponse.getMessage());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("this_del", apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(String errMessage) {
                super.onFailure(errMessage);
                Log.e("this_del", errMessage);
            }
        });
    }
}
