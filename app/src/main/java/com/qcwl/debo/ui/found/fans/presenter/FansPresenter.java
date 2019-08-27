package com.qcwl.debo.ui.found.fans.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.found.fans.bean.ArticleDetailBean;
import com.qcwl.debo.ui.found.fans.bean.FansDynamicBean;
import com.qcwl.debo.ui.found.fans.bean.FansDynamicCommentBean;
import com.qcwl.debo.ui.found.fans.bean.FansDynamicPraiseBean;
import com.qcwl.debo.ui.found.fans.bean.FansHomeDataBean;
import com.qcwl.debo.ui.found.fans.bean.FansListBean;
import com.qcwl.debo.ui.found.fans.contract.FansContract;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */

public class FansPresenter implements FansContract.Presenter {
    public static final int TYPE_FANS_HOME = 1;
    public static final int TYPE_FANS_LIST = 2;
    public static final int TYPE_FANS_FOCUS = 3;
    public static final int TYPE_FANS_DYNAMIC_LIST = 4;
    public static final int TYPE_FANS_PRAISE = 5;
    public static final int TYPE_FANS_ARTICLE_DETAIL = 6;
    public static final int TYPE_FANS_GROUP_MESSAGE = 7;
    public static final int TYPE_FANS_COMMENT_DYNAMIC = 8;
    public static final int TYPE_FANS_DYNAMIC_COMMENT_LIST = 9;
    public static final int TYPE_FANS_PRAISE_DYNAMIC_COMMENT = 10;
    public static final int TYPE_FANS_DYNAMIC_PRAISE_LIST = 11;
    public static final int TYPE_FANS_DELETE_DYNAMIC = 12;
    public static final int TYPE_FANS_DELETE_DYNAMIC_COMMENT = 13;
    public static final int TYPE_FANS_DYNAMIC_DETAIL = 14;

    private FansContract.View view;

    public FansPresenter(FansContract.View view) {
        this.view = view;
    }

    @Override
    public void requestFansHomeList(final Context context, String uid, int page) {
        Api.getFansHomeList(uid, page, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    FansHomeDataBean fansHomeDataBean = JSON.parseObject(apiResponse.getData(), FansHomeDataBean.class);
                    view.doSuccess(TYPE_FANS_HOME, fansHomeDataBean);
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void requestFansList(final Context context, int type, int page, String uid) {
        Api.getFansList(uid, type, page, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    FansListBean fansListBean = JSON.parseObject(apiResponse.getData(), FansListBean.class);
                    view.doSuccess(TYPE_FANS_LIST, fansListBean);
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }


    @Override
    public void focusFans(final Context context, String fans_uid, String follow_uid) {
        Api.focusfans(fans_uid, follow_uid, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    view.doSuccess(TYPE_FANS_FOCUS, "");
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void getFansDynamicList(final Context context, String uid, int page) {
        Api.requestCircleList(3, uid, page, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<FansDynamicBean> list = JSON.parseArray(apiResponse.getData(), FansDynamicBean.class);
                    view.doSuccess(TYPE_FANS_DYNAMIC_LIST, list);
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void praiseFansDynamic(final Context context, String uid, String moments_id, int upvote) {
        Api.praiseCircle(3, uid, moments_id, upvote, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        JSONObject object = new JSONObject(apiResponse.getData());
                        String upvote = object.getString("upvote");
                        view.doSuccess(TYPE_FANS_PRAISE, upvote);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void getArticleDetail(final Context context, String uid, String m_id) {
        Api.getArticleDetail(uid, m_id, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    ArticleDetailBean bean = JSON.parseObject(apiResponse.getData(), ArticleDetailBean.class);
                    view.doSuccess(TYPE_FANS_ARTICLE_DETAIL, bean);
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void sendGroupMessage(final Context context, String uid, int type, String title, String content, String img) {
        Api.sendGroupMessage(uid, type, title, content, img, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    view.doSuccess(TYPE_FANS_GROUP_MESSAGE, "");
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void commentDynamic(final Context context, String uid, String moments_id, String mc_id, String mc_content, String reply_uid) {
        Api.commentDynamic(uid, moments_id, mc_id, mc_content, reply_uid, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    FansDynamicCommentBean bean = JSON.parseObject(apiResponse.getData(), FansDynamicCommentBean.class);
                    view.doSuccess(TYPE_FANS_COMMENT_DYNAMIC, bean);
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void getDynamicCommentList(final Context context, String uid, String moments_id) {
        Api.getDynamicCommentList(uid, moments_id, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<FansDynamicCommentBean> list = JSON.parseArray(apiResponse.getData(), FansDynamicCommentBean.class);
                    view.doSuccess(TYPE_FANS_DYNAMIC_COMMENT_LIST, list);
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void praiseDynamicComment(final Context context, String uid, String mc_id) {
        Api.praiseDyanmicComment(uid, mc_id, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        JSONObject object = new JSONObject(apiResponse.getData());
                        String num = object.getString("upvote_num");
                        view.doSuccess(TYPE_FANS_PRAISE_DYNAMIC_COMMENT, num);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void getDynamicPraiseList(final Context context, String uid, String moments_id) {
        Api.getDynamicPraiseList(uid, moments_id, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        List<FansDynamicPraiseBean> list = JSON.parseArray(apiResponse.getData(), FansDynamicPraiseBean.class);
                        view.doSuccess(TYPE_FANS_DYNAMIC_PRAISE_LIST, list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void deleteFansDynamic(final Context context, String uid, String moments_id) {
        Api.deleteFansDynamic(uid, moments_id, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        view.doSuccess(TYPE_FANS_DELETE_DYNAMIC, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void deleteFansDynamicComment(final Context context, String uid, String mc_id) {
        Api.deleteFansDynamicComment(uid, mc_id, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        view.doSuccess(TYPE_FANS_DELETE_DYNAMIC_COMMENT, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void sendMessage(final Context context, String uid, String content) {
        Api.sendMessage(uid, content, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    view.doSuccess(TYPE_FANS_GROUP_MESSAGE, "");
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void getFansDynamicDetail(final Context context, String uid, String moments_id) {
        Api.getFansDetail(uid, moments_id, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<FansDynamicBean> list = JSON.parseArray(apiResponse.getData(), FansDynamicBean.class);
                    view.doSuccess(TYPE_FANS_DYNAMIC_DETAIL, list);
                } else {
                    ToastUtils.showShort(context, apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }


}
