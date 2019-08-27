package com.qcwl.debo.presenter;

import android.content.Context;
import android.util.Log;

import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.http.Constants;
import com.qcwl.debo.presenterInf.PublishWishingPresenterInf;
import com.qcwl.debo.utils.NetWorkUtils;

import org.apache.http.Header;
import org.json.JSONArray;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/7/31.
 */

public class PublishWishingPresenter implements PublishWishingPresenterInf.HandleData {

    private PublishWishingPresenterInf publishWishingPresenterInf;


    public PublishWishingPresenter(PublishWishingPresenterInf publishWishingPresenterInf) {
        this.publishWishingPresenterInf = publishWishingPresenterInf;
    }

    @Override
    public void publishWishingStar(Context context, String uid, String content) {
        if (NetWorkUtils.isConnected(context)) {
            Api.publishWishingStar(uid, content, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        publishWishingPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    } else {
                        publishWishingPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    publishWishingPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            publishWishingPresenterInf.getResult(-1, "网络异常", null);
        }

    }

    @Override
    public void publishAdvertising(Context context, String ad_type, String uid, String title, String ad_link, String price, String p_num, String ad_content, String start_time, String time_range, List<File> avatar) {
        if (NetWorkUtils.isConnected(context)) {
            Api.publishAdvertising(ad_type, uid, title, ad_link, price, p_num, ad_content, start_time, time_range, avatar, new ApiResponseHandler(context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    Log.i("PublishWishingPresenter","............apiResponse="+apiResponse.getMessage()+"   "+apiResponse.getCode()+"   "+apiResponse.getData());
                    if (apiResponse.getCode() == Constants.OP_SUCCESS) {
                        publishWishingPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), apiResponse.getData());
                    } else {
                        publishWishingPresenterInf.getResult(apiResponse.getCode(), apiResponse.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    publishWishingPresenterInf.getResult(-1, "请求超时", null);
                }
            });

        } else {
            publishWishingPresenterInf.getResult(-1, "网络异常", null);
        }
    }
}
