package com.qcwl.debo.presenterInf;

import android.content.Context;

/**
 * Created by Administrator on 2017/7/28.
 */

public interface ModifyRemarkPresenterInf extends PresenterInf {

    interface HandleData {

        /**
         * @param context
         * @param uid      当前登录用户id
         * @param f_uid    修改备注的好友id
         * @param f_mobile 修改备注好友的手机号
         * @param remark   修改的备注
         * @deprecated 修改好友备注 f_uid和f_mobile传一个即可
         */
        void modifyRemark(Context context, String uid, String f_uid, String f_mobile, String remark);

        /**
         * @param context
         * @param uid      当前登录用户id
         * @param f_uid    修改备注的人脉id
         * @param f_mobile 修改备注人脉的手机号
         * @param remark   修改的备注
         * @deprecated 修改人脉备注 f_uid和f_mobile传一个即可
         */
        void modifyRenMaiRemark(Context context, String uid, String f_uid, String f_mobile, String remark);

    }
}
