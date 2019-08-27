package com.qcwl.debo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.hyphenate.exceptions.HyphenateException;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.fragment.ContactFragment;
import com.qcwl.debo.fragment.FoundFragment;
import com.qcwl.debo.fragment.MessageFragment;
import com.qcwl.debo.fragment.MyFragment;
import com.qcwl.debo.ui.login.LoginActivity;
import com.qcwl.debo.ui.my.WishingStarActivity;
import com.qcwl.debo.ui.pay.PayDialog;
import com.qcwl.debo.utils.ActivityManager;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.LogOutDialog;
import com.qcwl.debo.view.UpdateDialog;
import com.qcwl.debo.widget.Constant;
import com.qcwl.debo.widget.DemoHelper;
import com.qcwl.debo.widget.InviteMessage;
import com.qcwl.debo.widget.InviteMessgeDao;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;

/*import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.ConnectHandler*/
;
//import com.qcwl.debo.utils.HMSPushHelper;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout my_ll;
    private RelativeLayout contact_ll, found_ll, message_ll;
    private TextView message_tv, contact_tv, found_tv, tv_count, my_tv, renmai_tip, tv_comment;
    private ImageView message_iv, contact_iv, found_iv, my_iv;
    private int[] iv_normal = {R.mipmap.message_normal, R.mipmap.contact_normal, R.mipmap.found_normal, R.mipmap.my_normal};
    private int[] iv_select = {R.mipmap.message_select, R.mipmap.contact_select, R.mipmap.found_select, R.mipmap.my_select};
    private List<ImageView> list_iv;
    private List<TextView> list_tv;
    private MessageFragment messageFragment;//嘚啵
    private ContactFragment contactFragment;//人脉
    private FoundFragment foundFragment;//探索
    private MyFragment myFragment;//自己
    private List<Fragment> list_fragment;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private int index;
    private UpdateDialog updateDialog;
    private String sql = "delete from conversation";
    private String mysql = "delete from person";
    private ConversationSqlite cs;
    private SQLiteDatabase db;
    private LogOutDialog.Builder builder;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;
    private MyContactListener myContactListener;
    private MyBroadCast myBrocastRecevier;
    private IntentFilter intentFilter;
    private String TAG = "MainActivity";
