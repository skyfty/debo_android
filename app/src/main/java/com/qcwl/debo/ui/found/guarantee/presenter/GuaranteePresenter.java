package com.qcwl.debo.ui.found.guarantee.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.found.guarantee.GuaranteeDetailBean;
import com.qcwl.debo.ui.found.guarantee.contract.GuaranteeContract;
import com.qcwl.debo.utils.ToastUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/19.
 */

public class GuaranteePresenter implements GuaranteeContract.Presenter {

    public static final int TYPE_DETAIL_LIST = 1;
    public static final int TYPE_CONFIRM_DETAIL = 2;

    private GuaranteeContract.View view;

    public GuaranteePresenter(GuaranteeContract.View view) {
        this.view = view;
    }

    @Override
    public void getGuaranteeDetailList(final Context context, String uid, int page) {
        Api.getGuaranteeDetailList(uid, page, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                try {
                    if (apiResponse.getCode() == 0) {
                        List<GuaranteeDetailBean> list = JSON.parseArray(apiResponse.getData(), GuaranteeDetailBean.class);
                        view.doSuccess(TYPE_DETAIL_LIST, list);
                    } else {
                        ToastUtils.showShort(context, apiResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void confirmGuaranteeDetail(final Context context, String uid, final int state, String tri_id, int request_type) {
        Api.confirmGuaranteeDetail(uid, tri_id, request_type, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                try {
                    //int code = apiResponse.getCode();
                    if (state == 1) {
                        view.doSuccess(TYPE_CONFIRM_DETAIL, "");
                    } else {
                        ToastUtils.showShort(context, apiResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
