package com.qcwl.debo.presenterInf;

import android.content.Context;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface NewFriendsPresenterInf extends PresenterInf {

    interface HandleData {
        /**
         * @param uid    用户的id
         * @param mobile 搜索联系人的帐号
         * @deprecated 搜索联系人
         */
        void searchById(Context context, String uid, String mobile);

        /**
         * @param uid   用户的id
         * @param f_uid 好友的id
         * @deprecated 添加好友
         */
        void addFriend(String uid, String f_uid);

    }

}
