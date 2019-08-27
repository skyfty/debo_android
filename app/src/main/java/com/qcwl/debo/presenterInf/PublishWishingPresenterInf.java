package com.qcwl.debo.presenterInf;

import android.content.Context;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface PublishWishingPresenterInf extends PresenterInf {

    interface HandleData {
        /**
         * @param uid     当前登录用户id
         * @param content 许愿星内容
         * @deprecated 发布许愿星
         */
        void publishWishingStar(Context context, String uid, String content);

        /**
         * @param uid        当前登录用户的id
         * @param ad_type    发布类型 1:碰一碰 2:撞一撞 3:许愿星
         * @param title      广告标题
         * @param ad_link    广告链接
         * @param price      红包金额
         * @param p_num      红包个数
         * @param ad_content 广告内容
         * @param start_time 广告开始时间
         * @param time_range 广告结束世间
         * @param avatar     头像文件
         * @deprecated 发布广告
         */
        void publishAdvertising(Context context, String ad_type, String uid, String title, String ad_link, String price, String p_num, String ad_content, String start_time, String time_range, List<File> avatar);

    }

}
