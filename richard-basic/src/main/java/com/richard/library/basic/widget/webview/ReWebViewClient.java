package com.richard.library.basic.widget.webview;

import static android.view.View.GONE;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.richard.library.context.AppContext;

/**
 * <pre>
 * Description : WebViewClient
 * Author : admin-richard
 * Date : 2018/9/28 09:21
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/9/28 09:21     admin-richard         new file.
 * </pre>
 * <p>
 * ERROR_UNKNOWN 通用错误
 * ERROR_HOST_LOOKUP 服务器或代理主机查找失败(链接有问题)
 * ERROR_UNSUPPORTED_AUTH_SCHEME 不支持身份验证
 * ERROR_AUTHENTICATION 用户身份验证失败
 * ERROR_PROXY_AUTHENTICATION 代理用户身份验证失败
 * ERROR_CONNECT 连接服务失败
 * ERROR_IO 读取写入失败
 * ERROR_TIMEOUT 连接超时
 * ERROR_REDIRECT_LOOP 太多重定向
 * ERROR_UNSUPPORTED_SCHEME URL格式有问题
 * ERROR_FAILED_SSL_HANDSHAKE 未能执行SSL握手
 * ERROR_BAD_URL URL格式有问题
 * ERROR_FILE 普通文件错误
 * ERROR_FILE_NOT_FOUND 文件没有发现
 * ERROR_TOO_MANY_REQUESTS 太多请求
 */
public class ReWebViewClient extends WebViewClient {

    private ReWebView mReWebView;
    private ErrorView mErrorView;

    private int sameUrlLoadingCount;
    private String tempLoadingUrl;
    private boolean isGoBack;//是否为点击返回按钮时加载
    private ReWebView.OverrideUrlLoadingCallback mOverrideUrlLoadingCallback;
    private ReWebView.OnLoadFinishListener onLoadFinishListener;


    public ReWebViewClient(ReWebView mReWebView, ErrorView mErrorView) {
        this.mReWebView = mReWebView;
        this.mErrorView = mErrorView;
        mReWebView.getWebView().setOnClickListener((View v) -> isGoBack = false);
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        this.handleError(view, errorCode, failingUrl, description);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest resourceRequest, WebResourceError webResourceError) {
        this.onReceivedError(
                view
                , webResourceError.getErrorCode()
                , webResourceError.getDescription().toString()
                , resourceRequest.getUrl().toString()
        );
        super.onReceivedError(view, resourceRequest, webResourceError);
    }

    @Override
    public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
        this.handleError(
                webView
                , webResourceResponse.getStatusCode()
                , webResourceRequest.getUrl().toString()
                , ""
        );
    }

    @Override
    public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
        if (!TextUtils.isEmpty(url) && url.equals(webView.getUrl())) {

        }
        super.onPageStarted(webView, url, bitmap);
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        if (!TextUtils.isEmpty(url) && url.equals(webView.getUrl())) {
            if (onLoadFinishListener != null) {
                onLoadFinishListener.onLoadFinish(mReWebView);
            }
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        return this.shouldOverrideUrlLoading(webView, webResourceRequest.getUrl().toString());
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (AppContext.isDebug()) {
            Log.d(ReWebView.WEB_TAG, "--[当前URL]-->>>" + url);
        }

        mErrorView.setVisibility(GONE);

        //电话 ， 邮箱 ， 短信
        if (this.handleCommonLink(url)) {
            return true;
        }

        //Intent scheme
        if (!url.startsWith("http:") && !url.startsWith("https:")
                && !url.startsWith("ftp:") && url.contains("://")) {
            this.handleIntentUrl(url);
            return true;
        }

        //解决无法返回上一级页面的问题
        sameUrlLoadingCount = !TextUtils.isEmpty(tempLoadingUrl) && tempLoadingUrl.equals(url)
                ? ++sameUrlLoadingCount
                : 0;

        if (isGoBack && sameUrlLoadingCount > 0) {
            return false;
        }
        tempLoadingUrl = url;

        if (null != mOverrideUrlLoadingCallback) {
            return mOverrideUrlLoadingCallback.onUrlLoading(mReWebView, url);
        }

        return false;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (request == null || request.getUrl() == null || TextUtils.isEmpty(request.getUrl().toString())) {
            return null;
        }
        return super.shouldInterceptRequest(view, request);
    }

    /**
     * 处理加载错误
     */
    private void handleError(WebView webView, int statusCode, String failURL, String description) {
        if (AppContext.isDebug()) {
            Log.e(ReWebView.WEB_TAG, String.format(
                    "[errorCode=%s]--[description=%s]--[failingUrl=%s]"
                    , statusCode
                    , description
                    , failURL
            ));
        }

//        if (!webView.getUrl().equals(failURL)) {
//            return;
//        }
//
//        mErrorView.setErrorMessage(String.format(
//                "加载页面失败\n[errorCode-%s]"
//                , String.valueOf(statusCode)
//        ));
//        mErrorView.setVisibility(VISIBLE);
    }

    public void setGoBack(boolean goBack) {
        isGoBack = goBack;
    }


    public void setOverrideUrlLoadingCallback(ReWebView.OverrideUrlLoadingCallback overrideUrlLoadingCallback) {
        mOverrideUrlLoadingCallback = overrideUrlLoadingCallback;
    }

    public void setOnLoadFinishListener(ReWebView.OnLoadFinishListener onLoadFinishListener) {
        this.onLoadFinishListener = onLoadFinishListener;
    }

    //---------------------------------------------------------------------------------------------
    public static final String INTENT_SCHEME = "intent://";

    /**
     * 处理跳转命令
     */
    private boolean handleCommonLink(String url) {
        if (url.startsWith(WebView.SCHEME_TEL)
                || url.startsWith("sms:")
                || url.startsWith(WebView.SCHEME_MAILTO)
                || url.startsWith(WebView.SCHEME_GEO)) {
            try {
                if ((mReWebView.getContext()) == null) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mReWebView.getContext().startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
            }
            return true;
        }
        return false;
    }

    /**
     * 处理intent URL
     */
    private void handleIntentUrl(String intentUrl) {
        try {
            if (TextUtils.isEmpty(intentUrl)) {
                return;
            }

            if (mReWebView.getContext() == null) {
                return;
            }
            PackageManager packageManager = mReWebView.getContext().getPackageManager();
            Intent intent = Intent.parseUri(intentUrl, Intent.URI_INTENT_SCHEME);
            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            // 跳到该应用
            if (info != null) {
                mReWebView.getContext().startActivity(intent);
            }
        } catch (Throwable ignore) {
            Toast.makeText(mReWebView.getContext(), "系统暂未安装支持该功能的APP", Toast.LENGTH_SHORT).show();
        }
    }
}
