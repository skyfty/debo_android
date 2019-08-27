package com.qcwl.debo.http;

import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qcwl.debo.utils.RAS;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 */
public class Api {

    /**
     * 登录接口
     */

    public static void Login(String mobile, String password, String province, String city, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("password", RAS.getPublicKeyStrRAS(password.getBytes()));
        params.put("province", RAS.getPublicKeyStrRAS(province.getBytes()));
        params.put("city", RAS.getPublicKeyStrRAS(city.getBytes()));

        ApiHttpClient.post(ApiHttpClient.API_URL + "user/login",
                params, handler);
    }

    /**
     * 登录接口
     */
    public static void Login(Map<String, String> map, AsyncHttpResponseHandler handler) {
        if (map == null || map.size() == 0) {
            return;
        }
        RequestParams params = new RequestParams();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/login",
                params, handler);
    }

    /**
     * 注册接口
     */

    public static void register(String user_nickname, String mobile, String code, String password, String confirm_password, String invitation_code, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("user_nickname", RAS.getPublicKeyStrRAS(user_nickname.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("code", RAS.getPublicKeyStrRAS(code.getBytes()));
        params.put("password", RAS.getPublicKeyStrRAS(password.getBytes()));
        params.put("confirm_password", RAS.getPublicKeyStrRAS(confirm_password.getBytes()));
        params.put("invitation_code", RAS.getPublicKeyStrRAS(invitation_code.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/register",
                params, handler);
    }

    /**
     * 注册接口
     */

    public static void register(Map<String, String> map, AsyncHttpResponseHandler handler) {
        if (map == null || map.size() == 0) {
            return;
        }
        RequestParams params = new RequestParams();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
            Log.i("Register", ".....params=" + entry.getKey() + "     " + entry.getValue());
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/register",
                params, handler);
    }

    /**
     * 获取验证码
     */

    public static void getCode(String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/send_message",
                params, handler);
    }

    /**
     * 找回密码
     */

    public static void findPwd(String mobile, String code, String new_pwd, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("code", RAS.getPublicKeyStrRAS(code.getBytes()));
        params.put("new_pwd", RAS.getPublicKeyStrRAS(new_pwd.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/reset_login_pwd",
                params, handler);
    }

    /**
     * 获取我的好友
     */

    public static void getContacts(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "friends/friend_list",
                params, handler);
    }

    /**
     * 获取我的人脉
     */

    public static void getRenMai(String mobile, String type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("type", RAS.getPublicKeyStrRAS(String.valueOf(type).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "connection/connect_list",
                params, handler);
    }

    /**
     * 搜索人脉列表
     */

    public static void searchRenMai(String uid, String conn_type, String content, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("conn_type", RAS.getPublicKeyStrRAS(conn_type.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "connection/find_connection", params, handler);
    }

    /**
     * 邀请人脉
     */

    public static void sendInvitation(String my_mobile, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("my_mobile", RAS.getPublicKeyStrRAS(my_mobile.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "connection/send_free_conn", params, handler);
    }

    /**
     * 同意人脉邀请
     */

    public static void acceptInvitation(String my_mobile, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("my_mobile", RAS.getPublicKeyStrRAS(my_mobile.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "connection/invite_free_conn", params, handler);
    }

    /**
     * 同意三方客邀请
     */

    public static void acceptSanFangfKeInvitation(String uid, String tri_id, String request_type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("tri_id", RAS.getPublicKeyStrRAS(tri_id.getBytes()));
        params.put("request_type", RAS.getPublicKeyStrRAS(request_type.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "exploration/start_tripartite", params, handler);
    }

    /**
     * 判断是否是自己购买的合约人脉
     */

    public static void isBuyRenMai(String my_mobile, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("my_mobile", RAS.getPublicKeyStrRAS(my_mobile.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/is_my_pur", params, handler);
    }

    /**
     * 解除自己的合约人脉
     */

    public static void terminationRenMai(String my_mobile, String con_mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("my_mobile", RAS.getPublicKeyStrRAS(my_mobile.getBytes()));
        params.put("con_mobile", RAS.getPublicKeyStrRAS(con_mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "connection/remove_contract_conn", params, handler);
    }

    /**
     * 搜索好友
     */

    public static void searchById(String uid, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/find_user",
                params, handler);
    }


    /**
     * 获取用户好友信息
     */

    public static void getUserInformation(String my_mobile, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("my_mobile", RAS.getPublicKeyStrRAS(my_mobile.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/get_user_info",
                params, handler);
    }

    /**
     * 获取用户人脉信息
     */

    public static void getRenMaiInformation(String my_mobile, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("my_mobile", RAS.getPublicKeyStrRAS(my_mobile.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "connection/conn_particulars",
                params, handler);
    }

    /**
     * 获取用户信息
     */

    public static void getUserInfoByUid(String my_uid, String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("my_uid", RAS.getPublicKeyStrRAS(my_uid.getBytes()));
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/get_user_info",
                params, handler);
    }


    /**
     * 获取用户个人信息
     */

    public static void getUserInfo(String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/get_user_info",
                params, handler);
    }

    /**
     * 编辑用户信息
     */

    public static void editUserInformation(String uid, String user_nickname, String address, String sex, String province, String city, String area, String signature, File avatar, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("user_nickname", RAS.getPublicKeyStrRAS(user_nickname.getBytes()));
        params.put("address", RAS.getPublicKeyStrRAS(address.getBytes()));
        params.put("sex", RAS.getPublicKeyStrRAS(sex.getBytes()));
        params.put("province", RAS.getPublicKeyStrRAS(province.getBytes()));
        params.put("city", RAS.getPublicKeyStrRAS(city.getBytes()));
        params.put("area", RAS.getPublicKeyStrRAS(area.getBytes()));
        params.put("signature", RAS.getPublicKeyStrRAS(signature.getBytes()));
        try {
            params.put("avatar", avatar);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/edit_userinfo",
                params, handler);
    }

    public static void editUserInformation(Map<String, String> map, File avatar, AsyncHttpResponseHandler handler) {

        // for (Map.Entry<String, String> key : map.entrySet()) { }

        RequestParams params = new RequestParams();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.put(entry.getKey(),
                        RAS.getPublicKeyStrRAS(entry.getValue().getBytes()));
            }
        }
        try {
            if (avatar != null) {
                params.put("avatar", avatar);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/edit_userinfo",
                params, handler);
    }

    /**
     * 获取人脉总览
     */

    public static void getRenMaiInfo(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/con_total_data", params, handler);
    }

    /**
     * 编辑人脉设置
     */

    public static void editRenMaiSetting(String uid, String conn_type, String operator, String pur_price, String day_num, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("conn_type", RAS.getPublicKeyStrRAS(conn_type.getBytes()));
        params.put("operator", RAS.getPublicKeyStrRAS(operator.getBytes()));
        params.put("pur_price", RAS.getPublicKeyStrRAS(pur_price.getBytes()));
        params.put("day_num", RAS.getPublicKeyStrRAS(day_num.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "connection/connection_set", params, handler);
    }

    /**
     * 获取人脉设置信息
     */

    public static void getRenMaiSetting(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "connection/return_conn_info", params, handler);
    }

    /**
     * 提交反馈信息
     */

    public static void commitFeedback(String uid, String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/help_and_feedback", params, handler);
    }

    /**
     * 修改密码
     */

    public static void modifyPwd(String uid, String old_pwd, String new_pwd, String confirm_pwd, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("old_pwd", RAS.getPublicKeyStrRAS(old_pwd.getBytes()));
        params.put("new_pwd", RAS.getPublicKeyStrRAS(new_pwd.getBytes()));
        params.put("confirm_pwd", RAS.getPublicKeyStrRAS(confirm_pwd.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/modify_login_pwd", params, handler);
    }

    /**
     * 发布许愿星
     */

    public static void publishWishingStar(String uid, String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Oneself/wishing_star", params, handler);
    }

    /**
     * 发布广告
     */

    public static void publishAdvertising(String ad_type, String uid, String title, String ad_link, String price, String p_num, String ad_content, String start_time, String time_range, List<File> avatar, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("ad_type", RAS.getPublicKeyStrRAS(ad_type.getBytes()));
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("title", RAS.getPublicKeyStrRAS(title.getBytes()));
        params.put("ad_link", RAS.getPublicKeyStrRAS(ad_link.getBytes()));
        params.put("price", RAS.getPublicKeyStrRAS(price.getBytes()));
        params.put("p_num", RAS.getPublicKeyStrRAS(p_num.getBytes()));
        params.put("ad_content", RAS.getPublicKeyStrRAS(ad_content.getBytes()));
        params.put("start_time", RAS.getPublicKeyStrRAS(start_time.getBytes()));
        params.put("time_range", RAS.getPublicKeyStrRAS(time_range.getBytes()));
        try {
            if (avatar != null && avatar.size() > 0) {
                for (int i = 0; i < avatar.size(); i++) {
                    params.put("img" + i, avatar.get(i));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "Oneself/touch_touch", params, handler);
    }

    /**
     * 添加好友
     */

    public static void addFriends(String uid, String f_mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("f_mobile", RAS.getPublicKeyStrRAS(f_mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "friends/add_friend",
                params, handler);
    }


    /**
     * 删除好友
     */

    public static void delContact(String uid, String f_mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("f_mobile", RAS.getPublicKeyStrRAS(f_mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "friends/del_friend",
                params, handler);
    }

    /**
     * 获取我的相册
     */

    public static void getMyPhoto(String uid, int page, String f_uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        params.put("f_uid", RAS.getPublicKeyStrRAS(f_uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/photo_album",
                params, handler);
    }

    /**
     * 修改好友备注
     */

    public static void modifyRemark(String uid, String f_uid, String f_mobile, String remark, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("f_uid", RAS.getPublicKeyStrRAS(f_uid.getBytes()));
        params.put("f_mobile", RAS.getPublicKeyStrRAS(f_mobile.getBytes()));
        params.put("remark", RAS.getPublicKeyStrRAS(remark.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "friends/modify_remark",
                params, handler);
    }

    /**
     * 修改人脉备注
     */

    public static void modifyRenMaiRemark(String uid, String f_uid, String f_mobile, String remark, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("f_uid", RAS.getPublicKeyStrRAS(f_uid.getBytes()));
        params.put("f_mobile", RAS.getPublicKeyStrRAS(f_mobile.getBytes()));
        params.put("remark", RAS.getPublicKeyStrRAS(remark.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "connection/connect_remark",
                params, handler);
    }

    /**
     * 朋友圈/人脉圈/粉丝动态-item点赞
     */
    public static void praiseCircle(int type, String uid, String moments_id, int is_praise, AsyncHttpResponseHandler handler) {
        String apiUrl = "";
        switch (type) {
            case 1:
                apiUrl = "moments/moments_upvote";
                break;
            case 2:
                apiUrl = "connection/connection_upvote";
                break;
            case 3:
                apiUrl = "fans/fans_upvote";
                break;
            default:
                break;
        }
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("moments_id", RAS.getPublicKeyStrRAS(moments_id.getBytes()));
        params.put("upvote", RAS.getPublicKeyStrRAS(String.valueOf(is_praise).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + apiUrl, params, handler);
    }

    /**
     * 朋友圈/人脉圈/粉丝动态-列表接口
     */
    public static void requestCircleList(int type, String uid, int page, AsyncHttpResponseHandler handler) {
        String apiUrl = "";
        switch (type) {
            case 1://朋友圈
                apiUrl = "moments/moments_lists";
                break;
            case 2://人脉圈
                apiUrl = "connection/con_circle_lists";
                break;
            case 3://粉丝动态
                apiUrl = "fans/fans_trends";
                break;
            default:
                break;
        }
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + apiUrl, params, handler);
    }

    /**
     * 朋友圈/人脉圈-item评论
     *
     * @param uid
     * @param moments_id
     * @param content
     * @param handler
     */
    public static void commentCircle(int type, String uid, String reply_uid, String moments_id, String content, AsyncHttpResponseHandler handler) {
        String apiUrl = "";
        switch (type) {
            case 1:
                apiUrl = "moments/moments_comment";
                break;
            case 2:
                apiUrl = "connection/connection_comment";
                break;
            default:
                break;
        }
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("reply_uid", RAS.getPublicKeyStrRAS(reply_uid.getBytes()));
        params.put("moments_id", RAS.getPublicKeyStrRAS(moments_id.getBytes()));
        params.put("mc_content", RAS.getPublicKeyStrRAS(content.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + apiUrl, params, handler);
    }

    //粉丝详情接口
    public static void getFansDetail(String uid, String moments_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("moments_id", RAS.getPublicKeyStrRAS(moments_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Fans/trends_show", params, handler);
    }

    /**
     * 获取顶部图片
     *
     * @param uid
     * @param handler
     */
    public static void getCircleTopImg(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Moments/get_moments_bg", params, handler);
    }

    /**
     * 修改圈子顶部图片
     *
     * @param uid
     * @param file
     * @param handler
     */
    public static void modifyCircleTopImg(String uid, File file, AsyncHttpResponseHandler handler) {
        if (file == null) {
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        try {
            params.put("img", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("apiResponse_upvideo2", "" + params);
        ApiHttpClient.post(ApiHttpClient.API_URL + "moments/modify_moments_bg", params, handler);
    }

    /**
     * 发布圈子动态
     *
     * @param uid
     * @param is_video 1---视频  0---图片
     * @param type     发布类型 1朋友圈 2人脉圈 3朋友圈人脉圈同时发布 4粉丝动态
     * @param content
     * @param files
     * @param handler
     */
    public static void publishCircleInfo(String uid, String position, String lat, String lng, int is_video, int type,int moments_type, String content, List<File> files, String[] contactCall,String[] renMaiCall, String[] renMaiIdss,String[] contactIdss, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("talk_type", type);
        if (!TextUtils.isEmpty(content)) {
            try {
                params.put("moments_content", content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(position)) {
            params.put("position", position);
            params.put("lat", lat);
            params.put("lng", lng);
        }
        if (type == 1){
            if (contactCall != null && contactCall.length != 0) {
                params.put("moments_remind_id", contactCall);
                params.put("moments_visible_id", contactIdss);
                params.put("moments_type", moments_type);
            }
        }else if (type == 2) {
            if (renMaiCall != null && renMaiCall.length != 0) {
                params.put("connection_remind_id", renMaiCall);
                params.put("connection_visible_id", renMaiIdss);
                params.put("connection_type", moments_type);
            }
        }else if(type == 3){
            if (contactCall != null && contactCall.length != 0) {
                params.put("moments_remind_id", contactCall);
                params.put("moments_visible_id", contactIdss);
                params.put("moments_type", moments_type);
                params.put("connection_remind_id", renMaiCall);
                params.put("connection_visible_id", renMaiIdss);
                params.put("connection_type", moments_type);
            }
        }
        try {
            Log.i("RAG======", "........files=" + files);
            if (files != null && files.size() > 0) {
                if (is_video == 0) {
                    for (int i = 0; i < files.size(); i++) {
                        params.put("img" + i, files.get(i));
                    }
                } else if (is_video == 1) {
                    params.put("img", files.get(0));
                    params.put("video", files.get(1));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(ApiHttpClient.API_URL2 + "appapi/moments/talk_moments_connect", params, handler);
    }

    //朋友圈、人脉圈删除接口
    public static void deleteCircleInfo(String uid, int cir_type, int com_type, String moments_id, String mc_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("cir_type", RAS.getPublicKeyStrRAS(String.valueOf(cir_type).getBytes()));
        params.put("com_type", RAS.getPublicKeyStrRAS(String.valueOf(com_type).getBytes()));
        params.put("moments_id", RAS.getPublicKeyStrRAS(moments_id.getBytes()));
        params.put("mc_id", RAS.getPublicKeyStrRAS(mc_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "moments/del_moments_connect", params, handler);
    }

    //相册详情
    public static void getAlbumDetailInfo(String uid, int circle_type, String moments_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("circle_type", RAS.getPublicKeyStrRAS(String.valueOf(circle_type).getBytes()));//	圈子类型 1朋友圈 2人脉圈
        params.put("moments_id", RAS.getPublicKeyStrRAS(moments_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/info_detail", params, handler);
    }

    //附近的人
    public static void getNearList(String uid, String lat, String lng, int range, AsyncHttpResponseHandler handler) {

        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("lat", RAS.getPublicKeyStrRAS(lat.getBytes()));
        params.put("lng", RAS.getPublicKeyStrRAS(lng.getBytes()));
        params.put("range", RAS.getPublicKeyStrRAS(String.valueOf(range).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Exploration/nearby_user", params, handler);
    }

    //附近的人---喇叭页面数据
    public static void getTrumpetList(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/horn_data", params, handler);
    }

    //发布粉丝动态
    public static void publishFansDynamicInfo(String uid, int is_video, int type, String content, List<File> files, AsyncHttpResponseHandler handler) {
        publishCircleInfo(uid, null, null, null, is_video, type,0, content, files, null,null ,null,null, handler);
    }

    /**
     * 获取我的群组
     */
    public static void getGroup(String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "friends/get_group_for_user", params, handler);
    }

    /**
     * 获取单个群组的详细信息
     */
    public static void getSingleGroupInfo(String groupid, String my_mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("groupid", RAS.getPublicKeyStrRAS(groupid.getBytes()));
        params.put("my_mobile", RAS.getPublicKeyStrRAS(my_mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "friends/get_group_users", params, handler);
    }


    /**
     * 添加群成员
     */
    public static void addGroupMember(String groupid, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("groupid", RAS.getPublicKeyStrRAS(groupid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "friends/add_group_members", params, handler);
    }


    /**
     * 删除群成员或者退出群组
     */
    public static void delGroupMember(String groupid, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("groupid", RAS.getPublicKeyStrRAS(groupid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "friends/delete_group_members", params, handler);
    }

    /**
     * 解散群租
     */
    public static void delGroup(String groupid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("groupid", RAS.getPublicKeyStrRAS(groupid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "friends/delete_group", params, handler);
    }

    /**
     * 创建群组
     */
    public static void createGroup(String groupname, String desc, String owner, String members, int qroup_type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("groupname", RAS.getPublicKeyStrRAS(groupname.getBytes()));
        params.put("desc", RAS.getPublicKeyStrRAS(desc.getBytes()));
        params.put("owner", RAS.getPublicKeyStrRAS(owner.getBytes()));
        params.put("members", RAS.getPublicKeyStrRAS(members.getBytes()));
        params.put("qroup_type", RAS.getPublicKeyStrRAS(String.valueOf(qroup_type).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "friends/create_chat_group", params, handler);
    }


    /**
     * 创建群组
     */
    public static void setGroupNick(String mobile, String name, String groupid, String is_show_name, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("name", RAS.getPublicKeyStrRAS(name.getBytes()));
        params.put("groupid", RAS.getPublicKeyStrRAS(groupid.getBytes()));
        params.put("is_show_name", RAS.getPublicKeyStrRAS(is_show_name.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/modify_group_member", params, handler);
    }

    /**
     * 关注好友
     */
    public static void attention(String fans_uid, String follow_mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("fans_uid", RAS.getPublicKeyStrRAS(fans_uid.getBytes()));
        params.put("follow_mobile", RAS.getPublicKeyStrRAS(follow_mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Fans/fans", params, handler);
    }

    /**
     * 判断是否关注过好友
     */
    public static void isAttention(String my_uid, String follow_mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("my_uid", RAS.getPublicKeyStrRAS(my_uid.getBytes()));
        params.put("follow_mobile", RAS.getPublicKeyStrRAS(follow_mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "fans/is_follow_with_interest", params, handler);
    }

    //粉丝首页接口（自己的和粉丝的首页通用，传递uid就行）
    public static void getFansHomeList(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Fans/fans_index/", params, handler);
    }


    //粉丝列表--typ=1 我的粉丝列表；2 我关注的粉丝列表
    public static void getFansList(String uid, int type, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("type", RAS.getPublicKeyStrRAS(String.valueOf(type).getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Fans/my_fans/", params, handler);
    }

    //粉丝关注、取消接口
    public static void focusfans(String fans_uid, String follow_uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("fans_uid", RAS.getPublicKeyStrRAS(fans_uid.getBytes()));
        params.put("follow_uid", RAS.getPublicKeyStrRAS(follow_uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Fans/fans", params, handler);
    }

    //获取广告红包
    public static void getAdRedPacket(String uid, int type, String scan_uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("type", RAS.getPublicKeyStrRAS(String.valueOf(type).getBytes()));
        if (!TextUtils.isEmpty(scan_uid)) {
            params.put("scan_uid", RAS.getPublicKeyStrRAS(scan_uid.getBytes()));
        }
        //新接口--appapi/Oneself/get_ad
        //原接口--appapi/Oneself/get_touch
        ApiHttpClient.post(ApiHttpClient.API_URL + "Oneself/get_ad", params, handler);
    }

    //确认红包
    public static void confirmRedPacket(String uid, String t_id, String scan_uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("t_id", RAS.getPublicKeyStrRAS(t_id.getBytes()));
        if (!TextUtils.isEmpty(scan_uid)) {
            params.put("scan_uid", RAS.getPublicKeyStrRAS(scan_uid.getBytes()));
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "Oneself/confirm_touch", params, handler);
    }

    public static void getArticleDetail(String uid, String m_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("m_id", RAS.getPublicKeyStrRAS(m_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Fans/read_more", params, handler);
    }

    public static void sendGroupMessage(String uid, int type, String title, String content, String img, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("type", RAS.getPublicKeyStrRAS(String.valueOf(type).getBytes()));
        if (!TextUtils.isEmpty(title)) {
            params.put("title", RAS.getPublicKeyStrRAS(title.getBytes()));
        }
        if (!TextUtils.isEmpty(content)) {
            params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        }
        if (!TextUtils.isEmpty(img)) {
            try {
                params.put("img", new File(img));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "fans/group_message", params, handler);
    }

    //粉丝群发消息
    public static void sendMessage(String uid, String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "fans/fans_notice", params, handler);
    }

    public static void commentDynamic(String uid, String moments_id, String mc_id, String mc_content, String reply_uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("moments_id", RAS.getPublicKeyStrRAS(moments_id.getBytes()));
        params.put("mc_id", RAS.getPublicKeyStrRAS(mc_id.getBytes()));
        params.put("mc_content", RAS.getPublicKeyStrRAS(mc_content.getBytes()));
        params.put("reply_uid", RAS.getPublicKeyStrRAS(reply_uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "fans/fans_comment", params, handler);
    }

    public static void getDynamicCommentList(String uid, String moments_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("moments_id", RAS.getPublicKeyStrRAS(moments_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "fans/fans_comment_lists", params, handler);
    }

    public static void praiseDyanmicComment(String uid, String mc_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mc_id", RAS.getPublicKeyStrRAS(mc_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "fans/fans_comment_upvote", params, handler);
    }

    //粉丝动态点赞列表
    public static void getDynamicPraiseList(String uid, String moments_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("moments_id", RAS.getPublicKeyStrRAS(moments_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "fans/fans_upvote_detail_list", params, handler);
    }


    //删除粉丝动态
    public static void deleteFansDynamic(String uid, String moments_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("moments_id", RAS.getPublicKeyStrRAS(moments_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "fans/del_fans_moments", params, handler);
    }

    //删除粉丝动态评论
    public static void deleteFansDynamicComment(String uid, String mc_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mc_id", RAS.getPublicKeyStrRAS(mc_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "fans/del_fans_comment", params, handler);
    }

    //许愿星放回
    public static void putbackWishingStar(String uid, String star_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("star_id", star_id);
        ApiHttpClient.post(ApiHttpClient.API_URL + "Oneself/wishing_back", params, handler);
    }

    //许愿星--摘星船记录
    public static void getWishingRecord(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Oneself/wishing_list", params, handler);

    }

    //广告列表
    public static void getAdvertisementList(String uid, int ad_type, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("ad_type", RAS.getPublicKeyStrRAS(String.valueOf(ad_type).getBytes()));//1 撞一撞；2 碰一碰；3 许愿星
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Oneself/touch_list", params, handler);

    }

    //财务报表
    public static void getSheetList(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/financial_statements", params, handler);
    }

    //广告撤销
    public static void backoutAdvertisement(String uid, String t_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("t_id", RAS.getPublicKeyStrRAS(t_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "oneself/touch_recall", params, handler);
    }

    /**
     * 生成订单接口
     *
     * @param uid
     * @param order_price
     * @param pay_type    支付方式 1为支付宝 2为微信 3零钱支付
     * @param indent      1、充值 ；2、购买合约人；3、发红包；4、领取红包；5、退款；6、购买小喇叭；7、三方客转入；8、三方客转出
     * @param handler
     */
    public static void generateOrder(String uid, String body, String order_price, int pay_type, int indent, String indentId,String date, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("order_price", RAS.getPublicKeyStrRAS(order_price.getBytes()));
        params.put("pay_type", RAS.getPublicKeyStrRAS(String.valueOf(pay_type).getBytes()));
        params.put("indent", RAS.getPublicKeyStrRAS(String.valueOf(indent).getBytes()));
        params.put("body", RAS.getPublicKeyStrRAS(body.getBytes()));
        if (indent == 2 || indent == 7) {//被购买的人脉uid
            params.put("con_uid", RAS.getPublicKeyStrRAS(indentId.getBytes()));
        } else if (indent == 3 || indent == 6) {//indent=3 时候是红包广告id；indent=6 时候是喇叭id；
            params.put("object_id", RAS.getPublicKeyStrRAS(indentId.getBytes()));
        } else if (indent == 18) {
            if (!TextUtils.isEmpty(indentId)) {
                String[] arr = indentId.split(",");
                if (arr == null || arr.length < 2) {
                    return;
                }
                params.put("con_uid", RAS.getPublicKeyStrRAS(arr[0].getBytes()));
                params.put("object_id", RAS.getPublicKeyStrRAS(arr[1].getBytes()));
            }
            params.put("is_purchase_debo_coins", RAS.getPublicKeyStrRAS("1".getBytes()));
            params.put("num", RAS.getPublicKeyStrRAS(order_price.getBytes()));
            params.put("regular", RAS.getPublicKeyStrRAS(date.getBytes()));
        }
        String sign = new StringBuilder()
                .append("uid").append("=").append(uid)
                .append("&")
                .append("order_price").append("=").append(order_price)
                .append("&")
                .append("indent").append("=").append(indent)
                .append("&")
                .append("key").append("=").append("4cf083b4865300083800a531b4ba3d04")
                .toString();

        params.put("sign", RAS.getPublicKeyStrRAS(sign.getBytes()));


        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/create_order", params, handler);
    }

    /**
     * 生成订单接口    发红包
     *
     * @param uid
     * @param order_price
     * @param pay_type    支付方式 1为支付宝 2为微信 3零钱支付
     * @param indent      1、充值 ；2、购买合约人；3、发红包；4、领取红包；5、退款；6、购买小喇叭；7、三方客转入；8、三方客转出
     * @param handler
     */
    public static void generateOrder_redPacket(String uid, String body, String order_price, int pay_type, int indent, String indentId, String p_type, String num, String phone, String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("order_price", RAS.getPublicKeyStrRAS(order_price.getBytes()));
        params.put("pay_type", RAS.getPublicKeyStrRAS(String.valueOf(pay_type).getBytes()));
        params.put("indent", RAS.getPublicKeyStrRAS(String.valueOf(indent).getBytes()));
        params.put("body", RAS.getPublicKeyStrRAS(body.getBytes()));
        if (indent == 2 || indent == 7) {//被购买的人脉uid
            params.put("con_uid", RAS.getPublicKeyStrRAS(indentId.getBytes()));
        } else if (indent == 3 || indent == 6) {//indent=3 时候是红包广告id；indent=6 时候是喇叭id；
            params.put("object_id", RAS.getPublicKeyStrRAS(indentId.getBytes()));
        }
        String sign = new StringBuilder()
                .append("uid").append("=").append(uid)
                .append("&")
                .append("order_price").append("=").append(order_price)
                .append("&")
                .append("indent").append("=").append(indent)
                .append("&")
                .append("key").append("=").append("4cf083b4865300083800a531b4ba3d04")
                .toString();

        params.put("sign", RAS.getPublicKeyStrRAS(sign.getBytes()));
        params.put("p_type", RAS.getPublicKeyStrRAS(p_type.getBytes()));
        params.put("num", RAS.getPublicKeyStrRAS(num.getBytes()));
        params.put("phone", RAS.getPublicKeyStrRAS(phone.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/create_order", params, handler);
    }

    /**
     * 生成订单接口    转账
     *
     * @param uid
     * @param order_price
     * @param pay_type    支付方式 1为支付宝 2为微信 3零钱支付
     * @param indent      1、充值 ；2、购买合约人；3、发红包；4、领取红包；5、退款；6、购买小喇叭；7、三方客转入；8、三方客转出
     * @param handler
     */
    public static void generateOrder_transfer(String uid, String body, String order_price, int pay_type, int indent, String indentId, String p_type, String num, String phone, String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("order_price", RAS.getPublicKeyStrRAS(order_price.getBytes()));
        params.put("pay_type", RAS.getPublicKeyStrRAS(String.valueOf(pay_type).getBytes()));
        params.put("indent", RAS.getPublicKeyStrRAS(String.valueOf(indent).getBytes()));
        params.put("body", RAS.getPublicKeyStrRAS(body.getBytes()));
        if (indent == 2 || indent == 7) {//被购买的人脉uid
            params.put("con_uid", RAS.getPublicKeyStrRAS(indentId.getBytes()));
        } else if (indent == 3 || indent == 6) {//indent=3 时候是红包广告id；indent=6 时候是喇叭id；
            params.put("object_id", RAS.getPublicKeyStrRAS(indentId.getBytes()));
        }
        String sign = new StringBuilder()
                .append("uid").append("=").append(uid)
                .append("&")
                .append("order_price").append("=").append(order_price)
                .append("&")
                .append("indent").append("=").append(indent)
                .append("&")
                .append("key").append("=").append("4cf083b4865300083800a531b4ba3d04")
                .toString();

        params.put("sign", RAS.getPublicKeyStrRAS(sign.getBytes()));
        params.put("p_type", RAS.getPublicKeyStrRAS(p_type.getBytes()));
        params.put("num", RAS.getPublicKeyStrRAS(num.getBytes()));
        params.put("phone", RAS.getPublicKeyStrRAS(phone.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        params.put("is_transfer_accounts", RAS.getPublicKeyStrRAS("1".getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/create_order", params, handler);
    }


    //查询余额、积分接口
    public static void getMyCoins(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/get_coins", params, handler);
    }

    public static void getGuaranteeDetailList(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "exploration/guarantee_detail", params, handler);
    }

    public static void confirmGuaranteeDetail(String uid, String tri_id, int request_type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("tri_id", tri_id);
        params.put("request_type", request_type);
        ApiHttpClient.post(ApiHttpClient.API_URL + "exploration/guarantee_operation", params, handler);
    }

    //获取银行卡信息
    public static void getBankcardInfo(String uid, String name, String cardNO, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        //params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("real_name", RAS.getPublicKeyStrRAS(name.getBytes()));
        params.put("bank_account", RAS.getPublicKeyStrRAS(cardNO.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/bank_card_info", params, handler);
    }

    //提交银行卡信息
    public static void submitBankcardInfo(String uid, String code, String username, String cardNO,
                                          String mobile, String bank_name, String bank_card_type,
                                          AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("code", RAS.getPublicKeyStrRAS(code.getBytes()));
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("real_name", RAS.getPublicKeyStrRAS(username.getBytes()));
        params.put("bank_account", RAS.getPublicKeyStrRAS(cardNO.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("bank_name", RAS.getPublicKeyStrRAS(bank_name.getBytes()));
        params.put("bank_card_type", RAS.getPublicKeyStrRAS(bank_card_type.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/add_bank_card", params, handler);
    }

    //获取自己的银行卡列表信息
    public static void getBankcardList(String uid, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/bank_list", params, handler);
    }

    //设置支付密码
    public static void setPayPwd(String uid, String pwd, String re_pwd, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("pwd", RAS.getPublicKeyStrRAS(pwd.getBytes()));
        params.put("re_pwd", RAS.getPublicKeyStrRAS(re_pwd.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/set_password", params, handler);
    }

    //零钱支付
    public static void coinsPay(String uid, String pwd, String order_sn, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("pwd", RAS.getPublicKeyStrRAS(pwd.getBytes()));
        params.put("order_sn", RAS.getPublicKeyStrRAS(order_sn.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/coins_pay", params, handler);
    }

    //零钱支付 聊天发红包
    public static void coinsPay_red(String uid, String pwd, String order_sn, String is_chat_money, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("pwd", RAS.getPublicKeyStrRAS(pwd.getBytes()));
        params.put("order_sn", RAS.getPublicKeyStrRAS(order_sn.getBytes()));
        params.put("is_chat_money", RAS.getPublicKeyStrRAS(is_chat_money.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/coins_pay", params, handler);
    }

    //提现功能
    public static void withdrawalCash(String uid, String pwd, String price, String rate_value, String bank_card, String bank_name, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("pwd", RAS.getPublicKeyStrRAS(pwd.getBytes()));
        params.put("price", RAS.getPublicKeyStrRAS(price.getBytes()));
        params.put("rate_value", RAS.getPublicKeyStrRAS(rate_value.getBytes()));
        params.put("bank_card", RAS.getPublicKeyStrRAS(bank_card.getBytes()));
        params.put("bank_name", RAS.getPublicKeyStrRAS(bank_name.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/withdraw_cash", params, handler);
    }

    //积分兑换率/提现费率
    public static void getRate(String uid, int type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("type", RAS.getPublicKeyStrRAS(String.valueOf(type).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/return_rate", params, handler);
    }

    //解除绑定的银行卡
    public static void unbindBankcard(String uid, String bank_card, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("bank_account", RAS.getPublicKeyStrRAS(bank_card.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/remove_bind_bank", params, handler);
    }

    //积分兑换
    public static void pointsExchange(String uid, String points, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("points", RAS.getPublicKeyStrRAS(points.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/points_exchange", params, handler);
    }

    //附近的人-喇叭喊话
    public static void trumpetShout(String uid, String lat, String lng, String range, String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("lat", RAS.getPublicKeyStrRAS(lat.getBytes()));
        params.put("lng", RAS.getPublicKeyStrRAS(lng.getBytes()));
        params.put("range", RAS.getPublicKeyStrRAS(range.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Exploration/nearby_notice", params, handler);
    }

    //查看历史消息
    public static void historyMessage(int type, String uid, int page, AsyncHttpResponseHandler handler) {
        String url = "";
        if (type == 1) {//评论消息
            url = "user/news_lists";
        } else if (type == 2) {//点赞消息
            url = "user/moments_connection_upvote";
        }
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + url, params, handler);
    }

    //交易记录
    public static void getTradingRecord(String uid,
                                        String mobile,
                                        int page,
                                        int indent,
                                        String start_time,
                                        String end_time,
                                        AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        if (page != 0){
            params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        }

        //1、充值 ；2、购买合约人；3、发红包；4、领取红包；5、退款；6、购买小喇叭；7、三方客转入；8、三方客转出；9、提现；10、积分兑换
        if (indent >= 1 && indent <= 10) {
            params.put("indent", RAS.getPublicKeyStrRAS(String.valueOf(indent).getBytes()));
        }
        if (!TextUtils.isEmpty(start_time)) {
            params.put("start_time", RAS.getPublicKeyStrRAS(start_time.getBytes()));
        }
        if (!TextUtils.isEmpty(end_time)) {
            params.put("end_time", RAS.getPublicKeyStrRAS(end_time.getBytes()));
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/transaction_record", params, handler);
    }

    //交易记录详情
    public static void tradingRecordDetail(String uid, String order_sn,
                                           AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("order_sn", RAS.getPublicKeyStrRAS(order_sn.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/order_details", params, handler);
    }

    public static void resetPayPwd(String uid, String mobile, String code, String pwd,
                                   AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("code", RAS.getPublicKeyStrRAS(code.getBytes()));
        params.put("new_pwd", RAS.getPublicKeyStrRAS(pwd.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/reset_password", params, handler);
    }

    //清除点赞评论提醒
    public static void clearRemind(String uid, int type, AsyncHttpResponseHandler handler) {
        //1、评论；2、点赞
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("type", RAS.getPublicKeyStrRAS(String.valueOf(type).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/empty_comment_upvote", params, handler);
    }

    //广告详情
    public static void getAdvertisementDetail(String uid, String t_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("t_id", RAS.getPublicKeyStrRAS(t_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Oneself/touch_show", params, handler);
    }

    //收红包
    public static void getRedPacket(String uid, String mobile, String t_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("r_id", RAS.getPublicKeyStrRAS(t_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/get_red_packets", params, handler);
    }

    //收转账
    public static void getTransfer(String uid, String mobile, String t_id, String is_transfer_accounts, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("r_id", RAS.getPublicKeyStrRAS(t_id.getBytes()));
        params.put("is_transfer_accounts", is_transfer_accounts);
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/get_red_packets", params, handler);
    }

    //个人红包记录列表
    public static void getRedPacketRecord(String uid, String type, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("type", RAS.getPublicKeyStrRAS(String.valueOf(type).getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/my_redpackert_record", params, handler);
    }

    //获取上传短视频信息   上传地址和上传凭证
    public static void getShotVideoAdress(String title, String filename, String url, String describe, String lat, String lng, String city, String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("title", RAS.getPublicKeyStrRAS(title.getBytes()));
        params.put("filename", RAS.getPublicKeyStrRAS(filename.getBytes()));
        params.put("coverurl", RAS.getPublicKeyStrRAS(url.getBytes()));
        params.put("describe", RAS.getPublicKeyStrRAS(describe.getBytes()));

        params.put("lat", RAS.getPublicKeyStrRAS(lat.getBytes()));
        params.put("lng", RAS.getPublicKeyStrRAS(lng.getBytes()));
        params.put("city", RAS.getPublicKeyStrRAS(city.getBytes()));
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.VIDEO_CERTIFICATE + "application/Common/Common/test.php", params, handler);
    }

    //更新   上传地址和上传凭证
    public static void getNew_ShotVideoAdress(String video_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.VIDEO_CERTIFICATE + "application/Common/Common/refreshResponse.php", params, handler);
    }

    //更新tokon信息
    public static void getShotVideoToken(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        ApiHttpClient.post(ApiHttpClient.VIDEO_CERTIFICATE + "application/Common/Common/server1/sts.php", params, handler);
    }

    //获取播放凭证
    public static void getShotVideoPlayer(String video_id, String follow_uid, String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        params.put("follow_uid", RAS.getPublicKeyStrRAS(follow_uid.getBytes()));
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.VIDEO_CERTIFICATE + "application/Common/Common/VideoPlayAuth.php", params, handler);
    }

    //上传成功之后 调接口
    public static void video_success_notyfy(String video_id, String lat, String lng, String city, String uid, String Title, String Description, File file, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        params.put("lat", RAS.getPublicKeyStrRAS(lat.getBytes()));
        params.put("lng", RAS.getPublicKeyStrRAS(lng.getBytes()));
        params.put("city", RAS.getPublicKeyStrRAS(city.getBytes()));
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));

        params.put("Title", RAS.getPublicKeyStrRAS(Title.getBytes()));
        params.put("Description", RAS.getPublicKeyStrRAS(Description.getBytes()));
        try {
            params.put("img", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ApiHttpClient.post(ApiHttpClient.API_URL + "video/video_success_notyfy", params, handler);
    }

    //获取发现模块的视频列表
    public static void getFoundVideo_list(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/new_find_video", params, handler);
    }

    //获取视频详细内容
    public static void getVideo_Info(String video_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/one_joke_video", params, handler);
    }

    //获取同城模块的视频列表
    public static void getNearbyVideo_list(String uid, int page, String city, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(String.valueOf(uid).getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        params.put("city", RAS.getPublicKeyStrRAS(city.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/joke_video_mycity", params, handler);
    }

    //获取关注模块的视频列表
    public static void getFollowVideo_list(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(String.valueOf(uid).getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/follow_video", params, handler);
    }

    //获取收益列表接口
    public static void getIncomeList(Map<String, String> map, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (map == null) {
            return;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "copartner/get_settle_accounts_list", params, handler);
    }

    //储蓄罐转出
    public static void transferAccounts(String uid, String mobile, String money, String sign, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("money", RAS.getPublicKeyStrRAS(money.getBytes()));
        params.put("sign", RAS.getPublicKeyStrRAS(sign.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/transfer_account_roll_out", params, handler);
    }

    //获取存储罐信息接口
    public static void getMyStorage(String uid, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/my_storage", params, handler);
    }

    //查询别人的位置信息
    public static void sendLocationRequest(String mobile, String other_mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("my_mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("the_other_mobile", RAS.getPublicKeyStrRAS(other_mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "exploration/send_location_request", params, handler);
    }

    //查询实名认证是否通过
    public static void verifyIdentity(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Copartner/check_identity_certification", params, handler);
    }

    //发送经纬度请求
    public static void receiveLocation(Map<String, String> map, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (map == null) {
            return;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.put(entry.getKey(), RAS.getPublicKeyStrRAS(entry.getValue().getBytes()));
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "exploration/return_position_info", params, handler);
    }

    //设置是否打开位置信息
    public static void setLocationInfoIsOpen(String uid, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "exploration/position_switch", params, handler);
    }

    //获取是否打开位置信息
    public static void locationInfoIsOpen(String uid, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "exploration/position_is_open", params, handler);
    }

    //合伙人身份认证接口
    public static void certification(String uid, String name, String number, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("real_name", RAS.getPublicKeyStrRAS(name.getBytes()));
        params.put("id_number", RAS.getPublicKeyStrRAS(number.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "oneself/identity_certification", params, handler);

    }

    //合伙人首页接口
    public static void getTotalLimitCoins(String uid, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "copartner/copartner_index", params, handler);
    }
    //合伙人获取利率
    public static void getRote(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(ApiHttpClient.API_URL + "Debo1/Copartner/get_rate", handler);
    }

    //嘚啵币商城列表（包括自己卖出的列表）
    public static void getCoinsShopList(String uid, int status, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("status", RAS.getPublicKeyStrRAS(String.valueOf(status).getBytes()));//1、上架中；2、已出售
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "copartner/debo_coins_shop_list", params, handler);
    }

    //卖出嘚啵币接口
    public static void sellCoins(String uid, String mobile, String num, String price, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("debo_coins", RAS.getPublicKeyStrRAS(num.getBytes()));
        params.put("money", RAS.getPublicKeyStrRAS(price.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "copartner/sale_debo_coins", params, handler);
    }

    //下架
    public static void soldOutCoins(String uid, String mobile, String debo_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        if (!TextUtils.isEmpty(debo_id)) {
            params.put("debo_id", RAS.getPublicKeyStrRAS(debo_id.getBytes()));
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "copartner/remove_debo_coins_goods", params, handler);
    }

    //合伙人删除交易记录接口
    public static void deleteCoinsTradingRecord(String uid, String mobile, String debo_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        if (!TextUtils.isEmpty(debo_id)) {
            params.put("debo_id", RAS.getPublicKeyStrRAS(debo_id.getBytes()));
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "copartner/del_trans", params, handler);
    }

    //合伙人嘚啵币提现
    public static void withdrawalCoins(String uid, String mobile, String num, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("num", RAS.getPublicKeyStrRAS(num.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "copartner/copartner_withdraw_cash", params, handler);
    }

    //嘚啵币交易记录
    public static void getCoinsTradingRecord(String uid, String mobile, int page, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "copartner/find_debo_coins_transaction", params, handler);
    }

    //段子点赞
    public static void getUpvote(String uid, String video_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/upvote", params, handler);
    }

    //段子评论
    public static void videoComment(String uid, String video_id, String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "video/video_comment", params, handler);
    }

    //段子关注
    public static void videoFollow(String uid, String video_id, String follow_uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        params.put("follow_uid", RAS.getPublicKeyStrRAS(follow_uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/video_follow", params, handler);
    }

    //评论列表接口
    public static void videoCommentList(String video_id, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "video/video_content_list", params, handler);
    }

    //抓一抓退出游戏
    public static void exitZyZ(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post("http://zyz.shangtongyuntian.com/exit.php", params, handler);
    }

    //我的段子列表接口
    public static void myVideoList(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/get_my_videos", params, handler);
    }

    //我的段子删除接口
    public static void myVideoDelete(String uid, String video_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(String.valueOf(uid).getBytes()));
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/delete_joke_video", params, handler);
    }

    /**
     * 发布段子动态
     *
     * @param uid
     * @param content
     * @param handler
     */
    public static void publishParagraphInfo(String uid, List<File> files, String lat, String lng, String city, String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        try {
            if (files != null && files.size() > 0) {
                params.put("img", files.get(0));
                params.put("video", files.get(1));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        params.put("lat", RAS.getPublicKeyStrRAS(lat.getBytes()));
        params.put("lng", RAS.getPublicKeyStrRAS(lng.getBytes()));
        params.put("city", RAS.getPublicKeyStrRAS(city.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        Log.e("apiResponse_upvideo", uid + "&" + content + "&" + lat + "&" + lng + "&" + city + "&" + files.get(0) + "&" + files.get(1) + "^&^" + params.toString());

        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/video_upload", params, handler);
    }

    //钱包
    public static void complaint(String uid, String order_sn, String content, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        params.put("order_sn", RAS.getPublicKeyStrRAS(order_sn.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/feedback", params, handler);
    }

    //签到
    public static void signIn(String uid, String year, String month, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("year", RAS.getPublicKeyStrRAS(year.getBytes()));
        params.put("month", RAS.getPublicKeyStrRAS(month.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/daily_sign_in", params, handler);
    }

    //聊天室列表
    public static void getChatRoomList(String uid, int page, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "user/chatroom_list", params, handler);
    }

    public static void addChatRoom(String uid, String city, String region, String description, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("city", RAS.getPublicKeyStrRAS(city.getBytes()));
        params.put("region", RAS.getPublicKeyStrRAS(region.getBytes()));
        params.put("description", RAS.getPublicKeyStrRAS(description.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/create_custom_chatrooms", params, handler);
    }

    //分享加积分   1、微信；2、微信朋友圈；3、QQ；4、微博
    public static void addJF(String uid, String video_id, String platform_type, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        params.put("platform_type", RAS.getPublicKeyStrRAS(platform_type.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "video/video_share_callback", params, handler);
    }

    //碰聊接口
    public static void getTouchChat(String uid, int sex, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("sex", RAS.getPublicKeyStrRAS(String.valueOf(sex).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Touchchat/select_chat", params, handler);
    }

    //碰聊详情
    public static void getTouchChatComment(String video_id, String uid, int page, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        params.put("page", RAS.getPublicKeyStrRAS(String.valueOf(page).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/one_joke_video", params, handler);
    }

    //碰聊：附近的人
    public static void getTouchChatNearby(String uid, String lat, String lng, int sex, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("lat", RAS.getPublicKeyStrRAS(lat.getBytes()));
        params.put("lng", RAS.getPublicKeyStrRAS(lng.getBytes()));
        params.put("sex", RAS.getPublicKeyStrRAS(String.valueOf(sex).getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Touchchat/nearbyChat", params, handler);
    }

    //撞一撞
    public static void getJumpFriendOrRedPacket(String uid, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Touchchat/bumpChat", params, handler);
    }

    //添加好友、撞一撞
    public static void AddJumpFriend(String uid, String oid, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("oid", RAS.getPublicKeyStrRAS(oid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Touchchat/add_friend", params, handler);
    }

    //备忘录 - 本月事件
    public static void getMonthEvent(String uid, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/month_memorandum", params, handler);
    }

    //备忘录 - 首页某天详细列表
    public static void getDayDetailsEvent(String uid, String time, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("time", RAS.getPublicKeyStrRAS(time.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/list_memorandum", params, handler);
    }

    //备忘录 - 增加备忘录类型
    public static void addMemorandumType(String uid, String m_type, String font_color, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("m_type", RAS.getPublicKeyStrRAS(m_type.getBytes()));
        params.put("font_color", RAS.getPublicKeyStrRAS(font_color.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/add_class", params, handler);
    }

    //备忘录 - 新增备忘录
    public static void addMemorandumDetails(String start_time, String end_time, String uid, String rem_time,
                                            String rem_content, String content, String font_color, String m_type, List<File> img, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("start_time", RAS.getPublicKeyStrRAS(start_time.getBytes()));
        params.put("end_time", RAS.getPublicKeyStrRAS(end_time.getBytes()));
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("rem_time", RAS.getPublicKeyStrRAS(rem_time.getBytes()));
        params.put("rem_content", RAS.getPublicKeyStrRAS(rem_content.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        params.put("font_color", RAS.getPublicKeyStrRAS(font_color.getBytes()));
        if (!m_type.equals("")) {
            params.put("m_type", RAS.getPublicKeyStrRAS(m_type.getBytes()));
        }
        try {
            if (img != null && img.size() > 0) {
                for (int i = 0; i < img.size(); i++) {
                    params.put("img" + (i + 1), img.get(i));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/add_memorandum", params, handler);
    }

    //备忘录 - 修改备忘录
    public static void updMemorandumDetails(String start_time, String mid, String uid, String rem_time,
                                            String rem_content, String content, String font_color, String m_type, List<File> img, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("m_id", RAS.getPublicKeyStrRAS(mid.getBytes()));
        params.put("start_time", RAS.getPublicKeyStrRAS(start_time.getBytes()));
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("rem_time", RAS.getPublicKeyStrRAS(rem_time.getBytes()));
        params.put("rem_content", RAS.getPublicKeyStrRAS(rem_content.getBytes()));
        params.put("content", RAS.getPublicKeyStrRAS(content.getBytes()));
        params.put("font_color", RAS.getPublicKeyStrRAS(font_color.getBytes()));
        if (!m_type.equals("")) {
            params.put("m_type", RAS.getPublicKeyStrRAS(m_type.getBytes()));
        }
        try {
            if (img != null && img.size() > 0) {
                for (int i = 0; i < img.size(); i++) {
                    params.put("img" + (i + 1), img.get(i));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/edit_memorandum", params, handler);
    }

    //备忘录 - 删除某一分类备忘录
    public static void delMemorandumType(String uid, String m_type, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("m_type", RAS.getPublicKeyStrRAS(m_type.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/del_memorandum", params, handler);
    }

    //备忘录 - 删除分类备忘录
    public static void delMemorandumClassify(String uid, String class_id, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("class_id", RAS.getPublicKeyStrRAS(class_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/del_class", params, handler);
    }

    //备忘录 - 删除单条备忘录
    public static void delMemorandumDetails(String uid, String m_id, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("m_id", RAS.getPublicKeyStrRAS(m_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/del_one_memorandum", params, handler);
    }

    //备忘录 - 添加提醒
    public static void addRemind(String uid, String m_id, String rem_time, String rem_content, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("m_id", RAS.getPublicKeyStrRAS(m_id.getBytes()));
        params.put("rem_time", RAS.getPublicKeyStrRAS(rem_time.getBytes()));
        params.put("rem_content", RAS.getPublicKeyStrRAS(rem_content.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/add_remind", params, handler);
    }

    //备忘录 - 备忘录详情
    public static void getMemorandumDetails(String uid, String m_id, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("m_id", RAS.getPublicKeyStrRAS(m_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/one_mem", params, handler);
    }

    //备忘录 - 主动完成事件
    public static void getMemorandumComplete(String uid, String m_id, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("m_id", RAS.getPublicKeyStrRAS(m_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/complete_event", params, handler);
    }

    //备忘录 - 获取分类
    public static void getClassify(String uid, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/get_class", params, handler);
    }

    //备忘录  - 获取分类详情数据
    public static void getClassifyInfo(String uid, String class_id, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("class_id", RAS.getPublicKeyStrRAS(class_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/one_class_info", params, handler);
    }

    //备忘录  - 获取分类详情数据（完成）
    public static void getClassifyInfoDone(String uid, String class_id, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("class_id", RAS.getPublicKeyStrRAS(class_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/one_class_info_done", params, handler);
    }

    //备忘录  - 获取分类详情数据（未完成）
    public static void getClassifyInfoUndo(String uid, String class_id, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("class_id", RAS.getPublicKeyStrRAS(class_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "memorandum/one_class_info_undo", params, handler);
    }

    //送礼物 - 单聊
    public static void sendGift(String uid, String oid, String pay_count, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("oid", RAS.getPublicKeyStrRAS(oid.getBytes()));
        params.put("pay_count", RAS.getPublicKeyStrRAS(pay_count.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/pay_gift", params, handler);
    }

    //送礼物 - 群聊
    public static void sendGroupGift(String uid, String q_uid, Object[] o_uid, String pay_count, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("q_uid", RAS.getPublicKeyStrRAS(q_uid.getBytes()));
        if (null != o_uid && o_uid.length > 0) {
            params.put("o_uid", RAS.getPublicKeyStrRAS(Arrays.toString(o_uid).getBytes()));
        }
        params.put("pay_count", RAS.getPublicKeyStrRAS(pay_count.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/pay_group_gift", params, handler);
    }

    //获取余额
    public static void getBalance(String uid, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/get_mycoins", params, handler);
    }


    /**
     * 查看用户段子信息
     *
     * @param uid
     * @param f_uid
     */
    public static void getUserVideo(String uid, String f_uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("f_uid", RAS.getPublicKeyStrRAS(f_uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/get_user_videos", params, handler);
    }

    /**
     * 段子播放页接口
     *
     * @param uid      用户id
     * @param video_id 段子id
     */
    public static void getVideoDetails(String uid, String video_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("video_id", RAS.getPublicKeyStrRAS(video_id.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Video/play_video", params, handler);
    }

    /**
     * 嘚啵，判断是人脉还是好友
     *
     * @param uid    用户id
     * @param mobile 会话列表手机号
     */
    public static void getUserState(String uid, String mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/user_state", params, handler);
    }

    /**
     * 可能认识的人
     */

    public static void getKnowUser(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/know_user", params, handler);
    }

    /**
     * 手机联系人
     */

    public static void getMobileUser(String uid, String[] mobile, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        params.put("mobile", mobile);
        ApiHttpClient.post(ApiHttpClient.API_URL + "User/mobile_user", params, handler);
    }


    /**
     * 碰聊接口
     */
    public static void getTouchChat2(String uid, ApiResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(uid.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "touchchat/rand_user", params, handler);
    }

    /**
     * 设置朋友圈权限
     */
    public static void setMomentsAuthority(String my_uid, String f_uid, String circle_state1, String circle_state2,String user_type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(my_uid.getBytes()));

        params.put(circle_state1, RAS.getPublicKeyStrRAS(circle_state2.getBytes()));
        //params.put("circle_state2", RAS.getPublicKeyStrRAS(circle_state2.getBytes()));
      if(user_type.equals("2")){
            Log.i("Api","...........user_type.equals(2)");
            params.put("c_uid", RAS.getPublicKeyStrRAS(f_uid.getBytes()));
            ApiHttpClient.post(ApiHttpClient.API_URL + "connection/set_con_authority", params, handler);
        }else{
            Log.i("Api","...........else");
            params.put("f_uid", RAS.getPublicKeyStrRAS(f_uid.getBytes()));
            ApiHttpClient.post(ApiHttpClient.API_URL + "moments/moments_authority", params, handler);
        }
    }

    /**
     * 设置黑名单
     */
    public static void setBlacklist(String my_uid, String f_uid, String blacklist, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(my_uid.getBytes()));
        params.put("f_uid", RAS.getPublicKeyStrRAS(f_uid.getBytes()));
        params.put("blacklist", RAS.getPublicKeyStrRAS(blacklist.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Friends/blacklist", params, handler);
    }
    /**
     * 获取红包状态
     */
    public static void getRedEnvelope(String my_uid, String mobile, String redId,String packets_type, AsyncHttpResponseHandler handler) {
        Log.i("Api","............id = "+my_uid+"    "+mobile+"    "+redId);
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(my_uid.getBytes()));
        params.put("r_id", RAS.getPublicKeyStrRAS(redId.getBytes()));
        params.put("f_id", RAS.getPublicKeyStrRAS(mobile.getBytes()));
        if (!packets_type.equals("")){
            params.put("packets_type", RAS.getPublicKeyStrRAS(packets_type.getBytes()));
        }
        ApiHttpClient.post(ApiHttpClient.API_URL + "Pay/is_first_get", params, handler);
    }
    /**
     *留言
     */
    public static void setPackets(String my_uid, String r_uid,String leave_word, AsyncHttpResponseHandler handler) {
        Log.i("Api","............id = "+my_uid+"    "+leave_word+"    "+r_uid);
        RequestParams params = new RequestParams();
        params.put("uid", RAS.getPublicKeyStrRAS(my_uid.getBytes()));
        params.put("r_id", RAS.getPublicKeyStrRAS(r_uid.getBytes()));
        params.put("leave_word", RAS.getPublicKeyStrRAS(leave_word.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "Pay/red_packets_leave_word", params, handler);
    }
    /**
     *微信支付完成支付后请求
     */
    public static void setsearchWxpayResult(String trade_no, AsyncHttpResponseHandler handler) {
        Log.i("Api","............trade_no = "+trade_no);
        RequestParams params = new RequestParams();
        params.put("trade_no", RAS.getPublicKeyStrRAS(trade_no.getBytes()));
        ApiHttpClient.post(ApiHttpClient.API_URL + "pay/searchWxpayResult", params, handler);
    }
    /**
     *微信支付完成支付后请求
     */
    public static void android_states(AsyncHttpResponseHandler handler) {
        ApiHttpClient.get(ApiHttpClient.API_URL + "Debo1/Copartner/android_states", handler);
    }

}
