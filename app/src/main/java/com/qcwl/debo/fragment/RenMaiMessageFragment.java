package com.qcwl.debo.fragment;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
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
 * 嘚啵-人脉Fragment
 */

public class RenMaiMessageFragment extends EaseConversationListFragment {

    private TextView errorText;
    static ConversationSqlite sqlite;
    static SQLiteDatabase db;
    static String nickname;

    @Override
    protected void initView() {
        super.initView();
        View errorView = View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
        if (errorView != null) {
            //     errorItemContainer.addView(errorView);
            //     errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
        }
    }


    protected void setUpView() {
        super.setUpView();
        conversationList.addAll(loadConversationList());
        // register context menu
        sqlite = ConversationSqlite.getInstance(getActivity());//new ConversationSqlite(getActivity());
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                final String username = conversation.conversationId();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    // start chat acitivity
                    final Intent intent = new Intent(getActivity(), ChatActivity.class);//用intent携带数据跳转到聊天界面
                    if (conversation.isGroup()) {
                        if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                            // it's group chat
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
                        } else {
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                        }

                    }


                    new RxPermissions(getActivity())
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
                                        db = sqlite.getReadableDatabase();
                                        Cursor cursor = db.rawQuery("select * from conversation where phone='" + username + "'" + "union select * from stranger where phone='" + username + "'", null);
                                        if (cursor.getCount() > 0) {
                                            while (cursor.moveToNext()) {
                                                if (TextUtils.isEmpty(cursor.getString(5))) {
                                                    nickname = cursor.getString(2);
                                                } else {
                                                    nickname = cursor.getString(5);
                                                }
                                            }
                                        } else {
                                            nickname = "";
                                        }
                                        cursor.close();
                                        db.close();
                                        sqlite.close();
                                        // it's single chat
                                        intent.putExtra(Constant.EXTRA_USER_ID, username).putExtra("nickname", nickname).putExtra("type","2");
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getActivity(), "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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


        super.setUpView();
        // end of red packet code
    }

/*    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())) {
            errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
            errorText.setText(R.string.the_current_network);
        }
    }*/

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
        if (conversationListView != null)
            conversationListView.filter(s);
    }


    /**
     * load conversation list
     *
     * @return +
     */
    protected List<EMConversation> loadConversationList() {
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
                    String type = "";
                    try {
                        type = conversation.getLastMessage().getStringAttribute("type");
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    if ("2".equals(type))
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

    public void hideSoft() {
        hideSoftKeyboard();
    }
}