//    public LocationClient locationClient = null;
//    public BDLocationListener myListener = new MyLocationListener();
//    private String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        Log.i(TAG,".....onCreate");
        initView();
        initFragment();
        monitor();

        //   StatService.start(this);

        if (sp.getInt("is_first_login") == 1) {
            //表示注册成功后第一次登录
            Log.i(TAG,".....is_first_login");
            initFirstLoginDialog();
            sp.setInt("is_first_login", 0);
        }
        setHeight();
        // 获取华为 HMS 推送 token
        //HMSPushHelper.getInstance().getHMSToken(this);
        /*HMSAgent.connect(this, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                Log.i(TAG,"HMS connect end:" + rst);
            }
        });*/
        new Thread() {
            @Override
            public void run() {
                super.run();
            }
        }.start();
    }

    private void setHeight() {

        final LinearLayout lin = (LinearLayout) findViewById(R.id.sssss);
        final RelativeLayout parent = (RelativeLayout) lin.getParent();
        ViewTreeObserver vto = parent.getViewTreeObserver();

        //view加载完成时回调   会监听子控件的加载情况  加载完成时回调
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub

                int height = parent.getMeasuredHeight();

                int width = lin.getMeasuredWidth();
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) parent.getLayoutParams();
                lp.height = parent.getHeight();
                lp.width = width;
               /* for(int i = 0 ; i < parent.getChildCount() ; i ++ ){
                    Log.i("MainActivity",String.format("child:%s,height:%s,width%s",parent.getChildAt(i) ,parent.getChildAt(i).getHeight(),parent.getChildAt(i).getWidth()));
                }*/
                parent.setLayoutParams(lp);
                Log.i("MainActivity", String.format("lp.height:%s,height:%s,width%s,lin.Height%s, lin.MeasuredHeight%s", lp.height, height, width, lin.getHeight(), lin.getMeasuredHeight()));
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    private void initFirstLoginDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("提示")
                .setMessage("首次登录赠送您20积分")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void initView() {
        list_iv = new ArrayList<>();
        list_tv = new ArrayList<>();

        message_ll = (RelativeLayout) findViewById(R.id.message_ll);
        message_iv = (ImageView) findViewById(R.id.message_iv);
        message_tv = (TextView) findViewById(R.id.message_tv);
        contact_ll = (RelativeLayout) findViewById(R.id.contact_ll);
        contact_iv = (ImageView) findViewById(R.id.contact_iv);
        contact_tv = (TextView) findViewById(R.id.contact_tv);
        found_ll = (RelativeLayout) findViewById(R.id.found_ll);
        found_iv = (ImageView) findViewById(R.id.found_iv);
        found_tv = (TextView) findViewById(R.id.found_tv);
        tv_count = (TextView) findViewById(R.id.tv_count);
        my_ll = (LinearLayout) findViewById(R.id.my_ll);
        my_iv = (ImageView) findViewById(R.id.my_iv);
        my_tv = (TextView) findViewById(R.id.my_tv);
        renmai_tip = (TextView) findViewById(R.id.renmai_tip);
        tv_comment = (TextView) findViewById(R.id.tv_comment);

        list_iv.add(message_iv);
        list_iv.add(contact_iv);
        list_iv.add(found_iv);
        list_iv.add(my_iv);

        list_tv.add(message_tv);
        list_tv.add(contact_tv);
        list_tv.add(found_tv);
        list_tv.add(my_tv);

        // register broadcast receiver to receive the change of group from
        // DemoHelper
        registerBroadcastReceiver();
        myContactListener = new MyContactListener();
        EMClient.getInstance().contactManager().setContactListener(myContactListener);


        myBrocastRecevier = new MyBroadCast();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.debo.update");  //添加要收到的广播
        registerReceiver(myBrocastRecevier, intentFilter);
    }

    private void initFragment() {
        list_fragment = new ArrayList<>();
        fm = getSupportFragmentManager();
        messageFragment = new MessageFragment();
        contactFragment = new ContactFragment();
        foundFragment = new FoundFragment();
        myFragment = new MyFragment();


        list_fragment.add(messageFragment);
        list_fragment.add(contactFragment);
        list_fragment.add(foundFragment);
        list_fragment.add(myFragment);


        ft = fm.beginTransaction();
        ft.add(R.id.fl, messageFragment);
        ft.add(R.id.fl, contactFragment);
        ft.add(R.id.fl, foundFragment);
        ft.add(R.id.fl, myFragment);
        ft.show(messageFragment).hide(contactFragment).hide(foundFragment).hide(myFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadLabel();
        showExceptionDialogFromIntent(getIntent());
        new TitleBarBuilder(this).setAlpha(1).setProgress(View.GONE);
//        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (builder != null) {
            builder.create().dismiss();
            builder = null;
            isExceptionDialogShow = false;
        }
        if (broadcastManager != null && broadcastReceiver != null) {
            broadcastManager.unregisterReceiver(broadcastReceiver);
        }
        if (myBrocastRecevier != null) {
            unregisterReceiver(myBrocastRecevier);
        }
        EMClient.getInstance().contactManager().removeContactListener(myContactListener);
        Beta.unregisterDownloadListener();
    }

    private void monitor() {
        message_ll.setOnClickListener(this);
        contact_ll.setOnClickListener(this);
        found_ll.setOnClickListener(this);
        my_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_ll:
                index = 0;
                break;
            case R.id.contact_ll:
                index = 1;
                break;
            case R.id.found_ll:
                index = 2;
                break;
            case R.id.my_ll:
                index = 3;
                break;
        }
        onTableChanged();
    }

    private void onTableChanged() {
        ft = fm.beginTransaction();
        for (int i = 0; i < 4; i++) {
            if (index == i) {
                list_iv.get(i).setImageResource(iv_select[i]);
                list_tv.get(i).setTextColor(getResources().getColor(R.color.font_select));
                ft.show(list_fragment.get(i));
            } else {
                list_iv.get(i).setImageResource(iv_normal[i]);
                list_tv.get(i).setTextColor(getResources().getColor(R.color.font_normal));
                ft.hide(list_fragment.get(i));
            }
        }
        ft.commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了  
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置  
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    public void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // refresh unread count
                updateUnreadLabel();
                // refresh conversation list
                if (messageFragment != null) {
                    messageFragment.refresh();
                }
            }
        });
    }

    private void toast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "" + text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadLabel();
