package com.qcwl.debo.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.qcwl.debo.MainActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.http.Constants;
import com.qcwl.debo.model.GroupBean;
import com.qcwl.debo.model.GroupInfoBean;
import com.qcwl.debo.presenterInf.GroupPresenterInf;
import com.qcwl.debo.utils.NetWorkUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.List;

import rx.Observer;

/**
 * Created by Administrator on 2017/7/14.
 */

public class GroupPresenter implements GroupPresenterInf.HandleData {
    private GroupPresenterInf groupPresenterInf;
    private ConversationSqlite cs;
    private SQLiteDatabase db;
    private String sql = "delete from groupList";

    public GroupPresenter(GroupPresenterInf groupPresenterInf) {
        this.groupPresenterInf = groupPresenterInf;
    }


    @Override
    public void getGroup(final Context context, String mobile) {
        if (context == null) {
            return;
        }
        if (NetWorkUtils.isConnected(context)) {
            Api.getGroup(mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(final ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        final List<GroupBean> list = JSON.parseArray(apiResponse.getData(), GroupBean.class);
                        //将联系人插入数据库

                        new RxPermissions((Activity) context)
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
                                            //cs = ConversationSqlite.getInstance(context);//new ConversationSqlite(context);
                                            cs = new ConversationSqlite(context);
                                            db = cs.getWritableDatabase();
                                            new Thread() {
                                                public void run() {

                                                    try {
                                                        db.execSQL(sql);
                                                        for (GroupBean gb : list) {
                                                            db.execSQL("insert into groupList(uid,name,headsmall,is_show_name,chatType) values (?,?,?,?,?)", new String[]{gb.getGroupid(), gb.getGroupname(), gb.getG_avatar(), gb.getIs_show_name(), gb.getQroup_type()});
                                                        }
//                                                        db.close();
//                                                        cs.close();
                                                    } catch (SQLException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }.start();
                                            groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), list);
                                        } else {
                                            Toast.makeText(context, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                    } else {
                        groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    groupPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            groupPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void createGroup(Context context, String groupname, String desc, String owner, String members, int qroup_type) {
        if (NetWorkUtils.isConnected(context)) {
            Api.createGroup(groupname, desc, owner, members, qroup_type, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    groupPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            groupPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void getSingleGroupInfo(Context context, String groupid, String my_mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getSingleGroupInfo(groupid, my_mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        List<GroupInfoBean> list = JSON.parseArray(apiResponse.getData(), GroupInfoBean.class);
                        groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), list.get(0));
                    } else {
                        groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    groupPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            groupPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void addGroupMember(Context context, String groupid, String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.addGroupMember(groupid, mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    groupPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            groupPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void delGroupMember(Context context, String groupid, String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.delGroupMember(groupid, mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        groupPresenterInf.getResult(apiResponse.getCode(), "退出群组", null);
                    } else {
                        groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    groupPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            groupPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void delGroup(final Context context, String groupid) {
        if (NetWorkUtils.isConnected(context)) {
            Api.delGroup(groupid, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        groupPresenterInf.getResult(apiResponse.getCode(), "解散群组", null);
                    } else {
                        groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    groupPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            groupPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void setGroupNick(Context context, String mobile, String name, String groupid, String is_show_name) {
        if (NetWorkUtils.isConnected(context)) {
            Api.setGroupNick(mobile, name, groupid, is_show_name, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        groupPresenterInf.getResult(apiResponse.getCode(), "设置完成", null);
                    } else {
                        groupPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    groupPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            groupPresenterInf.getResult(-1, "网络异常", null);
        }

    }


}
