/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qcwl.debo.chat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.hyphenate.easeui.ui.EaseGroupListener;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseAlertDialog.AlertDialogUser;
import com.hyphenate.easeui.widget.EaseExpandGridView;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.qcwl.debo.R;
import com.qcwl.debo.adapter.GridAdapter;
import com.qcwl.debo.model.GroupInfoBean;
import com.qcwl.debo.presenter.GroupPresenter;
import com.qcwl.debo.presenterInf.GroupPresenterInf;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


public class GroupDetailsActivity extends BaseActivity implements OnClickListener, GroupPresenterInf {

    private static final String TAG = "GroupDetailsActivity";
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_EXIT = 1;
    private static final int REQUEST_CODE_EXIT_DELETE = 2;
    private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;
    private static final int REQUEST_CODE_EDIT_GROUP_DESCRIPTION = 6;
    private static final int REQUEST_CODE_EDIT_GROUP_NICK = 7;


    private TextView group_name, notice, group_nick;
    private EaseExpandGridView userGridview;
    private LinearLayout group_management, changeGroupNameLayout, changeGroupDescriptionLayout, clearAllHistory, group_nick_ll, searchLayout;
    private View line;
    private GroupPresenter groupPresenter;
    private GroupInfoBean gb = new GroupInfoBean();

    private String groupId;
    private String type;
    private EMGroup group;
    private Button exitBtn;
    private Button deleteBtn;
    private GridAdapter membersAdapter;
    private ProgressDialog progressDialog;

    private EaseSwitchButton switchButton;
    private EaseSwitchButton offlinePushSwitch;
    private EaseSwitchButton nick_button;
    private EMPushConfigs pushConfigs;

    private String operationUserId = "";
    private ConversationSqlite sqlite;
    private SQLiteDatabase db;
    private ArrayList<GroupInfoBean.Infos> memberList = new ArrayList<>();

    GroupChangeListener groupChangeListener;

    private int group_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlite = ConversationSqlite.getInstance(this);//new ConversationSqlite(this);
        groupId = getIntent().getStringExtra("groupId");
        type = getIntent().getStringExtra("type");
        group = EMClient.getInstance().groupManager().getGroup(groupId);
        group_count = group.getMemberCount();
        setContentView(R.layout.em_activity_group_details);

        initView();