//                updateUnreadAddressLable();
                // refresh conversation list
                if (messageFragment != null) {
                    messageFragment.refresh();
                }

            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * get unread message count
     * 有无未读消息-嘚啵&人脉
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    /**
     * get unread message count
     * 有无未读消息-好友
     *
     * @return
     */
    public int getUnreadMsgByContact() {
        int count = 0;
        String type = "";
        for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.GroupChat || conversation.getType() == EMConversation.EMConversationType.Chat)
                try {
                    if (conversation.getLastMessage() != null)
                        type = conversation.getLastMessage().getStringAttribute("type");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            if (type != null) {
                if ("1".equals(type))
                    count = count + conversation.getUnreadMsgCount();
            }
        }

        return count;
    }

    /**
     * get unread message count
     * 有无未读消息-人脉
     *
     * @return
     */
    public int getUnreadMsgByRenMai() {
        int count = 0;
        String type = "";
        for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.GroupChat || conversation.getType() == EMConversation.EMConversationType.Chat)
                try {
                    if (conversation.getLastMessage() != null)
                        type = conversation.getLastMessage().getStringAttribute("type");
                } catch (HyphenateException e) {
                    //e.printStackTrace();
                }
            if (type != null) {
                if ("2".equals(type))
                    count = count + conversation.getUnreadMsgCount();
            }
        }

        return count;
    }

    /**
     * update unread message count
     * 刷新界面 有无未读消息
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();//嘚啵未读消息
        int contact_count = getUnreadMsgByContact();//好友未读消息
        int renmai_count = getUnreadMsgByRenMai();//人脉未读消息
        int notice = upNoticeCount();//探索提醒消息
        if (count > 0) {
            tv_count.setText(String.valueOf(count));
            tv_count.setVisibility(View.VISIBLE);
        } else {
            tv_count.setVisibility(View.GONE);
        }
        if (notice > 0) {
            renmai_tip.setVisibility(View.VISIBLE);
        } else {
            renmai_tip.setVisibility(View.GONE);
        }

        /*if (messageFragment != null)
            messageFragment.refreshCount(contact_count, renmai_count);*/

        if (contactFragment != null) {
            contactFragment.refresh(notice);
        }
        if (TextUtils.isEmpty(SPUtil.getInstance(MainActivity.this).getString("comment"))) {
            tv_comment.setVisibility(View.GONE);
        } else {
            tv_comment.setVisibility(View.VISIBLE);
            tv_comment.setText(SPUtil.getInstance(MainActivity.this).getString("comment").split(",").length + "");
        }

        if (foundFragment != null) {
            if (TextUtils.isEmpty(SPUtil.getInstance(MainActivity.this).getString("comment"))) {
                foundFragment.refresh(0);
            } else
                foundFragment.refresh(SPUtil.getInstance(MainActivity.this).getString("comment").split(",").length);
        }
    }

    /**
     * 有无提醒消息
     */
    public int upNoticeCount() {
        int count = 0;
        InviteMessgeDao dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        for (InviteMessage inviteMessage : msgs) {
            if (inviteMessage.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED) {
                count++;
            }
        }
        return count;
    }

    public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction("com.debo.contact");
                    intent.putExtra("flag", "add");
                    sendBroadcast(intent);
                }
            });


        }

        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
                            username.equals(ChatActivity.activityInstance.toChatUsername)) {
                        ChatActivity.activityInstance.finish();
                    }
                    Intent intent = new Intent();
                    intent.setAction("com.debo.contact");
                    intent.putExtra("flag", "del");
                    sendBroadcast(intent);
                }
            });
        }

        @Override
        public void onContactInvited(String username, String reason) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction("com.debo.contact");
                    intent.putExtra("flag", "notifation");
                    sendBroadcast(intent);

                    Intent message = new Intent();
                    message.setAction("com.debo.message");
                    sendBroadcast(message);
                    renmai_tip.setVisibility(View.VISIBLE);
                    updateUnreadLabel();
                }
            });
        }

        @Override
        public void onFriendRequestAccepted(final String s) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onFriendRequestDeclined(String s) {


        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        cs = ConversationSqlite.getInstance(this);////new ConversationSqlite(this);
        //  if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false) || intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) || intent.getBooleanExtra(Constant.ACCOUNT_FORBIDDEN, false)) {
        if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)) {
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            ft.show(messageFragment).hide(contactFragment).hide(foundFragment).hide(myFragment);
            ft.commitAllowingStateLoss();
            index = 0;
            onTableChanged();
            showExceptionDialogFromIntent(intent);

            new RxPermissions(this)
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Boolean granted) {
                            if (granted) {
                                db = cs.getWritableDatabase();
                                db.execSQL(sql);
                                db.execSQL(mysql);

                                sp.setIsLogin(false);
                                sp.setString("uid", "");
                                //db.close();
                            } else {
                                Toast.makeText(MainActivity.this, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean isExceptionDialogShow = false;

    private void showExceptionDialogFromIntent(Intent intent) {
        if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)) {
            showExceptionDialog(Constant.ACCOUNT_CONFLICT);
        } /*else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false)) {
            showExceptionDialog(Constant.ACCOUNT_REMOVED);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_FORBIDDEN, false)) {
            showExceptionDialog(Constant.ACCOUNT_FORBIDDEN);
        }*/
    }


    /**
     * show the dialog when user met some exception: such as login on another device, user removed or user forbidden
     */
    private void showExceptionDialog(String exceptionType) {
        if (!MainActivity.this.isFinishing()) {
            try {
                logOut();
                builder = new LogOutDialog.Builder(
                        MainActivity.this, true);
                builder.setMessage("帐号在其他设备登录");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                builder = null;
                                isExceptionDialogShow = false;
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                clearDatas();
                                overridePendingTransition(R.anim.out_from_left, R.anim.in_from_right);
                                startActivity(intent);
                                ActivityManager.getActivityManager().finishAll();
                            }
                        });
                builder.setNegativeButton("重新登录",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                builder = null;
                                isExceptionDialogShow = false;
                            }
                        });
                builder.create().show();
                isExceptionDialogShow = true;
            } catch (Exception e) {
            }
        }
    }

    private void logOut() {
        DemoHelper.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {

                runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        ToastUtils.showShort(MainActivity.this, "退出失败");
                    }
                });
            }
        });
    }

    private class MyBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateDialog = new UpdateDialog(MainActivity.this);
            updateDialog.show();
            checkUpde();
        }

    }

    private void checkUpde() {

        updateBtn(Beta.getStrategyTask());
        updateDialog.setClicklistener(new UpdateDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                DownloadTask task = Beta.startDownload();
                updateBtn(task);
                if (task.getStatus() == DownloadTask.DOWNLOADING) {
                } else if (task.getStatus() == DownloadTask.COMPLETE) {
                    updateDialog.dismiss();
                }
            }
        });

        Beta.registerDownloadListener(new DownloadListener() {
            @Override
            public void onReceive(DownloadTask task) {
                updateBtn(task);
            }

            @Override
            public void onCompleted(DownloadTask task) {
                updateBtn(task);
            }

            @Override
            public void onFailed(DownloadTask task, int code, String extMsg) {
                updateBtn(task);
            }
        });

    }

    public void updateBtn(DownloadTask task) {

        /*根据下载任务状态设置按钮*/
        switch (task.getStatus()) {
            case DownloadTask.INIT:
            case DownloadTask.DELETED:
            case DownloadTask.FAILED: {
                if (updateDialog != null)
                    updateDialog.setTask("马上更新");
            }
            break;
            case DownloadTask.COMPLETE: {
                if (updateDialog != null)
                    updateDialog.setTask("安装");
            }
            break;
            case DownloadTask.DOWNLOADING: {
                if (updateDialog != null) {
                    if (task.getTotalLength() != 0) {
                        float p = (float) task.getSavedLength() / (float) task.getTotalLength();
                        updateDialog.setProgress((int) (p * 100));
                    }

                }
            }
            break;
            case DownloadTask.PAUSED: {
                if (updateDialog != null)
                    updateDialog.setTask("继续下载");
            }
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,".........onActivityResult="+resultCode);
        if (resultCode == RESULT_OK) {
            String ad_id = data.getStringExtra("ad_id");
            String ad_title = data.getStringExtra("ad_title");
            String ad_price = data.getStringExtra("ad_price");
            PayDialog.createDialog(MainActivity.this, ad_title, 3, ad_id, ad_price, "").show();
        }
    }
}
