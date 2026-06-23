package com.richard.dev.common;

import android.webkit.JavascriptInterface;

import com.richard.library.basic.util.ToastUtil;
import com.richard.library.basic.web.JavaScriptMethod;
import com.richard.library.context.util.UIThread;

/**
 * @author: Administrator
 * @createDate: 2022/3/31 15:19
 * @version: 1.0
 * @description: 注入的js函数方法实现
 */
public class UserJavaScriptMethod implements JavaScriptMethod {

    private static final long serialVersionUID = -2502014283497152157L;

    @Override
    public String getInstanceName() {
        return "android";
    }

    @JavascriptInterface
    public void sendMsg(String msg){
        ToastUtil.show((UIThread.isMainThread() ? "主线程-" : "子线程-" ) + msg);
    }

}
