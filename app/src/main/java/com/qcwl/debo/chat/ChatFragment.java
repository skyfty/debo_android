package com.qcwl.debo.chat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.widget.chatrow.EaseChatCard;
import com.hyphenate.easeui.widget.chatrow.EaseChatRedPacket;
import com.hyphenate.easeui.widget.chatrow.EaseChatRedPacket2;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatTransfer;
import com.hyphenate.easeui.widget.chatrow.EaseChatTransfer2;
import com.hyphenate.easeui.widget.chatrow.EaseChatjiehongbao;
import com.hyphenate.easeui.widget.chatrow.EaseChatjiezhuanzhang;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;
import com.hyphenate.util.PathUtil;
import com.qcwl.debo.MainActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.chatroom.BalanceBean;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.GetRedPacketBean;
import com.qcwl.debo.model.GroupInfoBean;
import com.qcwl.debo.model.RedPacketInfoBean;
import com.qcwl.debo.presenter.GroupPresenter;
import com.qcwl.debo.presenter.MyInfoPresenter;
import com.qcwl.debo.presenterInf.GroupPresenterInf;
import com.qcwl.debo.presenterInf.MyInfoPresenterInf;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.KeyBoardUtils;
import com.qcwl.debo.ui.contact.ContactCardActivity;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.contact.SingleChatSettingActivity;
import com.qcwl.debo.ui.found.chat.ChatOverActivity;
import com.qcwl.debo.ui.found.fans.bean.FansBean;
import com.qcwl.debo.ui.login.WelcomeActivity;
import com.qcwl.debo.ui.my.ShowLocationActivity;
import com.qcwl.debo.ui.pay.SetCardDialog;
import com.qcwl.debo.utils.CustomDialog;
import com.qcwl.debo.utils.OnRedPacketDialogClickListener;
import com.qcwl.debo.utils.PicUtil;
import com.qcwl.debo.utils.RedPacketViewHolder;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.BottomAnimDialog;
import com.qcwl.debo.widget.ChatRowVoiceCall;
import com.qcwl.debo.widget.Constant;
import com.qcwl.debo.widget.DemoHelper;
import com.qcwl.debo.widget.EmojiconExampleGroupData;
import com.qcwl.debo.widget.RobotUser;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static com.hyphenate.easeui.EaseConstant.CHATTYPE_CHATROOM;
import static com.hyphenate.easeui.EaseConstant.CHATTYPE_GROUP;
import static com.hyphenate.easeui.EaseConstant.CHATTYPE_SINGLE;

/**
 * Created by Administrator on 2016/9/22.
 */

public class ChatFragment extends EaseChatFragment implements EaseChatFragmentHelper, MyInfoPresenterInf, OnClickListener, CompoundButton.OnCheckedChangeListener, GroupPresenterInf {


    // constant start from 11 to avoid conflict with constant in base class
    private static final int ITEM_VIDEO = 11;
    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;
    private static final int ITEM_CARD = 15;
    private static final int ITEM_ATTENTION = 19;//关注

    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private static final int REQUEST_CODE_SELECT_CARD = 17;
    private static final int REQUEST_CODE_SELECT_REFPACKET = 18;


    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;
    private static final int MESSAGE_TYPE_SENT_CARD = 5;
    private static final int MESSAGE_TYPE_RECV_CARD = 6;


    //red packet code : 红包功能使用的常�?
    private static final int MESSAGE_TYPE_SEND_RED_PACKET = 7;//发送红包
    private static final int MESSAGE_TYPE_RECV_RED_PACKET = 8;//接收红包

    private static final int MESSAGE_TYPE_SEND_RED_PACKET2 = 77;//发送红包
    private static final int MESSAGE_TYPE_RECV_RED_PACKET2 = 88;//接收红包

    private static final int ITEM_RED_PACKET = 16;

    //转账
    private static final int ITEM_TRANSFER = 20;
    private static final int MESSAGE_TYPE_SEND_TRANSFER = 21;
    private static final int MESSAGE_TYPE_RECV_TRANSFER = 22;
    private static final int REQUEST_CODE_SELECT_TRANSFER = 23;

    private static final int MESSAGE_TYPE_jiehongbao_TRANSFER = 24;
    private static final int MESSAGE_TYPE_SEND_jiehongbao = 25;
    private static final int MESSAGE_TYPE_RECV_jiehongbao = 26;

    private static final int MESSAGE_TYPE_zhuanzhang_TRANSFER = 27;
    private static final int MESSAGE_TYPE_SEND_zhuanzhang = 28;
    private static final int MESSAGE_TYPE_RECV_zhuanzhang = 29;

    //礼物
    private static final int SEND_GIFT = 30;
    private static final int MESSAGE_TYPE_SENT_GIFT = 31;
    private static final int MESSAGE_TYPE_RECV_GIFT = 32;

    private static final int OTHER_LOCATION = 33;

    //支付成功之后 红包回调码  7


    //   private static final int MESSAGE_TYPE_SEND_RED_PACKET_ACK = 7;
    //   private static final int MESSAGE_TYPE_RECV_RED_PACKET_ACK = 8;
    //   private static final int REQUEST_CODE_SEND_RED_PACKET = 16;
    //end of red packet code

    /**
     * if it is chatBot
     */
    private boolean isRobot;
    private final int PESSION_CODE = 1;
    private String[] permissions;
    private String[] location;
    private String[] record;
    private int flag;
    public static final int ITEM_TAKE_PICTURE = 1;
    public static final int ITEM_PICTURE = 2;
    public static final int ITEM_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECALL = 9;
    private MyBroadcastReceiver myBrocastRecevier;
    private MyInfoPresenter myInfoPresenter;
    private String mMoney;
    private String mGiftCount = "";
    private String mGiftMoney = "";
    private RadioButton mRadioButton, mRadioButton2, mRadioButton3, mRadioButton4, mRadioButton5, mRadioButton6, mRadioButton7, mRadioButton8;
    private RadioGroup mRadio1, mRadio2;
    private TextView mBalance;
    private Dialog mSendGifDialog;
    private int mDrawable;
    private String TAG = "ChatFragment";
    private GroupPresenter groupPresenter;
    private GroupInfoBean gb;
    private ArrayList<String> mMembers;
    private SPUtil spUtil;
    private View mRedPacketDialogView;
    private RedPacketViewHolder mRedPacketViewHolder;
    private CustomDialog mRedPacketDialog;
    private GetRedPacketBean mGetRedPacketBean;
    private String order_price;
    private String nickname2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        location = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        record = new String[]{Manifest.permission.RECORD_AUDIO};
        myBrocastRecevier = new MyBroadcastReceiver();
        myInfoPresenter = new MyInfoPresenter(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.debo.is_show_name"); // 添加要收到的广播
        getActivity().registerReceiver(myBrocastRecevier, intentFilter);
        getBalance();
        spUtil = SPUtil.getInstance(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
        if (chatType == CHATTYPE_SINGLE) {
            Map<String, RobotUser> robotMap = DemoHelper.getInstance().getRobotList();
            if (robotMap != null && robotMap.containsKey(toChatUsername)) {
                isRobot = true;
            }
        }
        super.setUpView();
        // set click listener
        titleBar.setLeftLayoutClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String module = getArguments().getString("module");
                String toChatUsername2 = getArguments().getString("userId");
                if (module != null && !module.equals("")) {
                   /* Intent intent = new Intent(getActivity(), ChatOverActivity.class);
                    intent.putExtra("mobile", toChatUsername2);
                    startActivityForResult(intent, 100);*/
                   getActivity().onBackPressed();
                } else {
                    if (EasyUtils.isSingleActivity(getActivity())) {
                        Log.i(TAG,"............setLeftLayoutClickListener");
                        /*Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);*/
                        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                        startActivity(intent);
                    }
                    onBackPressed();
                }
            }
        });
        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
                        startActivityForResult(new Intent(getActivity(), PickAtUserActivity.class).
                                putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            titleBar.setRightLayoutVisibility(View.GONE);
        }
        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    MPermissions.requestPermissions(ChatFragment.this, PESSION_CODE, record);
                    return false;
                } else {
                    return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {

                        @Override
                        public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                            sendVoiceMessage(voiceFilePath, voiceTimeLength);
                        }
                    });
                }
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }
        });

        myInfoPresenter.isAttention(getActivity(), SPUtil.getInstance(getActivity()).getString("uid"), getMobile(toChatUsername));
    }

    private String getMobile(String username) {
        if (TextUtils.isEmpty(username)) {
            return "";
        }
        int start = 0;
        for (int i = 0; i < username.length(); i++) {
            if (Character.isDigit(username.charAt(i))) {
                start = i;
                break;
            }
        }
        return username.substring(start);
    }

    @Override
    protected void registerExtendMenuItem() {//重写添加菜单条目方法  进行添加语音，视频通话等
        //use the menu in base class
        super.registerExtendMenuItem();//菜单条目父类已有  图片，拍摄
//        inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, ITEM_FILE, extendMenuItemClickListener);
        if (chatType == CHATTYPE_SINGLE) {
            //extend menu items
            //位置
            inputMenu.registerExtendMenuItem(R.string.attach_location, R.drawable.icon_location, ITEM_LOCATION, extendMenuItemClickListener);
            //语音(已经与视频合并成一个)
            //inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.mipmap.yuyin, ITEM_VOICE_CALL, extendMenuItemClickListener);
            //视频
            inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.mipmap.video_call, ITEM_VIDEO_CALL, extendMenuItemClickListener);
            //名片
            inputMenu.registerExtendMenuItem(R.string.attach_card, R.mipmap.mingpian, ITEM_CARD, extendMenuItemClickListener);
            //关注
            inputMenu.registerExtendMenuItem(R.string.attach_attention, R.mipmap.guanzhu, ITEM_ATTENTION, extendMenuItemClickListener);

        }
        //红包
        inputMenu.registerExtendMenuItem(R.string.attach_red_packet, R.mipmap.hongbao, ITEM_RED_PACKET, extendMenuItemClickListener);

        if (chatType == CHATTYPE_SINGLE) {
            //转账
            inputMenu.registerExtendMenuItem(R.string.attach_transfer, R.mipmap.zhuanzhang, ITEM_TRANSFER, extendMenuItemClickListener);
            //inputMenu.registerExtendMenuItem(R.string.attach_other_location, R.mipmap.other_loc, OTHER_LOCATION, extendMenuItemClickListener);
        }

        //聊天室暂时不支持礼物功能
        if (chatType != CHATTYPE_CHATROOM) {
            //礼物
            inputMenu.registerExtendMenuItem(R.string.attach_gift, R.mipmap.icon_gift, SEND_GIFT, extendMenuItemClickListener);
        }

        //聊天室暂时不支持红包功能
        //red packet code : 注册红包菜单选项
