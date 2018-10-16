package com.mp.sharedandroid.utils;

import android.graphics.Color;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Method;

/**
 * webView工具类
 * Created by lwk on 2018/8/22.
 */

public class WebViewUtil {

    public static void configWebview(WebView wb) {
        if (wb != null) {
            wb.getSettings().setJavaScriptEnabled(true);
            wb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            wb.getSettings().setSupportZoom(false);
            wb.getSettings().setBuiltInZoomControls(false);
            wb.setBackgroundColor(Color.parseColor("#00000000"));// 背景色设置为透明
            wb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            wb.setWebChromeClient(new WebChromeClient());
        }
    }
    /**
     * 每次使用完webView之后都要手动回收没存防止OOM
     *
     * @param webView
     */
    public static void DestoryWebView(WebView webView) {
        if (webView != null) {
            Method method = null;
            try {
                method = WebView.class.getMethod("onPause");
                method.invoke(webView);
            } catch (Exception e) {
            }
            webView.removeAllViews();
            webView.destroy();
        }
    }
}
