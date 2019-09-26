package com.bx.philosopher.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseActivity;

import butterknife.BindView;

/**
 * @ClassName: PayH5Activity
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/15 14:04
 */
public class PayH5Activity extends BaseActivity {

    @BindView(R.id.pay_webview)
    WebView pay_webview;
    @BindView(R.id.back)
    LinearLayout back;

    private String url;

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, PayH5Activity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        url = getIntent().getStringExtra("url");
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_pay;
    }

    @Override
    protected void initClick() {
        super.initClick();
        back.setOnClickListener(v -> {
            if (pay_webview.canGoBack()) {
                pay_webview.goBack();
            } else {
                finish();
            }
        });
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        WebSettings webSettings = pay_webview.getSettings();
        if (webSettings == null) return;
        // 支持 Js 使用
        webSettings.setJavaScriptEnabled(true);
        // 开启DOM缓存,默认状态下是不支持LocalStorage的
        webSettings.setDomStorageEnabled(true);
        // 开启数据库缓存
        webSettings.setDatabaseEnabled(true);
        // 支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        // 设置 WebView 的缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 支持启用缓存模式
        webSettings.setAppCacheEnabled(true);
        // Android 私有缓存存储，如果你不调用setAppCachePath方法，WebView将不会产生这个目录
        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());
        // 支持缩放
        webSettings.setSupportZoom(true);
        // 设置 UserAgent 属性
        webSettings.setUserAgentString("");
        pay_webview.setWebChromeClient(new WebChromeClient());
        pay_webview.setWebViewClient(new WebViewClient());
        pay_webview.addJavascriptInterface(new JSClose(), "JSClose");
        load();
    }

    private void load() {
        pay_webview.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (pay_webview.canGoBack()) {
            pay_webview.goBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class JSClose {
        @JavascriptInterface
        public void close() {
            PayH5Activity.this.finish();
        }
    }
}
