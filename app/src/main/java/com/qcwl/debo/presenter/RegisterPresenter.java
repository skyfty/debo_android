package com.qcwl.debo.presenter;

import android.content.Context;

import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.presenterInf.RegisterPresenterInf;
import com.qcwl.debo.utils.NetWorkUtils;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.ToastUtils;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2017/7/14.
 */

public class RegisterPresenter implements RegisterPresenterInf.HandleData {
    private RegisterPresenterInf data;

    public RegisterPresenter(RegisterPresenterInf data) {
        this.data = data;
    }

    @Override
    public void register(final Context context, Map<String, String> map) {
        if (!NetWorkUtils.isConnected(context)) {
            ToastUtils.showShort(context, "网络异常");
            return;
        }

        Api.register(map, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                data.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ToastUtils.showShort(context, "服务器访问失败 ");
            }

            @Override
            public void onFailure(String errMessage) {
                ToastUtils.showShort(context, errMessage);
            }
        });
    }

    @Override
    public void register(final Context context,
                         String user_nickname,
                         String phone,
                         String code,
                         String pwd,
                         String confirm_pwd,
                         String invitation_code) {
        if (NetWorkUtils.isConnected(context)) {
            Api.register(user_nickname, phone, code, pwd, confirm_pwd, invitation_code, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    data.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    ToastUtils.showShort(context, "服务器访问失败 ");
                }

                @Override
                public void onFailure(String errMessage) {
                    ToastUtils.showShort(context, errMessage);
                }
            });

        } else {
            ToastUtils.showShort(context, "网络异常");
        }
    }

    @Override
    public void getCode(final Context context, String phone) {
        if (NetWorkUtils.isConnected(context)) {
            Api.getCode(phone, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    data.getResult(100, apiResponse.getMessage(), null);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    ToastUtils.showShort(context, "服务器访问失败 ");
                }

                @Override
                public void onFailure(String errMessage) {
                    ToastUtils.showShort(context, errMessage);
                }
            });

        } else {
            ToastUtils.showShort(context, "网络异常");
        }
    }

    @Override
    public void forgetPwd(final Context context, String mobile, String code, String new_pwd) {
        if (NetWorkUtils.isConnected(context)) {
            Api.findPwd(mobile, code, new_pwd, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    data.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    ToastUtils.showShort(context, "服务器访问失败 ");
                }

                @Override
                public void onFailure(String errMessage) {
                    ToastUtils.showShort(context, errMessage);
                }
            });

        } else {
            ToastUtils.showShort(context, "网络异常");
        }
    }
}
