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
import com.qcwl.debo.ui.my.MyWalletActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.util.List;

public class TransferDetaleActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_detail,tv_name;
    private TextView tv_money;
    private String money;
    private String mobile;
    private String r_id;
    private GetRedPacketBean mGetRedPacketBean = new GetRedPacketBean();

    private String nickname = "";
    private String type;//1为点击自己发送的转账  2为点击别人发送的转账
    private String order_price;
    private String time;
    private TextView tv_transfer_time;
    private TextView tv_receipt_time;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_transfer_detale);
        initTitleBar();
        /*money = getIntent().getStringExtra("money");
        tv_money.setText(money);*/
        mobile = getIntent().getStringExtra("mobile");
        r_id = getIntent().getStringExtra("r_id");
        getRedPacket(mobile,r_id);
    }
    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("转账详情")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(this);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_transfer_time = (TextView) findViewById(R.id.tv_transfer_time);
        tv_receipt_time = (TextView) findViewById(R.id.tv_receipt_time);
        tv_detail.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_layout://点击返回
                finish();
                break;
            case R.id.tv_detail://进入钱包界面
                startActivity(new Intent(TransferDetaleActivity.this, MyWalletActivity.class));
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
                        tv_money.setText("￥" + mGetRedPacketBean.getGet_info().get(0).getMoney());
                        tv_receipt_time.setText("收钱时间 ：" + mGetRedPacketBean.getGet_info().get(0).getTime());
                        tv_transfer_time.setText("转账时间 ：" + mGetRedPacketBean.getGet_info().get(0).getOffer_time());
                    }
                } else if (apiResponse.getCode() == 1) {
                    //点击的是自己的转账  is_acc =1  领取    is_acc = 2   未领取
                    RedPacketInfoBean redPacketInfoBean = JSON.parseObject(apiResponse.getData(), RedPacketInfoBean.class);
                    if ("2".equals(redPacketInfoBean.getIs_acc())) {
                        tv_name.setText("等待" + nickname + "确认收款");//redPacketInfoBean.getOffer_name()为转账人的昵称，接口处理的有问题，使用前个页面传递昵称
                        tv_money.setText("￥" + redPacketInfoBean.getMoney());
                        tv_transfer_time.setText("转账时间 ：" + redPacketInfoBean.getOffer_time());
                    } else if ("1".equals(redPacketInfoBean.getIs_acc())) {
                        tv_name.setText(nickname + "已收款");//redPacketInfoBean.getOffer_name()
                        tv_money.setText("￥" + redPacketInfoBean.getMoney());
                        tv_receipt_time.setText("收钱时间 ：" + redPacketInfoBean.getAcc_time());
                        tv_transfer_time.setText("转账时间 ：" + redPacketInfoBean.getOffer_time());
                    }
                } else {
                    ToastUtils.showShort(TransferDetaleActivity.this, apiResponse.getMessage());
                }
            }
        });
    }


}
