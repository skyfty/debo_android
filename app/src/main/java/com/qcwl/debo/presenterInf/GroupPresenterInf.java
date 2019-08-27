package com.qcwl.debo.presenterInf;

import android.content.Context;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface GroupPresenterInf extends PresenterInf {

    interface HandleData {
        /**
         * @param mobile 当前登录用户手机号
         * @deprecated 获取一个用户参与的所有群组
         */
        void getGroup(Context context, String mobile);

        /**
         * @param groupname  群名称
         * @param desc       群描述
         * @param owner      群主
         * @param members    群成员(手机号以分号拼接)
         * @param qroup_type 群类型 1 好友群 2 人脉群
         * @deprecated 创建一个群聊组
         */
        void createGroup(Context context, String groupname, String desc, String owner, String members, int qroup_type);

        /**
         * @param groupid   群ID
         * @param my_mobile 当前用户的手机号
         * @deprecated 获取单个群的详细信息 包括群成员
         */
        void getSingleGroupInfo(Context context, String groupid, String my_mobile);

        /**
         * @param groupid 群ID
         * @param mobile  成员手机号 多人用逗号隔开
         * @deprecated 添加群组成员
         */
        void addGroupMember(Context context, String groupid, String mobile);

        /**
         * @param groupid 群ID
         * @param mobile  成员手机号 多人用逗号隔开
         * @deprecated 删除群组成员或者退出群组
         */

        void delGroupMember(Context context, String groupid, String mobile);
        /**
         * @param groupid 群ID
         * @deprecated 解散群租
         */
        void delGroup(Context context, String groupid);

        /**
         * @param groupid      群ID
         * @param mobile       成员手机号
         * @param name         昵称
         * @param is_show_name 是否显示昵称 1显示 2不显示
         * @deprecated 设置群昵称
         */
        void setGroupNick(Context context, String mobile, String name, String groupid, String is_show_name);

    }

}
