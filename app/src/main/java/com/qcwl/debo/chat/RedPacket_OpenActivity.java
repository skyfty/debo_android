package com.qcwl.debo.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.adapter.RedPacketRecordAdapter;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.GetRedPacketBean;
import com.qcwl.debo.model.RedPacketInfoBean;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.KeyBoardUtils;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.my.MyWalletActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by qcwl on 2017/10/25.
 * 红包详情
 */

public class RedPacket_OpenActivity extends BaseActivity implements View.OnClickListener {
    private RoundedImageView icon_head;
    private TextView tv_name, tv_sender_liuyan, tv_money, tv_input, tv_liuyan, tv_content, tv_lookRecord;
    private ListView lv_record;
    private String mobile;
    private String r_id;
    private RedPacketRecordAdapter adapter;
    private GetRedPacketBean mGetRedPacketBean = new GetRedPacketBean();
    private List<RedPacketInfoBean> mInfos = new ArrayList<>();
    private EditText edit_content;
    private LinearLayout layout_comment;
    private TextView text_send;
    private String idStr;
    private String TAG = "RedPacket_OpenActivity";
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.redpacketopen_layout);
        mobile = getIntent().getStringExtra("mobile");
        r_id = getIntent().getStringExtra("r_id");
        idStr = getIntent().getStringExtra("idStr");
        initTitleBar();
        initData();
        if (idStr!=null&&!idStr.equals("")){
            Log.i(TAG,"..........if");
            tv_liuyan.setVisibility(View.GONE);
        }
    }

    private void initData() {
        getRedPacket(mobile, r_id);
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("嘚啵红包")
                .setImageLeftRes(R.mipmap.back)
                .setTextRight("红包记录")
                .setLeftListener(this)
                .setRightListener(this);
        icon_head = (RoundedImageView) findViewById(R.id.icon_head);//发红包人头像
        tv_name = (TextView) findViewById(R.id.tv_name);//发红包人姓名
        tv_sender_liuyan = (TextView) findViewById(R.id.tv_send_liuyan);//发红包人留言
        tv_money = (TextView) findViewById(R.id.tv_money);//发红包金额
        tv_input = (TextView) findViewById(R.id.tv_input);//“已存入零钱，可用于发红包”按钮
        tv_input.setOnClickListener(this);
        tv_liuyan = (TextView) findViewById(R.id.tv_liuyan);//"留言"按钮
        tv_liuyan.setOnClickListener(this);
        tv_content = (TextView) findViewById(R.id.tv_content);//1个红包，共0.01元
        lv_record = (ListView) findViewById(R.id.lv_record);//红包记录列表
        tv_lookRecord = (TextView) findViewById(R.id.tv_lookRecord);//查看红包记录按钮

        layout_comment = (LinearLayout) findViewById(R.id.layout_comment);
        edit_content = (EditText) findViewById(R.id.edit_content);
        text_send = (TextView) findViewById(R.id.text_send);

        adapter = new RedPacketRecordAdapter(this, mInfos);
        lv_record.setAdapter(adapter);

        edit_content.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    layout_comment.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });
        text_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = edit_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(RedPacket_OpenActivity.this, "回复信息不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                setPackets(content);
            }
        });
    }

    private void setPackets(String content) {
        Api.setPackets(sp.getString("uid"), r_id, content, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                int code = apiResponse.getCode();
                if (code == 0){
                    tv_liuyan.setVisibility(View.GONE);
                    Toast.makeText(RedPacket_OpenActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    getRedPacket(mobile, r_id);
                    closeEditText();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_layout://点击“返回箭头”按钮
                finish();
                break;
            case R.id.right_layout://点击“红包记录”按钮
                Intent intent = new Intent();
                intent.setClass(RedPacket_OpenActivity.this, RedPacketRecorderActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_input://点击“已存入零钱，可用于发红包”按钮
                startActivity(new Intent(RedPacket_OpenActivity.this, MyWalletActivity.class));
                break;
            case R.id.tv_liuyan://点击“留言”按钮
                //收到红包后给发送红包人的留言--逻辑
                //ToastUtils.showShort(RedPacket_OpenActivity.this, "收到红包后给发送红包人的留言逻辑，待完成");
                openEditText();
                break;

        }
    }
    //请求红包数据
    private void getRedPacket(String mobile, String r_id) {
        Log.i(TAG, "getRedPacket" + sp.getString("uid").toString() +"    "+ mobile.toString()+"     " + r_id.toString());
        Api.getRedPacket(sp.getString("uid"), mobile, r_id, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i(TAG, "getRedPacket" + apiResponse.getData().toString());
                if (apiResponse.getCode() == 0) {
                    List<GetRedPacketBean> list = JSON.parseArray(apiResponse.getData(), GetRedPacketBean.class);
                    if (list.get(0) != null) {
                        mGetRedPacketBean = list.get(0);
                        List<RedPacketInfoBean> mlist = mGetRedPacketBean.getGet_info();
                        mInfos.clear();
                        mInfos.addAll(mlist);
                        if (mlist.size()==1){
                            Log.i(TAG,mlist.get(0).getMoney());
                            tv_money.setText(mlist.get(0).getMoney());
                            if (!mlist.get(0).getLeave_word().equals("")) {
                                Log.i(TAG,"......===if");
                                tv_liuyan.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                        }


                        tv_content.setText(mGetRedPacketBean.getOffer_info().getContent2());
                        if (!TextUtils.isEmpty(mGetRedPacketBean.getOffer_info().getContent())){
                            tv_sender_liuyan.setText(mGetRedPacketBean.getOffer_info().getContent());
                        }

                        tv_name.setText(mGetRedPacketBean.getOffer_info().getOffer_user_nickname() + "的红包");
                        //     ImgUtil.load(RedPacket_OpenActivity.this,mGetRedPacketBean.getOffer_info().getOffer_avatar(),icon_head);
                        if(Util.isOnMainThread()){
                            if (TextUtils.isEmpty(mGetRedPacketBean.getOffer_info().getOffer_avatar())){
                                ImgUtil.load(RedPacket_OpenActivity.this,com.hyphenate.easeui.R.drawable.head,icon_head);
                                //Glide.with(RedPacket_OpenActivity.this).load(com.hyphenate.easeui.R.drawable.head).into(icon_head);

                            }else{
                                ImgUtil.load(RedPacket_OpenActivity.this,mGetRedPacketBean.getOffer_info().getOffer_avatar(),icon_head);
                                /*Glide.with(RedPacket_OpenActivity.this)
                                        .load(mGetRedPacketBean.getOffer_info().getOffer_avatar())
                                        .bitmapTransform(new RoundedCornersTransformation(RedPacket_OpenActivity.this, ScreenUtils.dp2px(RedPacket_OpenActivity.this, 5), 0))
                                        .into(icon_head);*/
                            }

                        }
                        if ("0".equals(mGetRedPacketBean.getOffer_info().getIs_first_get())){
                            Log.i(TAG,".........mGetRedPacketBean");
                            Intent intent = new Intent();
                            intent.putExtra("content","领取了你的红包");
                            intent.putExtra("user_nickname",mGetRedPacketBean.getOffer_info().getOffer_user_nickname());
                            intent.putExtra("first_get_name",mGetRedPacketBean.getOffer_info().getFirst_get_name());
                            intent.putExtra("offer_user_nickname",mGetRedPacketBean.getOffer_info().getOffer_user_nickname());
                            setResult(RESULT_OK,intent);
                        }
                    }
                } else {
                    ToastUtils.showShort(RedPacket_OpenActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).pauseRequests();
    }

    public void openEditText() {
        layout_comment.setVisibility(View.VISIBLE);
        edit_content.setFocusable(true);
        edit_content.setFocusableInTouchMode(true);
        edit_content.requestFocus();
        KeyBoardUtils.openKeybord(edit_content, this);
    }
    public void closeEditText() {
        KeyBoardUtils.closeKeybord(edit_content, this);
        layout_comment.setVisibility(View.GONE);
    }
}
