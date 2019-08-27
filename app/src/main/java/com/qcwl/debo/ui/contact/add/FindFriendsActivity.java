package com.qcwl.debo.ui.contact.add;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.loopj.android.http.SaxAsyncHttpResponseHandler;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.KnowUserBean;
import com.qcwl.debo.ui.contact.AddFriendsActivity;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ShareUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.zxing.android.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsActivity extends BaseActivity {

    private ListView listView;
    private List<KnowUserBean> list = new ArrayList<KnowUserBean>(){};
    private KnowUserAdapter adapter;
    private List<KnowUserBean> list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        initTitleBar();
        initView();
        LoadKnowUser();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("添加好友").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        listView = (ListView)findViewById(R.id.listView);
        adapter = new KnowUserAdapter(this,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KnowUserBean knowUser = list.get(position);
                Intent intent = new Intent(FindFriendsActivity.this, ContactsContentActivity.class);
                intent.putExtra("mobile", knowUser.getMobile());
                intent.putExtra("my_mobile", sp.getString("phone"));
                intent.putExtra("f_uid", knowUser.getId());
                intent.putExtra("type", "1");
                startActivity(intent);
            }
        });
        /*TextView textNumber = (TextView) findViewById(R.id.textNumber);
        textNumber.setText("我的嘚啵号：" + sp.getString("phone"));*/
        findViewById(R.id.textSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FindFriendsActivity.this, AddFriendsActivity.class));
            }
        });
        /*findViewById(R.id.textNear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FindFriendsActivity.this, NearListActivity.class));
            }
        });*/
        findViewById(R.id.tv_orcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 1;
                String title = "扫一扫";
                String tip = "addFriend";
                startActivity(new Intent(FindFriendsActivity.this, CaptureActivity.class)
                        .putExtra("flag", flag)
                        .putExtra("tip", tip)
                        .putExtra("title", title));
            }
        });
        findViewById(R.id.tv_invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //邀请好友得红包
                String id = RAS.getPublicKeyStrRAS(SPUtil.getInstance(FindFriendsActivity.this).getString("uid").getBytes());
                ShareUtil.shareApplets(FindFriendsActivity.this, "http://debo.shangtongyuntian.com/data/upload/chatroom/debo_clock.png", "嘚啵一下，创造你的价值", "嘚啵是什么？他们的嘚啵完以后，居然领到了红包了耶！", "http://debo.shangtongyuntian.com/index1.php/appapi/User/web_prize?invitation_code="+ id,id);
            }
        });
        findViewById(R.id.relative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FindFriendsActivity.this,PhoneContactActivity.class));
            }
        });
    }
    private void LoadKnowUser(){
        Api.getKnowUser(sp.getString("uid"), new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i("FindFriendsActivity","........onSuccess="+apiResponse.getData());
                String date = apiResponse.getData();
                list2 = JSONArray.parseArray(date,KnowUserBean.class);
                if (list2 != null&&list2.size()!=0){
                    list.clear();
                    list.addAll(list2);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String errMessage) {
                super.onFailure(errMessage);
                Log.i("FindFriendsActivity","........onFailure="+errMessage);
            }
        });
    }
}
