package com.qcwl.debo.presenterInf;

import android.content.Context;

import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface MyInfoPresenterInf extends PresenterInf {

    interface HandleData {
        /**
         * @param my_mobile 自己的手机号
         * @param mobile    查看用户的手机号
         * @deprecated 获取用户信息
         */
        void getUserInformation(Context context, String my_mobile, String mobile);
        /**
         * @param mobile    查看用户的手机号
         * @deprecated 获取个人用户信息
         */
        void getUserInformation1(Context context, String mobile);
        /**
         * @param uid           当前登录用户的id
         * @param user_nickname 用户名
         * @param user_nickname 用户名
         * @param address       详细地址
         * @param sex           性别 0：保密 1：男 2：女
         * @param province      省
         * @param city          市
         * @param area          区
         * @param signature     个性签名
         * @param avatar        头像文件
         * @deprecated 编辑用户信息
         */
        void editUserInformation(Context context, String uid, String user_nickname, String address, String sex, String province, String city, String area, String signature, File avatar);

        void editUserInformation(Context context,Map<String,String> map, File avatar);

        /**
         * @param context
         * @param uid     当前登录用户的id
         * @deprecated 获取人脉总览
         */
        void getRenMaiInfo(Context context, String uid);

        /**
         * @param context
         * @param uid     当前登录用户的id
         * @param conn_type     人脉类型 1：开放人脉 2：贵族人脉
         * @param pur_price     贵族人脉购买金额
         * @param day_num     贵族人脉购买天数
         * @deprecated 编辑人脉设置
         */
        void editRenMaiSetting(Context context, String uid, String conn_type, String operator,String pur_price, String day_num);

        /**
         * @param context
         * @param uid     当前登录用户的id
         * @deprecated 获取人脉设置信息
         */
        void getRenMaiSetting(Context context, String uid);

        /**
         * @param context
         * @param uid     当前登录用户的id
         * @param content     反馈的内容信息
         * @deprecated 提交反馈信息
         */
        void commitFeedback(Context context, String uid,String content);

        /**
         * @param context
         * @param uid     当前登录用户的id
         * @param old_pwd     旧密码
         * @param new_pwd     新密码
         * @param confirm_pwd     确认密码
         * @deprecated 账号与安全 修改密码
         */
        void modifyPwd(Context context, String uid,String old_pwd,String new_pwd,String confirm_pwd);

        /**
         * @param context
         * @param fans_uid     当前登录用户的id
         * @param follow_mobile     关注人的手机号
         * @deprecated 关注好友
         */
        void attention(Context context, String fans_uid,String follow_mobile);

        /**
         * @param context
         * @param my_uid     当前登录用户的id
         * @param follow_mobile     关注人的手机号
         * @deprecated 判断是否关注好友
         */
        void isAttention(Context context, String my_uid,String follow_mobile);
        /**
         * @param context
         * @param my_uid     当前登录用户的id
         * @param f_uid     朋友id
         *  @param circle_state1     不让朋友（f_uid）看我圈子 1-看 2-不看
         * @param circle_state2     我不看朋友（f_uid）圈子 1-看 2-不看
         * @deprecated 判断是否关注好友
         */
        void setMomentsAuthority(Context context,  String my_uid, String f_uid,String circle_state1,String circle_state2);

    }

}
