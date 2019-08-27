package com.qcwl.debo.ui.found.memorandum.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.view.ProgressWebView;

import butterknife.ButterKnife;

public class DataStatisticsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBack;
    private TextView mTitle;
    private ProgressWebView mWebView;
    private final String webUrl = "http://debo.shangtongyuntian.com/index.php/appapi/memorandum/data_statistics";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_statistics);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        mBack = (ImageView) findViewById(R.id.left_image);
        mTitle = (TextView) findViewById(R.id.title);
        mWebView = (ProgressWebView) findViewById(R.id.webView);
        mTitle.setText("数据统计");
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.left_image:
                finish();
                break;
        }
    }

    private void initData() {

        String url = webUrl + "?uid=" + sp.getString("uid");
//        String url = webUrl + "?uid=" + 521;

        mWebView = (ProgressWebView) findViewById(R.id.webView);
        WebSettings settings = mWebView.getSettings();
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
//        settings.setUseWideViewPort(false);
//        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        // 设置缓存
        settings.setAppCacheEnabled(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
        // settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置可以被显示的屏幕控制
//        settings.setDisplayZoomControls(true);
        // 设置默认字体大小
        //settings.setDefaultFontSize(15);
        mWebView.setWebViewClient(new WebViewClient() {
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
        mWebView.loadUrl(url);  //加载url
        mWebView.requestFocus(); //获取焦点
    }


}
