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
package com.qcwl.debo.ui.contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.qcwl.debo.R;
import com.qcwl.debo.adapter.NewFriendsMsgAdapter;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.presenterInf.OnOperItemClickL;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.view.NormalListDialog;
import com.qcwl.debo.widget.InviteMessage;
import com.qcwl.debo.widget.InviteMessgeDao;

import java.util.Collections;
import java.util.List;


/**
 * Application and notification
 */
public class NewFriendsMsgActivity extends BaseActivity {
    private MeassageBrocastRecevier myBrocastRecevier;
    private IntentFilter intentFilter;
    private NewFriendsMsgAdapter adapter;
    private List<InviteMessage> msgs;
    private InviteMessgeDao dao;
    private ListView listView;
    private String[] mStringItems = {"删除","查看详情"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_friends_msg);
        initTitleBar();
        myBrocastRecevier = new MeassageBrocastRecevier();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.debo.message");  //添加要收到的广播
        registerReceiver(myBrocastRecevier, intentFilter);
        listView = (ListView) findViewById(R.id.newfriends_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewFriendsMsgActivity.this, NewFriendMsgContentActivity.class);
                intent.putExtra("mobile",msgs.get(position).getFrom());
                intent.putExtra("my_mobile", sp.getString("phone"));
                intent.putExtra("msg", msgs.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final NormalListDialog dialog = new NormalListDialog(NewFriendsMsgActivity.this, mStringItems);
                dialog.title("请选择")//
                        .isTitleShow(false)//
                        .itemPressColor(getResources().getColor(R.color.font_select))//
                        .itemTextColor(getResources().getColor(R.color.font_normal))//
                        .itemTextSize(16)//
                        .cornerRadius(5)//
                        .widthScale(0.65f)
                        .show();

                dialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int positions, long id) {
                        switch (positions) {
                            case 0:
                                dao.deleteMessage(msgs.get(positions).getFrom());
                                msgs.remove(position);
                                adapter.notifyDataSetChanged();
                                break;
                            case 1:
                                Intent intent = new Intent(NewFriendsMsgActivity.this, NewFriendMsgContentActivity.class);
                                intent.putExtra("mobile",msgs.get(position).getFrom());
                                intent.putExtra("my_mobile", sp.getString("phone"));
                                intent.putExtra("msg", msgs.get(position));
                                startActivity(intent);
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                return true;
            }
        });

    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).setTitle("新好友");
    }


    @Override
    protected void onResume() {
        super.onResume();
        dao = new InviteMessgeDao(this);
        msgs = dao.getMessagesList();
        Collections.reverse(msgs);
        adapter = new NewFriendsMsgAdapter(this, 1,msgs);
        listView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(myBrocastRecevier);
    }

    public class MeassageBrocastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            dao = new InviteMessgeDao(context);
            msgs = dao.getMessagesList();
            Collections.reverse(msgs);
            adapter = new NewFriendsMsgAdapter(context, 1, msgs);
            listView.setAdapter(adapter);
            dao.saveUnreadMessageCount(0);
        }

    }
}
