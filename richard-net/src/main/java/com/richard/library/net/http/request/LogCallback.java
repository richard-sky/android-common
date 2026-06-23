package com.richard.library.net.http.request;

import okhttp3.Request;

/**
 * @author: Richard
 * @createDate: 2024/9/9 9:51
 * @version: 1.0
 * @description: 日志拦截器日志回调
 */
public interface LogCallback {

    /**
     * 是否记录请求和响应header部分的日志内容
     */
    default boolean isLogHeader() {
        return true;
    }

    /**
     * 忽略记录日志的级别（详见LoggerInterceptor.IgnoreLevel）
     */
    default int ignoreLevel(Request request, ParamsTag params) {
        return LoggerInterceptor.IgnoreLevel.None;
    }

    /**
     * 请求日志内容回调
     *
     * @param request 请求对象
     * @param log     请求日志内容
     */
    default void onRequestLog(Request request, String log){

    }

    /**
     * 请求和响应日志内容回调
     *
     * @param request 请求对象
     * @param log     日志内容
     */
    void log(Request request, String log);

}
