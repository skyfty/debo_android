package com.qcwl.debo.presenterInf;

import android.content.Context;

import com.qcwl.debo.model.ContactsBean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface ContactListPresenterInf extends PresenterInf {

    interface HandleData {
        //获取联系人列表
        void getContactList(Context context, String uid, List<ContactsBean> SourceDateList);

        //根据输入框中的值来过滤数据并更新ListView
        void filterContactList(String filterStr);

        /**
         * @param my_mobile 自己的手机号
         * @param mobile    查看用户的手机号
         * @deprecated 获取好友详情
         */
        void getUserInformation(Context context, String my_mobile, String mobile);

        /**
         * @param mobile    查看用户的手机号
         * @deprecated 获取个人用户信息
         */
        void getUserInformation1(Context context, String mobile);
        /**
         * @param my_mobile 自己的手机号
         * @param mobile    查看用户的手机号
         * @deprecated 获取人脉详情
         */
        void getRenMaiInformation(Context context, String my_mobile, String mobile);

        void getUserInfoByUid(Context context, String my_uid, String uid);

        /**
         * @param cb   删除的联系人对象
         * @param uid  当前登录用户的id
         * @param list 存储联系人的集合
         * @deprecated 删除联系人
         */
        void delContact(Context context, String uid, ContactsBean cb, List<ContactsBean> list);

        /**
         * @param mobile         当前登录用户手机号
         * @param type           人脉类型 0:所有人脉 1:直接人脉 2:邀请人脉 3:合约人脉
         * @param SourceDateList 存储联系人的集合
         * @deprecated 获取所有人脉
         */
          void getReMai(Context context, String mobile, String type, List<ContactsBean> SourceDateList);

        /**
         * @param uid       当前登录用户ID
         * @param conn_type 人脉类型 1:开放人脉 2:贵族人脉
         * @param content   搜索传入的内容
         * @deprecated 查询贵族人脉
         */
        void getAristocracyReMai(Context context, String uid, String conn_type,int page, String content);

        /**
         * @param my_mobile 当前登录用户手机号
         * @param mobile    被邀请人的手机号
         * @deprecated 邀请人脉
         */
        void sendInvitation(Context context, String my_mobile, String mobile);

        /**
         * @param my_mobile 当前登录用户手机号
         * @param mobile    被邀请人的手机号
         * @deprecated 同意人脉邀请
         */
        void acceptInvitation(Context context, String my_mobile, String mobile);

        /**
         * @param uid 当前登录用户ID
         * @param tri_id    三方客唯一标识id
         * @param request_type    操作请求 1、确认（发送三方客邀请消息的时候使用）；2、取消（发送三方客邀请消息的时候使用 ，或者， 发送取消此次三方客合约的时候使用）；3、完成
         * @deprecated 同意三方客邀请
         */
        void acceptSanFangfKeInvitation(Context context, String uid, String tri_id,String request_type);

        /**
         * @param my_mobile 当前登录用户手机号
         * @param mobile    聊天人的手机号
         * @deprecated 判断是否是自己购买的合约人脉
         */
        void isBuyRenMai(Context context, String my_mobile, String mobile);

        /**
         * @param my_mobile 当前登录用户手机号
         * @param con_mobile    合约人人的手机号
         * @deprecated 判断是否是自己购买的合约人脉
         */
        void terminationRenMai(Context context, String my_mobile, String con_mobile);

    }

    //根据查询条件更新数据
    void updateDataByFilter(List<ContactsBean> filterDateList);

}
