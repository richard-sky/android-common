package com.richard.dev.common;

import android.webkit.ValueCallback;

import com.richard.library.basic.util.ToastUtil;
import com.richard.library.basic.web.JavaScriptExecutor;

/**
 * @ProjectName: App开发通用库
 * @Package: com.richard.dev.common
 * @ClassName: UserJavaScript
 * @CreateDate: 2022/3/11 14:03
 * @Author: Richard
 * @Version: 1.0
 * @Description: 描述
 */
public class UserJavaScript implements JavaScriptExecutor {

    private static final long serialVersionUID = 5045548035971359454L;

    @Override
    public String getJavaScriptMethodName() {
        return "show";
    }

    @Override
    public String[] getParams() {
        return new String[]{};
    }

    @Override
    public ValueCallback<String> getValueCallback() {
        return new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                ToastUtil.show(s);
            }
        };
    }
}
