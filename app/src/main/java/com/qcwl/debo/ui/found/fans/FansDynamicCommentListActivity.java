package com.qcwl.debo.ui.found.fans;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.KeyBoardUtils;
import com.qcwl.debo.ui.found.fans.adapter.FansDynamicCommentAdapter;
import com.qcwl.debo.ui.found.fans.bean.FansDynamicCommentBean;
import com.qcwl.debo.ui.found.fans.contract.FansContract;
import com.qcwl.debo.ui.found.fans.presenter.FansPresenter;
import com.qcwl.debo.utils.AndroidSoftEditUtils;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FansDynamicCommentListActivity extends BaseActivity implements FansContract.View {

    @Bind(R.id.swipe_target)
    RecyclerView recyclerView;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.edit_input)
    EditText editInput;
    @Bind(R.id.btn_send)
    Button btnSend;

    private FansDynamicCommentAdapter adapter;
    private List<FansDynamicCommentBean> items;

    private int page = 1;
    private String uid = "";
    private String reply_uid = "";
    private String moments_id = "";
    private String mc_id = "";

    private FansPresenter presenter = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_dynamic_comment_list);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("评论").setTextRight("点赞")
                .setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FansDynamicCommentListActivity.this
                        , FansDynamicPraiseListActivity.class)
                        .putExtra("moments_id", moments_id));
            }
        });
    }
    private ImmersionBar mImmersionBar;
    @Override
    public void statusBarSetting() {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(false)
                .keyboardEnable(true)
                .init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }

    private void initData() {
        if (getIntent() == null) {
            return;
        }
        moments_id = getIntent().getStringExtra("moments_id");
        uid = sp.getString("uid");
        swipeToLoadLayout.setRefreshEnabled(false);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
//                getFansList();
                swipeToLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeToLoadLayout.setLoadingMore(false);
                    }
                }, 1500);
            }
        });
        items = new ArrayList<>();
        adapter = new FansDynamicCommentAdapter(this, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        presenter = new FansPresenter(this);
        presenter.getDynamicCommentList(this, uid, moments_id);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
    }

    private void testData() {
        FansDynamicCommentBean bean = null;
        for (int i = 0; i < 10; i++) {
            bean = new FansDynamicCommentBean();
            bean.setMc_id("" + i);
            bean.setUid("" + i + "" + i);
            switch (i % 5) {
                case 0:
                    bean.setName("巴卫师兄");
                    bean.setAvatar("http://img4.imgtn.bdimg.com/it/u=671237967,3300686551&fm=26&gp=0.jpg");
                    bean.setMc_content("小师妹，师兄也能做个傀儡给你玩儿的。");
                    break;
                case 1:
                    bean.setName("莫忘尘");
                    bean.setAvatar("http://img2.imgtn.bdimg.com/it/u=1070393410,4158620846&fm=26&gp=0.jpg");
                    bean.setMc_content("小师妹，瞎感慨啥呢？师尊让你快去蒲家村帮师兄除妖~快来~");
                    break;
                case 2:
                    bean.setName("檀无心");
                    bean.setAvatar("http://img4.imgtn.bdimg.com/it/u=380020946,3721512050&fm=26&gp=0.jpg");
                    bean.setMc_content("紫萍，来天工阁，有个礼物要送给你~");
                    List<FansDynamicCommentBean> list = new ArrayList<>();
                    FansDynamicCommentBean commentBean = new FansDynamicCommentBean();
                    commentBean.setName("殷紫萍");
                    commentBean.setUid("108");
                    commentBean.setMc_id("1111");
                    commentBean.setMc_content("无心，等我帮师兄们料理完蒲家村之事就来~");
                    list.add(commentBean);
                    commentBean = new FansDynamicCommentBean();
                    commentBean.setName("檀无心");
                    commentBean.setUid("22");
                    commentBean.setMc_id("1122");
                    commentBean.setReply_name("殷紫萍");
                    commentBean.setReply_uid("108");
                    commentBean.setMc_content("好的，不急，我等你，不见不散呦~");
                    list.add(commentBean);
                    bean.setReplay_list(list);
                    break;
                case 3:
                    bean.setName("萧若兰");
                    bean.setAvatar("http://img3.imgtn.bdimg.com/it/u=1959782189,2791261238&fm=26&gp=0.jpg");
                    bean.setMc_content("紫萍，今天在青丘找到了东厂密信，断水可能有难，我……我不去参加你的收徒仪式了。");
                    break;
                case 4:
                    bean.setName("冷月心");
                    bean.setAvatar("http://img2.imgtn.bdimg.com/it/u=1029824850,320079902&fm=26&gp=0.jpg");
                    bean.setMc_content("二师姐，姚筝师妹今天可去找过你？");
                    break;
                default:
                    break;
            }
            if (i % 3 == 0) {
                bean.setIs_upvote(1);
            } else {
                bean.setIs_upvote(0);
            }
            bean.setUpvote_num("" + 5);
            bean.setComment_time("7-27 18:35");
            items.add(bean);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void doSuccess(int type, Object object) {
        switch (type) {
            case FansPresenter.TYPE_FANS_DYNAMIC_COMMENT_LIST:
                List<FansDynamicCommentBean> list = (List<FansDynamicCommentBean>) object;
                if (list != null) {
                    if (page == 1) {
                        items.clear();
                    }
                    items.addAll(list);
                }
                adapter.notifyDataSetChanged();
                break;
            case FansPresenter.TYPE_FANS_COMMENT_DYNAMIC:
                FansDynamicCommentBean commentBean = (FansDynamicCommentBean) object;
                if (TextUtils.isEmpty(mc_id)) {
                    items.add(0, commentBean);
                } else {
                    items.get(mPosition).getReplay_list().add(commentBean);
                }
                adapter.notifyDataSetChanged();
                editInput.setText("");
                //editInput.setFocusable(false);
                KeyBoardUtils.closeKeybord(editInput, this);
                break;
            case FansPresenter.TYPE_FANS_PRAISE_DYNAMIC_COMMENT:
                String praise_num = (String) object;
                items.get(mPosition).setUpvote_num(praise_num);
                if (is_praise == 1) {
                    items.get(mPosition).setIs_upvote(0);
                } else {
                    items.get(mPosition).setIs_upvote(1);
                }
                adapter.notifyDataSetChanged();
                break;
            case FansPresenter.TYPE_FANS_DELETE_DYNAMIC_COMMENT:
                if (innerPos == -1) {
                    items.remove(outerPos);
                } else {
                    items.get(outerPos).getReplay_list().remove(innerPos);
                }
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void doFailure(int code) {

    }

    private int mPosition;

    public void showSoftInput(int position, String mc_id, String reply_uid) {
        this.mPosition = position;
        this.mc_id = mc_id;
        this.reply_uid = reply_uid;
        editInput.setFocusable(true);
        KeyBoardUtils.openKeybord(editInput, this);
    }

    private void sendComment() {
        String contnet = editInput.getText().toString().trim();
        if (TextUtils.isEmpty(contnet)) {
            ToastUtils.showShort(this, " 评论内容不能为空");
            return;
        }
        presenter.commentDynamic(this, uid, moments_id, mc_id, contnet, reply_uid);
    }

    private int is_praise;

    public void praiseComment(int position, String mc_id, int is_praise) {
        this.mPosition = position;
        this.is_praise = is_praise;
        presenter.praiseDynamicComment(this, uid, mc_id);
    }

    private int outerPos;
    private int innerPos;

    public void deleteFansDynamicComment(final int outerPos, final int innerPos) {
        this.outerPos = outerPos;
        this.innerPos = innerPos;
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您确定要删除吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mc_id = "";
                        if (innerPos == -1) {
                            mc_id = items.get(outerPos).getMc_id();
                        } else {
                            mc_id = items.get(outerPos).getReplay_list().get(innerPos).getMc_id();
                        }
                        presenter.deleteFansDynamicComment(FansDynamicCommentListActivity.this, uid, mc_id);
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}


















