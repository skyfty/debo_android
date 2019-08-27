package com.qcwl.debo.utils;

import android.content.Context;

import com.qcwl.debo.ui.pay.wechatpay.MapUtils;
import com.qcwl.debo.ui.pay.wechatpay.MyWXPayUtils;

import java.util.Map;

/**
 * Created by ZC on 2018/6/10.
 */

public class PayControl {

    public static void payWeiXin(Map<String, Object> mapInfo,Context context){
        MyWXPayUtils.WXPayBuilder builder=new MyWXPayUtils.WXPayBuilder();
        builder .setAppId(MapUtils.getObject(mapInfo.get("appid"))) //微信支付AppID
                .setPartnerId(MapUtils.getObject(mapInfo.get("partnerid")))//微信支付商户号
                .setPrepayId(MapUtils.getObject(mapInfo.get("prepayid")))//预支付码
                .setPackageValue("Sign=WXPay")//"Sign=WXPay"
                .setNonceStr(MapUtils.getObject(mapInfo.get("noncestr")))
                .setTimeStamp(MapUtils.getObject(mapInfo.get("timestamp")))//时间戳
                .setSign(MapUtils.getObject(mapInfo.get("sign")))//签名
                .build()
                .toWXPayNotSign(context);
    }
//    /**
//     * 支付宝支付测试
//     */
//    public static void payAli(String payInfo,Activity activity,AliPayReq.OnAliPayListener listener){
//        new AliPayReq(listener).send(payInfo,activity);
//    }

}
