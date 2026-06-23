package com.richard.library.basic.widget.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.richard.library.context.util.DensityUtilKt;
import com.richard.library.context.AppContext;
import java.io.File;


/**
 * <pre>
 * Description : 自定义WebView组合控件
 * Author : admin-richard
 * Date : 2018/05/20 11:58
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/05/20 11:58     admin-richard         new file.
 * </pre>
 */
public class ReWebView extends FrameLayout {

    public static final String WEB_TAG = "webView";

    private WebView webView;
    private ProgressBarView mProgressBarView;
    private ErrorView mErrorView;//错误视图

    private ReWebChromeClient mReWebChromeClient;
    private ReWebViewClient mReWebViewClient;
    private ReDownLoadListener mReDownLoadListener;


    public ReWebView(@NonNull Context context) {
        super(context);
        init();
    }

    public ReWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReWebView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        if (isInEditMode()) {
            return;
        }

        webView = new WebView(getContext().getApplicationContext());
        //初始化进度条
        mProgressBarView = new ProgressBarView(getContext());
        this.post(() -> mProgressBarView.setLayoutParams(new LayoutParams(
                getMeasuredWidth()
                , DensityUtilKt.dp2px(2F,getContext()))
        ));
        this.addView(webView);
        this.addView(mProgressBarView);

        this.initErrorView();
        this.initWebViewSettings();
        webView.setClickable(true);
    }


    /**
     * 初始化错误页面View
     *
     * @return
     */
    private void initErrorView() {
        //根视图
        mErrorView = new ErrorView(getContext());
        mErrorView.setCallback(() -> getWebView().reload());
        this.addView(mErrorView);
    }


    /**
     * 初始化webView参数设置
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        webView.removeJavascriptInterface("accessibility");
        webView.removeJavascriptInterface("accessibilityTraversal");
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setHorizontalScrollBarEnabled(false);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSavePassword(false);
        webSettings.setTextZoom(100);
        webSettings.setDatabaseEnabled(true);
        //webSettings.setAppCacheEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportMultipleWindows(false);
        // 是否阻塞加载网络图片  协议http or https
        webSettings.setBlockNetworkImage(false);
        // 允许加载本地文件html  file协议
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            webSettings.setAllowFileAccessFromFileURLs(false);
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            webSettings.setAllowUniversalAccessFromFileURLs(false);
        }
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setUseWideViewPort(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setNeedInitialFocus(true);
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setDefaultFontSize(16);
        webSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        webSettings.setGeolocationEnabled(true);

        String dir = getContext().getFilesDir().getAbsolutePath() + "/cache";
        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        webSettings.setGeolocationDatabasePath(dir);
        webSettings.setDatabasePath(dir);
        //缓存文件最大值
        webSettings.setUserAgentString(System.getProperty("http.agent"));

        if (this.isConnected()) {
            //根据cache-control获取数据。
//            webSettings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
            webSettings.setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);
        } else {
            //没网，则从本地获取，即离线加载
            webSettings.setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //开启WebView Debug模式，便于AppIum自动化测试
        if (AppContext.isDebug() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        //文件下载监听
        mReDownLoadListener = new ReDownLoadListener(getContext());
        webView.setDownloadListener(mReDownLoadListener);

        //初始化WebChromeClient
        mReWebChromeClient = new ReWebChromeClient(this);
        webView.setWebChromeClient(mReWebChromeClient);

        //初始化WebViewClient
        mReWebViewClient = new ReWebViewClient(this, mErrorView);
        webView.setWebViewClient(mReWebViewClient);
    }

    /**
     * 网络是否连接
     */
    private boolean isConnected() {
        NetworkInfo info;
        ConnectivityManager cm = (ConnectivityManager) AppContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        info = cm != null ? cm.getActiveNetworkInfo() : null;
        return info != null && info.isConnected();
    }

    /**
     * 刷新进度条
     */
    void onProgressChanged(int newProgress) {
        if (newProgress == 0) {
            mProgressBarView.reset();
            mProgressBarView.show();
        } else if (newProgress >= 1 && newProgress <= 99) {
            mProgressBarView.setProgress(newProgress);
        } else if (newProgress >= 100) {
            mProgressBarView.setProgress(newProgress);
            mProgressBarView.hide();
        }
    }

    public void onResume() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.onResume();
    }

    public void onPause() {
        webView.getSettings().setJavaScriptEnabled(false);
        webView.onPause();
    }

    public void destroy() {
        if (mReDownLoadListener != null) {
            mReDownLoadListener.onDestroy();
        }

        webView.setDownloadListener(null);
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
//        this.clearAllCache();
        this.removeView(webView);
        webView.removeAllViews();
        webView.destroy();
    }


    /**
     * 清除缓存
     */
