package com.qcwl.debo.ui.my;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSON;
import com.baidu.mobstat.StatService;
import com.loopj.android.http.RequestParams;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiHttpClient;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.found.joke.CommentActivity;
import com.qcwl.debo.ui.found.joke.CommentBean;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.ProgressWebView;

import java.util.List;

/**
 * 关于
 */

public class AboutActivity extends BaseActivity {

    private String title = "";
    private String url = "";
    private int type = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.about_layout);
        if (getIntent() == null) {
            return;
        }
        type = getIntent().getIntExtra("type", 0);
        switch (type) {
            case 0:
                title = "关于嘚啵";
                url = "http://debo.shangtongyuntian.com/index.php/Appapi/User/about_debo";
                break;
            case 1:
                title = "积分说明";
                url = "http://debo.shangtongyuntian.com/index.php/appapi/user/point_introduce";
                break;
            case 2:
                title = "注册协议";
                url = "http://debo.shangtongyuntian.com/index.php/appapi/user/registration_agreement";
                break;
            case 3:
                title = "三方客说明";
                url = "http://debo.shangtongyuntian.com/index.php/appapi/user/tripartite_introduce";
                break;
            case 4:
                title = "抓一抓";
                String mobile = sp.getString("phone");
                url = "http://zyz.shangtongyuntian.com/user.php?action=login&login_name=" + mobile + "&login_pwd=" + mobile;
                break;
            case 5:
                title="合伙人服务协议";
                url="http://debo.shangtongyuntian.com/index.php/Appapi/copartner/debo_coins_user_agreement";
                break;
            case 6:
                title="嘚啵币用户服务合同";
                url="http://debo.shangtongyuntian.com/index.php/Appapi/copartner/debo_coins_service_contract";
                break;
            case 7:
                title="了解合伙人";
                url="http://debo.shangtongyuntian.com/index.php/Appapi/copartner/learn_about";
                break;
            case 8:
                title="存储罐协议";
                url="http://debo.shangtongyuntian.com/index.php/Appapi/copartner/storage_agreement";
                break;
            case 10:
                title="娱乐中心";
                url="http://h5.073100.net";
                break;
            default:
                break;
        }
        initTitleBar();

        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (type == 4) {
            clearCache();
        }

        switch (type) {
            case 0:
                StatService.onPageEnd(this, "结束关于嘚啵页面");
                break;
            case 1:
                StatService.onPageEnd(this, "结束积分说明页面");
                break;
            case 2:
                StatService.onPageEnd(this, "结束注册协议页面");
                break;
            case 3:
                StatService.onPageEnd(this, "结束三方客说明页面");
                break;
            case 4:
                StatService.onPageEnd(this, "结束抓一抓页面");
                break;
            case 5:
                StatService.onPageEnd(this, "结束合伙人服务协议页面");
                break;
            case 6:
                StatService.onPageEnd(this, "结束嘚啵币用户服务合同页面");
                break;
            case 7:
                StatService.onPageEnd(this, "结束了解合伙人页面");
                break;
            case 8:
                StatService.onPageEnd(this, "结束存储罐协议页面");
                break;
            case 10:
                StatService.onPageEnd(this, "结束娱乐中心页面");
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        switch (type){
            case 0:
                StatService.onPageStart(this,"启动关于嘚啵页面");
                break;
            case 1:
                StatService.onPageStart(this,"启动积分说明页面");
                break;
            case 2:
                StatService.onPageStart(this,"启动注册协议页面");
                break;
            case 3:
                StatService.onPageStart(this,"启动三方客说明页面");
                break;
            case 4:
                StatService.onPageStart(this,"启动抓一抓页面");
                break;
            case 5:
                StatService.onPageStart(this,"启动合伙人服务协议页面");
                break;
            case 6:
                StatService.onPageStart(this,"启动嘚啵币用户服务合同页面");
                break;
            case 7:
                StatService.onPageStart(this,"启动了解合伙人页面");
                break;
            case 8:
                StatService.onPageStart(this,"启动存储罐协议页面");
                break;
            case 10:
                StatService.onPageStart(this,"启动娱乐中心页面");
                break;
        }
    }

    private void clearCache() {
        RequestParams params=new RequestParams();
        params.put("uid", sp.getString("uid").getBytes());
        ApiHttpClient.post("http://zyz.shangtongyuntian.com/exit.php",
                params, new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {

                    }

                    @Override
                    public void onFailure(String errMessage) {

                    }
                });
        /*if (type==4){
            webView.loadUrl("http://zyz.shangtongyuntian.com/exit.php?uid="+sp.getString("uid"));  //加载新的url
        }*/
    }


    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle(title)
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (webView != null) {
                            try {
                                webView.destroy();
                            } catch (Exception e) {
                                System.out.println("后台no kills");
                            }
                        }
                        finish();
                    }
                });
    }

    ProgressWebView webView = null;

    private void initData() {
        webView = (ProgressWebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置不可以支持缩放
        settings.setSupportZoom(false);
        // 设置出现缩放工具
        settings.setBuiltInZoomControls(false);
        //扩大比例的缩放
        settings.setUseWideViewPort(false);
        settings.setLoadWithOverviewMode(false);
        // 设置缓存
        settings.setAppCacheEnabled(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
        // settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置可以被显示的屏幕控制
        settings.setDisplayZoomControls(true);
        // 设置默认字体大小
        //settings.setDefaultFontSize(15);
        webView.setWebViewClient(new WebViewClient() {
            //当点击链接时,希望覆盖而不是打开新窗口
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);  //加载新的url
                return true;    //返回true,代表事件已处理,事件流到此终止
            }
        });

/*        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                      //  webView.goBack();   //后退
                        if (type==4){
                            webView.loadUrl("http://zyz.shangtongyuntian.com/exit.php?uid="+sp.getString("uid"));  //加载新的url
                        }
                        finish();
                        return true;    //已处理
                    }
                }
                return false;
            }
        });*/
        webView.loadUrl(url);  //加载url
        webView.requestFocus(); //获取焦点
    }
}
