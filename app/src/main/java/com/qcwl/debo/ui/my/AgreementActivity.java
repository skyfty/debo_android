package com.qcwl.debo.ui.my;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.view.ProgressWebView;

/**
 * Created by Administrator on 2017/8/21.
 */

public class AgreementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.about_layout);
        initTitleBar();

        initData();
    }


    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("三方客协议").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    ProgressWebView webView = null;

    private void initData() {
        webView = (ProgressWebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
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

        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        webView.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
        String url="http://debo.shangtongyuntian.com/index.php/Appapi/User/about_debo";
        webView.loadUrl(url);  //加载url
        webView.requestFocus(); //获取焦点
    }
}