//    public void clearAllCache() {
//        webView.getSettings().setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);
//        webView.clearCache(true);
//        webView.clearFormData();
//        webView.clearHistory();
//    }


    /**
     * 添加javascript 调用方法
     *
     * @param object 调用的方法所在的实例
     * @param method JS引用的实例名
     */
    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object object, String method) {
        webView.addJavascriptInterface(object, method);
    }

    /**
     * 网页加载
     *
     * @param url 加载URL
     */
    public void loadUrl(String url) {
        if (AppContext.isDebug()) {
            Log.d(WEB_TAG, "--[原始URL]-->>>" + url);
        }
        webView.loadUrl(url);
    }

    /**
     * 网页加载
     *
     * @param htmlContent HTML源文件串
     */
    public void loadHTML(String htmlContent, boolean isAddViewPortMetaTag) {
        if (isAddViewPortMetaTag) {
            htmlContent = "<meta name=\"viewport\" content=\"width=device-width,initial-scale=0.8,minimum-scale=0.8,maximum-scale=0.8,user-scalable=no\" />"
                    .concat(htmlContent);
        }

        if (AppContext.isDebug()) {
            Log.d(WEB_TAG, String.format(
                    "--[HTML内容]-->>>\n%s"
                    , htmlContent
            ));
        }

        webView.loadData(htmlContent, "text/html; charset=UTF-8", "UTF-8");
    }

    /**
     * 网页加载
     *
     * @param baseUrl    基本URL
     * @param data       传递数据（或者HTML data）
     * @param mimeType   mineType
     * @param encoding   编码方式
     * @param historyUrl 历史URL
     */
    public void loadDataWithBaseURL(
            String baseUrl
            , String data
            , String mimeType
            , String encoding
            , String historyUrl
    ) {
        if (AppContext.isDebug()) {
            Log.d(WEB_TAG, String.format(
                    "--[baseUrl=%s]--[mimeType=%s]--[encoding=%s]--[historyUrl=%s]--[HTML内容]-->>>\n%s"
                    , baseUrl
                    , mimeType
                    , encoding
                    , historyUrl
                    , data
            ));
        }

        webView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    /**
     * 获取webView控件
     */
    public WebView getWebView() {
        return webView;
    }

    /**
     * 返回到上一级
     */
    public void goBack() {
        mReWebViewClient.setGoBack(true);
        webView.goBack();
    }

    /**
     * 设置UA
     */
    public void setUserAgentString(String userAgent) {
        WebSettings setting = getWebView().getSettings();
        setting.setUserAgentString(setting.getUserAgentString().concat(userAgent));
    }

    /**
     * 设置接收网页标题回调
     */
    public void setReceiveTitleCallback(ReceiveTitleCallback receiveTitleCallback) {
        this.mReWebChromeClient.setReceiveTitleCallback(receiveTitleCallback);
    }

    /**
     * 设置图片选择器回调
     */
    public void setPickerImageCallback(ReWebChromeClient.PickerImageCallback pickerImageCallback) {
        mReWebChromeClient.setPickerImageCallback(pickerImageCallback);
    }

    /**
     * 处理拍照或者选择相册选择以后的图片文件
     */
    public void handleImageFile(File imageFile) {
        mReWebChromeClient.handleImageFile(imageFile);
    }

    /**
     * 设置url拦截回调
     */
    public void setOverrideUrlLoadingCallback(OverrideUrlLoadingCallback overrideUrlLoadingCallback) {
        this.mReWebViewClient.setOverrideUrlLoadingCallback(overrideUrlLoadingCallback);
    }

    /**
     * 设置网页加载完成回调(可能会调用多次)
     */
    public void setOnLoadFinishCallback(OnLoadFinishListener onLoadFinishListener) {
        this.mReWebViewClient.setOnLoadFinishListener(onLoadFinishListener);
    }

    /**
     * 接收标题回调
     */
    public interface ReceiveTitleCallback {
        void callback(ReWebView view, String title);
    }

    /**
     * 加载url时回调
     */
    public interface OverrideUrlLoadingCallback {
        boolean onUrlLoading(ReWebView view, String url);
    }

    /**
     * 加载网页完成回调
     */
    public interface OnLoadFinishListener {
        void onLoadFinish(ReWebView webView);
    }
}
