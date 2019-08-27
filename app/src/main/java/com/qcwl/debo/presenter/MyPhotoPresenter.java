package com.qcwl.debo.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.http.Constants;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.model.MyPhotoBean;
import com.qcwl.debo.presenterInf.MyPhotoPresenterInf;
import com.qcwl.debo.utils.NetWorkUtils;
import com.qcwl.debo.utils.ToastUtils;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */

public class MyPhotoPresenter implements MyPhotoPresenterInf.HandleData {
    private MyPhotoPresenterInf myPhotoPresenterInf;


    public MyPhotoPresenter(MyPhotoPresenterInf myPhotoPresenterInf) {
        this.myPhotoPresenterInf = myPhotoPresenterInf;
    }


    @Override
    public void getMyPhoto(Context context, String uid, final int page, String f_uid) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getMyPhoto(uid, page, f_uid, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        List<MyPhotoBean> mp = JSON.parseArray(apiResponse.getData(), MyPhotoBean.class);
                        myPhotoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), mp);
                    } else {
                        myPhotoPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    myPhotoPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            ToastUtils.showShort(context,"网络异常");
        }
    }

    @Override
    public void getMyInfo(final Context context, String uid) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getCircleTopImg(uid, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == 0) {
                        ContactsBean cb = JSON.parseObject(apiResponse.getData(),ContactsBean.class);
                        myPhotoPresenterInf.getResult(apiResponse.getCode(),apiResponse.getMessage(), cb);
                    } else {
                        myPhotoPresenterInf.getResult(apiResponse.getCode(),apiResponse.getMessage(), null);
                    }
                }
            });
        }else {
            ToastUtils.showShort(context,"网络异常");
        }
    }
}
