package com.qcwl.debo.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.http.Constants;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenterInf.NewFriendsPresenterInf;
import com.qcwl.debo.utils.NetWorkUtils;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */

public class NewFriendsPresenter implements NewFriendsPresenterInf.HandleData {
    private NewFriendsPresenterInf friendsPresenterInf;

    public NewFriendsPresenter(NewFriendsPresenterInf friendsPresenterInf) {
        this.friendsPresenterInf = friendsPresenterInf;
    }

    @Override
    public void searchById(final Context context, final String uid, String mobile) {
        if (NetWorkUtils.isConnected(context)) {
            Api.searchById(uid, mobile, new ApiResponseHandler(context) {

                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        List<ContactsBean> list = JSON.parseArray(apiResponse.getData(),ContactsBean.class);
                        ContactsBean cb = list.get(0);
                        friendsPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), cb);
                    } else {
                        friendsPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    friendsPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            friendsPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    private String name;

    @Override
    public void addFriend(String uid, String filterStr) {

    }

}
