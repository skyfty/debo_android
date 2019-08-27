package com.qcwl.debo.ui.circle;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AlbumDetailActivity extends BaseActivity {//AppCompatActivity

    @Bind(R.id.status_bar)
    View statusBar;
    @Bind(R.id.image_back)
    ImageView imageBack;
    @Bind(R.id.layout_top)
    LinearLayout layoutTitle;
    @Bind(R.id.list_view)
    ListView listView;

    @Bind(R.id.layout_comment)
    LinearLayout layoutComment;
    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.text_send)
    TextView textSend;

    private List<CircleBean> items;
    private CircleAdapter adapter;

    private AlbumDetailActivity instance;

    public String uid = "";//"34";//19

    private int type = 1;//	圈子类型 1朋友圈 2人脉圈

    String reply_uid = "";
    String moments_id = "";

    private int is_both = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.bind(this);
        instance = this;
        uid = SPUtil.getInstance(instance).getString("uid");
        initView();
        items = new ArrayList<>();
        adapter = new CircleAdapter(items, this);
        listView.setAdapter(adapter);
        View footerView = new View(this);
        footerView.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenUtils.dp2px(this, 50)));
        listView.addFooterView(footerView);
        if (getIntent() == null) {
            return;
        }
        type = getIntent().getIntExtra("type", 1);
        moments_id = getIntent().getStringExtra("moments_id");
        is_both = getIntent().getIntExtra("is_both", 1);
        getAlbumDetailInfo(type, moments_id);
    }

    private void initView() {
        int statusHeight = ScreenUtils.getStatusHeight(this);
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight));
        layoutTitle.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight + ScreenUtils.dp2px(this, 50)));
        layoutTitle.getBackground().setAlpha(255);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeEditText();
                KeyBoardUtils.closeKeybord(editContent, instance);
                finish();
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
                    editContent.setText("");
                    closeEditText();
                    KeyBoardUtils.closeKeybord(editContent, instance);
                    return true;
                }
                return false;
            }
        });
    }

    private void getAlbumDetailInfo(int type, String moments_id) {
        Api.getAlbumDetailInfo(uid, type, moments_id, new ApiResponseHandler(instance) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    CircleBean circleBean = JSON.parseObject(apiResponse.getData(), CircleBean.class);
                    if (circleBean != null) {
                        items.add(circleBean);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(instance, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    String moment_mobile;

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
                    } else if ("0".equals(praiseBean.getUpvote())) {//取消赞is_praise == 0
                        List<PraiseBean> list = items.get(position).getUpvote_list();
                        for (int i = 0; i < list.size(); i++) {
                            if (praiseBean.getUpvote_id().equals(list.get(i).getUpvote_id())) {
                                list.remove(i);
                                break;
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    imageView.setEnabled(true);
                    notifyMessage(items.get(position).getMobile());
                } else {
                    imageView.setEnabled(true);
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

    public void commentCircle(String reply_uid, String moments_id, String content) {
        Api.commentCircle(type, uid, reply_uid, moments_id, content, new ApiResponseHandler(instance) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    editContent.setText("");
                    closeEditText();
                    KeyBoardUtils.closeKeybord(editContent, instance);
                    CommentBean commentBean = JSON.parseObject(apiResponse.getData(), CommentBean.class);
                    if (commentBean != null) {
                        items.get(mPosition).getComment_list().add(commentBean);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(instance, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteComment(final int cir_type, final int com_type, final int outerPos, final int innerPos) {
        if (is_both == 1) {
            deleteOne(cir_type, com_type, outerPos, innerPos);
        } else {
            deleteTwo(12, com_type, outerPos, innerPos);
        }
    }

    private void deleteOne(final int cir_type, final int com_type, final int outerPos, final int innerPos) {
        new AlertDialog.Builder(instance)
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
                        delete(cir_type, com_type, outerPos, innerPos);
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    int mywhich = 0;

    private void deleteTwo(final int cir_type, final int com_type, final int outerPos, final int innerPos) {

        new AlertDialog.Builder(instance)
                .setTitle("提示")
                .setSingleChoiceItems(new String[]{"从朋友圈删除", "从人脉圈删除", "全部删除"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mywhich = which;
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int mycir_type = 12;
                        if (mywhich == 0) {
                            mycir_type = 1;
                        } else if (mywhich == 1) {
                            mycir_type = 2;
                        } else if (mywhich == 2) {
                            mycir_type = 12;
                        }
                        delete(mycir_type, com_type, outerPos, innerPos);
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void delete(int cir_type, final int com_type, final int outerPos, final int innerPos) {
        CircleBean circleBean = items.get(outerPos);
        try {
            String mc_id = "";
            if (com_type == 1) {
                mc_id = "0";
            } else if (com_type == 2) {
                mc_id = circleBean.getComment_list().get(innerPos).getMc_id();
            }
            Api.deleteCircleInfo(uid, cir_type, com_type, circleBean.getMoments_id(), mc_id, new ApiResponseHandler(instance) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == 0) {
                        if (com_type == 1) {
                            items.remove(outerPos);
                        } else if (com_type == 2) {
                            items.get(outerPos).getComment_list().remove(innerPos);
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(instance, "已删除", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(instance, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
    }

    private void closeEditText() {
        editContent.setFocusable(false);
        editContent.setFocusableInTouchMode(false);
        editContent.clearFocus();
        layoutComment.setVisibility(View.GONE);
    }
}
