package com.qcwl.debo.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.zxing.android.Intents;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/17.
 */

public class ShareUtil {
    private static String platform_type = "";

    //分享段子 文章
    public static void openShare(final Activity context, String image, final String video_id, String content, String descripe, String url) {
        Log.i("ShareUtil", "..............openShare.=" + url+"   "+image);
        final UMWeb web = new UMWeb(replaceAllBlank(url));
        web.setTitle(content);
        web.setDescription(descripe);
        UMImage umImage = new UMImage(context, image);
        umImage.compressStyle = UMImage.CompressStyle.SCALE;
        web.setThumb(umImage);
        new ShareAction(context).withMedia(web)
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE/*, SHARE_MEDIA.SINA*/)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                        Log.i("平台类型", "onStart" + platform.toString());
                    }

                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        //结果回调
                        Log.i("平台类型", "onResult" + platform.toString());
                        if (platform.toString().equals("WEIXIN")) {
                            platform_type = "1";
                        } else if (platform.toString().equals("QQ")) {
                            platform_type = "3";
                        } else if (platform.toString().equals("QZONE")) {
                            platform_type = "5";
                        } else if (platform.toString().equals("WEIXIN_CIRCLE")) {
                            platform_type = "2";
                        }/*else if (platform.toString().equals("SINA")){
                            platform_type ="4";
                        }*/
                        addJF(context, video_id, platform_type);
                    }

                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        Log.i("平台类型", "onError" + t.toString());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {

                    }
                }).open();
    }

    public static String replaceAllBlank(String str) {
        String s = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
         /*\n 回车(\u000a)
            \t 水平制表符(\u0009)
            \s 空格(\u0008)
               \r 换行(\u000d)*/
            Matcher m = p.matcher(str);
            s = m.replaceAll("");
        }
        return s;
    }

    //分享加积分接口
    private static void addJF(final Context mContext, String video_id, String platform_type) {
        Log.i("加积分接口", SPUtil.getInstance(mContext).getString("uid") + "video_id" + video_id + "platform_type" + platform_type);
        Api.addJF(SPUtil.getInstance(mContext).getString("uid"), video_id, platform_type, new ApiResponseHandler(mContext) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    Log.i("加积分接口", "apiResponse0=" + apiResponse);
                } else {
                    Log.i("加积分接口", "apiResponse_else=" + apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(String errMessage) {
                Log.i("加积分接口", "onFailure" + errMessage);
                super.onFailure(errMessage);
            }
        });
    }


    //分享
    public static void openShareLogo(final Activity context, int image, String content, String descripe, String url) {
        final UMWeb web = new UMWeb(url);
        web.setTitle(content);
        web.setDescription(descripe);
        web.setThumb(new UMImage(context, image));
        new ShareAction(context).withMedia(web)
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE/*, SHARE_MEDIA.SINA*/)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA platform) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        Log.i("平台类型", "onResult" + platform.toString());
                    }

                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        ToastUtils.showShort(context, "取消分享");
                    }
                }).open();
    }


    public static void share(final Activity context, String url) {
        new ShareAction(context).withMedia(new UMWeb(url))
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE/*, SHARE_MEDIA.SINA*/)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA platform) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        //  回调
                    }

                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {

                    }
                }).open();
    }


    public static void shareApplets(final Activity context, String image, String content, String descripe, String url, String id) {
        /*UMMin umMin = new UMMin(url);
        //兼容低版本的网页链接
        umMin.setThumb(new UMImage(context, image));
        // 小程序消息封面图片
        umMin.setTitle(content);
        // 小程序消息title
        umMin.setDescription(descripe);
        //正式版
        //Config.setMiniPreView();
        //测试版
        Config.setMiniTest();
        // 小程序消息描述
        umMin.setPath("pages/chat/chat?invitation_code=" + id);
        //小程序页面路径
        umMin.setUserName("gh_3391fb58f9ea");
        // 小程序原始id,在微信平台查询*/
        new ShareAction(context)
               // .withMedia(umMin)
                //.setPlatform(SHARE_MEDIA.WEIXIN)
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE/*, SHARE_MEDIA.SINA*/)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        if (share_media == SHARE_MEDIA.WEIXIN){
                            UMMin umMin = new UMMin(url);
                            //兼容低版本的网页链接
                            umMin.setThumb(new UMImage(context, image));
                            // 小程序消息封面图片
                            umMin.setTitle(content);
                            // 小程序消息title
                            umMin.setDescription(descripe);
                            /*//正式版
                            Config.setMiniPreView();*/
                            //测试版
                            //Config.setMiniTest();
                            // 小程序消息描述
                            umMin.setPath("pages/chat/chat?invitation_code=" + id);
                            //小程序页面路径
                            umMin.setUserName("gh_3391fb58f9ea");
                            // 小程序原始id,在微信平台查询
                            new ShareAction(context)
                             .withMedia(umMin)
                            .setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener).share();
                        }else{
                            UMWeb web = new UMWeb(replaceAllBlank(url));
                            web.setTitle(content);
                            web.setDescription(descripe);
                            UMImage umImage = new UMImage(context, image);
                            umImage.compressStyle = UMImage.CompressStyle.SCALE;
                            web.setThumb(umImage);
                            new ShareAction(context).withMedia(web)
                                    .setPlatform(share_media)
                                    .setCallback(umShareListener).share();
                        }
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        ToastUtils.showShort(context, "分享成功！");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                }).open();
    }
    public static UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            Log.i("平台类型", "onStart=" + share_media.toString());
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Log.i("平台类型", "onResult=" + share_media.toString());
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Log.i("平台类型", "onError=" + share_media.toString());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Log.i("平台类型", "onCancel=" + share_media.toString());
        }
    };

}
