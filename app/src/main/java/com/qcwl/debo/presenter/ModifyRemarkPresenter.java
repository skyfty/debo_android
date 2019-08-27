package com.qcwl.debo.presenter;

import android.content.Context;

import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.http.Constants;
import com.qcwl.debo.presenterInf.ModifyRemarkPresenterInf;
import com.qcwl.debo.utils.NetWorkUtils;

import org.apache.http.Header;
import org.json.JSONArray;

/**
 * Created by Administrator on 2017/7/28.
 */

public class ModifyRemarkPresenter implements ModifyRemarkPresenterInf.HandleData {

    private ModifyRemarkPresenterInf modifyRemarkPresenterInf;

    public ModifyRemarkPresenter(ModifyRemarkPresenterInf modifyRemarkPresenterInf) {
        this.modifyRemarkPresenterInf = modifyRemarkPresenterInf;
    }

    @Override
    public void modifyRemark(Context context, String uid, String f_uid, String f_mobile, String remark) {
        if (NetWorkUtils.isConnected(context)) {
            Api.modifyRemark(uid, f_uid, f_mobile, remark, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        modifyRemarkPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        modifyRemarkPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    modifyRemarkPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            modifyRemarkPresenterInf.getResult(-1, "网络异常", null);
        }
    }

    @Override
    public void modifyRenMaiRemark(Context context, String uid, String f_uid, String f_mobile, String remark) {
        if (NetWorkUtils.isConnected(context)) {
            Api.modifyRenMaiRemark(uid, f_uid, f_mobile, remark, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        modifyRemarkPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        modifyRemarkPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    modifyRemarkPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            modifyRemarkPresenterInf.getResult(-1, "网络异常", null);
        }
    }
}
