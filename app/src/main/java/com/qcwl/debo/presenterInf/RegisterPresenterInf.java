package com.qcwl.debo.presenterInf;

import android.content.Context;

import java.util.Map;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface RegisterPresenterInf extends PresenterInf {

    interface HandleData {

        /**
         * @param phone           用户手机号
         * @param code            验证码
         * @param pwd             密码
         * @param confirm_pwd     确认密码
         * @param invitation_code 扫码注册时传入扫码人的手机号
         * @deprecated 注册新用户
         */
        void register(Context context, String user_nickname, String phone, String code, String pwd, String confirm_pwd, String invitation_code);

        void register(Context context, Map<String, String> map);

        /**
         * @param phone 用户手机号
         * @deprecated 获取验证码
         */
        void getCode(Context context, String phone);

        /**
         * @param mobile  用户手机号
         * @param code    验证码
         * @param new_pwd 新密码
         * @decated 找回密码
         */
        void forgetPwd(Context context, String mobile, String code, String new_pwd);
    }
}

