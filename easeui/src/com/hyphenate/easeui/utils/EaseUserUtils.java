package com.hyphenate.easeui.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.hyphenate.easeui.sqlite.StrangerBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class EaseUserUtils {

    static EaseUserProfileProvider userProvider;
    //    static ConversationSqlite sqlite;
//    static SQLiteDatabase db;
    static String nickname, headsmall, remark;
    private static Cursor cursor;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     *
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null)
            return userProvider.getUser(username);

        return null;
    }


    /**
     * set user's nickname
     */
    public static void setUserNick(Context context, String username, TextView textView, ImageView imageView) {
        try {
            if (context == null) {
                return;
            }
//            if ("系统消息".equals(username.substring(0, 4))) {
//                Glide.with(context).load(R.drawable.icon_system_message).into(imageView);
//                textView.setText("系统消息");
//            } else if ("人脉邀请".equals(username.substring(0, 4))) {
//                Glide.with(context).load(R.drawable.heyuerenmai).into(imageView);
//                textView.setText("人脉邀请");
//            } else if ("三方客邀请".equals(username.substring(0, 5))) {
//                Glide.with(context).load(R.drawable.ic_trading_guarantee).into(imageView);
//                textView.setText("三方客邀请");
//            } else if ("三方客完成申请".equals(username.substring(0, 7))) {
//                Glide.with(context).load(R.drawable.ic_trading_guarantee).into(imageView);
//                textView.setText("三方客完成申请");
//            } else if ("三方客撤销申请".equals(username.substring(0, 7))) {
//                Glide.with(context).load(R.drawable.ic_trading_guarantee).into(imageView);
//                textView.setText("三方客撤销申请");
//            }
            if (username == null) {
                return;
            }
            if (username.contains("系统消息")) {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(context,R.drawable.icon_system_message,imageView);
                   // Glide.with(context).load(R.drawable.icon_system_message).into(imageView);
                }
                textView.setText("系统消息");
            } else if (username.contains("人脉邀请")) {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(context,R.drawable.heyuerenmai,imageView);
                   // Glide.with(context).load(R.drawable.heyuerenmai).into(imageView);
                }
                textView.setText("人脉邀请");
            } else if (username.contains("三方客邀请")) {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(context,R.drawable.ic_trading_guarantee,imageView);
                    //Glide.with(context).load(R.drawable.ic_trading_guarantee).into(imageView);
                }
                textView.setText("三方客邀请");
            } else if (username.contains("三方客完成申请")) {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(context,R.drawable.ic_trading_guarantee,imageView);
                   // Glide.with(context).load(R.drawable.ic_trading_guarantee).into(imageView);
                }
                textView.setText("三方客完成申请");
            } else if (username.contains("三方客撤销申请")) {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(context,R.drawable.ic_trading_guarantee,imageView);
                    //Glide.with(context).load(R.drawable.ic_trading_guarantee).into(imageView);
                }
                textView.setText("三方客撤销申请");
            } else if (username.contains("邀请注册送积分")) {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(context,R.drawable.ic_trading_guarantee,imageView);
                    //Glide.with(context).load(R.drawable.ic_trading_guarantee).into(imageView);
                }
                textView.setText("邀请注册送积分");
            } else {
                ConversationSqlite sqlite = ConversationSqlite.getInstance(context);
                SQLiteDatabase db = sqlite.getReadableDatabase();
                if (username.equals(EMClient.getInstance().getCurrentUser())) {
                    cursor = db.rawQuery("select * from person where phone='"
                            + username + "'", null);
                } else {
                    cursor = db.rawQuery("select * from conversation where phone='"
                            + username + "'", null);
                }
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        nickname = cursor.getString(2);
                        headsmall = cursor.getString(3);
                        remark = cursor.getString(5);
                    }
                    if (textView != null) {
                        if (TextUtils.isEmpty(remark)) {
                            if (TextUtils.isEmpty(nickname)) {
                                textView.setText(username);
                            } else {
                                textView.setText(nickname);
                            }
                        } else {
                            textView.setText(remark);
                        }

                    }
                    if (imageView != null) {
                        if (TextUtils.isEmpty(headsmall)) {
                            if (Util.isOnMainThread()) {
                                ImgUtil.setGlideHead(context,R.mipmap.head,imageView);
                               // Glide.with(context).load(R.drawable.head).into(imageView);
                            }
                            //   Glide.with(context).load(R.drawable.head).bitmapTransform(new RoundedCornersTransformation(context,5,0)).into(imageView);
                        } else {
                            if (Util.isOnMainThread()) {
                                ImgUtil.setGlideHead(context,headsmall,imageView);
                               // Glide.with(context).load(headsmall).into(imageView);
                            }
                            //  Glide.with(context).load(headsmall).bitmapTransform(new RoundedCornersTransformation(context,5,0)).into(imageView);
                        }
                        cursor.close();
                        db.close();
                        sqlite.close();
                    }
                } else {
                    //            cursor = db.rawQuery("select * from stranger where phone='"
                    //                    + username + "'", null);
                    //            if (cursor.getCount() > 0) {
                    //                while (cursor.moveToNext()) {
                    //                    nickname = cursor.getString(2);
                    //                }
                    //            }else {
                    saveStrager(username, textView, imageView, context);
                    //            }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int checkIsFriend(Context context, String username) {
        ConversationSqlite sqlite = ConversationSqlite.getInstance(context);
        SQLiteDatabase db = sqlite.getReadableDatabase();

        cursor = db.rawQuery("select * from conversation where phone='" + username + "'", null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }


    private static void saveStrager(final String username, final TextView textView, final ImageView imageView, final Context context) {
        Log.i("EaseUserUtils","saveStrager="+username+"    "+context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("phone", ""));
        Map<String, String> map = new HashMap<>();
        map.put("mobile", RAS.getPublicKeyStrRAS(username.getBytes()));
        map.put("my_mobile", RAS.getPublicKeyStrRAS(context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("phone", "").getBytes()));
        OkHttpUtils.post().params(map).url("http://debo.shangtongyuntian.com/index.php/Appapi/User/get_user_info").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("EaseUserUtils","response="+response);
                ConversationSqlite sqlite = ConversationSqlite.getInstance(context);
                SQLiteDatabase db = sqlite.getReadableDatabase();
                if (!db.isOpen()) {
                    return;
                }
                StrangerBean s = JSON.parseObject(response, StrangerBean.class);
                if (s.getCode() == 0) {
                    StrangerBean.Datas datas = s.getData();
                    nickname = datas.getUser_nickname();
                    headsmall = datas.getAvatar();
                    if (imageView != null) {
                        Context context = imageView.getContext();
                        if (TextUtils.isEmpty(headsmall)) {
                            if (Util.isOnMainThread()) {
                                ImgUtil.setGlideHead(context, R.drawable.head, imageView);
                                //Glide.with(context).load(R.drawable.head).into(imageView);
                            }
                            // Glide.with(context).load(R.drawable.head).bitmapTransform(new RoundedCornersTransformation(context,5,0)).into(imageView);
                        } else {
                            if (Util.isOnMainThread()) {
                                ImgUtil.setGlideHead(context, headsmall, imageView);
                                // Glide.with(context).load(headsmall) .into(imageView);
                                //    Glide.with(context).load(headsmall).bitmapTransform(new RoundedCornersTransformation(context,5,0)).into(imageView);
                            }
                        }
                    }
                    if (textView != null) {
                        if (TextUtils.isEmpty(nickname)) {
                            textView.setText(username);
                        } else {
                            textView.setText(nickname);
                        }
                    }

                    cursor = db.rawQuery("select * from stranger where phone='"
                            + username + "'", null);
                    if (cursor.getCount() > 0) {
                        db.execSQL("update stranger set name=? ,headsmall=? where phone=" + username, new Object[]{datas.getUser_nickname(), datas.getAvatar()});
                    } else {
                        db.execSQL("insert into stranger(uid,name,headsmall,phone) values (?,?,?,?)", new String[]{datas.getId(), datas.getUser_nickname(), datas.getAvatar(), datas.getMobile()});
                    }
                    cursor.close();
                    db.close();
                    sqlite.close();
                } else {
                    if (Util.isOnMainThread()) {
                        ImgUtil.setGlideHead(context, R.drawable.head, imageView);
                       // Glide.with(context).load(R.drawable.head).into(imageView);
                    }
                    //  Glide.with(context).load(headsmall).bitmapTransform(new RoundedCornersTransformation(context,5,0)).into(imageView);
                }
            }
        });
    }

    public static String[] getUserInfoArray(final Context context, final String mobile) {

        final String[] array = new String[]{"", ""};
        {
            final ConversationSqlite sqlite = ConversationSqlite.getInstance(context);
            final SQLiteDatabase db = sqlite.getReadableDatabase();
            if (!db.isOpen()) {
                return array;
            }
            if (mobile.equals(EMClient.getInstance().getCurrentUser())) {
                cursor = db.rawQuery("select * from person where phone='"
                        + mobile + "'", null);
            } else {
                cursor = db.rawQuery("select * from conversation where phone='"
                        + mobile + "'", null);
            }
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    nickname = cursor.getString(2);
                    headsmall = cursor.getString(3);
                    remark = cursor.getString(5);
                    array[0] = nickname;
                    array[1] = headsmall;
                }
            } else {
                final Map<String, String> map = new HashMap<>();
               /* map.put("mobile", mobile);
                map.put("my_mobile", context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("phone", ""));*/
                map.put("mobile", RAS.getPublicKeyStrRAS(mobile.getBytes()));
                map.put("my_mobile", RAS.getPublicKeyStrRAS(context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("phone", "").getBytes()));
                OkHttpUtils
                        .post()
                        .params(map)
                        .url("http://debo.shangtongyuntian.com/index.php/Appapi/User/get_user_info")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                ConversationSqlite sqlite = ConversationSqlite.getInstance(context);
                                SQLiteDatabase db = sqlite.getReadableDatabase();
                                StrangerBean s = JSON.parseObject(response, StrangerBean.class);
                                if (TextUtils.isEmpty(response) || s == null) {
                                    return;
                                }
                                if (s.getCode() != 0) {
                                    return;
                                }
                                StrangerBean.Datas datas = s.getData();
                                if (datas == null) {
                                    return;
                                }
                                nickname = datas.getUser_nickname();
                                headsmall = datas.getAvatar();
                                array[0] = nickname;
                                array[1] = headsmall;
                                cursor = db.rawQuery("select * from stranger where phone='"
                                        + mobile + "'", null);
                                if (cursor.getCount() > 0) {
                                    db.execSQL("update stranger set name=? ,headsmall=? where phone=" + mobile, new Object[]{datas.getUser_nickname(), datas.getAvatar()});
                                } else {
                                    db.execSQL("insert into stranger(uid,name,headsmall,phone) values (?,?,?,?)", new String[]{datas.getId(), datas.getUser_nickname(), datas.getAvatar(), datas.getMobile()});
                                }
                                cursor.close();
                                db.close();
                                sqlite.close();

                            }
                        });
            }
        }

        return array;
    }

}
