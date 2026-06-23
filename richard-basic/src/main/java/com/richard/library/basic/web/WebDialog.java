package com.richard.library.basic.web;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.richard.library.basic.basic.BasicScaffoldDialogFragment;
import com.richard.library.basic.databinding.ViewWebBinding;
import com.richard.library.context.util.ThreadUtil;

/**
 * <pre>
 * Description : 网页浏览Dialog
 * Author : admin-richard
 * Date : 2018/7/29 14:29
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/7/29 14:29     admin-richard         new file.
 * </pre>
 */
public class WebDialog extends BasicScaffoldDialogFragment {

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
     * @param manager    必填 FragmentManager
     * @param htmlLoader 必填 网页富文本内容加载器
     */
    public static void start(FragmentManager manager, HtmlLoader htmlLoader) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("htmlLoader", htmlLoader);

        WebDialog fragment = new WebDialog();
        fragment.setArguments(bundle);
        fragment.show(manager);
    }

    /**
     * 打开Web界面
     *
     * @param manager        必填 FragmentManager
     * @param title          选填 网页标题
     * @param htmlContent    必填 网页内容或者跳转url
     * @param isTextViewShow 必填 是否使用TextView控件HTML化显示
     */
    public static void start(FragmentManager manager, String title, String htmlContent, boolean isTextViewShow) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("htmlContent", htmlContent);
        bundle.putBoolean("isTextViewShow", isTextViewShow);

        WebDialog fragment = new WebDialog();
        fragment.setArguments(bundle);
        fragment.show(manager);
    }

    /**
     * 打开Web界面
     *
     * @param manager 必填 FragmentManager
     * @param webURL  必填 跳转url
     */
    public static void start(FragmentManager manager, String webURL) {
        Bundle bundle = new Bundle();
        bundle.putString("webURL", webURL);

        WebDialog fragment = new WebDialog();
        fragment.setArguments(bundle);
        fragment.show(manager);
    }

    /**
     * 打开Web界面
     *
     * @param manager 必填 FragmentManager
     * @param title   选填 网页标题
     * @param webURL  必填 跳转url
     */
    public static void start(FragmentManager manager, String title, String webURL) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("webURL", webURL);

        WebDialog fragment = new WebDialog();
        fragment.setArguments(bundle);
        fragment.show(manager);
    }

    /**
     * 打开Web界面
     *
     * @param manager            必填 FragmentManager
     * @param title              选填 网页标题
     * @param webURL             必填 跳转url
     * @param javaScriptExecutor 选填 js执行者
     */
    public static void start(FragmentManager manager, String title, String webURL, JavaScriptExecutor javaScriptExecutor) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("webURL", webURL);
        bundle.putSerializable("javaScriptExecutor", javaScriptExecutor);

        WebDialog fragment = new WebDialog();
        fragment.setArguments(bundle);
        fragment.show(manager);
    }

    /**
     * 打开Web界面
     *
     * @param manager          必填 FragmentManager
     * @param title            选填 网页标题
     * @param webURL           必填 跳转url
     * @param javaScriptMethod 选填 js函数方法实现
     */
    public static void start(FragmentManager manager, String title, String webURL, JavaScriptMethod javaScriptMethod) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("webURL", webURL);
        bundle.putSerializable("javaScriptMethod", javaScriptMethod);

        WebDialog fragment = new WebDialog();
        fragment.setArguments(bundle);
        fragment.show(manager);
    }

    @Override
    public void initLayoutView() {
        binding = ViewWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitleTextViewShow(true);
        super.setMargin(30, 70);

        //获取传递参数
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString("title");
            webURL = bundle.getString("webURL");
            htmlLoader = (HtmlLoader) bundle.getSerializable("htmlLoader");
            javaScriptExecutor = (JavaScriptExecutor) bundle.getSerializable("javaScriptExecutor");
            javaScriptMethod = (JavaScriptMethod) bundle.getSerializable("javaScriptMethod");
        }

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
            if (TextUtils.isEmpty(WebDialog.this.title)) {
                navigationbar.setTitle(title);
            }
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
                    if (isDetached()) {
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
    public void onDestroyView() {
        if (htmlLoadThread != null) {
            htmlLoadThread.cancel(true);
            htmlLoadThread = null;
        }
        if (javaScriptMethod != null) {
            binding.webView.getWebView().removeJavascriptInterface(javaScriptMethod.getInstanceName());
        }
        binding.webView.destroy();
        super.onDestroyView();
    }
}
