package com.qcwl.debo.presenter;

import android.Manifest;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.LoginBean;
import com.qcwl.debo.presenterInf.LoginPresenterInf;
import com.qcwl.debo.ui.login.LoginActivity;
import com.qcwl.debo.utils.NetWorkUtils;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.widget.DemoHelper;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.Map;

import rx.Observer;

/**
 * Created by Administrator on 2017/7/14.
 */

public class LoginPresenter implements LoginPresenterInf.HandleData {
    private LoginPresenterInf loginPresenterInf;
    private SPUtil sp;
    private ConversationSqlite cs;
    private SQLiteDatabase db;
    private String user, pwd;
    private String TAG = "LoginPresenter";
    public LoginPresenter(LoginPresenterInf loginPresenterInf) {
        this.loginPresenterInf = loginPresenterInf;
    }

    @Override
    public void login(final Context context, final Map<String, String> map) {
        if (!NetWorkUtils.isConnected(context)) {
            return;
        }
        user = map.get("mobile");
        pwd = map.get("password");
        for (String keys : map.keySet()) {
            if (keys == null||keys.equals(""))
                continue;
            String vs = map.get(keys);
            if (vs!=null&&!vs.equals("")) {
                String vras = RAS.getPublicKeyStrRAS(vs.getBytes());
                map.put(keys, vras);
            }
        }
        Api.Login(map, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                // String user = map.get("mobile");
                //String pwd = map.get("password");
                Log.i(TAG,".....SUCCESS="+apiResponse.toString()+"              "+user);
                handleLoginResult(context, user, pwd, apiResponse);
            }
        });
    }

    @Override
    public void login(final Context context, final String user, final String pwd) {
        if (NetWorkUtils.isConnected(context)) {
            Api.Login(user, pwd, "", "", new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    Log.i(TAG,".....onSuccess="+apiResponse.toString());
                    handleLoginResult(context, user, pwd, apiResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.i(TAG,".....onFailure");
                    loginPresenterInf.getResult(-1, "服务器访问失败", null);
                }

                @Override
                public void onFailure(String errMessage) {
                    Log.i(TAG,".....onFailure");
                    loginPresenterInf.getResult(-1, errMessage, null);
                }
            });

        } else {
            loginPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    private void handleLoginResult(final Context context, final String user, final String pwd, ApiResponse apiResponse) {
        if (apiResponse.getCode() == 0) {
            final LoginBean loginBean = JSON.parseObject(apiResponse.getData(), LoginBean.class);
            DemoHelper.getInstance().setCurrentUserName(loginBean.getMobile());
            sp = SPUtil.getInstance(context);
            sp.setString("uid", loginBean.getId());
            sp.setString("phone", loginBean.getMobile());
            sp.setString("name", loginBean.getUser_nickname());
            sp.setString("headsmall", loginBean.getAvatar());
            sp.setString("sex", loginBean.getSex());
            sp.setString("sign", loginBean.getSignature());
            sp.setString("qr_code", loginBean.getQrcode());
            sp.setString("address", loginBean.getCity() + " " + loginBean.getArea());
            sp.setInt("is_set_pay_pwd", loginBean.getIs_set_pay_pwd());
            sp.setInt("is_first_login", loginBean.getIs_first());
            sp.setString("is_con_set_right", loginBean.getIs_con_set_right());
            sp.setString("user", loginBean.getMobile());
            sp.setString("city", loginBean.getCity());
            // 登录环信
            EMClient.getInstance().login(user, "88888888", new EMCallBack() {
                @Override
                public void onSuccess() {
                    ((LoginActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EMClient.getInstance().groupManager().loadAllGroups();
                            EMClient.getInstance().chatManager().loadAllConversations();
                            sp.setIsLogin(true);
                            new RxPermissions((LoginActivity) context)
                                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .subscribe(new Observer<Boolean>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(Boolean granted) {
                                            if (granted) {
                                                //cs = ConversationSqlite.getInstance(context);
                                                cs = new ConversationSqlite(context);
                                                db = cs.getWritableDatabase();
                                                if (!db.isOpen()) {
                                                    return;
                                                }
                                                db.execSQL("insert into person(uid,name,headsmall,phone) values (?,?,?,?)", new String[]{sp.getString("uid"), sp.getString("name"), sp.getString("headsmall"), sp.getString("phone")});
                                                db.close();
                                            } else {
                                                Toast.makeText(context, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            loginPresenterInf.getResult(0, "登录成功", null);
//                                    JPushInterface.setAlias(LoginActivity.this, lb.getMobile(), new TagAliasCallback() {
//                                        @Override
//                                        public void gotResult(int i, String s, Set<String> set) {
//                                        }
//                                    });
                        }
                    });


                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(int code, final String error) {
                    ((LoginActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginPresenterInf.getResult(-1, error, null);
                        }
                    });
                }
            });
        } else {
            loginPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
        }
    }

}
