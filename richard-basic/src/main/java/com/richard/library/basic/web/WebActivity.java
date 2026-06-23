package com.richard.library.basic.web;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.BasicScaffoldActivity;
import com.richard.library.basic.databinding.ViewWebBinding;
import com.richard.library.context.util.ThreadUtil;


/**
 * <pre>
 * Description : 网页浏览Activity
 * Author : admin-richard
 * Date : 2018/7/29 14:29
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/7/29 14:29     admin-richard         new file.
 * </pre>
 */
public class WebActivity extends BasicScaffoldActivity {

    private ViewWebBinding binding;
    private ThreadUtil.RunTask htmlLoadThread;

    //其它界面传递的参数
    private String title;
    private String webURL;
    private HtmlLoader htmlLoader;
    private JavaScriptExecutor javaScriptExecutor;
    private JavaScriptMethod javaScriptMethod;

    /**
     * 打开Web界面
     *
     * @param context    必填 context
     * @param htmlLoader 必填 网页富文本内容加载器
     */
    public static void start(Context context, HtmlLoader htmlLoader) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("htmlLoader", htmlLoader);
        context.startActivity(intent);
    }

    /**
     * 打开Web界面
     *
     * @param context 必填 context
     * @param webURL  必填 跳转url
     */
    public static void start(Context context, String webURL) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("webURL", webURL);
        context.startActivity(intent);
    }

    /**
     * 打开Web界面
     *
     * @param context 必填 context
     * @param title   选填 网页标题
     * @param webURL  必填 跳转url
     */
    public static void start(Context context, String title, String webURL) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("webURL", webURL);
        context.startActivity(intent);
    }

    /**
     * 打开Web界面
     *
     * @param context            必填 context
     * @param title              选填 网页标题
     * @param webURL             必填 跳转url
     * @param javaScriptExecutor 选填 js执行者
     */
    public static void start(Context context, String title, String webURL, JavaScriptExecutor javaScriptExecutor) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("webURL", webURL);
        intent.putExtra("javaScriptExecutor", javaScriptExecutor);
        context.startActivity(intent);
    }

    /**
     * 打开Web界面
     *
     * @param context          必填 context
     * @param title            选填 网页标题
     * @param webURL           必填 跳转url
     * @param javaScriptMethod 选填 js函数方法实现
     */
    public static void start(Context context, String title, String webURL, JavaScriptMethod javaScriptMethod) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("webURL", webURL);
        intent.putExtra("javaScriptMethod", javaScriptMethod);
        context.startActivity(intent);
    }

    @Override
    public void initLayoutView() {
        binding = ViewWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setLeftImageView(R.mipmap.icon_back);
        navigationbar.setLeftImageViewShow(true);
        navigationbar.setTitleTextViewShow(true);
        navigationbar.setRightImageViewShow(true);
        navigationbar.setRightImageView(R.mipmap.icon_close);

        //获取传递参数
        title = getIntent().getStringExtra("title");
        webURL = getIntent().getStringExtra("webURL");
        htmlLoader = (HtmlLoader) getIntent().getSerializableExtra("htmlLoader");
        javaScriptExecutor = (JavaScriptExecutor) getIntent().getSerializableExtra("javaScriptExecutor");
        javaScriptMethod = (JavaScriptMethod) getIntent().getSerializableExtra("javaScriptMethod");

        //设置标题
        if (htmlLoader != null) {
            title = htmlLoader.getTitle();
        }
        navigationbar.setTitle(TextUtils.isEmpty(title) ? "" : title);

        //向WebView注入js函数方法
        if (javaScriptMethod != null) {
            binding.webView.addJavascriptInterface(javaScriptMethod, javaScriptMethod.getInstanceName());
        }

        //注册js执行者
        this.registerJavaScriptExecutor(javaScriptExecutor);

        //加载网页
        this.startLoadURL();
    }


    @Override
    public void bindListener() {
        //网页标题文本回调
        binding.webView.setReceiveTitleCallback((view, title) -> {
            if (TextUtils.isEmpty(WebActivity.this.title)) {
                navigationbar.setTitle(title);
            }
        });

        navigationbar.setRightImageViewClickListener((v) -> {
            finish();
        });
    }


    /**
     * 开始加载网页
     */
    private void startLoadURL() {
        if (!TextUtils.isEmpty(webURL)) {
            binding.webView.loadUrl(webURL);
            return;
        }

        if (htmlLoader == null) {
            return;
        }

        if (htmlLoadThread != null) {
            htmlLoadThread.cancel(true);
            htmlLoadThread = null;
        }

        htmlLoadThread = new ThreadUtil.RunTask() {
            @Override
            public void runEvent() {
                String data = htmlLoader.getHtml();
                runOnUiThread(() -> {
                    if (isFinishing()) {
                        return;
                    }
                    if (htmlLoader.isTextViewShow()) {
                        binding.webView.setVisibility(View.GONE);
                        binding.tvHtmlText.setVisibility(View.VISIBLE);
                        binding.tvHtmlText.setText(
                                data.contains("html") || data.contains("HTML")
                                        ? Html.fromHtml(data)
                                        : data
                        );
                    } else {
                        binding.webView.loadHTML(data, false);
                    }
                });
            }
        };

        ThreadUtil.executeByCached(htmlLoadThread);
    }


    /**
     * 注册js执行者
     */
    private void registerJavaScriptExecutor(JavaScriptExecutor javaScriptExecutor) {
        if (javaScriptExecutor == null) {
            return;
        }
        binding.webView.setOnLoadFinishCallback(webView -> javaScriptExecutor.execute(webView.getWebView()));
    }


    @Override
    public void onBackPressed() {
        if (binding.webView.getWebView().canGoBack()) {
            binding.webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        binding.webView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        binding.webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        binding.webView.destroy();
        if (javaScriptMethod != null) {
            binding.webView.getWebView().removeJavascriptInterface(javaScriptMethod.getInstanceName());
        }
        super.onDestroy();
    }
}