package com.qcwl.debo.ui.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.qcwl.debo.R;
import com.qcwl.debo.application.App;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.my.WishingStarActivity;
import com.qcwl.debo.ui.pay.alipay.PayResult;
import com.qcwl.debo.ui.pay.wechatpay.MapUtils;
import com.qcwl.debo.ui.pay.wechatpay.MyWXPayUtils;
import com.qcwl.debo.utils.JsonUtils;
import com.qcwl.debo.utils.MD5;
import com.qcwl.debo.utils.PayControl;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2017/8/15.
 */

public class PayDialog {
    private static Dialog dialog = null;
    private static Context mContext;
    private static String mPrice;
    private static int mIndent;//1、充值 ；2、购买合约人；3、发红包；4、领取红包；5、退款；6、购买小喇叭；7、三方客转入；8、三方客转出；18、购买嘚啵币
    private static String mIndentId;
    private static String mBody;
    private static String date;//合伙人购买时间
    public static Dialog createDialog(Context context, String body, int indent, String indentId, String price,String position) {
        mContext = context;
        mBody = body;
        mIndent = indent;
        mIndentId = indentId;
        mPrice = price;
        date = position;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pay, null);
        listener(view);
        dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }


    private static void listener(View view) {
        if (mIndent == 1) {
            view.findViewById(R.id.layout_coin).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.layout_coin).setVisibility(View.VISIBLE);
        }

        //调用零钱支付
        view.findViewById(R.id.text_coin_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.dismiss();
                    dialog = null;
                    pay(mBody, mPrice, 3, mIndent,date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //调用微信支付
        view.findViewById(R.id.text_wxpay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog = null;
                Log.e("paybean_weixin", mBody + "/" + mPrice + "/" + 2 + "/" + mIndent+"/"+SPUtil.getInstance(mContext).getString("uid"));

//                ToastUtils.showShort(mContext,"正在开发中，切莫着急……");
//                pay(mBody, mPrice, 2, mIndent);
                payWX();
            }
        });
        //调用支付宝支付
        view.findViewById(R.id.text_alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog = null;
                pay(mBody, mPrice, 1, mIndent,date);

            }
        });
    }


    private static void payWX() {
        String sign = new StringBuilder()
                .append("uid").append("=").append(SPUtil.getInstance(mContext).getString("uid"))
                .append("&")
                .append("order_price").append("=").append(mPrice)
                .append("&")
                .append("indent").append("=").append("1")
                .append("&")
                .append("key").append("=").append("4cf083b4865300083800a531b4ba3d04")
                .toString();
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://debo.shangtongyuntian.com/index.php/Appapi/Pay/create_order"
                + "?uid=" + SPUtil.getInstance(mContext).getString("uid")
                + "&order_price=" + mPrice
                + "&pay_type=2"
                + "&indent=1"
                + "&body=充值"
                + "&sign=" + MD5.getMessageDigest(sign.getBytes()), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("ssssssssssssssssssss", "........payWX="+response);
//                Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String data = jsonObject.getString("data");
                    if (jsonObject.getInt("code")==0) {
                        Map<String, Object> mapFromString = JsonUtils.getMapFromString(data);
                        mHandler.obtainMessage(WXPAY, mapFromString).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private static String order_sn = "";

    private static void pay(final String body, final String price, final int pay_type, final int indent,final String date) {
        Api.generateOrder(SPUtil.getInstance(mContext).getString("uid"), body, price, pay_type, indent, mIndentId,date, new ApiResponseHandler(mContext) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        //需要在支付完成后根据indent处理页面跳转问题
                        switch (pay_type) {
                            case 1://支付宝
                                JSONObject object = new JSONObject(apiResponse.getData());
                                aliPay(object.optString("order_sn"));
                                break;
                            case 2://微信
//                                Map<String, Object> weChatPay =(Map<String, Object>) apiResponse.getData();
//                                Log.e("ssssssssssssssss",apiResponse+"");
//                                WXPayBean payBean = JSON.parseObject(apiResponse.getData(), WXPayBean.class);
//                                Log.e("paybean_weixin",payBean.toString());
//                                Map<String, Object> weChatPay = (Map<String, Object>) payBean;
//                                payWeiXin(weChatPay,mContext);
//                                wxPay(payBean);
                                break;
                            case 3://零钱
                                JSONObject object2 = new JSONObject(apiResponse.getData());
                                order_sn = object2.optString("order_sn");
                                Log.i("PayDialog","............createDialog="+order_sn+"   "+body+"       "+price);
                                if (SPUtil.getInstance(mContext).getInt("is_set_pay_pwd") == 1) {
                                    CoinsPayDialog.createDialog(mContext, order_sn, body, price).show();
                                } else {
                                    SetPayPwdDialog.createDialog(mContext, order_sn, body, price).show();
                                }
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(mContext, apiResponse.getMessage());
                    Log.e("ssssssssssssssss", apiResponse + "");
                }
            }
        });
    }

    private static void wxPay(WXPayBean entity) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, App.WEIXIN_APPID);
        // 将该app注册到微信
        api.registerApp(App.WEIXIN_APPID);
        try {
            PayReq req = new PayReq();
            req.appId = App.WEIXIN_APPID;//entity.getAppId();
            req.partnerId = entity.getPartnerId();//entity.getPartnerId();
            req.prepayId = entity.getPrepayId();
            req.nonceStr = entity.getNonceStr();//AppUtil.getRandomStringByLength(32);//
            req.timeStamp = "" + (System.currentTimeMillis() / 1000);//entity.getTimeStamp();
            req.packageValue = "Sign=WXPay";
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信

            StringBuilder sb = new StringBuilder();
            sb.append("appid").append("=").append(req.appId)
                    .append("&")
                    .append("noncestr").append("=").append(req.nonceStr)
                    .append("&")
                    .append("package").append("=").append(req.packageValue)
                    .append("&")
                    .append("partnerid").append("=").append(req.partnerId)
                    .append("&")
                    .append("prepayid").append("=").append(req.prepayId)
                    .append("&")
                    .append("timestamp").append("=").append(req.timeStamp)
                    .append("&")
                    .append("key=").append(entity.getKey());

            String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();

            req.sign = appSign;
            // 将该app注册到微信
            api.registerApp(App.WEIXIN_APPID);
//            api.sendReq(req);
            req.extData = "app data"; // optional
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.handleIntent(new Intent(), (IWXAPIEventHandler) mContext);
            api.sendReq(req);
            ToastUtils.showShort(mContext, "微信支付");

        } catch (Exception e) {
            ToastUtils.showShort(mContext, "未检测手机有微信");
            e.printStackTrace();
        }
    }

    public static void payWeiXin(Map<String, Object> mapInfo, Context context) {
        MyWXPayUtils.WXPayBuilder builder = new MyWXPayUtils.WXPayBuilder();
        builder.setAppId(MapUtils.getObject(mapInfo.get("appid"))) //微信支付AppID
                .setPartnerId(MapUtils.getObject(mapInfo.get("mch_id")))//微信支付商户号
                .setPrepayId(MapUtils.getObject(mapInfo.get("prepay_id")))//预支付码
                .setPackageValue("Sign=WXPay")//"Sign=WXPay"
                .setNonceStr(MapUtils.getObject(mapInfo.get("nonce_str")))
                .setTimeStamp(MapUtils.getObject(mapInfo.get("timestamp")))//时间戳
                .setSign(MapUtils.getObject(mapInfo.get("sign")))//签名
                .build()
                .toWXPayNotSign(context);
    }

    private static final int SDK_PAY_FLAG = 1;
    private static final int WXPAY = 2;
    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Log.e("alipay==========", "handleMessage: " + resultInfo);
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                        if (!(mContext instanceof WishingStarActivity)) {
                            ((Activity) mContext).finish();
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
                case WXPAY:
                    Map<String, Object> mapInfo = (Map<String, Object>) msg.obj;
                    //Toast.makeText(mContext, "来了", Toast.LENGTH_SHORT).show();
                    SPUtil.getInstance(mContext).setString("order_sn",mapInfo.get("order_sn")+"");
                    PayControl.payWeiXin(mapInfo, mContext);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * get the sdk version. 获取SDK版本号
     */

    public String getSDKVersion() {
        return new PayTask((Activity) mContext).getVersion();
    }

    //支付宝支付业务
    private static void aliPay(final String order_sn) {
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(appid);
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//
//        String sign = OrderInfoUtil2_0.getSign(params, privateKey);
//        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) mContext);
                Map<String, String> result = alipay.payV2(order_sn, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

}
