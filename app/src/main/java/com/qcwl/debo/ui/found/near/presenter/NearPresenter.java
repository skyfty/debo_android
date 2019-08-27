package com.qcwl.debo.ui.found.near.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.found.near.bean.NearBean;
import com.qcwl.debo.ui.found.near.bean.TrumpetBean;
import com.qcwl.debo.ui.found.near.contact.NearContact;
import com.qcwl.debo.utils.ToastUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/4.
 */

public class NearPresenter implements NearContact.Presenter {

    private NearContact.View view;

    public NearPresenter(NearContact.View view) {
        this.view = view;
    }

    @Override
    public void getTrumpetList(final Context context, String uid) {
        Api.getTrumpetList(uid, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<TrumpetBean> items = JSON.parseArray(apiResponse.getData(), TrumpetBean.class);
                    view.doTrumpetListSuccess(items);
                } else {
                    ToastUtils.showShort(context, "" + apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void getNearList(final Context context, String uid, double lat, double lng, int distance) {
        Api.getNearList(uid, "" + lat, "" + lng, distance, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<NearBean> list = JSON.parseArray(apiResponse.getData(), NearBean.class);
                    view.doNearListSuccess(list);
                } else {
                    ToastUtils.showShort(context, "" + apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }

    @Override
    public void trumpetShout(final Context context, String uid, String lat, String lng, String range, String content) {
        Api.trumpetShout(uid, lat, lng, range, content, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    view.doTrumpetShout();
                } else {
                    ToastUtils.showShort(context, "" + apiResponse.getMessage());
                    view.doFailure(apiResponse.getCode());
                }
            }
        });
    }
}
