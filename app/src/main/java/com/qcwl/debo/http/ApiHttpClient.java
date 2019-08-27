package com.qcwl.debo.http;


import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ApiHttpClient {


 //   public static final String API_URL = "http://123.57.148.47/debo/index.php/";
 //   public static final String VIDEO_URL = "http://123.57.148.47/debo/";

    public static final String API_URL2 = "http://debo.shangtongyuntian.com/index.php/";
    public static final String API_URL = "http://debo.shangtongyuntian.com/index1.php/appapi/";
    public static final String VIDEO_URL = "http://debo.shangtongyuntian.com/";
    public static final String VIDEO_CERTIFICATE = "http://debo.shangtongyuntian.com/";//短视频凭证相关
    public static final String DELETE = "DELETE";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static AsyncHttpClient client = new AsyncHttpClient();

    public ApiHttpClient() {
    }

    public static AsyncHttpClient getHttpClient() {
        return client;
    }

    public static void cancelAll(Context context) {
        client.cancelRequests(context, true);
    }

    public static void delete(String partUrl, AsyncHttpResponseHandler handler) {
        client.delete(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("DELETE ").append(partUrl).toString());
    }

    public static void get(String partUrl, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("GET ").append(partUrl).toString());
    }

    public static void get(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder("GET ").append(partUrl).append("?").append(params).toString());
    }

    public static String getAbsoluteApiUrl(String partUrl) {
        String url = partUrl;
        if (!partUrl.startsWith("http:") && !partUrl.startsWith("https:")) {
            url = String.format(API_URL, partUrl);
        }
        Log.d("BASE_CLIENT", "request:" + url);
        return url;
    }

    public static String getApiUrl() {
        return API_URL;
    }

    public static void getDirect(String url, AsyncHttpResponseHandler handler) {
        client.get(url, handler);
        log(new StringBuilder("GET ").append(url).toString());
    }

    public static void log(String log) {
        Log.d("BaseApi", log);
    }

    public static void post(String partUrl, AsyncHttpResponseHandler handler) {
        client.post(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("POST ").append(partUrl).toString());
    }

    public static void post(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        if (client == null)
            client = new AsyncHttpClient();
        client.post(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder("POST ").append(partUrl).append("?").append(params).toString());
    }

    public static void postDirect(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.post(url, params, handler);
        log(new StringBuilder("POST ").append(url).append("?").append(params).toString());
    }

    public static void put(String partUrl, AsyncHttpResponseHandler handler) {
        client.put(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("PUT ").append(partUrl).toString());
    }

    public static void post(String host, String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(host + partUrl, params, handler);
        log(new StringBuilder("POST ").append(host).append(partUrl).toString());
    }

    public static void put(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        client.put(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder("PUT ").append(partUrl).append("?").append(params).toString());
    }

    public static void setUserAgent(String userAgent) {
        client.setUserAgent(userAgent);
    }

}
