package com.qcwl.debo.ui.found.fans.contract;

import android.content.Context;

/**
 * Created by Administrator on 2017/8/2.
 */

public interface FansContract {

    interface View {
        void doSuccess(int type, Object object);

        void doFailure(int code);
    }

    interface Presenter {
        void requestFansHomeList(Context context, String uid, int page);

        void requestFansList(Context context, int type, int page, String uid);

        void focusFans(Context context, String fans_uid, String follow_uid);

        void getFansDynamicList(Context context, String uid, int page);

        void praiseFansDynamic(Context context, String uid, String moments_id, int upvote);

        void getArticleDetail(Context context, String uid, String m_id);

        void sendGroupMessage(Context context, String uid, int type, String title, String content, String img);

        void commentDynamic(Context context, String uid, String moments_id, String mc_id, String mc_content, String reply_uid);

        void getDynamicCommentList(Context context, String uid, String moments_id);

        void praiseDynamicComment(Context context, String uid, String mc_id);

        void getDynamicPraiseList(Context context, String uid, String moments_id);

        void deleteFansDynamic(Context context, String uid, String moments_id);

        void deleteFansDynamicComment(Context context, String uid, String moments_id);

        void sendMessage(Context context, String uid, String content);

        void getFansDynamicDetail(Context context, String uid, String moments_id);
    }

}
