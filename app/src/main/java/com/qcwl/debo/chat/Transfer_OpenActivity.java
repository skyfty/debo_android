package com.qcwl.debo.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.GetRedPacketBean;
import com.qcwl.debo.model.RedPacketInfoBean;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import java.util.List;


/**
 * Created by qcwl on 2017/10/25.
 * 转账详情
 */

public class Transfer_OpenActivity extends BaseActivity implements View.OnClickListener {
    private ImageView icon_tishi;
    private TextView tv_name, tv_money, tv_yes, tv_zhuanzhang, tv_shouzhang;
    private LinearLayout ll_return;
    private String mobile;
    private String r_id;
    private GetRedPacketBean mGetRedPacketBean = new GetRedPacketBean();

    private String nickname = "";
    private String type;//1为点击自己发送的转账  2为点击别人发送的转账
    private TextView tv_back_package;
    private TextView tv_back_count;
    private String order_price;
    private String time;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.transferopen_layout);
        nickname = getIntent().getStringExtra("nickname");
        mobile = getIntent().getStringExtra("mobile");
        r_id = getIntent().getStringExtra("r_id");
        type = getIntent().getStringExtra("type");
        order_price = getIntent().getStringExtra("order_price");
        time = getIntent().getStringExtra("time");
        initTitleBar();
       // initData();

    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("转账详情")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(this);
        icon_tishi = (ImageView) findViewById(R.id.icon_tishi);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_yes = (TextView) findViewById(R.id.tv_yes);
        ll_return = (LinearLayout) findViewById(R.id.ll_return);
        tv_zhuanzhang = (TextView) findViewById(R.id.tv_zhuanzhang);
        tv_shouzhang = (TextView) findViewById(R.id.tv_shouzhang);
        tv_back_package = (TextView) findViewById(R.id.tv_back_package);
        tv_back_count = (TextView) findViewById(R.id.tv_back_count);
        tv_yes.setOnClickListener(this);
        if (type.equals("1")){
            tv_yes.setVisibility(View.GONE);
            tv_back_count.setText("一天内未确认，将退还给您！");
            tv_back_package.setText("重发转账信息");
        }else if(type.equals("2")){
            tv_back_count.setText("一天内未确认，将退还给对方！");
            tv_back_package.setText("");
        }
        tv_money.setText("￥"+order_price);
        tv_name.setText("待"+nickname+"确认收款");
        tv_zhuanzhang.setText("转账时间 ：" + time);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_layout://点击返回
                finish();
                break;
            case R.id.tv_yes://点击确认收钱
                getRedPacket(mobile, r_id);
                //ToastUtils.showShort(Transfer_OpenActivity.this, "确认收钱逻辑，待完成");
                break;
            case R.id.ll_return://点击立即退还
               // ToastUtils.showShort(Transfer_OpenActivity.this, "立即退还逻辑，待完成");
                break;

        }
    }
    //请求红包数据
    private void getRedPacket(String mobile, String r_id) {
        Log.i("走了没", "getRedPacket" + sp.getString("uid").toString() + mobile.toString() + r_id.toString());
        Api.getTransfer(sp.getString("uid"), mobile, r_id, "1", new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i("走了没", "getRedPacket" + apiResponse.getData().toString());
                if (apiResponse.getCode() == 0) {
                    //点别人的转账
                    List<GetRedPacketBean> list = JSON.parseArray(apiResponse.getData(), GetRedPacketBean.class);
                    if (list!=null&&list.size()>0&&list.get(0) != null) {
                        mGetRedPacketBean = list.get(0);
                        if (mGetRedPacketBean.getGet_info().size()<=0)
                            return;
                        tv_name.setText("已收款");
                        tv_money.setText("￥" + mGetRedPacketBean.getGet_info().get(0).getMoney());
                        tv_shouzhang.setText("收钱时间 ：" + mGetRedPacketBean.getGet_info().get(0).getTime());
                        tv_zhuanzhang.setText("转账时间 ：" + mGetRedPacketBean.getGet_info().get(0).getOffer_time());

                        if ("0".equals(mGetRedPacketBean.getOffer_info().getIs_first_get())) {
                            Intent intent = new Intent();
                            intent.putExtra("content", "领取了你的转账");
                            intent.putExtra("r_id", r_id);
                            intent.putExtra("order_price", mGetRedPacketBean.getGet_info().get(0).getMoney());
                            intent.putExtra("user_nickname", mGetRedPacketBean.getOffer_info().getOffer_user_nickname());
                            intent.putExtra("first_get_name", mGetRedPacketBean.getOffer_info().getFirst_get_name());
                            intent.putExtra("offer_user_nickname", mGetRedPacketBean.getOffer_info().getOffer_user_nickname());
                            setResult(RESULT_OK, intent);
                        }
                    }
                } else if (apiResponse.getCode() == 1) {
                    //点击的是自己的转账  is_acc =1  领取    is_acc = 2   未领取
                    RedPacketInfoBean redPacketInfoBean = JSON.parseObject(apiResponse.getData(), RedPacketInfoBean.class);
                    if ("2".equals(redPacketInfoBean.getIs_acc())) {
                        tv_name.setText("等待" + nickname + "确认收款");//redPacketInfoBean.getOffer_name()为转账人的昵称，接口处理的有问题，使用前个页面传递昵称
                        tv_money.setText("￥" + redPacketInfoBean.getMoney());
                        tv_zhuanzhang.setText("转账时间 ：" + redPacketInfoBean.getOffer_time());
                    } else if ("1".equals(redPacketInfoBean.getIs_acc())) {
                        tv_name.setText(nickname + "已收款");//redPacketInfoBean.getOffer_name()
                        tv_money.setText("￥" + redPacketInfoBean.getMoney());
                        tv_shouzhang.setText("收钱时间 ：" + redPacketInfoBean.getAcc_time());
                        tv_zhuanzhang.setText("转账时间 ：" + redPacketInfoBean.getOffer_time());
                    }
                } else {
                    ToastUtils.showShort(Transfer_OpenActivity.this, apiResponse.getMessage());
                }
            }
        });
    }


}