//        if (chatType != Constant.CHATTYPE_CHATROOM) {
//            inputMenu.registerExtendMenuItem(R.string.attach_red_packet, R.drawable.em_chat_red_packet_selector, ITEM_RED_PACKET, extendMenuItemClickListener);
//        }
        //end of red packet code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case ContextMenuActivity.RESULT_CODE_COPY: // copy
                    clipboard.setPrimaryClip(ClipData.newPlainText(null,
                            ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage()));
                    break;
                case ContextMenuActivity.RESULT_CODE_DELETE: // delete
                    conversation.removeMessage(contextMenuMessage.getMsgId());
                    messageList.refresh();
                    break;

                case ContextMenuActivity.RESULT_CODE_FORWARD: // forward
                    Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
                    intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
                    intent.putExtra("type", type);
                    intent.putExtra("is_show_name", isShowName);
                    startActivity(intent);
                    break;
                case ContextMenuActivity.RESULT_CODE_RECALL: // retract 撤回
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                              /*  EMMessage msgNotification = EMMessage.createTxtSendMessage(" ",contextMenuMessage.getTo());
                                EMTextMessageBody txtBody = new EMTextMessageBody(getResources().getString(R.string.msg_recall_by_self));
                                msgNotification.addBody(txtBody);
                                msgNotification.setMsgTime(contextMenuMessage.getMsgTime());
                                msgNotification.setLocalTime(contextMenuMessage.getMsgTime());
                                msgNotification.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
                                msgNotification.setStatus(EMMessage.Status.SUCCESS);*/

                                EMClient.getInstance().chatManager().recallMessage(contextMenuMessage);
                                //EMClient.getInstance().chatManager().saveMessage(msgNotification);
                                messageList.refresh();
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                    break;
                default:
                    break;
            }
        }

        if (requestCode == REQUEST_CODE_LOCAL) {
            if (data == null) {
                return;
            }
            List<String> imgs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (imgs == null || imgs.size() == 0) {
                return;
            }
            for (String url : imgs) {
                Uri uri = PicUtil.getImageContentUri(getActivity(), url);
                if (uri != null) {
                    sendPicByUri(uri);
                }
            }

        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //send the video
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");

                        File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        String[] array = EaseUserUtils.getUserInfoArray(getActivity(), username);
                        inputAtUsername(array[0], false);
                    }
                    break;
                //red packet code : 发�?�红包消息到聊天界面
                case REQUEST_CODE_SELECT_REFPACKET:
                    if (data != null)
                        //领取红包发送消息到聊天界面
                        sendRedPacketMessge(data);
                    break;
                //名片
                case REQUEST_CODE_SELECT_CARD:
                    if (data != null)
                        sendCardMessage(data);
                    break;
                //end of red packet code
                //转账
                case REQUEST_CODE_SELECT_TRANSFER:
                    if (data != null)
                        sendTransferMessge(data);
                    break;
                //领取红包
                case MESSAGE_TYPE_jiehongbao_TRANSFER:
                    if (data != null) {
                        if (chatType == CHATTYPE_SINGLE) {
                            if (!data.getStringExtra("user_nickname").equals(SPUtil.getInstance(getActivity()).getString("name"))) {
                                sendjiehongbaoMessage(data);
                            }
                        } else {
                            sendjiehongbaoMessage(data);
                        }
                    }
                    break;
                //领取转账
                case MESSAGE_TYPE_zhuanzhang_TRANSFER:
                    if (data != null) {
                        if (!data.getStringExtra("user_nickname").equals(SPUtil.getInstance(getActivity()).getString("name"))) {
                            //sendzhuanzhangMessage(data);
                            sendZhuanZhangMessage(data);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0 && o != null) {
            if (o instanceof FansBean) {
                FansBean f = (FansBean) o;
//                if ("0".equals(f.getIs_fans())) {
//                    inputMenu.registerExtendMenuItem(R.string.attach_attention, R.mipmap.ic_yes_care, ITEM_ATTENTION, extendMenuItemClickListener);
//                } else {
//                    inputMenu.registerExtendMenuItem(R.string.attach_have_attention, R.mipmap.ic_care, ITEM_ATTENTION, extendMenuItemClickListener);
//                }
            } else if (o instanceof String) {
                String str = (String) o;
                EaseChatExtendMenu.ChatMenuItemModel c = inputMenu.getExtendMenu().getMyAdapter().getItem(5);
                if ("1".equals(str)) {
                    c.setName("关注");
                    c.setImage(R.mipmap.ic_care);
                } else {
                    c.setName("已关注");
                    c.setImage(R.mipmap.ic_yes_care);
                }
                inputMenu.getExtendMenu().getMyAdapter().notifyDataSetChanged();
                ToastUtils.showShort(getActivity(), message);
            }
            if (o instanceof GroupInfoBean) {
                gb = (GroupInfoBean) o;
                EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
                List<GroupInfoBean.Infos> affiliations = gb.getAffiliations();
                mMembers = new ArrayList<>();
                for (GroupInfoBean.Infos s : affiliations) {
                    if (s.getMember() != null) {
                        mMembers.add(s.getMember());
                    }
                }
                Object[] array = mMembers.toArray();
                sendGroupGift(mGiftMoney, group.getOwner(), mMembers);
            }
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("setting".equals(intent.getStringExtra("flag"))) {
                if ("1".equals(intent.getStringExtra("is_show_name"))) {
                    messageList.messageAdapter.setShowUserNick(false);
                } else if ("2".equals(intent.getStringExtra("is_show_name"))) {
                    messageList.messageAdapter.setShowUserNick(true);
                }
                messageList.refresh();
            } else if ("updateName".equals(intent.getStringExtra("flag"))) {
                //nickname = intent.getStringExtra("name");
                titleBar.setTitle(intent.getStringExtra("name"));
            } else if ("clear_history".equals(intent.getStringExtra("flag"))) {
                emptyHistory();
            }
        }
    }

    private void sendTransferMessge(Intent intent) {
        EMMessage message = EMMessage.createTxtSendMessage("【转账】", toChatUsername);
        //tophone 对方的手机号      idStr 自己的id  uidheadStr 自己的头像   ID 扩展消息名片  messagetype 1代表红包  is_transfer true   Redbaoid 红包id
        JSONObject object = new JSONObject();
        String content = "";
        try {
            object.put("tophone", toChatUsername);
            object.put("idStr", SPUtil.getInstance(getActivity()).getString("uid"));
            object.put("uidheadStr", SPUtil.getInstance(getActivity()).getString("headsmall"));
            object.put("messagetype", "2");
            object.put("ID", "扩展消息名片");
            object.put("is_transfer", true);
            object.put("Redbaoid", intent.getStringExtra("r_id"));//红包id
            object.put("phone", SPUtil.getInstance(getActivity()).getString("phone"));//
            object.put("order_price", intent.getStringExtra("order_price"));//转账金额
            if (intent.getStringExtra("content").isEmpty()) {
                content = "转账";
            } else {
                content = intent.getStringExtra("content");
            }
            object.put("content", content);//留言
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("getChat_room", toChatUsername + "/*/" + nickname + "/" + SPUtil.getInstance(getActivity()).getString("phone"));
        message.setAttribute("ext", object);
        sendMessage(message);
    }

    private void sendRedPacketMessge(Intent intent) {
        EMMessage message = EMMessage.createTxtSendMessage("【红包】", toChatUsername);
        //tophone 对方的手机号      idStr 自己的id  uidheadStr 自己的头像   ID 扩展消息名片  messagetype 1代表红包  is_red_packet true   Redbaoid 红包id
        JSONObject object = new JSONObject();
        String content;
        try {
            object.put("tophone", toChatUsername);
            object.put("idStr", SPUtil.getInstance(getActivity()).getString("uid"));
            object.put("uidheadStr", SPUtil.getInstance(getActivity()).getString("headsmall"));
            object.put("messagetype", "1");
            object.put("ID", "扩展消息名片");
            object.put("is_red_packet", true);
            object.put("Redbaoid", intent.getStringExtra("r_id"));//红包id
            object.put("phone", SPUtil.getInstance(getActivity()).getString("phone"));//红包id
            if (intent.getStringExtra("content").isEmpty()) {
                content = "恭喜发财！大吉大利";
            } else {
                content = intent.getStringExtra("content");
            }
            object.put("content", content);//留言
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setAttribute("ext", object);
        sendMessage(message);
    }

    private void sendCardMessage(Intent intent) {
        final EMMessage message = EMMessage.createTxtSendMessage("【名片】", toChatUsername);
        //ID 扩展消息名片  headStr  选中人头像  idStr 自己的id  nameStr  选中人的昵称  phone 选中人的电话  uidStr 别人的id
        JSONObject object = new JSONObject();
        try {
            Log.i(TAG, ".............sendCardMessage=" + type + "     " + intent.getStringExtra("phone")+"     "+intent.getStringExtra("headStr"));
            object.put("ID", "扩展消息名片");
            object.put("headStr", intent.getStringExtra("headStr"));
            object.put("idStr", SPUtil.getInstance(getActivity()).getString("uid"));
            object.put("nameStr", intent.getStringExtra("nameStr"));
            object.put("phone", intent.getStringExtra("phone"));
            object.put("sex", intent.getStringExtra("sex"));
            object.put("touxiang", SPUtil.getInstance(getActivity()).getString("headsmall"));
            object.put("uidStr", intent.getStringExtra("uidStr"));
            object.put("user_area", intent.getStringExtra("user_area"));
            object.put("is_card", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setAttribute("ext", object);
        SetCardDialog setCardDialog = new SetCardDialog(getContext(),
                ((ChatActivity) getActivity()).getIntent().getStringExtra("nickIconUrl"),
                ((ChatActivity) getActivity()).getIntent().getStringExtra("nickname"),
                "[个人名片] " + intent.getStringExtra("nameStr"),
                intent.getStringExtra("sex"));

        setCardDialog.setClickListener(new SetCardDialog.SetCardDialogListener() {
            @Override
            public void onItem1Listener(String liuYanContent) {
                try {
                    Log.i(TAG, ".............onItem1Listener=" + message.getStringAttribute("ext"));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                sendMessage(message);
                if (!TextUtils.isEmpty(liuYanContent)) {
                    sendTextMessage(liuYanContent);
                }

            }

            @Override
            public void onItem2Listener() {

            }
        });
        //setCardDialog.show();


    }


    private void sendjiehongbaoMessage(Intent intent) {
        EMMessage message = EMMessage.createTxtSendMessage("【领取红包】", toChatUsername);
        //ID 扩展消息名片  headStr  选中人头像  idStr 自己的id  nameStr  选中人的昵称  phone 选中人的电话  uidStr 别人的id
        JSONObject object = new JSONObject();
        try {
            object.put("ID", "扩展消息接收红包");
            object.put("is_jiehongbao", true);
            object.put("user_nickname", intent.getStringExtra("user_nickname"));//收红包人的名称
            object.put("content", SPUtil.getInstance(getActivity()).getString("content"));
            object.put("myUserName", SPUtil.getInstance(getActivity()).getString("name"));//发红包人的名称
            object.put("first_get_name", intent.getStringExtra("first_get_name"));//谁领取 名字
            object.put("offer_user_nickname", intent.getStringExtra("offer_user_nickname"));//谁发的 名字
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("this_is_mp", "收红包");
        message.setAttribute("ext", object);
        sendMessage(message);
    }


    private void sendJieRedPacketMessge(String r_id) {
        EMMessage message = EMMessage.createTxtSendMessage("【领取红包】", toChatUsername);
        //tophone 对方的手机号      idStr 自己的id  uidheadStr 自己的头像   ID 扩展消息名片  messagetype 1代表红包  is_red_packet true   Redbaoid 红包id
        JSONObject object = new JSONObject();
        String content;
        try {
            object.put("tophone", toChatUsername);
            object.put("idStr", SPUtil.getInstance(getActivity()).getString("uid"));
            object.put("uidheadStr", SPUtil.getInstance(getActivity()).getString("headsmall"));
            object.put("messagetype", "1");
            object.put("ID", "扩展消息名片");
            object.put("is_jiehongbao", true);
            object.put("Redbaoid", r_id);//红包id
            object.put("phone", SPUtil.getInstance(getActivity()).getString("phone"));//红包id
            object.put("state", "收到红包");//红包id
            //if (intent.getStringExtra("content").isEmpty()) {
            content = "恭喜发财！大吉大利";
            /*} else {
                content = intent.getStringExtra("content");
            }*/
            object.put("content", content);//留言
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("this_is_hb", "红包");
        message.setAttribute("ext", object);
        sendMessage(message);
    }


    private void sendzhuanzhangMessage(Intent intent) {
        EMMessage message = EMMessage.createTxtSendMessage("【转账消息】", toChatUsername);
        //ID 扩展消息名片  headStr  选中人头像  idStr 自己的id  nameStr  选中人的昵称  phone 选中人的电话  uidStr 别人的id
        JSONObject object = new JSONObject();
        try {
            object.put("ID", "扩展消息确认转账");
            object.put("is_zhuanzhang", true);
            object.put("user_nickname", intent.getStringExtra("user_nickname"));//收红包人的名称
            object.put("content", SPUtil.getInstance(getActivity()).getString("content"));
            object.put("myUserName", SPUtil.getInstance(getActivity()).getString("name"));//发红包人的名称
            object.put("first_get_name", intent.getStringExtra("first_get_name"));//谁领取 名字
            object.put("offer_user_nickname", intent.getStringExtra("offer_user_nickname"));//谁发的 名字
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("this_is_sq", "收钱");
        message.setAttribute("ext", object);
        sendMessage(message);
    }

    private void sendZhuanZhangMessage(Intent intent) {
        EMMessage message = EMMessage.createTxtSendMessage("【转账】", toChatUsername);
        //tophone 对方的手机号      idStr 自己的id  uidheadStr 自己的头像   ID 扩展消息名片  messagetype 1代表红包  is_zhuanzhang true   Redbaoid 红包id
        JSONObject object = new JSONObject();
        String content = "";
        try {
            object.put("tophone", toChatUsername);
            object.put("idStr", SPUtil.getInstance(getActivity()).getString("uid"));
            object.put("uidheadStr", SPUtil.getInstance(getActivity()).getString("headsmall"));
            object.put("messagetype", "2");
            object.put("ID", "扩展消息名片");
            object.put("is_zhuanzhang", true);
            object.put("Redbaoid", intent.getStringExtra("r_id"));//红包id
            object.put("phone", SPUtil.getInstance(getActivity()).getString("phone"));//
            object.put("order_price", intent.getStringExtra("order_price"));//转账金额
            object.put("content", "已收钱");//留言
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("this_is_mp", "转账");
        Log.e("getChat_room", toChatUsername + "/*/" + nickname + "/" + SPUtil.getInstance(getActivity()).getString("phone"));
        message.setAttribute("ext", object);
        sendMessage(message);
    }

    private void sendGiftMessage() {
        Log.i(TAG, "....sendGiftMessage=" + mGiftCount);
        Log.e(TAG, "uidheadStr=" + SPUtil.getInstance(getActivity()).getString("avatar") + "      " + SPUtil.getInstance(getActivity()).getString("headsmall"));
        EMMessage message = EMMessage.createTxtSendMessage("【礼物消息】", toChatUsername);
        //ID 扩展消息名片  headStr  选中人头像  idStr 自己的id  nameStr  选中人的昵称  phone 选中人的电话  uidStr 别人的id
        JSONObject object = new JSONObject();
        try {
            object.put("ID", "扩展消息名片");
            object.put("gift", mGiftCount);
            object.put("is_card", true);
            object.put("idStr", SPUtil.getInstance(getActivity()).getString("uid"));
            object.put("uidheadStr", SPUtil.getInstance(getActivity()).getString("headsmall"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setAttribute("ext", object);
        sendMessage(message);
    }

    @Override
    protected void sendMessageWithOtherParams(EMMessage message) {
        super.sendMessageWithOtherParams(message);
        message.setAttribute("user_nickname", SPUtil.getInstance(getActivity()).getString("name"));
        message.setAttribute("avatar", SPUtil.getInstance(getActivity()).getString("headsmall"));
    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if (isRobot) {
            //set message extension
            message.setAttribute("em_robot_message", isRobot);
        }
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }


    @Override
    public void onEnterToChatDetails() {
        if (chatType == Constant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityForResult((new Intent(getActivity(), GroupDetailsActivity.class).putExtra("groupId", toChatUsername).putExtra("type", type)), REQUEST_CODE_GROUP_DETAIL);
        } else if (chatType == CHATTYPE_SINGLE) {
            Log.i("ChatFragment", ".....................SingleChatSettingActivity");
            startActivity(new Intent(getActivity(), SingleChatSettingActivity.class).putExtra("mobile", toChatUsername).putExtra("module", getArguments().getString("module")));
        } else if (chatType == Constant.CHATTYPE_CHATROOM) {
            startActivityForResult(new Intent(getActivity(), ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername), REQUEST_CODE_GROUP_DETAIL);
        }
    }

    @Override
    public void onAvatarClick(String username) {
        if (TextUtils.isEmpty(username) || username.length() != 11) {
            return;
        }

        Log.i(TAG, "....TYPE111=" + type);
        if (type == null) {
            type = "1";
        }
        Intent intent = new Intent(getActivity(), ContactsContentActivity.class);
        intent.putExtra("mobile", username);
        intent.putExtra("my_mobile", SPUtil.getInstance(getActivity()).getString("phone"));
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    public void onAvatarLongClick(String username) {
        inputAtUsername(username);
    }


    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        message.getBody();
        if (message.getType() == EMMessage.Type.TXT) {
            JSONObject object = null;
            try {
                object = new JSONObject(message.getStringAttribute("ext"));
                Log.i("走了没", "is_carddddd" + object.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (object != null) {
                    if (object.optString("is_card").equals("true")) {
                        if (object.has("gift")) {
                            Log.i("走了没", "is_Gift" + object.toString());
                            isSelected(object.getString("gift"));
                            return true;
                        } else {
                            Log.i(TAG, "....TYPE0000=" + type);
                            Intent intent = new Intent(getActivity(), ContactsContentActivity.class);
                            intent.putExtra("my_mobile", SPUtil.getInstance(getActivity()).getString("phone"));
                            intent.putExtra("mobile", object.getString("phone"));
                            intent.putExtra("type", type);
                            startActivity(intent);
                            Log.i("走了没", "is_card" + object.toString());
                            return true;
                        }
                    } else if (object.optString("is_red_packet").equals("true")) {
                        Log.i("走了没", "is_red_packet" + object.toString() + "     " + spUtil.getString("uid"));
                        //点击红包操作
                        //判断是群聊还是单聊  var2 = EMMessage.ChatType.GroupChat;
                        /*Intent intent = new Intent(getActivity(), RedPacket_OpenActivity.class);
                        intent.putExtra("mobile", object.getString("phone"));
                        intent.putExtra("r_id", object.getString("Redbaoid"));
                        startActivityForResult(intent, MESSAGE_TYPE_jiehongbao_TRANSFER);*/
                        //一点击就成功之后 就发送
                        if (spUtil.getString("uid").equals(object.getString("idStr"))) {
                            Log.i(TAG, "......if....if=" + object.getString("phone") + "    " + object.getString("Redbaoid"));
                            //点击自己发送的未领取的红包
                            Intent intent = new Intent(getActivity(), RedPacket_OpenActivity.class);
                            intent.putExtra("mobile", object.getString("phone"));
                            intent.putExtra("r_id", object.getString("Redbaoid"));
                            intent.putExtra("idStr", object.getString("idStr"));
                            startActivityForResult(intent, MESSAGE_TYPE_jiehongbao_TRANSFER);
                        } else {
                            Log.i(TAG, ".....else.....else state");
                            //点击别人发送的红包
                            loadRedEnvelope(spUtil.getString("uid"), object.getString("idStr"), object.getString("Redbaoid"), "", object.getString("phone"), "");
                        }
                        return true;
                    } else if (object.optString("is_transfer").equals("true")) {
                        Log.i("走了没", "is_transfer" + object.toString());
                        //判断是群聊还是单聊  var2 = EMMessage.ChatType.GroupChat;
                        /*if (spUtil.getString("uid").equals(object.getString("idStr"))) {
                            Intent intent = new Intent(getActivity(), Transfer_OpenActivity.class);
                            intent.putExtra("nickname", nickname);
                            intent.putExtra("mobile", object.getString("phone"));
                            intent.putExtra("r_id", object.getString("Redbaoid"));
                            intent.putExtra("order_price", object.getString("order_price"));
                            intent.putExtra("type", "1");
                            startActivityForResult(intent, MESSAGE_TYPE_zhuanzhang_TRANSFER);
                        } else {*/
                        nickname2 = nickname;
                        order_price = object.getString("order_price");
                        loadRedEnvelope(spUtil.getString("uid"), object.getString("idStr"), object.getString("Redbaoid"), "1", object.getString("phone"), "transfer");
                        //}
                        return true;
                    } else if (object.optString("is_jiehongbao").equals("true")) {
                        if (spUtil.getString("uid").equals(object.getString("idStr"))) {
                            Log.i(TAG, ".....if.....else state=" + object.getString("phone") + "    " + object.getString("Redbaoid"));
                            //点击自己发送的已领取的红包
                            Intent intent = new Intent(getActivity(), RedPacket_OpenActivity.class);
                            intent.putExtra("mobile", object.getString("tophone"));
                            intent.putExtra("r_id", object.getString("Redbaoid"));
                            intent.putExtra("idStr", object.getString("idStr"));
                            startActivity(intent);
                        } else {
                            Log.i(TAG, ".....else.....if state");
                            //点击别人发送的已领取的红包
                            Intent intent = new Intent(getActivity(), RedPacket_OpenActivity.class);
                            intent.putExtra("mobile", object.getString("tophone"));
                            intent.putExtra("r_id", object.getString("Redbaoid"));
                            intent.putExtra("idStr", object.getString("idStr"));
                            startActivity(intent);
                        }
                        return true;
                    } else if (object.optString("is_zhuanzhang").equals("true")) {
                        if (spUtil.getString("uid").equals(object.getString("idStr"))) {
                            Log.i(TAG, ".....if.....else state=" + object.getString("phone") + "    " + object.getString("Redbaoid"));
                            //点击自己发送的已领取的转账
                            Intent intent = new Intent(getActivity(), TransferDetaleActivity.class);
                            intent.putExtra("mobile", object.getString("tophone"));
                            intent.putExtra("r_id", object.getString("Redbaoid"));
                            startActivity(intent);
                        } else {
                            Log.i(TAG, ".....else.....if state");
                            //点击别人发送的已领取的转账
                            Intent intent = new Intent(getActivity(), TransferDetaleActivity.class);
                            intent.putExtra("mobile", object.getString("tophone"));
                            intent.putExtra("r_id", object.getString("Redbaoid"));
                            startActivity(intent);
                        }
                        Log.i(TAG, ".....else.....if state");
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        //red packet code : 处理红包回执透传消息
        for (EMMessage message : messages) {
            EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
            String action = cmdMsgBody.action();//获取自定义action

        }
        //end of red packet code
        super.onCmdMessageReceived(messages);
    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {   //长按操作
        // no message forward when in chat room
       /* startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message", message)
                        .putExtra("ischatroom", chatType == EaseConstant.CHATTYPE_CHATROOM),
                REQUEST_CODE_CONTEXT_MENU);*/
        //new BottomAnimDialog(getActivity(),"测试","测试2","测试3").show();
        initDialog(message, chatType == EaseConstant.CHATTYPE_CHATROOM);

    }

    private void initDialog(EMMessage message, boolean isChatroom) {

        mDialog = new Dialog(getActivity(),R.style.dialog);
        //去除标题栏
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //2.填充布局
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        int type = message.getType().ordinal();
        View dialogView = null;
        if (type == EMMessage.Type.TXT.ordinal()) {
            JSONObject object = null;
            try {
                object = new JSONObject(message.getStringAttribute("ext"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)
                    || message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
                //red packet code : 屏蔽红包消息的转发功�?
                    ) {
                //end of red packet code
                dialogView = inflater.inflate(R.layout.em_context_menu_for_location, null);
            } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                Log.i(TAG, "         =" + "MESSAGE_ATTR_IS_BIG_EXPRESSION");
                dialogView = inflater.inflate(R.layout.em_context_menu_for_image, null);
            } else try {
                if (object != null) {
                    if (object.getString("is_card").equals("true")) {
                        dialogView = inflater.inflate(R.layout.em_context_menu_for_location, null);
                    }
                } else {
                    dialogView = inflater.inflate(R.layout.em_context_menu_for_text, null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type == EMMessage.Type.LOCATION.ordinal()) {
            dialogView = inflater.inflate(R.layout.em_context_menu_for_location, null);
        } else if (type == EMMessage.Type.IMAGE.ordinal()) {
            Log.i(TAG, "         =" + "IMAGE");
            dialogView = inflater.inflate(R.layout.em_context_menu_for_image, null);
        } else if (type == EMMessage.Type.VOICE.ordinal()) {
            dialogView = inflater.inflate(R.layout.em_context_menu_for_voice, null);
        } else if (type == EMMessage.Type.VIDEO.ordinal()) {
            dialogView = inflater.inflate(R.layout.em_context_menu_for_video, null);
        } else if (type == EMMessage.Type.FILE.ordinal()) {
            dialogView = inflater.inflate(R.layout.em_context_menu_for_location, null);
        }
        if (isChatroom
            //red packet code : 屏蔽红包消息的撤回功�?
                ) {
            //end of red packet code
            /*View v = findViewById(R.id.forward);
            if (v != null) {
                v.setVisibility(View.GONE);
            }*/
        }

        if (dialogView == null) {
            return;
        }
        //将自定义布局设置进去
        mDialog.setContentView(dialogView);
        //3.设置指定的宽高,如果不设置的话，弹出的对话框可能不会显示全整个布局，当然在布局中写死宽高也可以
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = mDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = 500;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //注意要在Dialog show之后，再将宽高属性设置进去，才有效果
        mDialog.show();
        //mDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        window.setAttributes(lp);

        TextView tv_delete = (TextView) dialogView.findViewById(R.id.tv_delete);
        TextView tv_forward = (TextView) dialogView.findViewById(R.id.tv_forward);
        TextView tv_copy = (TextView) dialogView.findViewById(R.id.tv_copy);
        if (tv_copy != null) {
            tv_copy.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy();
                }
            });
        }
        if (tv_delete != null) {
            tv_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete();
                }
            });
        }

        if (tv_forward != null) {
            tv_forward.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    forward();
                }
            });
        }

        //设置点击其它地方不让消失弹窗
        //mDialog.setCancelable(false);
    }
    public void copy(){
        /*setResult(RESULT_CODE_COPY);
        finish();*/
        clipboard.setPrimaryClip(ClipData.newPlainText(null,
                ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage()));
        mDialog.dismiss();
    }
    public void delete(){
        /*setResult(RESULT_CODE_DELETE);
        finish();*/
        conversation.removeMessage(contextMenuMessage.getMsgId());
        messageList.refresh();
        mDialog.dismiss();
    }
    public void forward(){
        /*setResult(RESULT_CODE_FORWARD);
        finish();*/
        Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
        intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
        intent.putExtra("type", type);
        intent.putExtra("is_show_name", isShowName);
        startActivity(intent);
        mDialog.dismiss();
    }
    public void recall(View view){
        /*setResult(RESULT_CODE_RECALL);
        finish();*/
    }
    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {//（图片，位置，拍摄等）点击事件
        //动态申请权限、做相应逻辑
        //权限申请flag （1拍照；2相册；3定位；4影像；55语音通话、66视频通话；）
        switch (itemId) {
            case ITEM_VIDEO://拍摄permissions
                flag = 4;
                MPermissions.requestPermissions(ChatFragment.this, PESSION_CODE, permissions);
                break;
            case ITEM_FILE: //点击相册
                selectFileFromLocal();//获取视频文件file  permissions
                break;
            /*case ITEM_VOICE_CALL://点击语音通话
                flag = 66;
                MPermissions.requestPermissions(ChatFragment.this, PESSION_CODE,
                        Manifest.permission.RECORD_AUDIO);
                break;
            case ITEM_VIDEO_CALL://点击视频通话
                flag = 55;
                MPermissions.requestPermissions(ChatFragment.this, PESSION_CODE,
                        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
                break;*/

            case ITEM_VIDEO_CALL:
                final BottomAnimDialog bottomAnimDialog = new BottomAnimDialog(getContext(), "语音通话", "视频通话", "取消");

                bottomAnimDialog.setClickListener(new BottomAnimDialog.BottonAnimDialogListener() {
                    @Override
                    public void onItem1Listener() {
                        flag = 66;
                        MPermissions.requestPermissions(ChatFragment.this, PESSION_CODE,
                                Manifest.permission.RECORD_AUDIO);
                        bottomAnimDialog.dismiss();
                    }

                    @Override
                    public void onItem2Listener() {
                        flag = 55;
                        MPermissions.requestPermissions(ChatFragment.this, PESSION_CODE,
                                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
                        bottomAnimDialog.dismiss();
                    }

                    @Override
                    public void onItem3Listener() {

                        bottomAnimDialog.dismiss();
                    }
                });
                bottomAnimDialog.show();

                break;
            //点击转账  进入填写转账页面�?
            case ITEM_TRANSFER:
                Intent intent2 = new Intent();
                intent2.setClass(getContext(), Transfer_pushActivity.class);
                intent2.putExtra("tophone", toChatUsername);
                intent2.putExtra("chatType", chatType);
                startActivityForResult(intent2, REQUEST_CODE_SELECT_TRANSFER);
                break;
            //red packet code : 点击红包  进入填写红包页面�?
            case ITEM_RED_PACKET:
                Intent intent1 = new Intent();
                intent1.setClass(getContext(), RedPacket_pushActivity.class);
                intent1.putExtra("tophone", toChatUsername);
                intent1.putExtra("chatType", chatType);
                startActivityForResult(intent1, REQUEST_CODE_SELECT_REFPACKET);
                break;
            //end of red packet code
            case ITEM_CARD:
                Intent card = new Intent(getActivity(), ContactCardActivity.class);
                startActivityForResult(card, REQUEST_CODE_SELECT_CARD);
                break;
            case ITEM_ATTENTION:
                myInfoPresenter.attention(getActivity(), SPUtil.getInstance(getActivity()).getString("uid"), getMobile(toChatUsername));
                break;
            case ITEM_TAKE_PICTURE:
                flag = 1;
                MPermissions.requestPermissions(ChatFragment.this, PESSION_CODE, permissions);
                break;
            case ITEM_PICTURE:
                flag = 2;
                MPermissions.requestPermissions(ChatFragment.this, PESSION_CODE, permissions);
                break;
            case ITEM_LOCATION:
                flag = 3;
                MPermissions.requestPermissions(ChatFragment.this, PESSION_CODE, location);
                break;
            case OTHER_LOCATION:
                //查询好友位置
                sendLocationRequest();
                break;
            case SEND_GIFT:
                sendGift();
                break;
            default:
                break;
        }
        //keep exist extend menu
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(ChatFragment.this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(PESSION_CODE)
    public void requestPermissionSuccess() {//申请权限成功后的逻辑
        //权限申请flag （1拍照；2相册；3定位；4影像；55语音通话、66视频通话；）
        switch (flag) {//相册，位置，（短按拍照+长按摄影），（个人定位+好友定位=位置），（语音+视频），
            case 1://点击拍照
                selectPicFromCamera();
                break;
            case 2://点击从相册选择
                Log.e("pic_....", "调用系统相册");
                MultiImageSelector.create()
                        .showCamera(true) // show camera or not. true by default
                        .multi().count(9)
                        .start(this, REQUEST_CODE_LOCAL);
                //selectPicFromLocal();
                break;
            case 3://点击进入百度地图进行定位
                /*Intent data = new Intent(getActivity(), EaseBaiduMapActivity.class);
                startActivityForResult(data, REQUEST_CODE_MAP);*/
                final BottomAnimDialog bottomAnimDialog = new BottomAnimDialog(getContext(), "个人位置", "查看好友位置", "取消");

                bottomAnimDialog.setClickListener(new BottomAnimDialog.BottonAnimDialogListener() {
                    @Override
                    public void onItem1Listener() {
                        Intent data = new Intent(getActivity(), EaseBaiduMapActivity.class);
                        startActivityForResult(data, REQUEST_CODE_MAP);
                        bottomAnimDialog.dismiss();
                    }

                    @Override
                    public void onItem2Listener() {
                        sendLocationRequest();
                        bottomAnimDialog.dismiss();
                    }

                    @Override
                    public void onItem3Listener() {

                        bottomAnimDialog.dismiss();
                    }
                });
                bottomAnimDialog.show();
                break;
            case 4://点击摄影

                Log.e(TAG, "点击摄影");
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
                break;
            case 55://点击视频通话
                startVideoCall();
                break;
            case 66://点击语音通话
                startVoiceCall();
                break;
        }
    }

    @PermissionDenied(PESSION_CODE)
    public void requestPermissionFailed() {//申请权限失败后的逻辑
        Toast.makeText(getActivity(), "权限被禁止，请您去设置界面开启!", Toast.LENGTH_SHORT).show();
    }

    /**
     * send gift
     */
    protected void sendGift() {
        mSendGifDialog = getDialog();
        Window window = mSendGifDialog == null ? null : mSendGifDialog.getWindow();
        if (mSendGifDialog != null && window != null) {
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                Window dialogWindow = mSendGifDialog.getWindow();
                dialogWindow.setBackgroundDrawable(null);
                if (dialogWindow != null) {
                    dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim);//修改动画样式
                    dialogWindow.setGravity(Gravity.BOTTOM);//改成bottom,底部显示
                }
                mSendGifDialog.show();
            }
        }

    }

    public Dialog getDialog() {
        Dialog dialog = new Dialog(getActivity(), R.style.loading_dialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.send_gift_area, null);
        mBalance = (TextView) view.findViewById(R.id.balance_gift);
        mBalance.setText(mMoney);
        Button send_gift = (Button) view.findViewById(R.id.send_gift);
        mRadio1 = (RadioGroup) view.findViewById(R.id.radio1);
        mRadio2 = (RadioGroup) view.findViewById(R.id.radio2);
        mRadioButton = (RadioButton) view.findViewById(R.id.radio_btn);
        mRadioButton2 = (RadioButton) view.findViewById(R.id.radio_btn2);
        mRadioButton3 = (RadioButton) view.findViewById(R.id.radio_btn3);
        mRadioButton4 = (RadioButton) view.findViewById(R.id.radio_btn4);
        mRadioButton5 = (RadioButton) view.findViewById(R.id.radio_btn5);
        mRadioButton6 = (RadioButton) view.findViewById(R.id.radio_btn6);
        mRadioButton7 = (RadioButton) view.findViewById(R.id.radio_btn7);
        mRadioButton8 = (RadioButton) view.findViewById(R.id.radio_btn8);
        send_gift.setOnClickListener(this);
        mRadioButton.setOnCheckedChangeListener(this);
        mRadioButton2.setOnCheckedChangeListener(this);
        mRadioButton3.setOnCheckedChangeListener(this);
        mRadioButton4.setOnCheckedChangeListener(this);
        mRadioButton5.setOnCheckedChangeListener(this);
        mRadioButton6.setOnCheckedChangeListener(this);
        mRadioButton7.setOnCheckedChangeListener(this);
        mRadioButton8.setOnCheckedChangeListener(this);
        /**
         * 设置点击发送后，判断classtype（1：单聊/2：群聊）
         */
        dialog.setContentView(view);
        return dialog;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == true && buttonView.isChecked() && buttonView == mRadioButton7) {
            setRadioGroupDisAll(mRadio1);
            mGiftCount = "0";
            mGiftMoney = "1";
            mDrawable = R.mipmap.kiss;
        } else if (isChecked == true && buttonView.isChecked() && buttonView == mRadioButton3) {
            setRadioGroupDisAll(mRadio1);
            mGiftCount = "1";
            mGiftMoney = "1";
            mDrawable = R.mipmap.cake;
        } else if (isChecked == true && buttonView.isChecked() && buttonView == mRadioButton5) {
            setRadioGroupDisAll(mRadio2);
            mGiftCount = "2";
            mGiftMoney = "1";
            mDrawable = R.mipmap.confession_balloon;
        } else if (isChecked == true && buttonView.isChecked() && buttonView == mRadioButton8) {
            setRadioGroupDisAll(mRadio1);
            mGiftCount = "3";
            mDrawable = R.mipmap.small_bear;
        } else if (isChecked == true && buttonView.isChecked() && buttonView == mRadioButton6) {
            setRadioGroupDisAll(mRadio2);
            mGiftCount = "4";
            mGiftMoney = "1";
            mDrawable = R.mipmap.fireworks;
        } else if (isChecked == true && buttonView.isChecked() && buttonView == mRadioButton) {
            setRadioGroupDisAll(mRadio1);
            mGiftCount = "5";
            mGiftMoney = "1";
            mDrawable = R.mipmap.an_crown;
        } else if (isChecked == true && buttonView.isChecked() && buttonView == mRadioButton2) {
            setRadioGroupDisAll(mRadio2);
            mGiftCount = "6";
            mGiftMoney = "1";
            mDrawable = R.mipmap.aircraft;
        } else if (isChecked == true && buttonView.isChecked() && buttonView == mRadioButton4) {
            setRadioGroupDisAll(mRadio2);
            mGiftCount = "7";
            mGiftMoney = "1";
            mDrawable = R.mipmap.car2;
        }
    }

    /**
     * 设置RadioGroup 全部取消选中
     *
     * @param radioGroup
     */
    private void setRadioGroupDisAll(RadioGroup radioGroup) {
        radioGroup.clearCheck();
    }

    private int MESSAGE_SUCCESS = 1;
    private Dialog mDialog;

    private Handler handel = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MESSAGE_SUCCESS) {
                mDialog.dismiss();
            }
            return false;
        }
    });

    private void GiftGifImg(int c) {
        mDialog = getGifFDialog(c);
        Window window = mDialog == null ? null : mDialog.getWindow();
        if (mDialog != null && window != null) {
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                Window dialogWindow = mDialog.getWindow();
                dialogWindow.setBackgroundDrawable(null);
                if (dialogWindow != null) {
                    dialogWindow.setWindowAnimations(R.style.picker_view_scale_anim2);//修改动画样式
                    dialogWindow.setGravity(Gravity.FILL);//改成bottom,底部显示
                }
                mDialog.show();
            }
        }
    }

    public Dialog getGifFDialog(int c) {
        Dialog dialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.send_img, null);
        ImageView gif = (ImageView) view.findViewById(R.id.img_gif);
        if (null != this)
            Glide.with(this)
                    .load(c)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .listener(new RequestListener<Integer, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // 获取gif动画时长
                            GifDrawable drawable = (GifDrawable) resource;
                            GifDecoder decoder = drawable.getDecoder();
                            long duration = 0;
                            for (int i = 0; i < drawable.getFrameCount(); i++) {
                                duration += decoder.getDelay(i);
                            }
                            Log.e("peter", "动画时长" + duration);
                            //后面这段代码根据你的具体业务需求，添加相应的代码块
                            handel.sendEmptyMessageDelayed(MESSAGE_SUCCESS,
                                    duration + 700);
                            return false;
                        }
                    })
                    .into(new GlideDrawableImageViewTarget(gif, 1));
        dialog.setContentView(view);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_gift:
                if (mGiftCount.equals("") && mGiftCount.length() == 0) {
                    ToastUtils.showShort(getActivity(), "你还没有选择礼物");
                    return;
                }
                Log.i("android_mGiftCount", mGiftCount);
                if (chatType == CHATTYPE_SINGLE) {
                    //请求单人聊天接口
                    SendSingleGift(mGiftMoney);
                } else if (chatType == CHATTYPE_GROUP) {
                    //请求群组聊天接口
                    Log.i("sssss", toChatUsername + "/" + nickname);
                    groupPresenter = new GroupPresenter(this);
                    groupPresenter.getSingleGroupInfo(getActivity(), toChatUsername, SPUtil.getInstance(getActivity()).getString("phone"));

                }
                break;
        }
    }

    /**
     * select file
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) { //api 19 and later, we can't use this way, demo just select from images
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * make a voice call
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false).putExtra("nickname", nickname).putExtra("type", type));
            // videoCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * chat row provider
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 38;//24
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (message.getType() == EMMessage.Type.TXT) {
                //voice call
                JSONObject object = null;
                try {
                    object = new JSONObject(message.getStringAttribute("ext"));
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    //video call
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                }//messagee recall
                else if (message.getBooleanAttribute(Constant.MESSAGE_TYPE_RECALL, false)) {
                    return MESSAGE_TYPE_RECALL;
                } else try {
                    if (object != null) {
                        Log.i(TAG, "..............object111111=" + object.optString("is_state"));
                        if (object.optString("is_card").equals("true")) {
                            if (object.has("gift")) {
                                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_GIFT : MESSAGE_TYPE_SENT_GIFT;
                            } else {
                                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_CARD : MESSAGE_TYPE_SENT_CARD;
                            }
                        } else if (object.optString("is_red_packet").equals("true")) {
                            Log.i(TAG, "..............is_red_packet0=" + message);
                            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET : MESSAGE_TYPE_SEND_RED_PACKET;
                        } else if (object.optString("is_transfer").equals("true")) {
                            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TRANSFER : MESSAGE_TYPE_SEND_TRANSFER;
                        } else if (object.optString("is_jiehongbao").equals("true")) {
                            Log.i(TAG, "..............is_jiehongbao=" + message);
                            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_jiehongbao : MESSAGE_TYPE_SEND_jiehongbao;
                        } else if (object.optString("is_zhuanzhang").equals("true")) {
                            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_zhuanzhang : MESSAGE_TYPE_SEND_zhuanzhang;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //red packet code : 红包消息和红包回执消息的chat row type
                //end of red packet code
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            Log.i(TAG, "....getCustomChatRow=" + message.getType());
            if (message.getType() == EMMessage.Type.TXT) {
                // voice call or video call
                JSONObject object = null;
                try {
                    object = new JSONObject(message.getStringAttribute("ext"));
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    return new ChatRowVoiceCall(getActivity(), message, position, adapter);
                } else
                    try {
                        if (object != null) {
                            if (object.optString("is_card").equals("true")) {
                                if (object.has("gift")) {
                                    return new EaseChatCard(getActivity(), message, position, adapter);
                                } else {
                                    return new EaseChatCard(getActivity(), message, position, adapter);
                                }
                            } else if (object.optString("is_state").equals("true")) {
                                Log.i(TAG, "..............state=" + message);
                                return new EaseChatRedPacket2(getActivity(), message, position, adapter);
                            } else if (object.optString("is_red_packet").equals("true")) {
                                Log.i(TAG, "..............is_red_packet1=" + message);
                               /* if (object.optString("state").equals("收到红包")) {
                                    Log.i(TAG,"..............state000="+message);
                                    return new EaseChatRedPacket2(getActivity(), message, position, adapter);
                                }else {*/
                                return new EaseChatRedPacket(getActivity(), message, position, adapter);
                                //}
                            } else if (object.optString("is_jiehongbao").equals("true")) {
                                Log.i(TAG, "..............is_jiehongbao=" + message);
                                //return new EaseChatjiehongbao(getActivity(), message, position, adapter);
                                return new EaseChatRedPacket2(getActivity(), message, position, adapter);
                            } else if (object.optString("is_transfer").equals("true")) {
                                return new EaseChatTransfer(getActivity(), message, position, adapter);
                            } else if (object.optString("is_zhuanzhang").equals("true")) {
                                //return new EaseChatjiezhuanzhang(getActivity(), message, position, adapter);
                                return new EaseChatTransfer2(getActivity(), message, position, adapter);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                //red packet code : 红包消息和红包回执消息的chat row
                //end of red packet code
            }
            return null;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(myBrocastRecevier);
    }

    private void isSelected(String str) {
        Log.i(TAG, "....isSelected=" + str);
        switch (str) {
            case "0":
                GiftGifImg(R.mipmap.kiss);
                break;
            case "1":
                GiftGifImg(R.mipmap.cake);
                break;
            case "2":
                GiftGifImg(R.mipmap.confession_balloon);
                break;
            case "3":
                GiftGifImg(R.mipmap.small_bear);
                break;
            case "4":
                GiftGifImg(R.mipmap.fireworks);
                break;
            case "5":
                GiftGifImg(R.mipmap.an_crown);
                break;
            case "6":
                GiftGifImg(R.mipmap.aircraft);
                break;
            case "7":
                GiftGifImg(R.mipmap.car2);
                break;
            default:
                break;
        }
    }

    private void SendSingleGift(final String pay_count) {
        Api.sendGift(SPUtil.getInstance(getActivity()).getString("phone"), toChatUsername, pay_count, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    Log.i("send_gift_suc", apiResponse.getMessage());
                    mMoney = (Float.parseFloat(mMoney) - Float.parseFloat(pay_count)) + "";
                    mBalance.setText(mMoney);
                    Log.i("send_gift_suc", mMoney + "**");
//                    sendGiftMessage();
                    ToastUtils.showShort(getActivity(), "发送成功");
                    mSendGifDialog.dismiss();
                    sendGiftMessage();
                    GiftGifImg(mDrawable);
                } else {
                    Log.i("send_gift_fail", apiResponse.getMessage());
                    ToastUtils.showShort(getActivity(), "发送失败");
                }
            }
        });
    }

    private void sendGroupGift(final String pay_count, String q_uid, ArrayList<String> members) {
        Api.sendGroupGift(SPUtil.getInstance(getActivity()).getString("phone"), q_uid, members.toArray(), pay_count, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    Log.i("send_gift_suc", apiResponse.getMessage());
                    mMoney = (Float.parseFloat(mMoney) - Float.parseFloat(pay_count)) + "";
                    mBalance.setText(mMoney);
                    ToastUtils.showShort(getActivity(), "发送成功");
                    mSendGifDialog.dismiss();
                    sendGiftMessage();
                    GiftGifImg(mDrawable);
                } else {
                    Log.i("send_gift_fail", apiResponse.getMessage());
                    ToastUtils.showShort(getActivity(), "发送失败");
                }
            }
        });
    }

    private void getBalance() {
        Api.getBalance(SPUtil.getInstance(getActivity()).getString("uid"), new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    BalanceBean bean = JSON.parseObject(apiResponse.getData(), BalanceBean.class);
                    mMoney = bean.getMoney();
                    Log.i("balance", mMoney + "*" + apiResponse.getMessage());
                } else {
                    Log.i("balance", apiResponse.getCode() + "*" + apiResponse.getMessage());
                }
            }
        });
    }


    private void sendLocationRequest() {
        if (!TextUtils.isEmpty(SPUtil.getInstance(getActivity()).getString("phone"))) {
            Api.sendLocationRequest(SPUtil.getInstance(getActivity()).getString("phone"), "" + toChatUsername,
                    new ApiResponseHandler(getActivity()) {
                        @Override
                        public void onSuccess(ApiResponse apiResponse) {
                            if (apiResponse.getCode() == 0) {
                                ToastUtils.showShort(getActivity(), "位置请求成功，正在查询...");
                                Log.i("ssss-location", "位置信息请求成功 , other_id: " + toChatUsername);
                                EMClient.getInstance().chatManager().addMessageListener(messageListener);
                            } else {
                                Toast.makeText(getActivity(), "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            for (EMMessage message : messages) {
                Log.i(TAG, "....接受到消息");
                DemoHelper.getInstance().getNotifier().onNewMsg(message);
                String value = message.getStringAttribute("location", "");
                if ("receive".equals(value)) {
                    receiveLocationResult(message);
                }
            }
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
            //消息被撤回
            Log.i(TAG, "....调用了撤回方法");
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }

    };

    //处理收到消息后的结果
    public void receiveLocationResult(EMMessage message) {
        Log.i(TAG, "....receiveLocationResult");
        Intent intent = new Intent(getActivity(), ShowLocationActivity.class);
        intent.putExtra("lat", message.getStringAttribute("lat", ""));
        intent.putExtra("lng", message.getStringAttribute("lng", ""));
        intent.putExtra("mobile", message.getStringAttribute("mobile", ""));
        intent.putExtra("user_nickname", message.getStringAttribute("user_nickname", ""));
        intent.putExtra("avatar", message.getStringAttribute("avatar", ""));
        startActivity(intent);
    }

    //MainActivity中消息刷新处理
    public void refreshUIWithMessage() {

    }

    @Override
    public void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        String type2 = intent.getStringExtra("type");
        Bundle bundle = getArguments();
        String type = bundle.getString("type");
        String phone = bundle.getString("phone");
        String headStr = bundle.getString("headStr");
        Log.i(TAG, ".............type=" + type + "     " + phone +"     "+headStr);
        if (type != null && type.equals("sharecard") == true) {
            sendCardMessage(intent);
        }
    }

    public void showRedPacketDialog() {
        if (mRedPacketDialogView == null) {
            /**
             * 红包View
             */
            mRedPacketDialogView = View.inflate(getActivity(), R.layout.activity_red_envelope, null);
            mRedPacketViewHolder = new RedPacketViewHolder(getActivity(), mRedPacketDialogView);
            /**
             * 红包dialog
             */
            mRedPacketDialog = new CustomDialog(getActivity(), mRedPacketDialogView, R.style.custom_dialog);
            mRedPacketDialog.setCancelable(false);//不允许点击取消和返回
        }
        /**
         * 传递数据
         */
        mRedPacketViewHolder.setData(user_nickname, avatar, content, content2);
        /**
         * 事件监听
         */
        mRedPacketViewHolder.setOnRedPacketDialogClickListener(new OnRedPacketDialogClickListener() {
            /**
             * 关闭红包
             */
            @Override
            public void onCloseClick() {
                mRedPacketDialog.dismiss();
            }

            /**
             * 领取红包,调用接口
             */
            @Override
            public void onOpenClick() {
                getRedPacket();
            }
        });
        /**
         * CustomDialog显示
         */
        mRedPacketDialog.show();
        SpringSystem springSystem = SpringSystem.create();
        Spring spring = springSystem.createSpring();
        spring.setCurrentValue(1.0f);
        spring.setSpringConfig(new SpringConfig(90, 3));
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                super.onSpringUpdate(spring);
                float currentValue = (float) spring.getCurrentValue();
                mRedPacketDialogView.setScaleX(currentValue);
                mRedPacketDialogView.setScaleY(currentValue);

            }
        });
        spring.setEndValue(1.05);

    }

    String user_nickname, avatar, content, content2;
    int is_first_get;
    String f_id, r_id, mobile;

    private void loadRedEnvelope(String uid, String f_id, String r_id, String packets_type, String mobile, String type) {
        this.f_id = f_id;
        this.r_id = r_id;
        this.mobile = mobile;
        Api.getRedEnvelope(uid, f_id, r_id, packets_type, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i(TAG, "................onSuccess=" + apiResponse.getData() + "   " + apiResponse.getCode() + "    " + apiResponse.getMessage());
                if (apiResponse.getCode() == 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(apiResponse.getData());
                        is_first_get = Integer.parseInt(jsonObject.getString("is_first_get"));
                        if (is_first_get == 0) {
                            Log.i(TAG, "................onSuccess2222" + apiResponse.getData() + "   " + apiResponse.getCode() + "    " + apiResponse.getMessage());
                            try {
                                user_nickname = jsonObject.getString("user_nickname");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                avatar = jsonObject.getString("avatar");
                                content = jsonObject.getString("content");
                                content2 = jsonObject.getString("content2");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (type.equals("")) {
                                showRedPacketDialog();
                            } else if (type.equals("transfer")) {
                                Log.i(TAG, ".............=" + spUtil.getString("uid") + "     " + f_id);
                                if (spUtil.getString("uid").equals(f_id)) {
                                    Log.i(TAG, ".............ififif");
                                    Intent intent = new Intent(getActivity(), Transfer_OpenActivity.class);
                                    intent.putExtra("nickname", nickname2);
                                    intent.putExtra("mobile", mobile);
                                    intent.putExtra("r_id", r_id);
                                    intent.putExtra("order_price", order_price);
                                    intent.putExtra("type", "1");
                                    startActivityForResult(intent, MESSAGE_TYPE_zhuanzhang_TRANSFER);
                                } else {
                                    Log.i(TAG, "................onSuccess5555");
                                    Intent intent = new Intent(getActivity(), Transfer_OpenActivity.class);
                                    intent.putExtra("nickname", "");
                                    intent.putExtra("mobile", mobile);
                                    intent.putExtra("r_id", r_id);
                                    intent.putExtra("order_price", order_price);
                                    intent.putExtra("type", "2");
                                    intent.putExtra("time", jsonObject.getString("time"));
                                    startActivityForResult(intent, MESSAGE_TYPE_zhuanzhang_TRANSFER);
                                }
                            }
                        } else {
                            if (type.equals("")) {
                                Intent intent = new Intent(getActivity(), RedPacket_OpenActivity.class);
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("r_id", r_id);
                                startActivityForResult(intent, MESSAGE_TYPE_jiehongbao_TRANSFER);
                            } else if (type.equals("transfer")) {
                                if (spUtil.getString("uid").equals(f_id)) {
                                    Intent intent = new Intent(getActivity(), RedPacket_OpenActivity.class);
                                    intent.putExtra("mobile", mobile);
                                    intent.putExtra("r_id", r_id);
                                    intent.putExtra("idStr", f_id);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getActivity(), TransferDetaleActivity.class);
                                    intent.putExtra("mobile", mobile);
                                    intent.putExtra("r_id", r_id);
                                    startActivityForResult(intent, MESSAGE_TYPE_jiehongbao_TRANSFER);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (apiResponse.getCode() == 1) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(apiResponse.getData());
                        is_first_get = Integer.parseInt(jsonObject.getString("is_first_get"));
                        if (is_first_get == 0) {
                            if (type.equals("transfer")) {
                                Log.i(TAG, "............getCode() == 1.=" + spUtil.getString("uid") + "     " + f_id);
                                if (spUtil.getString("uid").equals(f_id)) {
                                    Log.i(TAG, ".............getCode() == 111");
                                    Intent intent = new Intent(getActivity(), Transfer_OpenActivity.class);
                                    intent.putExtra("nickname", nickname2);
                                    intent.putExtra("mobile", mobile);
                                    intent.putExtra("r_id", r_id);
                                    intent.putExtra("order_price", order_price);
                                    intent.putExtra("type", "1");
                                    startActivityForResult(intent, MESSAGE_TYPE_zhuanzhang_TRANSFER);
                                }
                            }
                        } else {
                            if (type.equals("transfer")) {
                                Log.i(TAG, "............getCode() ==else 1" + spUtil.getString("uid") + "     " + f_id);
                                if (spUtil.getString("uid").equals(f_id)) {
                                    Intent intent = new Intent(getActivity(), TransferDetaleActivity.class);
                                    intent.putExtra("mobile", mobile);
                                    intent.putExtra("r_id", r_id);
                                    startActivityForResult(intent, MESSAGE_TYPE_jiehongbao_TRANSFER);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //请求红包数据
    private void getRedPacket() {
        Log.i("走了没", "getRedPacket" + spUtil.getString("uid").toString() + mobile + r_id);
        Api.getRedPacket(spUtil.getString("uid"), mobile, r_id, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i("走了没", "getRedPacket...............onSuccess=" + apiResponse.getData().toString());
                if (apiResponse.getCode() == 0) {
                    sendJieRedPacketMessge(r_id);
                    Intent intent = new Intent(getActivity(), RedPacket_OpenActivity.class);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("r_id", r_id);
                    startActivityForResult(intent, MESSAGE_TYPE_jiehongbao_TRANSFER);
                    mRedPacketDialog.dismiss();

                } else {
                    ToastUtils.showShort(getActivity(), apiResponse.getMessage());
                }
            }
        });
    }


}
