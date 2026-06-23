package com.richard.library.basic.web;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.io.Serializable;

/**
 * <pre>
 * Description : js 执行者
 * Author : admin-richard
 * Date : 2018/11/16 14:00
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/11/16 14:00     admin-richard         new file.
 * </pre>
 */
public interface JavaScriptExecutor extends Serializable {

    /**
     * 获取执行的js方法名（不包含括号）
     */
    String getJavaScriptMethodName();

    /**
     * 获取执行方法的参数列表
     */
    String[] getParams();

    /**
     * 获取执行js方法体返回结果
     */
    ValueCallback<String> getValueCallback();


    /**
     * 执行js
     */
    default void execute(WebView webView) {
        StringBuilder methodBuilder = new StringBuilder();
        methodBuilder.append("javascript:");
        methodBuilder.append(getJavaScriptMethodName());
        methodBuilder.append("(");

        String[] paramsList = getParams();
        if (paramsList != null && paramsList.length > 0) {
            for (int i = 0; i < paramsList.length; i++) {
                methodBuilder.append(paramsList[i]);
                if (i < paramsList.length - 1) {
                    methodBuilder.append(",");
                }
            }
        }

        methodBuilder.append(")");
        webView.evaluateJavascript(
                methodBuilder.toString()
                , getValueCallback()
        );
    }

}
