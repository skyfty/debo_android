package com.qcwl.debo.ui.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.login.LoginActivity;
import com.qcwl.debo.utils.DataCleanManager;
import com.qcwl.debo.utils.ShareUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.TipDialog;
import com.qcwl.debo.widget.InviteMessgeDao;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/18.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout message_remind, clear_cache, clear_all_history, help_and_feedback, account_and_security, layoutShare;
    private EaseSwitchButton disturb;
    private TextView logout;
    private LinearLayout about;
    private Handler handler;
    private List<EMConversation> emConversations;
    private TipDialog tipDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.setting);
        initTitleBar();
        initView();
        monitor();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle("设置")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
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

    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this, "启动设置页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, "结束设置页面");
    }

    private void initView() {
        handler = new Handler();
        message_remind = (LinearLayout) findViewById(R.id.message_remind);
        disturb = (EaseSwitchButton) findViewById(R.id.disturb);
        logout = (TextView) findViewById(R.id.logout);
        about = (LinearLayout) findViewById(R.id.about);
        clear_cache = (LinearLayout) findViewById(R.id.clear_cache);
        clear_all_history = (LinearLayout) findViewById(R.id.clear_all_history);
        help_and_feedback = (LinearLayout) findViewById(R.id.help_and_feedback);
        account_and_security = (LinearLayout) findViewById(R.id.account_and_security);
        layoutShare = (LinearLayout) findViewById(R.id.layout_share);
    }

    private void monitor() {
        message_remind.setOnClickListener(this);
        disturb.setOnClickListener(this);
        logout.setOnClickListener(this);
        about.setOnClickListener(this);
        clear_cache.setOnClickListener(this);
        clear_all_history.setOnClickListener(this);
        help_and_feedback.setOnClickListener(this);
        account_and_security.setOnClickListener(this);
        layoutShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_share:
                //分享嘚啵链接   appapi/video/video_play_web   http://www.shangtongyuntian.com/dl
                ShareUtil.openShareLogo(SettingActivity.this, R.mipmap.logo, "嘚啵一下，创造你的价值", "嘚啵是什么？他们的嘚啵完以后，居然领到了红包了耶！", "http://www.shangtongyuntian.com/dl");
                break;
            case R.id.message_remind:
                startActivity(new Intent(this, MessageRemindActivity.class));
                break;
            case R.id.disturb:
                if (disturb.isSwitchOpen()) {
                    disturb.closeSwitch();
                    ToastUtils.showShort(this, "已关闭勿扰");
                } else {
                    disturb.openSwitch();
                    ToastUtils.showShort(this, "已开启勿扰");
                }
                break;
            case R.id.logout:
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                clearDatas();
                overridePendingTransition(R.anim.out_from_left, R.anim.in_from_right);
                startActivity(intent);
                // ActivityManager.getActivityManager().finishAll();
                break;
            case R.id.about:
                Intent intent1 = new Intent();
                intent1.setClass(this, AboutActivity.class);
                intent1.putExtra("type", 0);
                startActivity(intent1);
                break;
            case R.id.clear_cache:
                clearCaches();
                break;
            case R.id.clear_all_history:
                tipDialog = new TipDialog(this);
                tipDialog.show();
                tipDialog.setTip("清除提示");
                tipDialog.setContent("是否清除所有聊天记录");
                tipDialog.setButtonText("取消", "清除");
                tipDialog.setClicklistener(new TipDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {
                        tipDialog.dismiss();
                        clearChatRecord();
                    }

                    @Override
                    public void doCancel() {
                        tipDialog.dismiss();
                    }
                });

                break;
            case R.id.help_and_feedback:
                startActivity(new Intent(this, HelpAndFeedbackActivity.class));
                break;
            case R.id.account_and_security:
                startActivity(new Intent(this, AccountAndSecurity.class));
                break;
        }
    }

    private void clearCaches() {
        showProgressDialog("正在清理缓存,请稍候...");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String f_size = DataCleanManager.getTotalCacheSize(SettingActivity.this);
                DataCleanManager.clearAllCache(SettingActivity.this);
                hideProgressDialog();
                ToastUtils.showShort(SettingActivity.this, "清除了" + f_size + "缓存");
            }
        }, 1000);

    }

    private void clearChatRecord() {
        showProgressDialog("正在清除,请稍候...");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                emConversations = loadConversationList();
                for (EMConversation em : emConversations) {
                    if (em.getType() == EMConversation.EMConversationType.GroupChat) {
                        EaseAtMessageHelper.get().removeAtMeGroup(em.conversationId());
                    }
                    try {
                        // delete conversation
                        EMClient.getInstance()
                                .chatManager()
                                .deleteConversation(em.conversationId(),
                                        true);
                        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(SettingActivity.this);
                        inviteMessgeDao.deleteMessage(em.conversationId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                hideProgressDialog();
                ToastUtils.showShort(SettingActivity.this, "清除完成");
            }
        }, 1000);

    }

    /**
     * load conversation list
     *
     * @return +
     */
    private List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    public static void openShare(final Activity context, String image, String content, String url) {
        final UMWeb web = new UMWeb(url);
        web.setTitle(content);
        web.setDescription(" ");
        web.setThumb(new UMImage(context, image));
        new ShareAction(context).withMedia(web)
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)//SHARE_MEDIA.SINA
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA platform) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA platform) {

                    }

                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        ToastUtils.showShort(context, "取消分享");
                    }
                }).open();
    }
}
