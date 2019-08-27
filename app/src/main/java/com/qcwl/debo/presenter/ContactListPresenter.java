package com.qcwl.debo.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.http.Constants;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.model.RenMaiBean;
import com.qcwl.debo.model.SearchRenMaiBean;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.utils.NetWorkUtils;
import com.qcwl.debo.utils.PinYinUtils;
import com.qcwl.debo.widget.sortlistview.PinyinComparator;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observer;

/**
 * Created by Administrator on 2017/7/14.
 */

public class ContactListPresenter implements ContactListPresenterInf.HandleData {
    private ContactListPresenterInf contactListPresenter;
    private ConversationSqlite cs;
    private SQLiteDatabase db;
    private List<ContactsBean> cb;
    private PinyinComparator pinyinComparator = new PinyinComparator();
    private String sortString;
    private String sql = "delete from conversation";
    private String renmai = "delete from renmai";
    private List<ContactsBean> SourceDateList;

    public ContactListPresenter(ContactListPresenterInf contactListPresenter) {
        this.contactListPresenter = contactListPresenter;
    }

    public void setList(List<ContactsBean> SourceDateList) {
        this.SourceDateList = SourceDateList;
    }

    //获取联系人列表
    @Override
    public synchronized void getContactList(final Context context, final String uid, final List<ContactsBean> SourceDateList) {
        this.SourceDateList = SourceDateList;
        //cs = ConversationSqlite.getInstance(context);//new ConversationSqlite(context);
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

                            if (NetWorkUtils.isConnected(context)) {
                                Api.getContacts(uid, new ApiResponseHandler(context) {

                                    @Override
                                    public void onSuccess(ApiResponse apiResponse) {
                                        SourceDateList.clear();
                                        if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                                            Log.i("ContactListPresenter","........getContacts="+apiResponse.getData());
                                            cb = JSON.parseArray(apiResponse.getData(), ContactsBean.class);
                                            SourceDateList.addAll(filledData(cb));
                                            // 根据a-z进行排序源数据
                                            Collections.sort(SourceDateList, pinyinComparator);
                                            if (cs == null){
                                                cs = new ConversationSqlite(context);
                                            }

                                            db = cs.getWritableDatabase();
                                            //将联系人插入数据库
                                            new Thread() {
                                                public void run() {

                                                    try {
                                                        db.execSQL(sql);
                                                        for (ContactsBean c : cb) {
                                                            db.execSQL("insert into conversation(uid,name,headsmall,phone) values (?,?,?,?)", new String[]{c.getId(), c.getUser_nickname(), c.getAvatar(), c.getMobile()});
                                                        }
//                                                    db.close();
//                                                    cs.close();
                                                    } catch (SQLException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }.start();
                                            contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), SourceDateList);
                                        } else {
                                            contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                        contactListPresenter.getResult(-1, "请求超时", null);
                                    }
                                });
                            } else {
                                contactListPresenter.getResult(-1, "网络异常", null);
                            }
                        } else {
                            Toast.makeText(context, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    private String name;
    private String mobile;

    @Override
    public void filterContactList(String filterStr) {
        Log.i("ContactListPresenter","..............filterContactList="+filterStr);
        List<ContactsBean> filterDateList = new ArrayList<ContactsBean>();
        if (TextUtils.isEmpty(filterStr)) {
            Log.i("ContactListPresenter","..............TextUtils.isEmpty(filterStr)="+SourceDateList);
            filterDateList = SourceDateList;
        } else {
            Log.i("ContactListPresenter","..............else");
            filterDateList.clear();
            for (ContactsBean sortModel : SourceDateList) {
                if (TextUtils.isEmpty(sortModel.getUser_nickname())) {
                    name = sortModel.getMobile();
                } else {
                    name = sortModel.getUser_nickname();
                }
                mobile = sortModel.getMobile();
                if (name.indexOf(filterStr.toString()) != -1 || PinYinUtils.getPinYinFirstLetter(name).startsWith(filterStr.toString())
                        || mobile.indexOf(filterStr.toString()) != -1 || PinYinUtils.getPinYinFirstLetter(mobile).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }

            }

        }
        contactListPresenter.updateDataByFilter(filterDateList);
    }

    @Override
    public void getUserInformation(Context context, String my_mobile, String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getUserInformation(my_mobile, mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        ContactsBean cb = JSON.parseObject(apiResponse.getData(), ContactsBean.class);
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), cb);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void getUserInformation1(Context context, String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getUserInfo(mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        ContactsBean cb = JSON.parseObject(apiResponse.getData(), ContactsBean.class);
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), cb);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }


    @Override
    public void getRenMaiInformation(Context context, String my_mobile, String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getRenMaiInformation(my_mobile, mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        ContactsBean cb = JSON.parseObject(apiResponse.getData(), ContactsBean.class);
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), cb);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void getUserInfoByUid(Context context, String my_uid, String uid) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getUserInfoByUid(my_uid, uid, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        ContactsBean cb = JSON.parseObject(apiResponse.getData(), ContactsBean.class);
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), cb);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });
        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void delContact(Context context, String uid, final ContactsBean cb, final List<ContactsBean> list) {
        if (NetWorkUtils.isConnected(context)) {
            Api.delContact(uid, cb.getMobile(), new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        list.remove(cb);
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public synchronized void getReMai(final Context context, String mobile, String type, final List<ContactsBean> SourceDateList) {
        this.SourceDateList = SourceDateList;
        if (NetWorkUtils.isConnected(context)) {
            Api.getRenMai(mobile, type, new ApiResponseHandler(context) {

                @Override
                public void onSuccess(final ApiResponse apiResponse) {
                    if (context!=null){
                        new RxPermissions((Activity) context)
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new Observer<Boolean>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.i("getReMai","....ERROR="+e.getMessage());
                                    }

                                    @Override
                                    public void onNext(Boolean granted) {
                                        Log.i("getReMai","....onNext=");
                                        if (granted) {
                                            SourceDateList.clear();
                                            if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                                                cb = JSON.parseArray(apiResponse.getData(), ContactsBean.class);
                                                Log.i("getReMai","....cb="+cb.size());
                                                SourceDateList.addAll(filledData(cb));
                                                // 根据a-z进行排序源数据
                                                Collections.sort(SourceDateList, pinyinComparator);
                                                if (cs == null){
                                                    cs = new ConversationSqlite(context);
                                                }
                                                db = cs.getWritableDatabase();
                                                //将联系人插入数据库
                                                new Thread() {
                                                    public void run() {

                                                        try {
                                                            db.execSQL(renmai);
                                                            for (ContactsBean c : cb) {
                                                                db.execSQL("insert into renmai(uid,name,headsmall,phone) values (?,?,?,?)", new String[]{c.getId(), c.getUser_nickname(), c.getAvatar(), c.getMobile()});
                                                            }
//                                                            db.close();
//                                                            cs.close();
                                                        } catch (SQLException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }.start();
                                                contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                                                if (cb == null||cb.size() == 0){
                                                    //Toast.makeText(context, "没有人脉！", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                                            }
                                        } else {
                                            Toast.makeText(context, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void getAristocracyReMai(Context context, String uid, String conn_type,int page, String content) {
        if (NetWorkUtils.isConnected(context)) {
            Api.searchRenMai(uid, conn_type, content,page, new ApiResponseHandler(context) {

                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        List<SearchRenMaiBean> list = JSON.parseArray(apiResponse.getData(), SearchRenMaiBean.class);
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), list);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void sendInvitation(Context context, String my_mobile, String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.sendInvitation(my_mobile, mobile, new ApiResponseHandler(context) {

                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void acceptInvitation(Context context, String my_mobile, String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.acceptInvitation(my_mobile, mobile, new ApiResponseHandler(context) {

                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void acceptSanFangfKeInvitation(Context context, String uid, String tri_id, String request_type) {
        if (NetWorkUtils.isConnected(context)) {
            Api.acceptSanFangfKeInvitation(uid, tri_id, request_type, new ApiResponseHandler(context) {

                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void isBuyRenMai(Context context, String my_mobile, String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.isBuyRenMai(my_mobile, mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        RenMaiBean renMaiBean = JSON.parseObject(apiResponse.getData(), RenMaiBean.class);
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), renMaiBean);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void terminationRenMai(Context context, String my_mobile, String con_mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.terminationRenMai(my_mobile, con_mobile, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        contactListPresenter.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    contactListPresenter.getResult(-1, "请求超时", null);
                }
            });

        } else {
            contactListPresenter.getResult(-1, "网络异常", null);
        }
    }

    /**
     * 为ListView填充数据
     *
     * @param list
     * @return
     */
    private List<ContactsBean> filledData(List<ContactsBean> list) {
        List<ContactsBean> mSortList = new ArrayList<ContactsBean>();

        for (int i = 0; i < list.size(); i++) {
            ContactsBean sortModel = new ContactsBean();
            if (TextUtils.isEmpty(list.get(i).getUser_nickname())) {
                // 汉字转换成拼音
                sortString = PinYinUtils.getPinYinFirstLetter(list.get(i).getMobile()).toUpperCase();
            } else {
                sortModel.setUser_nickname(list.get(i).getUser_nickname());
                // 汉字转换成拼音
                sortString = PinYinUtils.getPinYinFirstLetter(list.get(i).getUser_nickname()).toUpperCase();

            }
            sortModel.setMobile(list.get(i).getMobile());
            sortModel.setId(list.get(i).getId());
            sortModel.setAvatar(list.get(i).getAvatar());
            sortModel.setPur_time(list.get(i).getPur_time());
            sortModel.setCon_fans_num(list.get(i).getCon_fans_num());

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

}
