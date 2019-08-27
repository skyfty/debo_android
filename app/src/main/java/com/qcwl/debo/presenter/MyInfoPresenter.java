package com.qcwl.debo.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.http.Constants;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.model.RenMaiZongLanBean;
import com.qcwl.debo.model.SettingBean;
import com.qcwl.debo.presenterInf.MyInfoPresenterInf;
import com.qcwl.debo.ui.found.fans.bean.FansBean;
import com.qcwl.debo.utils.NetWorkUtils;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.SPUtil;

import org.apache.http.Header;
import org.json.JSONArray;

import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/14.
 */

public class MyInfoPresenter implements MyInfoPresenterInf.HandleData {
    private MyInfoPresenterInf myInfoPresenterInf;


    public MyInfoPresenter(MyInfoPresenterInf myInfoPresenterInf) {
        this.myInfoPresenterInf = myInfoPresenterInf;
    }


    @Override
    public void getUserInformation(Context context, String my_mobile, String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getUserInformation(my_mobile, mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        ContactsBean cb = JSON.parseObject(apiResponse.getData(), ContactsBean.class);
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), cb);
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void getUserInformation1(Context context,String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getUserInfo(mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        Log.i("MyInfoPresenter",".............getUserInformation1="+apiResponse.getData());
                        ContactsBean cb = JSON.parseObject(apiResponse.getData(), ContactsBean.class);
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), cb);
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }


    @Override
    public void editUserInformation(final Context context, String uid, String user_nickname, String address, String sex, String province, String city, String area, String signature, File avatar) {
        if (NetWorkUtils.isConnected(context)) {
            Api.editUserInformation(uid, user_nickname, address, sex, province, city, area, signature, avatar, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        final ContactsBean cb = JSON.parseObject(apiResponse.getData(), ContactsBean.class);
                        SPUtil sp = SPUtil.getInstance(context);
                        sp.setString("name", cb.getUser_nickname());
                        sp.setString("headsmall", cb.getAvatar());
                        sp.setString("sign", cb.getSignature());
                        sp.setString("city", cb.getCity());
                        sp.setString("sex", cb.getSex());
                        final ConversationSqlite sqlite = ConversationSqlite.getInstance(context);
                        final SQLiteDatabase db = sqlite.getWritableDatabase();
                        if (!db.isOpen()) {
                            return;
                        }
                        new Thread() {
                            @Override
                            public void run() {
                                db.execSQL("update person set name=? ,headsmall=? where phone=" + cb.getMobile(), new Object[]{cb.getUser_nickname(), cb.getAvatar()});
                                db.close();
                                sqlite.close();
                            }
                        }.start();

                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void editUserInformation(final Context context, Map<String, String> map, File avatar) {
        if (!NetWorkUtils.isConnected(context)) {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
            return;
        }

        Api.editUserInformation(map, avatar, new ApiResponseHandler(context) {

            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                    final ContactsBean cb = JSON.parseObject(apiResponse.getData(), ContactsBean.class);
                    SPUtil sp = SPUtil.getInstance(context);
                    sp.setString("name", cb.getUser_nickname());
                    sp.setString("headsmall", cb.getAvatar());
                    sp.setString("sign", cb.getSignature());
                    final ConversationSqlite sqlite = ConversationSqlite.getInstance(context);
                    final SQLiteDatabase db = sqlite.getWritableDatabase();
                    if (!db.isOpen()) {
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            db.execSQL("update person set name=? ,headsmall=? where phone=" + cb.getMobile(), new Object[]{cb.getUser_nickname(), cb.getAvatar()});
                            db.close();
                            sqlite.close();
                        }
                    }.start();

                    myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                }
            }
        });
    }

    @Override
    public void getRenMaiInfo(Context context, String uid) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getRenMaiInfo(uid, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        RenMaiZongLanBean rb = JSON.parseObject(apiResponse.getData(), RenMaiZongLanBean.class);
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), rb);
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void editRenMaiSetting(Context context, String uid, String conn_type, String operator, String pur_price, String day_num) {
        if (NetWorkUtils.isConnected(context)) {
            Api.editRenMaiSetting(uid, conn_type, operator, pur_price, day_num, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void getRenMaiSetting(Context context, String uid) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getRenMaiSetting(uid, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        SettingBean settingBean = JSON.parseObject(apiResponse.getData(), SettingBean.class);
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), settingBean);
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void commitFeedback(Context context, String uid, String content) {
        if (NetWorkUtils.isConnected(context)) {
            Api.commitFeedback(uid, content, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void modifyPwd(Context context, String uid, String old_pwd, String new_pwd, String confirm_pwd) {
        if (NetWorkUtils.isConnected(context)) {
            Api.modifyPwd(uid, old_pwd, new_pwd, confirm_pwd, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void attention(Context context, String fans_uid, String follow_mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.attention(fans_uid, follow_mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), apiResponse.getData());
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void isAttention(Context context, String my_uid, String follow_mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.isAttention(my_uid, follow_mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        FansBean f = JSON.parseObject(apiResponse.getData(), FansBean.class);
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), f);
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });
        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }


    @Override
    public void setMomentsAuthority(Context context, String my_uid, String f_uid,String circle_state1,String circle_state2) {
        if (NetWorkUtils.isConnected(context)) {
            Api.setMomentsAuthority(my_uid, f_uid,circle_state1,circle_state2,"", new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        FansBean f = JSON.parseObject(apiResponse.getData(), FansBean.class);
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), f);
                    } else {
                        myInfoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myInfoPresenterInf.getResult(-1, "请求超时", null);
                }
            });
        } else {
            myInfoPresenterInf.getResult(-1, "网络异常", null);
        }
    }

}
