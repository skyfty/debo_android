package com.qcwl.debo.fragment;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.hyphenate.exceptions.HyphenateException;
import com.qcwl.debo.MainActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.ContactsBean;
import com.hyphenate.easeui.model.TypeStateBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.TipDialog;
import com.qcwl.debo.widget.Constant;
import com.qcwl.debo.widget.InviteMessgeDao;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import rx.Observer;

/**
 * Created by Administrator on 2017/7/13.
 * 嘚啵-好友Fragment
 */

public class ContactMessageFragment extends EaseConversationListFragment implements ContactListPresenterInf{
    private TextView errorText;
    private ConversationSqlite sqlite;
    private SQLiteDatabase db;
    private Cursor cursor;
    private String nickIconUrl,nickname, isShowName;
    private TipDialog dialog;
    private ContactListPresenter contactListPresenter;
    private String name, mobile, tri_id;
    private int default_i;
    private List<TypeStateBean> typeState2;
    private String date1;

    @Override
    protected void initView() {
        super.initView();
        View errorView = View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
        contactListPresenter = new ContactListPresenter(this);
        if (errorView != null) {
            //    errorItemContainer.addView(errorView);
            //    errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
        }
    }


    protected void setUpView() {
        super.setUpView();
        Log.i("ContactMessageFragment","..............setUpView");
        conversationList.addAll(loadConversationList());
        //ids = ids;

        // register context menu
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//嘚啵模块中fragment条目点击

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final EMConversation conversation = conversationListView.getItem(position);
                final String username = conversation.conversationId();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    if (!TextUtils.isEmpty(username)&&username.length()>=4&&"人脉邀请".equals(username.substring(0, 4))) {
                        dialog = new TipDialog(getActivity());
                        dialog.show();
                        dialog.setTip("人脉邀请");
                        try {
                            name = conversation.getLastMessage().getStringAttribute("name");
                            mobile = conversation.getLastMessage().getStringAttribute("mobile");
                            if (TextUtils.isEmpty(name)) {
                                dialog.setContent(mobile + "给您发来一条人脉的邀请信息");
                            } else {
                                dialog.setContent(name + "给您发来一条人脉的邀请信息");
                            }
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        dialog.setClicklistener(new TipDialog.ClickListenerInterface() {
                            @Override
                            public void doConfirm() {
                                dialog.dismiss();
                                contactListPresenter.acceptInvitation(getActivity(), SPUtil.getInstance(getActivity()).getString("phone"), mobile);
                                try {
                                    // delete conversation
                                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                                    inviteMessgeDao.deleteMessage(conversation.conversationId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                refresh();
                                ((MainActivity) getActivity()).updateUnreadLabel();
                            }

                            @Override
                            public void doCancel() {
                                dialog.dismiss();
                                try {
                                    // delete conversation
                                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                                    inviteMessgeDao.deleteMessage(conversation.conversationId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                refresh();
                                ((MainActivity) getActivity()).updateUnreadLabel();
                            }
                        });
                    } else if (!TextUtils.isEmpty(username)&&username.length()>=5&&"三方客邀请".equals(username.substring(0, 5))) {
                        dialog = new TipDialog(getActivity());
                        dialog.show();
                        dialog.setTip("三方客邀请");
                        try {
                            name = conversation.getLastMessage().getStringAttribute("name");
                            mobile = conversation.getLastMessage().getStringAttribute("mobile");
                            tri_id = conversation.getLastMessage().getStringAttribute("tri_id");
                            if (TextUtils.isEmpty(name)) {
                                dialog.setContent(mobile + "给您发来一条三方客邀请信息");
                            } else {
                                dialog.setContent(name + "给您发来一条三方客邀请信息");
                            }
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        dialog.setClicklistener(new TipDialog.ClickListenerInterface() {
                            @Override
                            public void doConfirm() {
                                dialog.dismiss();
                                contactListPresenter.acceptSanFangfKeInvitation(getActivity(), SPUtil.getInstance(getActivity()).getString("uid"), tri_id, "1");
                                try {
                                    // delete conversation
                                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                                    inviteMessgeDao.deleteMessage(conversation.conversationId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                refresh();
                                ((MainActivity) getActivity()).updateUnreadLabel();
                            }

                            @Override
                            public void doCancel() {
                                dialog.dismiss();
                                try {
                                    contactListPresenter.acceptSanFangfKeInvitation(getActivity(), SPUtil.getInstance(getActivity()).getString("uid"), tri_id, "2");
                                    // delete conversation
                                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                                    inviteMessgeDao.deleteMessage(conversation.conversationId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                refresh();
                                ((MainActivity) getActivity()).updateUnreadLabel();
                            }
                        });
                    } else if (!TextUtils.isEmpty(username)&&username.length()>=7&&"三方客完成申请".equals(username.substring(0, 7))) {
                        dialog = new TipDialog(getActivity());
                        dialog.show();
                        dialog.setTip("三方客完成申请");
                        try {
                            name = conversation.getLastMessage().getStringAttribute("name");
                            mobile = conversation.getLastMessage().getStringAttribute("mobile");
                            tri_id = conversation.getLastMessage().getStringAttribute("tri_id");
                            if (TextUtils.isEmpty(name)) {
                                dialog.setContent(mobile + "给您发来一条三方客邀请信息");
                            } else {
                                dialog.setContent(name + "给您发来一条三方客邀请信息");
                            }
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        dialog.setClicklistener(new TipDialog.ClickListenerInterface() {
                            @Override
                            public void doConfirm() {
                                dialog.dismiss();
                                contactListPresenter.acceptSanFangfKeInvitation(getActivity(), SPUtil.getInstance(getActivity()).getString("uid"), tri_id, "3");
                                try {
                                    // delete conversation
                                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                                    inviteMessgeDao.deleteMessage(conversation.conversationId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                refresh();
                                ((MainActivity) getActivity()).updateUnreadLabel();
                            }

                            @Override
                            public void doCancel() {
                                dialog.dismiss();
                                try {
                                    // delete conversation
                                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                                    inviteMessgeDao.deleteMessage(conversation.conversationId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                refresh();
                                ((MainActivity) getActivity()).updateUnreadLabel();
                            }
                        });
                    } else if (!TextUtils.isEmpty(username)&&username.length()>=7&&"三方客撤销申请".equals(username.substring(0, 7))) {
                        dialog = new TipDialog(getActivity());
                        dialog.show();
                        dialog.setTip("三方客撤销申请");
                        try {
                            name = conversation.getLastMessage().getStringAttribute("name");
                            mobile = conversation.getLastMessage().getStringAttribute("mobile");
                            tri_id = conversation.getLastMessage().getStringAttribute("tri_id");
                            if (TextUtils.isEmpty(name)) {
                                dialog.setContent(mobile + "给您发来一条三方客邀请信息");
                            } else {
                                dialog.setContent(name + "给您发来一条三方客邀请信息");
                            }
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        dialog.setClicklistener(new TipDialog.ClickListenerInterface() {
                            @Override
                            public void doConfirm() {
                                dialog.dismiss();
                                contactListPresenter.acceptSanFangfKeInvitation(getActivity(), SPUtil.getInstance(getActivity()).getString("uid"), tri_id, "2");
                                try {
                                    // delete conversation
                                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                                    inviteMessgeDao.deleteMessage(conversation.conversationId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                refresh();
                                ((MainActivity) getActivity()).updateUnreadLabel();
                            }

                            @Override
                            public void doCancel() {
                                dialog.dismiss();
                                try {
                                    // delete conversation
                                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                                    inviteMessgeDao.deleteMessage(conversation.conversationId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                refresh();
                                ((MainActivity) getActivity()).updateUnreadLabel();
                            }
                        });
                    } else if (!TextUtils.isEmpty(username)&&username.length()>=7&&"邀请注册送积分".equals(username.substring(0, 7))) {
                        dialog = new TipDialog(getActivity());
                        dialog.show();
                        dialog.setTip("邀请注册送积分");
                        try {
                            name = conversation.getLastMessage().getStringAttribute("name");
                            mobile = conversation.getLastMessage().getStringAttribute("mobile");
                            if (TextUtils.isEmpty(name)) {
                                dialog.setContent(mobile + "通过您的邀请码成功注册嘚啵，小波给您送50积分，再接再厉哟~");
                            } else {
                                dialog.setContent(name + "通过您的邀请码成功注册嘚啵，小波给您送50积分，再接再厉哟~");
                            }
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        dialog.setClicklistener(new TipDialog.ClickListenerInterface() {
                            @Override
                            public void doConfirm() {
                                dialog.dismiss();
                                try {
                                    // delete conversation
                                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                                    inviteMessgeDao.deleteMessage(conversation.conversationId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                refresh();
                                ((MainActivity) getActivity()).updateUnreadLabel();
                            }

                            @Override
                            public void doCancel() {
                                dialog.dismiss();
                                try {
                                    // delete conversation
                                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
                                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                                    inviteMessgeDao.deleteMessage(conversation.conversationId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                refresh();
                                ((MainActivity) getActivity()).updateUnreadLabel();
                            }
                        });
                    }  else {

                        new RxPermissions(getActivity())
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new Observer<Boolean>() {
                                    @Override
                                    public void onCompleted() {
                                        Log.i("收到信息","onCompleted");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.i("收到信息","onError");
                                    }

                                    @Override
                                    public void onNext(Boolean granted) {
                                        if (granted) {
                                            sqlite = ConversationSqlite.getInstance(getActivity());
                                            db = sqlite.getReadableDatabase();
                                            // start chat acitivity
                                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                                            if (conversation.isGroup()) {
                                                if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                                                    // it's group chat
                                                    intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
                                                } else {
                                                    intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                                                    cursor = db.rawQuery("select * from groupList where uid=" + username, null);
                                                    if (cursor.getCount() > 0) {
                                                        while (cursor.moveToNext()) {
                                                            isShowName = cursor.getString(4);
                                                        }
                                                    }
                                                }

                                            } else {

                                                cursor = db.rawQuery("select * from conversation where phone='" + username + "'" + "union select * from stranger where phone='" + username + "'", null);
                                                if (cursor.getCount() > 0) {
                                                    while (cursor.moveToNext()) {
                                                        if (TextUtils.isEmpty(cursor.getString(5))) {
                                                            nickname = cursor.getString(2);
                                                        } else {
                                                            nickname = cursor.getString(5);
                                                        }
                                                        nickIconUrl = cursor.getString(3);
                                                    }
                                                } else {
                                                    nickIconUrl ="";
                                                    nickname = "";
                                                }
                                            }
                                            cursor.close();
                                            db.close();
                                            sqlite.close();
                                            // it's single chat
                                            intent.putExtra(Constant.EXTRA_USER_ID, username).putExtra("nickname", nickname).putExtra("nickIconUrl", nickIconUrl).putExtra("is_show_name", isShowName).putExtra("type", "1");
                                            Log.i("333",username+nickname+isShowName+nickIconUrl);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getActivity(), "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
        // red packet code : 绾㈠寘鍥炴墽娑堟伅鍦ㄤ細璇濆垪琛ㄦ渶鍚庝竴鏉℃秷鎭殑灞曠ず
        conversationListView.setConversationListHelper(new EaseConversationList.EaseConversationListHelper() {
            @Override
            public String onSetItemSecondaryText(EMMessage lastMessage) {

                return null;
            }
        });

        //？为什么调用两次 super.setUpView
        //super.setUpView();
        // end of red packet code
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
       /* if (NetUtils.hasNetwork(getActivity())) {
            errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
            errorText.setText(R.string.the_current_network);
        }*/
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
        } else if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = false;
        }
        EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
        if (tobeDeleteCons == null) {
            return true;
        }
        if (tobeDeleteCons.getType() == EMConversation.EMConversationType.GroupChat) {
            EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.conversationId());
        }
        try {
            // delete conversation
            EMClient.getInstance()
                    .chatManager()
                    .deleteConversation(tobeDeleteCons.conversationId(),
                            deleteMessage);
            InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
            inviteMessgeDao.deleteMessage(tobeDeleteCons.conversationId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();

        // update unread count
        ((MainActivity) getActivity()).updateUnreadLabel();
        return true;
    }

    public void refreshMessage(CharSequence s) {
        if (conversationListView != null) {
            conversationListView.filter(s);
        }

    }

    /**
     * load conversation list
     *
     * @return +
     */
    protected List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        Log.i("Contact","......loadConversationList="+conversations);
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            String arrayId="";
            default_i = 0;
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    String type = "";
                    String callExt = "";
                    String id = conversation.conversationId();
                    if (id.indexOf("系统消息") == -1) {
                        Log.i("Contact",".系统消息");
                        //continue;
                         arrayId+=id+",";
                         default_i+=1;
                    }

                    try {
                        type = conversation.getLastMessage().getStringAttribute("type");
//                        for (EMMessage message : conversation.getAllMessages()) {
//                            if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false) || message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
//                                callExt = EMClient.getInstance().callManager().getCurrentCallSession().getExt();
//                            }
//                        }
                    } catch (HyphenateException e) {
                        //e.printStackTrace();
                    }
                    Log.i("ContactMessageFragment","...........TYPE="+type+"    "+arrayId);
                    //type为2的时候  显示的为人脉会话列表
                    //if (!"2".equals(type))
                        sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }

            String ss = "15313567914,15235369738,15330256965,38996209106946,18601764051,13503380106,15176319999,15110289623";
            if (arrayId.length()!=0) {
                try {
                    ids = RAS.getPublicKeyStrRAS(arrayId.substring(0, arrayId.length() - 1).getBytes());
                }catch (Exception e){
                    e.printStackTrace();
                    ids = "";
                }

            }else {
                ids = "";
            }
            Log.i("ContactMessageFragment","...........TYPE222="+ids);
            uid = RAS.getPublicKeyStrRAS(SPUtil.getInstance(getActivity()).getString("uid").getBytes());
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

    public void hideSoft() {
        hideSoftKeyboard();
    }

    @Override
    public void updateDataByFilter(List<ContactsBean> filterDateList) {

    }

    @Override
    public void getResult(int code, String message, Object o) {
        ToastUtils.showShort(getActivity(), message);
    }
}
