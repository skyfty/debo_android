package com.qcwl.debo.presenterInf;

import android.content.Context;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface MyPhotoPresenterInf extends PresenterInf {

    interface HandleData {
        /**
         * @param uid 当前登录用户id
         * @param page 数据的页数
         * @param f_uid 当查看某个好友的动态时的好友id
         * @deprecated 获取我的相册
         */
        void getMyPhoto(Context context, String uid, int page,String f_uid);

        /**
         *
         * @param context
         * @param uid
         * @deprecated 获取我的相册个人信息
         */
        void getMyInfo(Context context,String uid);
    }

}
