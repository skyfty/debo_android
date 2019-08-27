package com.qcwl.debo.ui.circle;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.baidu.mobstat.StatService;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.found.fans.CameraActivity;
import com.qcwl.debo.ui.my.remind.RemindActivity;
import com.qcwl.debo.utils.SPUtil;
import com.we.widget.WeRecycleView;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/7/25.
 */

public class CircleFragment1 extends Fragment implements Circle2Adapter.CircleCallback {


    private static final String TAG = "CircleFragment";
    @Bind(R.id.tv_title)
    TextView tvtitle;
    @Bind(R.id.image_arrow)
    ImageView imagearrow;
    @Bind(R.id.swipe_target)
    WeRecycleView listView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    /*@Bind(R.id.status_bar)
    View statusBar;*/
    @Bind(R.id.image_back)
    ImageView imageBack;
    /*@Bind(R.id.tabLayout)
    TabLayout tabLayout;*/
    @Bind(R.id.text_publish)
    ImageView imageCamera;
    @Bind(R.id.layout_title)
    LinearLayout layoutTitle;

    @Bind(R.id.layout_comment)
    LinearLayout layoutComment;
    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.text_send)
    TextView textSend;

    private View headerView;
    //    private View inputView;

    private ImageView headerViewBg, comment_iv;
    private CircleImageView headerPhoto;
    private TextView headerName, comment_tv;
    private LinearLayout comment_linear;
    private int page = 1;

    private List<CircleBean> items, friendsList, contactsList;
    private Circle2Adapter adapter2;
    //    int top, alpha;

    public String uid = "";//"34";//19

    private Context instance = null;
    private CircleFragment1 circleFragment;

    private int type = 1;

    int scrollPos = 0, scrollPos2 = 0;
    int scrollTop = 0, scrollTop2 = 0;

    private int spacing = 0;

    private String mobile;
    private final int COL_3F51B5 = Color.parseColor("#71cca2");
    private final int RED = Color.red(COL_3F51B5);
    private final int GREEN = Color.green(COL_3F51B5);
    private final int BLUE = Color.blue(COL_3F51B5);
    private PopupWindow mPopupWindow;
    private float bgAlpha = 1f;
    private boolean bright = false;

    private static final long DURATION = 500;
    private static final float START_ALPHA = 0.7f;
    private static final float END_ALPHA = 1f;
    private AnimUtil animUtil;
    private TextView tv_1, tv_2;
    private LinearLayoutManager linearManager;
    private int dy1 = 0;
    private Map<String,String> map = new HashMap<String,String>();
    private HashMap<String, String> map1;
    private int canSelectCount = 1;
    float percent1 = 1;
    float percent2 = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        ButterKnife.bind(this, view);

        instance = getActivity();
        circleFragment = this;
        spacing = ScreenUtils.getStatusHeight(instance) + ScreenUtils.dp2px(getActivity(), 50);
        uid = SPUtil.getInstance(instance).getString("uid");
        initView();
        initData();
        // 页面埋点
        StatService.onPageStart(getActivity(), "启动圈子界面");
        return view;
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

    private void initView() {
        //statusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenUtils.getStatusHeight(instance)));
        swipeToLoadLayout.setRefreshEnabled(false);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getCircleList();
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeToLoadLayout == null) {
                            return;
                        }
                        swipeToLoadLayout.setRefreshing(false);
                        swipeToLoadLayout.setRefreshEnabled(false);
                    }
                }, 1500);
            }
        });
        swipeToLoadLayout.setOnLoadMoreListener(new com.aspsine.swipetoloadlayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                getCircleList();
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
        headerView = LayoutInflater.from(instance).inflate(R.layout.layout_circle_header, null);
        comment_linear = (LinearLayout) headerView.findViewById(R.id.comment_linear);
        comment_iv = (ImageView) headerView.findViewById(R.id.comment_iv);
        comment_tv = (TextView) headerView.findViewById(R.id.comment_tv);

        if (TextUtils.isEmpty(SPUtil.getInstance(getActivity()).getString("comment"))) {
            headerView.setLayoutParams(new AbsListView.LayoutParams(-1, dp2px(290)));
            comment_linear.setVisibility(View.GONE);
        }
        else {
            comment_linear.setVisibility(View.VISIBLE);
            headerView.setLayoutParams(new AbsListView.LayoutParams(-1, dp2px(360)));
            EaseUserUtils.setUserNick(getActivity(), SPUtil.getInstance(getActivity()).getString("comment").split(",")[SPUtil.getInstance(getActivity()).getString("comment").split(",").length - 1], null, comment_iv);
            comment_tv.setText("有" + SPUtil.getInstance(getActivity()).getString("comment").split(",").length + "条消息");
            comment_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SPUtil.getInstance(getActivity()).setString("comment", "");
                    comment_linear.setVisibility(View.GONE);
                    startActivity(new Intent(getActivity(), RemindActivity.class));
                }
            });
        }
       // headerViewBg = (ImageView) headerView.findViewById(R.id.img_circle_bg);
        headerViewBg.setBackgroundResource(R.color.default_image_bg);
        headerPhoto = (CircleImageView) headerView.findViewById(R.id.img_photo);
        headerName = (TextView) headerView.findViewById(R.id.text_name);

        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setScrollMode(WeRecycleView.SCROLL_SCALE_HEAD);
        listView.setMaxScale(4f);
        listView.setCloseOffset(dp2px(70));
        listView.addItemDecoration(new SpaceItemDecoration(dp2px(getActivity(),1)));
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                    Log.i(TAG, ".....scrollPos====1==" + layoutManager);
                    //View view = layoutManager.findViewByPosition(0);
                        View view = recyclerView.getChildAt(0);
                        if (view == null){

                            Log.i(TAG, ".....view == null");
                            return;
                        }
                    Log.i(TAG, ".....scrollPos====1111==" + view.getTop());
                    if (layoutManager instanceof LinearLayoutManager) {
                        linearManager = (LinearLayoutManager) layoutManager;
                        //获取最后一个可见view的位置
                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        //获取第一个可见view的位置
                        int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                        Log.i(TAG, ".....scrollStateChanged====" + firstItemPosition + "     " + lastItemPosition);
                        switch (type) {
                            case 1:
                                scrollPos = linearManager.findFirstVisibleItemPosition();
                                scrollTop = (view == null) ? 0 : view.getTop();
                                Log.i(TAG, ".....scrollPos====2==" + scrollPos + "     " + scrollTop);
                                if (scrollPos == 0 && scrollTop == 0){
                                    swipeToLoadLayout.setRefreshEnabled(true);
                                    swipeToLoadLayout.setRefreshing(true);
                                }
                                break;
                            case 2:
                                scrollPos2 = linearManager.findFirstVisibleItemPosition();
                                scrollTop2 = (view == null) ? 0 : view.getTop();
                                if (scrollPos2 == 0 && scrollTop2 == 0){
                                    swipeToLoadLayout.setRefreshEnabled(true);
                                    swipeToLoadLayout.setRefreshing(true);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    Log.i(TAG,".....scrollPos====SCROLL_STATE_FLING");
                }else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    Log.i(TAG,".....scrollPos====SCROLL_STATE_TOUCH_SCROLL");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i(TAG,".....scrollPos====4=="+dy);
            }
        });
        listView.setOnHeadExpandListener(new WeRecycleView.OnHeadExpandListener() {
            @Override
            public void onExpand(float percent) {
                if (type == 1){
                    percent1 = percent;
                }else if(type == 2){
                    percent2 = percent;
                }

                Log.i(TAG, "......PERCENT=" + percent);
                layoutTitle.setBackgroundColor(Color.argb((int) (255 * (1 - percent)), RED, GREEN, BLUE));
                if (percent == 1.0){
                    /*swipeToLoadLayout.setRefreshEnabled(true);
                    swipeToLoadLayout.setRefreshing(true);*/
                }
            }
            @Override
            public void onEnterSafeArea(boolean isEnter) {
            }
        });
        //如果有titleBar，这就设titlebar的高度，没有就不需要设置
        //listView.setCloseOffset(50 * mDensity);

        /*listView.addHeaderView(headerView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 不滚动时保存当前滚动到的位置
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    View topView = listView.getChildAt(0);
                    switch (type) {
                        case 1:
                            scrollPos = listView.getFirstVisiblePosition();
                            scrollTop = (topView == null) ? 0 : topView.getTop();
                            break;
                        case 2:
                            scrollPos2 = listView.getFirstVisiblePosition();
                            scrollTop2 = (topView == null) ? 0 : topView.getTop();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });*/
        tvtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow == null) {
                    mPopupWindow = new PopupWindow();
                    animUtil = new AnimUtil();
                }
                showPop();
                toggleBright();
                imagearrow.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.arrow_top));
            }
        });
        textSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(instance, "回复信息不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                commentCircle(reply_uid, moments_id, content);
            }
        });
        editContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    layoutComment.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });


        headerViewBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MPermissions.requestPermissions(circleFragment, CODE_PHOTO, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
        headerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ContactsContentActivity.class).putExtra("my_mobile", mobile).putExtra("mobile", mobile).putExtra("type", "" + type));
            }
        });
    }

    private void showPop() {
        // 设置布局文件
        mPopupWindow.setContentView(LayoutInflater.from(getActivity()).inflate(R.layout.circle_popupwindow, null));
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置pop透明效果
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
        // 设置pop出入动画
        mPopupWindow.setAnimationStyle(R.style.pop_add);
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.setFocusable(true);
        // 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.setTouchable(true);
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPopupWindow.setOutsideTouchable(true);
        // 相对于 + 号正下面，同时可以设置偏移量
        mPopupWindow.showAsDropDown(tvtitle, -150, 0,Gravity.CENTER_VERTICAL);
        // 设置pop关闭监听，用于改变背景透明度
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
                toggleBright();
                imagearrow.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.arrow_bottom));
            }
        });
        tv_1 = mPopupWindow.getContentView().findViewById(R.id.tv_1);
        tv_2 = mPopupWindow.getContentView().findViewById(R.id.tv_2);
        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                if (type == 1){
                    return;
                }
                layoutTitle.setBackgroundColor(Color.argb((int) (255 * (1 - percent1)), RED, GREEN, BLUE));
                tvtitle.setText("朋友圈");
                imagearrow.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.arrow_bottom));

                layoutComment.setVisibility(View.GONE);
                items.clear();
                type = 1;
                if (friendsList.size() == 0) {
                    page = 1;
                    getCircleList();
                }
                else {
                    items.addAll(friendsList);
                }
                adapter2.notifyDataSetChanged();
                if ((scrollPos == 0 || scrollTop == 0)&&percent1 == 1) {
                    Log.i(TAG, ".............scrollPos====33331="+percent1);
                    return;
                }
               /* else if (percent1 == 0 && scrollPos == 0) {
                    Log.i(TAG, ".............scrollPos====33333==" + scrollPos + "   =" + scrollTop);
                    linearManager.scrollToPositionWithOffset(scrollPos+1, scrollTop+dp2px(70));
                }*/
                else {
                    Log.i(TAG, ".............else4=" + scrollPos + "   =" + scrollTop);
                    linearManager.scrollToPositionWithOffset(scrollPos, scrollTop);

                }
                Log.i(TAG, ".............scrollPos====3====" + scrollPos + "   =" + scrollTop);
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                if (type == 2){
                    return;
                }
                layoutTitle.setBackgroundColor(Color.argb((int) (255 * (1 - percent2)), RED, GREEN, BLUE));
                tvtitle.setText("人脉圈");
                imagearrow.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.arrow_bottom));

                layoutComment.setVisibility(View.GONE);
                items.clear();
                type = 2;
                if (contactsList.size() == 0) {
                    page = 1;
                    getCircleList();
                }
                else {
                    items.addAll(contactsList);
                }
                adapter2.notifyDataSetChanged();
                Log.i(TAG,".............CLICK1111="+scrollPos2+"   ="+scrollTop2);
                if (scrollPos2 == 0 ||scrollTop2 == 0){
                    return;
                }
                linearManager.scrollToPositionWithOffset(scrollPos2,scrollTop2);
            }
        });
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;  // 0.0-1.0
        getActivity().getWindow().setAttributes(lp);
        // everything behind this window will be dimmed.
        // 此方法用来设置浮动层，防止部分手机变暗无效
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    // 设置pop关闭监听，用于改变背景透明度


    private void toggleBright() {
        // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
        animUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
        animUtil.addUpdateListener(new AnimUtil.UpdateListener() {
            @Override
            public void progress(float progress) {
                // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                bgAlpha = bright ? progress : (START_ALPHA + END_ALPHA - progress);
                backgroundAlpha(bgAlpha);
            }
        });
        animUtil.addEndListner(new AnimUtil.EndListener() {
            @Override
            public void endUpdate(Animator animator) {
                // 在一次动画结束的
                // 时候，翻转状态
                bright = !bright;
            }
        });
        animUtil.startAnimator();
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(instance, dpVal);
    }

    private void initData() {
        items = new ArrayList<>();
        friendsList = new ArrayList<>();
        contactsList = new ArrayList<>();
        //adapter = new CircleAdapter(items, this);
        adapter2 = new Circle2Adapter(items, this,map);
        listView.setAdapter(adapter2);
        getCircleTopImg();
        page = 1;
        getCircleList();

    }

    private void getCircleList() {
        Api.requestCircleList(type, uid, page, new ApiResponseHandler(instance) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    Log.i(TAG,".........="+apiResponse.getData());
                    List<CircleBean> list = JSON.parseArray(apiResponse.getData(), CircleBean.class);
                    if (page == 1) {
                        items.clear();
                    }
                    if (list != null && list.size() > 0) {
                        items.addAll(list);
                    }
                    adapter2.notifyDataSetChanged();
                    switch (type) {
                        case 1:
                            friendsList.clear();
                            friendsList.addAll(items);
                            break;
                        case 2:
                            contactsList.clear();
                            contactsList.addAll(items);
                            break;
                        default:
                            break;
                    }
                }
                else {
                    Toast.makeText(instance, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void praiseCircle(final int position, final int is_praise, final ImageView imageView) {
        Api.praiseCircle(type, uid, items.get(position).getMoments_id(), is_praise, new ApiResponseHandler(instance) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    PraiseBean praiseBean = JSON.parseObject(apiResponse.getData(), PraiseBean.class);
                    if (praiseBean == null) {
                        return;
                    }
                    items.get(position).setIs_upvote(praiseBean.getUpvote());
                    if ("1".equals(praiseBean.getUpvote())) {//点赞is_praise==1
                        items.get(position).getUpvote_list().add(0, praiseBean);
                    }
                    else if ("0".equals(praiseBean.getUpvote())) {//取消赞is_praise == 0
                        List<PraiseBean> list = items.get(position).getUpvote_list();
                        for (int i = 0; i < list.size(); i++) {
                            if (praiseBean.getUpvote_id().equals(list.get(i).getUpvote_id())) {
                                list.remove(i);
                                break;
                            }
                        }
                    }
                    adapter2.notifyDataSetChanged();
                    imageView.setEnabled(true);
                    notifyMessage(items.get(position).getMobile());
                }
                else {
                    imageView.setEnabled(true);
                    Toast.makeText(instance, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void commentCircle(String reply_uid, String moments_id, String content) {
        Api.commentCircle(type, uid, reply_uid, moments_id, content, new ApiResponseHandler(instance) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    KeyBoardUtils.closeKeybord(editContent, instance);
                    editContent.setText("");
                    layoutComment.setVisibility(View.GONE);
                    Log.i(TAG,"...................="+apiResponse.getData());
                    CommentBean commentBean = JSON.parseObject(apiResponse.getData(), CommentBean.class);
                    if (commentBean != null) {
                        items.get(mPosition).getComment_list().add(commentBean);
                    }

                    notifyMessage(moment_mobile);
                    adapter2.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(instance, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void notifyMessage(String moment_mobile) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);

        //支持单聊和群聊，默认单聊，如果是群聊添加下面这行
        String action = "comment";//action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        cmdMsg.setTo(moment_mobile);
        cmdMsg.addBody(cmdBody);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }

    public void deleteComment(final int cir_type, final int com_type, final int outerPos, final int innerPos) {
        new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("您确定要删除吗？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(cir_type, com_type, outerPos, innerPos);
                dialog.dismiss();
            }
        }).create().show();
    }

    public void getCircleTopImg() {
        Api.getCircleTopImg(uid, new ApiResponseHandler(instance) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        map.clear();
                        JSONObject object = new JSONObject(apiResponse.getData());
                        map1 = new HashMap<String,String>();
                        map1.put("moments_background_img",object.getString("moments_background_img"));
                        map1.put("avatar",object.getString("avatar"));
                        map1.put("user_nickname",object.getString("user_nickname"));
                        map.putAll(map1);
                        Log.i(TAG,"....getCircleTopImg="+object);
                       /* String bgImg = object.getString("moments_background_img");
                        String avatar = object.getString("avatar");
                        String nickname = object.getString("user_nickname");
                        mobile = object.getString("mobile");
                        if (TextUtils.isEmpty(bgImg)) {
                            headerViewBg.setImageResource(R.mipmap.debo_logo);
                        }
                        else {
                            ImgUtil.load(instance, bgImg, headerViewBg);
                        }
                        if (TextUtils.isEmpty(avatar)) {
                            headerPhoto.setImageResource(R.mipmap.head);
                        }
                        else {
                            ImgUtil.loadHead(instance, avatar, headerPhoto);
                        }
                        headerName.setText("" + nickname);*/
                        adapter2.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                   // adapter2.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(instance, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void delete(int cir_type, final int com_type, final int outerPos, final int innerPos) {
        CircleBean circleBean = items.get(outerPos);
        try {
            String mc_id = "";
            if (com_type == 1) {
                mc_id = "0";
            }
            else if (com_type == 2) {
                mc_id = circleBean.getComment_list().get(innerPos).getMc_id();
            }
            Api.deleteCircleInfo(uid, cir_type, com_type, circleBean.getMoments_id(), mc_id, new ApiResponseHandler(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == 0) {
                        if (com_type == 1) {
                            items.remove(outerPos);
                        }
                        else if (com_type == 2) {
                            items.get(outerPos).getComment_list().remove(innerPos);
                        }
                        adapter2.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "已删除", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.image_back, R.id.text_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                hideKeyboard(imageBack);
                getActivity().finish();
                break;
            case R.id.text_publish:
                dialog();
                break;
        }
    }

    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

        }
    }

    private int mPosition = 0;

    public void openEditText(int position, String reply_uid) {
        this.mPosition = position;
        this.moment_mobile = items.get(position).getMobile();
        this.reply_uid = reply_uid;
        this.moments_id = items.get(position).getMoments_id();
        layoutComment.setVisibility(View.VISIBLE);
        editContent.setFocusable(true);
        editContent.setFocusableInTouchMode(true);
        editContent.requestFocus();
        KeyBoardUtils.openKeybord(editContent, instance);

       /* totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] position1 = new int[2];
                layoutComment.getLocationOnScreen(position1);
                int[] position2 = new int[2];
                editContent.getLocationOnScreen(position2);
                listView.scrollBy(0, position1[1] - position2[1] + totalHeight/adapter.getCount());
            }
        }, 300);*/
    }

    public void closeEditText() {
        KeyBoardUtils.closeKeybord(editContent, instance);
        layoutComment.setVisibility(View.GONE);
    }

    String reply_uid, moments_id, moment_mobile;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_PHOTO:
                    if (data == null) {
                        return;
                    }
                    List<String> imgs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (imgs == null || imgs.size() == 0) {
                        return;
                    }
                    modifyTopImg(Luban.get(getActivity()).launch(new File(imgs.get(0))));
                    break;
                case CODE_PUBLISH:
                case CODE_CAMERA:
                    page = 1;
                    getCircleList();
                    break;
                default:
                    break;
            }
        }
    }

    private void modifyTopImg(File file) {
        if (file == null) {
            Toast.makeText(instance, "图片为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Api.modifyCircleTopImg(uid, file, new ApiResponseHandler(instance) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        JSONObject object = new JSONObject(apiResponse.getData());
                        String imgUrl = object.getString("moments_background_img");
                        ImgUtil.load(instance, imgUrl, headerViewBg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(instance, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static final int CODE_PHOTO = 199;
    public static final int CODE_PUBLISH = 299;

    @PermissionGrant(CODE_PHOTO)
    public void requestPermissionSuccess() {
        new AlertDialog.Builder(instance).setItems(new String[]{"更换相册封面"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MultiImageSelector.create().showCamera(true) // show camera or not. true by default
                        //.count(canSelectCount) // max select image size, 9 by default. used width #.multi()
                        .single() // single mode
                        //.multi() // multi mode, default mode;
                        //.origin(imgs) // original select data set, used width #.multi()
                        .start(circleFragment, CODE_PHOTO);
            }
        }).create().show();
    }

    @PermissionDenied(CODE_PHOTO)
    public void requestPermissionFailed() {
        Toast.makeText(instance, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void dialog() {
        new AlertDialog.Builder(getActivity()).setItems(new String[]{"视频", "图片"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        MPermissions.requestPermissions(circleFragment, CODE_CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA);
                        StatService.onEvent(getActivity(), "圈子", "发布视频动态");
                        break;
                    case 1:
                        startActivityForResult(new Intent(getActivity(), PublishCircleActivity.class), CODE_PUBLISH);
                        StatService.onEvent(getActivity(), "圈子", "发布图片动态");
                        break;
                    default:
                        break;
                }
            }
        }).create().show();
    }

    public static final int CODE_CAMERA = 200;

    @PermissionGrant(CODE_CAMERA)
    public void requestPermissionSuccess2() {
        startActivityForResult(new Intent(getActivity(), CameraActivity.class).putExtra("is_cycle", 1), CODE_CAMERA);
    }

    @PermissionDenied(CODE_CAMERA)
    public void requestPermissionFailed2() {
        Toast.makeText(instance, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        StatService.onPageEnd(getActivity(), "结束圈子界面");
    }

    @Override
    public void callBack() {
    }

    @Override
    public void callHeadBg() {
        //更新头部背景图片
        new AlertDialog.Builder(instance).setItems(new String[]{"更换相册封面"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MultiImageSelector.create().showCamera(true) // show camera or not. true by default
                        //.count(canSelectCount) // max select image size, 9 by default. used width #.multi()
                        .single() // single mode
                        //.multi() // multi mode, default mode;
                        //.origin(imgs) // original select data set, used width #.multi()
                        .start(circleFragment, CODE_PHOTO);
            }
        }).create().show();
    }
    public void selectPictrue() {
        canSelectCount = 9 - items.size() + 1;
        MPermissions.requestPermissions(this, CODE_PHOTO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    public  int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}