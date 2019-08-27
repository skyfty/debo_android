package com.qcwl.debo.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.view.ProgressWebView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;


/**
 * Created by Administrator on 2017/5/23.
 */

public class ErWeiMaWebActivity extends BaseActivity {
    private ProgressWebView webView;
    private ScrollView scrollView;
    private TextView textView;
    private String urls;
    private TextView textTitle;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.erweima_web);
        textTitle = (TextView) findViewById(R.id.text_title);
        webView = (ProgressWebView) findViewById(R.id.webView);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        textView = (TextView) findViewById(R.id.text_view);
        if (getIntent() == null) {
            return;
        }
        urls = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            textTitle.setText("扫描结果");
        } else {
            textTitle.setText(title);
        }
        webView.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        initWebView();

        if (urls!=null&&urls.startsWith("http://") || urls.startsWith("https://")) {
            webView.loadUrl(urls);  //加载url
        } else {
            webView.loadUrl("http://" + urls);
        }
        webView.requestFocus(); //获取焦点
//        if (isUrl(urls)) {
//            webView.setVisibility(View.VISIBLE);
//            scrollView.setVisibility(View.GONE);
//            initWebView();
//            webView.loadUrl(urls);  //加载url
//            webView.requestFocus(); //获取焦点
//        } else {
//            webView.setVisibility(View.GONE);
//            scrollView.setVisibility(View.VISIBLE);
//            textView.setText("" + urls);
//        }
    }

    /**
     * 验证网址Url
     *
     * @param str 待验证的字符串
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isUrl(String str) {
        String regex =
                // "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
                // "^(http|https|ftp)\\\\://([a-zA-Z0-9\\\\.\\\\-]+(\\\\:[a-zA-Z0-9\\\\.&amp;%\\\\$\\\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\\\-]+\\\\.)*[a-zA-Z0-9\\\\-]+\\\\.[a-zA-Z]{2,4})(\\\\:[0-9]+)?(/[^/][a-zA-Z0-9\\\\.\\\\,\\\\?\\\\\\'\\\\\\\\/\\\\+&amp;%\\\\$#\\\\=~_\\\\-@]*)*$";
                "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&amp;%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/[^/][a-zA-Z0-9\\.\\,\\?\\\'\\\\/\\+&amp;%\\$#\\=~_\\-@]*)*$";
        return match(regex, str);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private void initWebView() {
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
        settings.setDefaultFontSize(15);
        webView.setWebViewClient(new WebViewClient() {
            //当点击链接时,希望覆盖而不是打开新窗口
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);  //加载新的url
                return true;    //返回true,代表事件已处理,事件流到此终止
            }
        });

//        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
//        webView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//                        webView.goBack();   //后退
//                        return true;    //已处理
//                    }
//                }
//                return false;
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (webView.canGoBack()) {
            webView.goBack();
        }else{
            finish();
        }
    }
}