        pushConfigs = EMClient.getInstance().pushManager().getPushConfigs();
        if (pushConfigs == null)
            new Thread() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().pushManager().getPushConfigsFromServer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            }.start();


        monitor();

    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);
    }

    private void initView() {
        group_name = (TextView) findViewById(R.id.group_name);
        userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
        notice = (TextView) findViewById(R.id.notice);
        changeGroupNameLayout = (LinearLayout) findViewById(R.id.rl_change_group_name);
        group_management = (LinearLayout) findViewById(R.id.group_management);
        line = findViewById(R.id.line);
        changeGroupDescriptionLayout = (LinearLayout) findViewById(R.id.rl_change_group_description);
        clearAllHistory = (LinearLayout) findViewById(R.id.clear_all_history);
        group_nick_ll = (LinearLayout) findViewById(R.id.group_nick_ll);
        group_nick = (TextView) findViewById(R.id.group_nick);
        switchButton = (EaseSwitchButton) findViewById(R.id.switch_btn);
        searchLayout = (LinearLayout) findViewById(R.id.rl_search);
        offlinePushSwitch = (EaseSwitchButton) findViewById(R.id.switch_block_offline_message);
        nick_button = (EaseSwitchButton) findViewById(R.id.nick_button);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);
        deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);

        groupPresenter = new GroupPresenter(this);
        groupPresenter.getSingleGroupInfo(this, groupId, sp.getString("phone"));

        group_name.setText(group.getGroupName());
        notice.setText(group.getDescription());

        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
            // 显示解散按钮
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            // 显示退出按钮
            exitBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.GONE);
        }
        if (group.isMsgBlocked()) {
            switchButton.openSwitch();
        } else {
            switchButton.closeSwitch();
        }
        List<String> disabledIds = EMClient.getInstance().pushManager().getNoPushGroups();
        if (disabledIds != null && disabledIds.contains(groupId)) {
            offlinePushSwitch.openSwitch();
        } else {
            offlinePushSwitch.closeSwitch();
        }

        boolean isOwner = isCurrentOwner(group);
        exitBtn.setVisibility(isOwner ? View.GONE : View.VISIBLE);
        deleteBtn.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        changeGroupNameLayout.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        changeGroupDescriptionLayout.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        line.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        group_management.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        groupChangeListener = new GroupChangeListener();

    }

    private void monitor() {
        clearAllHistory.setOnClickListener(this);
        changeGroupNameLayout.setOnClickListener(this);
        changeGroupDescriptionLayout.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        offlinePushSwitch.setOnClickListener(this);
        nick_button.setOnClickListener(this);
        group_nick_ll.setOnClickListener(this);

        userGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == memberList.size() + 1) {
                    if (gb.isEdit()) {
                        gb.setEdit(false);
                        for (GroupInfoBean.Infos infos : gb.getAffiliations()) {
                            infos.setDel(false);
                        }
                    } else {
                        gb.setEdit(true);
                        for (GroupInfoBean.Infos infos : gb.getAffiliations()) {
                            infos.setDel(true);
                        }
                    }
                    membersAdapter.notifyDataSetChanged();
                    //移除成员
                } else if (position == memberList.size()) {
                    ArrayList<String> string_list = new ArrayList<String>();
                    for (GroupInfoBean.Infos infos : memberList) {
                        if ("1".equals(infos.getMember_type())) {
                            string_list.add(infos.getOwner());
                        } else {
                            string_list.add(infos.getMember());
                        }
                    }
                    //添加成员
                    startActivityForResult((new Intent(GroupDetailsActivity.this, GroupPickContactsActivity.class).putExtra("groupId", groupId).putExtra("title", "选择联系人").putStringArrayListExtra("memberList", string_list)), REQUEST_CODE_ADD_USER);
                } else {
                    //成员点击详情
                    Intent intent = new Intent(GroupDetailsActivity.this, ContactsContentActivity.class);
                    if ("1".equals(gb.getAffiliations().get(position).getMember_type())) {
                        intent.putExtra("mobile", gb.getAffiliations().get(position).getOwner());
                    } else {
                        intent.putExtra("mobile", gb.getAffiliations().get(position).getMember());
                    }
                    intent.putExtra("my_mobile", sp.getString("phone"));
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void getResult(int code, String message, Object o) {

        if (code == 0) {
            if (o != null) {
                if (o instanceof GroupInfoBean) {
                    gb = (GroupInfoBean) o;
                    updateGroup();
                    group_nick.setText(gb.getMyname_for_group());
                    if ("1".equals(gb.getIs_show_name())) {
                        nick_button.closeSwitch();
                    } else {
                        nick_button.openSwitch();
                    }
                    memberList.clear();
                    memberList.addAll(gb.getAffiliations());
                    Log.i("sssss_meb",gb.getAffiliations().toString());
                    membersAdapter = new GridAdapter(this, memberList);
                    membersAdapter.setEMGroup(group);
                    userGridview.setAdapter(membersAdapter);
                    group_count = memberList.size();
                }
            } else {
                if ("退出群组".equals(message) || "解散群组".equals(message)) {
                    dialogDismiss();
                    setResult(RESULT_OK);
                    finish();
                    if (ChatActivity.activityInstance != null)
                        ChatActivity.activityInstance.finish();
                    Intent intent = new Intent();
                    intent.setAction("com.debo.groupList");
                    sendBroadcast(intent);
                } else if ("设置完成".equals(message)) {
                    Intent intent = new Intent();
                    String isShowName;
                    intent.setAction("com.debo.is_show_name");
                    if (nick_button.isSwitchOpen()) {
                        isShowName = "2";
                        intent.putExtra("is_show_name", isShowName);
                    } else {
                        isShowName = "1";
                        intent.putExtra("is_show_name", isShowName);
                    }
                    intent.putExtra("flag", "setting");
                    sendBroadcast(intent);
                    dialogDismiss();
                    db = sqlite.getWritableDatabase();
                    db.execSQL("update groupList set is_show_name=? where uid=" + groupId, new Object[]{isShowName});
                    db.close();
                    sqlite.close();
                } else {
                    dialogDismiss();
                    groupPresenter.getSingleGroupInfo(GroupDetailsActivity.this, groupId, sp.getString("phone"));
                }

            }
        } else {
            dialogDismiss();
            ToastUtils.showShort(this, message);
        }
    }

    private void dialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    boolean isCurrentOwner(EMGroup group) {
        String owner = group.getOwner();
        if (owner == null || owner.isEmpty()) {
            return false;
        }
        return owner.equals(EMClient.getInstance().getCurrentUser());
    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String st1 = getResources().getString(R.string.being_added);
        String st2 = getResources().getString(R.string.is_quit_the_group_chat);
        String st3 = getResources().getString(R.string.chatting_is_dissolution);
        String st4 = getResources().getString(R.string.are_empty_group_of_news);
        final String st5 = getResources().getString(R.string.is_modify_the_group_name);
        final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
        final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);

        final String st8 = getResources().getString(R.string.is_modify_the_group_description);
        final String st9 = getResources().getString(R.string.Modify_the_group_description_successful);
        final String st10 = getResources().getString(R.string.change_the_group_description_failed_please);

        if (resultCode == RESULT_OK) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setMessage(st1);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    final String[] newmembers = data.getStringArrayExtra("newmembers");
                    if (newmembers.length > 0) {
                        progressDialog.setMessage(st1);
                        progressDialog.show();
                        addMembersToGroup(newmembers);
                    }
                    break;

                case REQUEST_CODE_EDIT_GROUPNAME: //修改群名称
                    final String returnData = data.getStringExtra("data");
                    progressDialog.setMessage(st5);
                    progressDialog.show();

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().changeGroupName(groupId, returnData);
                                Intent intent = new Intent();
                                intent.setAction("com.debo.is_show_name");
                                intent.putExtra("name", returnData);
                                intent.putExtra("flag", "updateName");
                                sendBroadcast(intent);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        String groupName = group.getGroupName();
                                        if (groupName.length() >= 10) {
                                            groupName = groupName.substring(0, 10) + "...";
                                        }
                                        new TitleBarBuilder(GroupDetailsActivity.this).setTitle(groupName + "(" + group_count + ")");
                                        dialogDismiss();
                                        group_name.setText(returnData);
                                    }
                                });

                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        dialogDismiss();
                                        //Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                    break;
                case REQUEST_CODE_EDIT_GROUP_DESCRIPTION:
                    final String returnData1 = data.getStringExtra("data");
                    progressDialog.setMessage(st5);
                    progressDialog.show();

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().changeGroupDescription(groupId, returnData1);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        dialogDismiss();
                                        notice.setText(returnData1);
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        dialogDismiss();
                                        Toast.makeText(getApplicationContext(), st10, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                    break;
                case REQUEST_CODE_EDIT_GROUP_NICK:
                    final String returnData2 = data.getStringExtra("data");
                    progressDialog.setMessage("正在设置昵称...");
                    progressDialog.show();
                    group_nick.setText(returnData2);
                    String type;
                    if (nick_button.isSwitchOpen()) {
                        type = "2";
                    } else {
                        type = "1";
                    }
                    groupPresenter.setGroupNick(this, sp.getString("phone"), returnData2, groupId, type);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 点击退出群组按钮
     *
     * @param view
     */
    public void exitGroup(View view) {

        String msg = "是否退出该群";
        new EaseAlertDialog(this, null, msg, null, new AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("正在退出群聊……");
                    progressDialog.show();
                    groupPresenter.delGroupMember(GroupDetailsActivity.this, groupId, sp.getString("phone"));
                }
            }
        }, true).show();
    }

    /**
     * 点击解散群组按钮
     *
     * @param view
     */
    public void exitDeleteGroup(View view) {
        String msg = "是否解散该群";
        new EaseAlertDialog(this, null, msg, null, new AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("正在解散群聊…………");
                    progressDialog.show();
                    groupPresenter.delGroup(GroupDetailsActivity.this, groupId);
                }
            }
        }, true).show();
    }

    /**
     * 清空群聊天记录
     */
    private void clearGroupHistory() {

        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(group.getGroupId(), EMConversationType.GroupChat);
        if (conversation != null) {
            conversation.clearAllMessages();
        }
        Toast.makeText(this, R.string.messages_are_empty, Toast.LENGTH_SHORT).show();
    }


    /**
     * 增加群成员
     *
     * @param newmembers
     */
    private void addMembersToGroup(final String[] newmembers) {
        String str = "";
        for (int i = 0; i < newmembers.length; i++) {
            if (i == 0) str += newmembers[i];
            else str += "," + newmembers[i];
        }
        groupPresenter.addGroupMember(this, groupId, str);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_btn:
                toggleBlockGroup();
                break;
            case R.id.switch_block_offline_message:
                toggleBlockOfflineMsg();
                break;
            case R.id.nick_button:
                toggleBlockNIck();
                break;
            case R.id.clear_all_history: // 清空聊天记录
                String st9 = getResources().getString(R.string.sure_to_empty_this);
                new EaseAlertDialog(GroupDetailsActivity.this, null, st9, null, new AlertDialogUser() {

                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (confirmed) {
                            clearGroupHistory();
                        }
                    }
                }, true).show();

                break;

            case R.id.rl_change_group_name:
                startActivityForResult(new Intent(this, EditActivity.class).putExtra("title", "群名称").putExtra("data", group.getGroupName()), REQUEST_CODE_EDIT_GROUPNAME);
                break;
            case R.id.rl_change_group_description:
                startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", group.getDescription()).putExtra("title", "群公告"), REQUEST_CODE_EDIT_GROUP_DESCRIPTION);
                break;
            case R.id.group_nick_ll:
                startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", group_nick.getText().toString()).putExtra("title", "群昵称"), REQUEST_CODE_EDIT_GROUP_NICK);
                break;
            case R.id.rl_search:
                startActivity(new Intent(this, GroupSearchMessageActivity.class).putExtra("groupId", groupId));

                break;

            default:
                break;
        }

    }

    private void toggleBlockOfflineMsg() {
        if (EMClient.getInstance().pushManager().getPushConfigs() == null) {
            return;
        }
        createProgressDialog();
        progressDialog.setMessage("processing...");
        progressDialog.show();
//		final ArrayList list = (ArrayList) Arrays.asList(groupId);
        final List<String> list = new ArrayList<String>();
        list.add(groupId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (offlinePushSwitch.isSwitchOpen()) {
                        EMClient.getInstance().pushManager().updatePushServiceForGroup(list, false);
                    } else {
                        EMClient.getInstance().pushManager().updatePushServiceForGroup(list, true);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogDismiss();
                            if (offlinePushSwitch.isSwitchOpen()) {
                                offlinePushSwitch.closeSwitch();
                            } else {
                                offlinePushSwitch.openSwitch();
                            }
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogDismiss();
                            //Toast.makeText(GroupDetailsActivity.this, "progress failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private ProgressDialog createProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(GroupDetailsActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        return progressDialog;
    }

    private void toggleBlockGroup() {
        if (switchButton.isSwitchOpen()) {
            EMLog.d(TAG, "change to unblock group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(getString(R.string.Is_unblock));
            progressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().unblockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.closeSwitch();
                                dialogDismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                dialogDismiss();
                                Toast.makeText(getApplicationContext(), R.string.remove_group_of, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            }).start();

        } else {
            String st8 = getResources().getString(R.string.group_is_blocked);
            final String st9 = getResources().getString(R.string.group_of_shielding);
            EMLog.d(TAG, "change to block group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(st8);
            progressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().blockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.openSwitch();
                                dialogDismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                dialogDismiss();
                                Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }).start();
        }
    }

    private void toggleBlockNIck() {
//        if (progressDialog == null) {
//            progressDialog = new ProgressDialog(GroupDetailsActivity.this);
//            progressDialog.setCanceledOnTouchOutside(false);
//        }
//        progressDialog.setMessage("正在设置...");
//        progressDialog.show();
        if (nick_button.isSwitchOpen()) {
            nick_button.closeSwitch();
            groupPresenter.setGroupNick(this, sp.getString("phone"), group_nick.getText().toString(), groupId, "1");
        } else {
            nick_button.openSwitch();
            groupPresenter.setGroupNick(this, sp.getString("phone"), group_nick.getText().toString(), groupId, "2");
        }
    }

    Dialog createMemberMenuDialog() {
        final Dialog dialog = new Dialog(GroupDetailsActivity.this);
        dialog.setTitle("group");
        dialog.setContentView(R.layout.em_chatroom_member_menu);

        int ids[] = {R.id.menu_item_add_admin,
                R.id.menu_item_rm_admin,
                R.id.menu_item_remove_member,
                R.id.menu_item_add_to_blacklist,
                R.id.menu_item_remove_from_blacklist,
                R.id.menu_item_transfer_owner,
                R.id.menu_item_mute,
                R.id.menu_item_unmute};

        for (int id : ids) {
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(id);
            linearLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    dialog.dismiss();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                switch (v.getId()) {
                                    case R.id.menu_item_add_admin:
                                        EMClient.getInstance().groupManager().addGroupAdmin(groupId, operationUserId);
                                        break;
                                    case R.id.menu_item_rm_admin:
                                        EMClient.getInstance().groupManager().removeGroupAdmin(groupId, operationUserId);
                                        break;
                                    case R.id.menu_item_remove_member:
                                        EMClient.getInstance().groupManager().removeUserFromGroup(groupId, operationUserId);
                                        break;
                                    case R.id.menu_item_add_to_blacklist:
                                        EMClient.getInstance().groupManager().blockUser(groupId, operationUserId);
                                        break;
                                    case R.id.menu_item_remove_from_blacklist:
                                        EMClient.getInstance().groupManager().unblockUser(groupId, operationUserId);
                                        break;
                                    case R.id.menu_item_mute:
                                        List<String> muteMembers = new ArrayList<String>();
                                        muteMembers.add(operationUserId);
                                        EMClient.getInstance().groupManager().muteGroupMembers(groupId, muteMembers, 20 * 60 * 1000);
                                        break;
                                    case R.id.menu_item_unmute:
                                        List<String> list = new ArrayList<String>();
                                        list.add(operationUserId);
                                        EMClient.getInstance().groupManager().unMuteGroupMembers(groupId, list);
                                        break;
                                    case R.id.menu_item_transfer_owner:
                                        EMClient.getInstance().groupManager().changeOwner(groupId, operationUserId);
                                        break;
                                    default:
                                        break;
                                }
                                updateGroup();
                            } catch (final HyphenateException e) {
                                runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      Toast.makeText(GroupDetailsActivity.this, e.getDescription(), Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                                );
                                e.printStackTrace();

                            } finally {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            }
                        }
                    }).start();
                }
            });
        }
        return dialog;
    }

    void setVisibility(Dialog viewGroups, int[] ids, boolean[] visibilities) throws Exception {
        if (ids.length != visibilities.length) {
            throw new Exception("");
        }

        for (int i = 0; i < ids.length; i++) {
            View view = viewGroups.findViewById(ids[i]);
            view.setVisibility(visibilities[i] ? View.VISIBLE : View.GONE);
        }
    }

    int[] ids = {
            R.id.menu_item_transfer_owner,
            R.id.menu_item_add_admin,
            R.id.menu_item_rm_admin,
            R.id.menu_item_remove_member,
            R.id.menu_item_add_to_blacklist,
            R.id.menu_item_remove_from_blacklist,
            R.id.menu_item_mute,
            R.id.menu_item_unmute
    };


    protected void updateGroup() {
        group = EMClient.getInstance().groupManager().getGroup(groupId);
        String groupName = gb.getName();
        if (groupName.length() >= 10) {
            groupName = groupName.substring(0, 10) + "...";
        }
        new TitleBarBuilder(this).setTitle(groupName + "(" + gb.getAffiliations_count() + ")").setImageLeftRes(R.mipmap.back).setLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        group_name.setText(group.getGroupName());
        notice.setText(group.getDescription());
    }

    public void back(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().groupManager().removeGroupChangeListener(groupChangeListener);
        super.onDestroy();

    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
        ImageView badgeDeleteView;
    }

    private class GroupChangeListener extends EaseGroupListener {

        @Override
        public void onInvitationAccepted(final String groupId, String inviter, String reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    groupPresenter.getSingleGroupInfo(GroupDetailsActivity.this, groupId, sp.getString("phone"));
                }
            });
        }

        @Override
        public void onInvitationDeclined(String s, String s1, String s2) {

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            finish();
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            finish();
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

        }

        @Override
        public void onMuteListAdded(final String groupId, final List<String> mutes, final long muteExpire) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    groupPresenter.getSingleGroupInfo(GroupDetailsActivity.this, groupId, sp.getString("phone"));
                }
            });
        }

        @Override
        public void onMuteListRemoved(final String groupId, final List<String> mutes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    groupPresenter.getSingleGroupInfo(GroupDetailsActivity.this, groupId, sp.getString("phone"));
                }
            });
        }

        @Override
        public void onAdminAdded(final String groupId, String administrator) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    groupPresenter.getSingleGroupInfo(GroupDetailsActivity.this, groupId, sp.getString("phone"));
                }
            });
        }

        @Override
        public void onAdminRemoved(final String groupId, String administrator) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    groupPresenter.getSingleGroupInfo(GroupDetailsActivity.this, groupId, sp.getString("phone"));
                }
            });
        }

        @Override
        public void onOwnerChanged(final String groupId, String newOwner, String oldOwner) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    groupPresenter.getSingleGroupInfo(GroupDetailsActivity.this, groupId, sp.getString("phone"));
                }
            });
        }

        @Override
        public void onMemberJoined(final String groupId, String member) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShort(GroupDetailsActivity.this, "成员添加了");
                    groupPresenter.getSingleGroupInfo(GroupDetailsActivity.this, groupId, sp.getString("phone"));
                }
            });
        }

        @Override
        public void onMemberExited(final String groupId, String member) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    groupPresenter.getSingleGroupInfo(GroupDetailsActivity.this, groupId, sp.getString("phone"));
                }
            });

        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

        }

        @Override
        public void onSharedFileDeleted(String s, String s1) {

        }
    }

    public void removeUserFromGroup(final int position) throws Exception {
        //EMClient.getInstance().groupManager().removeUserFromGroup(groupId,userId);
        EMClient.getInstance().groupManager().asyncRemoveUserFromGroup(groupId, memberList.get(position).getMember(), new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        memberList.remove(position);
                        membersAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GroupDetailsActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

}
